
package Servidor;

import java.io.File;


public class ProtocoloServidor {

    private String rutaServidor;

    public ProtocoloServidor(String rutaServidor) {
        this.rutaServidor = rutaServidor;
    }

    public File processInput(String entrada) {
        if (entrada != null) {
            File archivo = new File(rutaServidor + "\\" + entrada);

            // Verificar que el archivo existe y que no es un directorio
            if (!archivo.exists() || archivo.isDirectory()) {
                return null;
            }
            return archivo;

        }
        return null;
    }
}
