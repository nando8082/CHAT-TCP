package chat;


import java.net.Socket;

/**
 *
 * @author elvis, daniel
 */
public class SalaComun {

    private int conexionesClientes;
    private int conexionesActuales;
    private int maximasConexiones;
    private Socket listaSockets[] = new Socket[maximasConexiones];
    private String mensajes;

    public SalaComun(int maximasConexiones, int conexionesClientes, int conexionesActuales, Socket[] listaSockets) {
        this.setConexionesClientes(conexionesClientes);
        this.setConexionesActuales(conexionesActuales);
        this.maximasConexiones = maximasConexiones;
        this.listaSockets = listaSockets;
        this.mensajes = "";

    }

    public int getConexionesClientes() {
        return conexionesClientes;
    }

    public void setConexionesClientes(int conexionesClientes) {
        this.conexionesClientes = conexionesClientes;
    }

    public int getConexionesActuales() {
        return conexionesActuales;
    }

    public void setConexionesActuales(int conexionesActuales) {
        this.conexionesActuales = conexionesActuales;
    }

    public int getMaximasConexiones() {
        return maximasConexiones;
    }

    public void setMaximasConexiones(int maximasConexiones) {
        this.maximasConexiones = maximasConexiones;
    }

    public String getMensajes() {
        return mensajes;
    }

    public void setMensajes(String mensajes) {
        this.mensajes = mensajes;
    }

    public void aniadirSocket(Socket socket, int i) {
        
       listaSockets[i] = socket;
           

    }

    public Socket getElementSocket(int i) {
        return listaSockets[i];
    }

}