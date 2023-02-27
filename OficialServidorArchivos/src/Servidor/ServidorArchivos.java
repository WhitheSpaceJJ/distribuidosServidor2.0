package Servidor;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ServidorArchivos implements Runnable {

    private final Socket clientSocket;
    private final String rutaServidor;
    private final String ipCliente;

    public ServidorArchivos(Socket clientSocket, String rutaServidor) {
        this.clientSocket = clientSocket;
        this.rutaServidor = rutaServidor;
        this.ipCliente = clientSocket.getInetAddress().getHostAddress();
    }

    @Override
    public void run() {
        try {
            // Obtener el flujo de entrada del cliente
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            ProtocoloServidor pro = new ProtocoloServidor(rutaServidor);
            File inputLine;
            while (true) {
                inputLine = pro.processInput(inFromClient.readLine());
                if (inputLine != null) {
                    break;
                }
            }

            // Obtener el flujo de salida del cliente
            DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());

            // Enviar un mensaje de confirmación al cliente
            outToClient.writeBytes("El archivo " + inputLine.getName() + " existe y se enviará por UDP" + '\n');

            // Cerrar la conexión TCP con el cliente
            clientSocket.close();

            // Crear un socket de servidor para enviar el archivo por UDP
            DatagramSocket datagramSocket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(ipCliente); // Dirección IP del cliente
            int puertoCliente = 9876;
            // Leer el contenido del archivo
            FileInputStream fis = new FileInputStream(inputLine);

            int packetSize = (inputLine.length() < 1024) ? Math.toIntExact(inputLine.length()) : 1024;

            // Definir el tamaño del paquete
            // Leer el archivo en bloques de 1024 bytes y enviar cada bloque como un paquete UDP
            byte[] buffer = new byte[packetSize];
            int bytesRead = fis.read(buffer);
            long counter = 0; // Contador para el número de paquetes enviados
            List<byte[]> lista = new ArrayList<>();
            while (bytesRead != -1) {
                // Convertir el contador a 8 bytes y agregarlo al principio del bloque
                ByteBuffer bb = ByteBuffer.allocate(8).putLong(counter);
                byte[] counterBytes = bb.array();
                byte[] block = new byte[packetSize + 8];
                System.arraycopy(counterBytes, 0, block, 0, 8);
                System.arraycopy(buffer, 0, block, 8, bytesRead);

                lista.add(block);

                counter++; // Incrementar el contador
                bytesRead = fis.read(buffer);

            }
            for (int i = 0; i < lista.size(); i++) {
                byte[] block = lista.get(i);
                // Enviar el paquete UDP con el contenido del archivo al cliente
                DatagramPacket packet = new DatagramPacket(block, block.length, address, puertoCliente);
                datagramSocket.send(packet);
            }
            try {
                System.out.println("Número de paquetes enviados: " + counter);
                // Enviar un paquete vacío con un contador mayor al número de paquetes enviados para indicar el final del archivo
                ByteBuffer bb = counter >= 2 ? ByteBuffer.allocate(lista.size()) : ByteBuffer.allocate(8);
                bb.putLong(counter);
                byte[] counterBytes = bb.array();
                byte[] endPacket = new byte[8];
                System.arraycopy(counterBytes, 0, endPacket, 0, 8);
                DatagramPacket packet = new DatagramPacket(endPacket, endPacket.length, address, puertoCliente);
                datagramSocket.send(packet);
                System.out.println("El archivo se envió correctamente");
            } catch (IOException | BufferOverflowException s) {
                System.out.println("Error: " + s.getMessage());
                System.exit(0);
            }

            // Cerrar conexiones
            fis.close();
            datagramSocket.close();
        } catch (IOException e) {
            System.out.println("Error; " + e.getMessage());
        }
    }

}
