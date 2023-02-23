/**
 *
 * @author giova
 */
package udpechoserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UDPEchoServer {

    public static void main(String[] args) throws IOException {
        //Primero se verifica que la ruta del servidor exista, y que esta sea una ruta adecuado
        String rutaServidor = "";
        Scanner teclado = new Scanner(System.in);
        System.out.println("Para establecer una carpeta que sea el servidor de archivosescriba la ruta de esta carpetera. \n"
                + "Ejemplo; C:\\Users\\josej\\OneDrive\\Escritorio\\nombre-carpeta");
        System.out.println("--------------------------------------------------");
        do {
            System.out.println("Ahora digite la ruta ");
            rutaServidor = teclado.nextLine();
            File carpeta = new File(rutaServidor);
            if (carpeta.exists() && carpeta.isDirectory()) {
                break;
            } else {
                System.out.println("--------------------------------------------------");
                System.out.println("Porfavor digite la ruta como en el ejemplo.");
            }
        } while (true);
        // Se extrae la informacion de los archivos actuales nombres, por ende antes de iniciarlo
        //ser verifica que existan archivos en el servidor
        do {
            File carpeta = new File(rutaServidor);
            List<String> nombres = new ArrayList<>();
            int auxiliar = -1;
            if (carpeta.exists() && carpeta.isDirectory()) {
                File[] archivos = carpeta.listFiles();
                if (archivos.length == 0) {
                    auxiliar = 0;
                } else {
                    for (File archivo : archivos) {
                        String nombreArchivo = archivo.getName();
                        nombres.add(nombreArchivo);
                    }
                }
            }
            if (auxiliar == 0) {
                System.out.println("--------------------------------------------------");
                System.out.println("No existen archivos en la carpeta, coloque algunos");
            }else{
            break;
            }
        } while (true);

        //Se inicia el servidor, pero antes de eso se le preguntara si desea enviar algun archivo.
    }
}

//   private static final int ECHOMAX = 25;
//        if (args.length != 1) {
//            throw new IllegalArgumentException("Paramater(s)<Port>");
//        }
//        int servPort = Integer.parseInt(args[0]);
//        byte[] archivo = leerArchivoTexto("C:\\Users\\giova\\Desktop\\Prueba.txt");
//        System.out.println(archivo.length);
//        int servPort = 80;
//        DatagramSocket socket = new DatagramSocket(servPort);
//        DatagramPacket packet = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);
//        while(true){
//            socket.receive(packet);
//            System.out.println("Handling client at "+packet.getAddress().getHostAddress()
//            +"on port"+packet.getPort());
//            System.out.println(new String(packet.getData()));
//            socket.send(packet);
//            packet.setLength(ECHOMAX);
//            
//        }
////    }
//
//    public static byte[] leerArchivoTexto(String ruta) {
//        // TODO code application logic here
//        File archivo = null;
//        FileReader fr = null;
//        BufferedReader br = null;
//        String linea = "";
//        try {
//            // Apertura del fichero y creacion de BufferedReader para poder
//            // hacer una lectura comoda (disponer del metodo readLine()).
//            archivo = new File(ruta);
//            fr = new FileReader(archivo);
//            br = new BufferedReader(fr);
//
//            // Lectura del fichero
//            while (true) {
//                if (br.readLine() == null) {
//                    break;
//                }
//                linea += br.readLine();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            // En el finally cerramos el fichero, para asegurarnos
//            // que se cierra tanto si todo va bien como si salta 
//            // una excepcion.
//            try {
//                if (null != fr) {
//                    fr.close();
//                }
//            } catch (Exception e2) {
//                e2.printStackTrace();
//            }
//        }
//        return linea.getBytes();
//    }
//}
