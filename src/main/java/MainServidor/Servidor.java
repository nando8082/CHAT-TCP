package MainServidor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import chat.GuardarMensajes;
import chat.HiloServidor;
import chat.SalaComun;

/**
 *
 * @author elvis, daniel
 */
public class Servidor {

    // objeto para la clase SalaComun
    private static SalaComun salaComun;
    static final int maximo = 10;
    private static GuardarMensajes guardar = new GuardarMensajes();
    private final static File f = new File("mensajesServidor.txt");

    public Servidor() {

    }

    public static void main(String[] args) {

        int puerto = 6500;
        ServerSocket serverSocket = null;

        // Pedir la contraseña al usuario 
        String password = JOptionPane.showInputDialog(null, "Ingrese la contraseña:", "Autenticación requerida", JOptionPane.PLAIN_MESSAGE);

        // Verificar si la contraseña es correcta
        if (!password.equals("admin")) {
            JOptionPane.showMessageDialog(null, "Contraseña incorrecta. El servidor no se iniciará.");
            return;
        }
 
        //construimos el jOptionPane
        JFrame frame = new JFrame("Servidor");
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setSize(550, 550);

        Color color = new Color(53, 96, 171);
        frame.getContentPane().setBackground(color);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //label para el titulo
        JLabel label = new JLabel();
        label.setText("Servidor escuchando por el puerto " + puerto);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        frame.getContentPane().add(label, BorderLayout.NORTH);
        //panel para el label
        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(null);
        panel.setBackground(color);

        //text area
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        frame.add(textArea, BorderLayout.CENTER);

        JScrollPane scrollpanel = new JScrollPane();
        scrollpanel.setBounds(30, 25, 475, 450);
        scrollpanel.setViewportView(textArea); //para usar textarea
        frame.add(scrollpanel);

        try {

            serverSocket = new ServerSocket(puerto);
            String men = "Servidor iniciado en el puerto: " + puerto;
            System.out.println(men);
            guardar.guardarMensajesEnTXT(men, f);

        } catch (IOException ex) { //controlamos el error
            String mensajeError = "No se puedo iniciar el servidor en el puerto " + puerto;
            System.out.println(mensajeError);
            JOptionPane.showMessageDialog(null, mensajeError);
            ex.printStackTrace();
        }

        // lista de listaSocket de los clientes
        Socket[] listaSockets = new Socket[maximo];
        // crear el objeto para la clase SalaComun              
        salaComun = new SalaComun(maximo, 0, 0, listaSockets);
        // crear el nuevoSocket servidor

        while (salaComun.getConexionesClientes() < maximo) {
            // aceptar una nueva conexión
            Socket nuevoSocket = null;

            try {

                nuevoSocket = serverSocket.accept();
                String mensaje = "Se ha aceptado la conexion \n";
                textArea.append(mensaje);
                System.out.println(mensaje);
                guardar.guardarMensajesEnTXT(mensaje, f);

            } catch (IOException ex) {//controlamos el error
                String mensajeError = "No se pudo aceptar peticion";
                System.out.println(mensajeError + ex.getMessage());
                JOptionPane.showMessageDialog(null, mensajeError);

            }

            //incluimos en la lista          
            salaComun.aniadirSocket(nuevoSocket, salaComun.getConexionesClientes());
            salaComun.setConexionesActuales(salaComun.getConexionesActuales() + 1);
            salaComun.setConexionesClientes(salaComun.getConexionesClientes() + 1);

            // iniciar un nuevo hilo para gestionar la conexión
            Thread hilo = new Thread(new HiloServidor(nuevoSocket, salaComun, textArea));
            hilo.start();

        }

        String mensaje = "Demasiadas conexiones desde " + serverSocket.getInetAddress().getHostAddress();
        textArea.append(mensaje + "\n");
        guardar.guardarMensajesEnTXT(mensaje, f);
        JOptionPane.showMessageDialog(frame, "Demasiadas conexiones, se cerrarán todas las conexiones");
        //cerrar conexiones con bucle 
        for (Socket listaSocket : listaSockets) {
            try {
                listaSocket.close();
            } catch (IOException ex) {
                System.out.println("No se pudo cerrar conexion" + ex.getMessage());
            }
        }

        try {
            serverSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
