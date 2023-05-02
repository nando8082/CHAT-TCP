
package chat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author elvis, daniel
 */
public class GuardarMensajes {

    public GuardarMensajes() {
    }

    public static void guardarMensajesEnTXT(String mensajes, File archivo) {
        try {
            Date fechaActual = new Date();
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss =>");
            String fecha = formatoFecha.format(fechaActual);
            FileWriter fw = new FileWriter(archivo, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pw.println(fecha + " " + mensajes);
            pw.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }
}