package Cliente;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Cliente {

    public static void main(String[] args) {

        String rutaServidor="";

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        JPanel accessory = new JPanel();
        accessory.add(new JLabel("Seleccione la carpeta donde desea descargar los archivos"));
        fileChooser.setAccessory(accessory);
        int returnValue = fileChooser.showOpenDialog(new JFrame());
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String selectedFolder = fileChooser.getSelectedFile().getAbsolutePath();

            rutaServidor = selectedFolder;
        }
        Scanner tec = new Scanner(System.in);

        try {
            // PArte del TCP
            // Dirección IP y puerto del servidor
            String direccionServidor = "localhost";
            int puertoServidor = 1234;

            // Crear socket de cliente para conectarse al servidor por TCP
            Socket clientSocket = new Socket(direccionServidor, puertoServidor);

            InputStream inputStream = clientSocket.getInputStream();
            byte[] mybuffer = new byte[1024];
            int bytesRead = inputStream.read(mybuffer);
            String mensaje = new String(mybuffer, 0, bytesRead);
            String msj2 = "Ingrese el nombre del archivo que desea junto con su tipo ej: archivo.txt: ";
            String nombreArchivo = JOptionPane.showInputDialog(null, mensaje + "\n" + msj2);

            System.out.print("");

            // Obtener el flujo de entrada del servidor
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Obtener el flujo de salida del servidor
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

            // Enviar el nombre del archivo al servidor
            outToServer.writeBytes(nombreArchivo + '\n');

            // Leer la respuesta del servidor (mensaje de confirmación)
            String mensajeConfirmacion = inFromServer.readLine();

            // Cerrar la conexión TCP con el servidor
            clientSocket.close();

            //UDP
            // Crear un socket de cliente para recibir el archivo por UDP
            DatagramSocket datagramSocket = new DatagramSocket(9876);

// Crear un buffer para recibir los datos del archivo
            byte[] buffer = new byte[1032]; // Aumentamos el tamaño del buffer para poder recibir los 8 bytes del contador al principio

// Crear un paquete para recibir los datos del archivo
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            List<byte[]> listaBytes = new ArrayList<>();
// Crear una variable para almacenar el último contador recibido
            long esperados = 0;
            datagramSocket.setSoTimeout(5000);
            while (true) {
                // Recibir el archivo del servidor

                datagramSocket.receive(packet);

                // Si se recibe un paquete de 8 bytes, asumimos que es el último paquete
                if (packet.getLength() == 8) {
                    esperados = ByteBuffer.wrap(packet.getData()).getLong();
                    break;
                }

                byte[] bytes = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());

                listaBytes.add(bytes);

            }

            // Crear un archivo local para guardar el archivo recibido
            File archivo = new File(rutaServidor + "\\" + nombreArchivo);

            // Escribir los datos del archivo recibido en el archivo local
            FileOutputStream fileOutputStream = new FileOutputStream(archivo);
            listaBytes = barajearArreglo(listaBytes);
            List<byte[]> listaB = ordenarArreglo(listaBytes);
            for (int i = 0; i < listaBytes.size(); i++) {
                fileOutputStream.write(Arrays.copyOfRange(listaB.get(i), 8, listaB.get(i).length));
            }
            
            JFrame frame = new JFrame("RESULTADOS");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel label = new JLabel(mensajeConfirmacion + "\n" + 
                " , Paquetes esperados: " + esperados + "\n" + 
                        " , Se ha descargado correctamente el archivo " + archivo.getName());
        frame.add(label);
        frame.pack();
        frame.setVisible(true);

            // Cerrar conexiones
            fileOutputStream.close();
            datagramSocket.close();
        } catch (IOException e) {
            System.out.println("Error, el paquete que se esperaba nunca llegó :(");

        }
        
        
    }

    // Método para obtener la lista de contadores a partir de la lista de arreglos de bytes
    public static List<Integer> contadores(List<byte[]> lista) {
        List<Integer> contadorList = new ArrayList<>();

        for (byte[] bytes : lista) {
            ByteBuffer buffer = ByteBuffer.wrap(bytes, 0, 8);
            long contador = buffer.getLong();
            contadorList.add((int) contador);
        }

        return contadorList;
    }

    // Método para encontrar los números faltantes en la secuencia de contadores
    public static List<Integer> faltantes(List<byte[]> ordenada, long espera) {
        List<Integer> contadores = contadores(ordenada);
        List<Integer> faltantesNumers = new ArrayList<>();

        for (int i = 0; i < espera; i++) {
            if (!contadores.contains(i)) { // Verificar si el número falta en la secuencia de contadores
                if (i >= 0 && i < espera) { // Verificar que el número esté dentro del rango esperado
                    faltantesNumers.add(i);
                }
            }
        }

        return faltantesNumers;
    }

    public static List<byte[]> ordenarArreglo(List<byte[]> lista) {
        lista.sort(Comparator.comparingLong(arreglo -> ByteBuffer.wrap(arreglo, 0, 8).getLong()));
        return lista;
    }

    public static List<byte[]> barajearArreglo(List<byte[]> lista) {
        Collections.shuffle(lista);
        return lista;
    }
}
