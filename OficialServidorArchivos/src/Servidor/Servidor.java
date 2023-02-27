package Servidor;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Servidor {

    public static void main(String[] args) {

        String rutaServidor;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        JPanel accessory = new JPanel();
        accessory.add(new JLabel("Seleccione la carpeta donde desea guardar los archivos"));
        fileChooser.setAccessory(accessory);
        int returnValue = fileChooser.showOpenDialog(new JFrame());
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String selectedFolder = fileChooser.getSelectedFile().getAbsolutePath();

            rutaServidor = selectedFolder;

            try {

                int puertoServidor = 1234;

                // Crear un socket de servidor TCP
                ServerSocket serverSocket = new ServerSocket(puertoServidor);

                // Crear un ExecutorService para manejar las solicitudes de los clientes
                ExecutorService executorService = Executors.newFixedThreadPool(10);
                System.out.println("El servidor se ha levantado correctamente");
                while (true) {
                    // Esperar a que un cliente se conecte
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Nuevo cliente conectado: " + clientSocket.getInetAddress().getHostName());

                    // Le mandamos al cliente todos los archivos que pueda descargar
                    OutputStream outputStream = clientSocket.getOutputStream();
                    File folder = new File(rutaServidor);
                    File[] files = folder.listFiles();
                    String archivos = "\n";
                    for (File file : files) {
                        if (file.isFile()) {
                            archivos += " - " + file.getName() + "\n";
                        }
                    }
                    String mensaje = "Archivos para descargar disponibles: " + archivos;
                    outputStream.write(mensaje.getBytes());

                    // Crear un objeto Runnable para manejar la solicitud del cliente
                    Runnable worker = new ServidorArchivos(clientSocket, rutaServidor);

                    // Ejecutar el objeto Runnable en el ExecutorService
                    executorService.execute(worker);
                }
            } catch (IOException e) {
                System.out.println("Ha ocurrido algun error.");
            }
        }

    }
}
