
package chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author elvis, daniel
 */
public class HiloServidor implements Runnable {

    private Socket socket;
    private SalaComun salaComun;
    private JTextArea textArea;

    private static GuardarMensajes guardar = new GuardarMensajes();
    private final static File f = new File("mensajesServidor.txt");

    // constructor
    public HiloServidor(Socket socket, SalaComun salaComun, JTextArea textArea) {
        this.socket = socket;
        this.salaComun = salaComun;
        this.textArea = textArea;

    }

    @Override
    public void run() {
        try {
            // crear los streams de flujoEntrada y flujoSalida
            DataInputStream flujoEntrada = new DataInputStream(socket.getInputStream());
            DataOutputStream flujoSalida = new DataOutputStream(socket.getOutputStream());

            // recibir el nombre del cliente
            String nombre = flujoEntrada.readUTF();
            String mensaje = "Cliente " + nombre + " se ha conectado con la IP " + socket.getInetAddress().getHostAddress();
            System.out.println(mensaje);
            textArea.append(mensaje + "\n");
            guardar.guardarMensajesEnTXT(mensaje, f);

            // comprobar si el número máximo de conexiones ha sido alcanzado
            if (salaComun.getConexionesActuales() > salaComun.getMaximasConexiones()) {
                //  flujoSalida.writeUTF("No hay conexiones disponibles");
                mensaje = "no hay conexiones disponibles";
                System.out.println(mensaje);
                textArea.append(mensaje + "\n");
                guardar.guardarMensajesEnTXT(mensaje, f);
                return;
            }

            while (true) {
                // Recibir el mensaje del cliente
                String mensajeRecibido = flujoEntrada.readUTF();
                mensaje = "Mensaje Recibido de: " + mensajeRecibido;
                System.out.println(mensaje);
                textArea.append(mensaje + "\n");
                // guardar.guardarMensajesEnTXT(mensaje,f);

                //enviar el mensaje al cliente
                salaComun.setMensajes(mensajeRecibido);
                flujoSalida.writeUTF(salaComun.getMensajes());

                // recorrer la lista de sockets para enviar el mensaje a los demás clientes
                for (int i = 0; i < salaComun.getConexionesActuales(); i++) {

                    if (salaComun.getElementSocket(i) != socket && salaComun.getElementSocket(i).isConnected()) {
                        DataOutputStream flujoSalidaCliente = new DataOutputStream(salaComun.getElementSocket(i).getOutputStream());
                        flujoSalidaCliente.writeUTF(salaComun.getMensajes());
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}