

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Principal {

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
        // Se extrae la informacion de los archivos actuales nombres
        File carpeta = new File(rutaServidor);
        List<String> nombres = new ArrayList<>();
        List<File> archivosReales = new ArrayList<>();
        File[] archivos = null;
        do {
            int auxiliar = -1;
            if (carpeta.exists() && carpeta.isDirectory()) {
                archivos = carpeta.listFiles();
                if (archivos.length == 0) {
                    auxiliar = 0;
                } else {
                    for (File archivo : archivos) {
                        if (!archivo.isDirectory()) {
                            String nombreArchivo = archivo.getName();
                            nombres.add(nombreArchivo);
                            archivosReales.add(archivo);
                        }
                    }
                }
            }
            if (auxiliar == 0) {
                System.out.println("--------------------------------------------------");
                System.out.println("No existen archivos en la carpeta, coloque algunos");
            } else {
                break;
            }
        } while (true);
        try {
            System.out.println(nombres.size());
            byte[] archivo = Files.readAllBytes(archivosReales.get(0).toPath());
            escribirArchivo(rutaServidor, archivo,nombres.get(0));
            System.out.println("Se escribio crrectamente");
        } catch (Exception e) {
            System.out.println("Error");
        }

//        try {
//            String ruta = "C:\\Users\\josej\\OneDrive\\Escritorio\\Recibidos";
//            byte[] archivo1 = Files.readAllBytes(archivos[0].toPath());
//            OutputStream out = new FileOutputStream(ruta);
//            File file=Files.
//            out.write(archivo1);
//        } catch (Exception e) {
//        }
        //Se inicia el servidor, pero antes de eso se le preguntara si desea enviar algun archivo.
    }

     public static String getFileExtension(String nombre) {
        int lastDotIndex = nombre.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return nombre.substring(lastDotIndex + 1).toLowerCase();
    }
     
    public static void escribirArchivo(String ruta, byte[] bytes,String nombre) throws IOException {
        String tipo=getFileExtension(nombre);
        try (FileOutputStream fos = new FileOutputStream(ruta +"\\Copia\\"+nombre+"."+tipo)) {
            fos.write(bytes);
        }
    }
    //Este metodo debera procesar las respuestas de los clientes ,por lo tanto es mejor que este en un protocolo
    //El cual verifique, si  se desea una solicitud de algun archivo,pues existan archivos, o en cambio si desea embiar
    //algun archivo, al servidor que este no contenga el mismo nombre  que otro, o si lo tiene que lo actualice
    //pero al actualizarlo tenemos la posibilidad de que otro cliente acceda al mismoen una version anterio.

    //Este metodo en algun protocolo
    public static void procesarRespuesta() {
    }

}
