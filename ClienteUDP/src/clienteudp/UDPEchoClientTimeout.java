/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package clienteudp;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import javax.imageio.IIOException;

/**
 *
 * @author giova
 */
public class UDPEchoClientTimeout {

    /**
     * @param args the command line arguments
     */
    private static final int TIMEOUT = 3000;
    private static final int MAXTRIES = 5;

    public static void main(String[] args) throws IOException {

        do {
            String puerto = "";
            String mensaje = "";
            Scanner teclado = new Scanner(System.in);
            System.out.print("Direccion: ");
            puerto = teclado.nextLine();
            System.out.print("Mensaje: ");
            mensaje = teclado.nextLine();
            InetAddress serverAddress = InetAddress.getByName(puerto);
            byte[] bytesToSend = mensaje.getBytes();
            int servPort = 80;

            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setSoTimeout(TIMEOUT);
                DatagramPacket sendPacket = new DatagramPacket(bytesToSend,
                        bytesToSend.length, serverAddress, servPort);
                DatagramPacket receivePacket
                        = new DatagramPacket(new byte[bytesToSend.length], bytesToSend.length);
                int tries = 0;
                boolean receivedResponse = false;
                do {
                    socket.send(sendPacket);
                    try {
                        socket.receive(receivePacket);
                        if (!receivePacket.getAddress().equals(serverAddress)) {
                            throw new IIOException("Received packet from an unkonw source");
                        }
                        receivedResponse = true;
                    } catch (InterruptedIOException e) {
                        tries += 1;
                        System.out.println("Timed out, " + (MAXTRIES - tries) + "more tries....");
                    }
                } while ((!receivedResponse) && (tries < MAXTRIES));
                if (receivedResponse) {
                    System.out.println("Received: " + new String(receivePacket.getData()));
                } else {
                    System.out.println("No response -- giving up");
                }
            }
            String salir = "";
            System.out.println("Desea enviar otro mensaje 0 salir y 1 continuar");
            salir = teclado.nextLine();
            if (salir == "1") {
                break;
            }
        } while (true);

    }

}