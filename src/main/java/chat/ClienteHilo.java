package chat;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import MainCliente.Cliente;

/**
 *
 * @author elvis, daniel
 */
public class ClienteHilo implements Runnable {

    private Socket socket;
    String nickname;

    private static GuardarMensajes guardar = new GuardarMensajes();
    private final static File f = new File("mensajesClientes.txt");
    private final static File f2 = new File("mensajesServidor.txt");

    ArrayList<String> listaNombres = null;

    public ClienteHilo(Socket socket, String nickname, ArrayList<String> lista) {
        this.socket = socket;
        this.nickname = nickname;
        this.listaNombres = lista;

    }

    @Override
    public void run() {
        try {

            //Enviar la información al servidor
            DataOutputStream flujoSalida = new DataOutputStream(socket.getOutputStream());
            DataInputStream flujoEntrada = new DataInputStream(socket.getInputStream());

            //enviamos nickname al server           
            flujoSalida.writeUTF(nickname);
            flujoSalida.flush();

            //------------------------------------------------------------
            //construimos y personalizamos el jframe
            //frame
            JTextField cajaMensaje = new JTextField();
            JScrollPane scrollpanel;
            JTextArea textAreaCli;
            JButton botonEnviar = new JButton("Enviar");
            JButton botonSalir = new JButton("Salir");

            //personalizamos el frame
            JFrame ventana = new JFrame("Cliente - " + nickname);
            ventana.setSize(560, 420);
            ventana.setLayout(null);
            ventana.setResizable(false);
            //damos color
            Color color = new Color(53, 96, 171);
            ventana.getContentPane().setBackground(color);

            cajaMensaje.setBounds(10, 10, 400, 30);
            ventana.add(cajaMensaje);

            textAreaCli = new JTextArea();
            textAreaCli.setEditable(false);
            ventana.add(textAreaCli);

            scrollpanel = new JScrollPane();
            scrollpanel.setBounds(10, 50, 400, 320);
            scrollpanel.setViewportView(textAreaCli); //para usar textarea
            ventana.add(scrollpanel);

            botonEnviar.setBounds(420, 10, 110, 30);
            ventana.add(botonEnviar);

            botonSalir.setBounds(420, 50, 110, 30);
            ventana.add(botonSalir);

            DefaultListModel listaConectados;
            //dibujamos la caja pra mostrar la lista
            listaConectados = new DefaultListModel<>();
            listaConectados.addElement("Historial Clientes");
            JList jListConectados = new JList<>(listaConectados);
            JScrollPane scrollPaneLista = new JScrollPane(jListConectados);
            scrollPaneLista.setBounds(420, 90, 110, 280);
            ventana.add(scrollPaneLista);

            actualizarListaFrame(listaConectados, jListConectados);

            ventana.setLocationRelativeTo(null);
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            cajaMensaje.requestFocusInWindow();
            ventana.setVisible(true);

            //------------------------------------------------  
            //funcionalidad del jFrame
            //accion del boton enviar    
            botonEnviar.addActionListener(e -> {
                try {

                    String mensaje = nickname + ": " + cajaMensaje.getText();
                    flujoSalida.writeUTF(mensaje);
                    guardar.guardarMensajesEnTXT(mensaje, f);
                    flujoSalida.flush();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                cajaMensaje.setText("");
            });

            //para que cuando se pulse enter tambien haga como el boton enviar
            cajaMensaje.addActionListener(e -> {
                try {

                    String mensaje = nickname + ": " + cajaMensaje.getText();
                    flujoSalida.writeUTF(mensaje);
                    guardar.guardarMensajesEnTXT(mensaje, f);
                    flujoSalida.flush();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                cajaMensaje.setText("");
            });

         
        
        //boton salir actua como desconectar
        botonSalir.addActionListener(e -> {
            try {

                String mensaje = nickname + " se ha desconectado";
                flujoSalida.writeUTF(mensaje);
                guardar.guardarMensajesEnTXT(mensaje, f);
                guardar.guardarMensajesEnTXT(mensaje, f2);
                flujoSalida.flush();
                //borramos el nombre de la lista y actualizamos el fichero de texto
                synchronized (listaNombres) {
                    listaNombres.remove(nickname);
                    Cliente.guardarArrayListEnArchivo(listaNombres);
                }

                socket.close();
                ventana.dispose();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        //cerrar ventana tambien actua como desconectar
        ventana.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    String mensaje = nickname + " se ha desconectado";
                    flujoSalida.writeUTF(mensaje);
                    guardar.guardarMensajesEnTXT(mensaje, f);
                    guardar.guardarMensajesEnTXT(mensaje, f2);
                    flujoSalida.flush();
                    //borramos el nombre de la lista y actualizamos el fichero de texto
                    synchronized (listaNombres) {
                        listaNombres.remove(nickname);
                        Cliente.guardarArrayListEnArchivo(listaNombres);
                    }

                    socket.close();
                    ventana.dispose();
                    //System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //rellenamos el textArea
     
        while (true) {           
            String mensaje = flujoEntrada.readUTF();
            textAreaCli.append(mensaje + "\n");
        }

        
    }
        
    catch (IOException e) {
            e.printStackTrace();
    }
}
    

//pintar clientes conectados 
public void actualizarListaFrame(DefaultListModel listaConectados, JList jListConectados) {
        for (String listaNombre : listaNombres) {
            listaConectados.addElement(listaNombre);
        }
        
        jListConectados.setModel(listaConectados);
    }
    
}