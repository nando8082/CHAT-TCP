package MainCliente;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JOptionPane;

import chat.ClienteHilo;
import chat.Nombres;
/**
 *
 * @author elvis, daniel
 */
public class Cliente {

    public static void main(String[] args) throws IOException {

        ArrayList<String> listaNombres = Nombres.getListaNombres();
        cargarArrayListDesdeArchivo(listaNombres);
        String nickname = "";

        //jOptionPane para nickname
        nickname = JOptionPane.showInputDialog(null, "Digita tu Nombre/Nickname", "CHAT SOCKETS PINARGO / LOJA", JOptionPane.INFORMATION_MESSAGE);

        //controlamos que nombre no este vacio
        while (nickname.isEmpty()) {
            nickname = JOptionPane.showInputDialog(null, "Nombre/Nickname no puede estar vacio, Por favor digita tu Nombre/Nickname", "SOCKETS PINARGO / LOJA", JOptionPane.INFORMATION_MESSAGE);
        }

        //este bloque no funciona, echar un vistazo mas tarde: no comprueba el nombre en el arraylist
        while (existeNombre(listaNombres, nickname)) {
            JOptionPane.showMessageDialog(null, "Nombre/nickname utilizado. Por favor, ingrese un nombre/nickname diferente.");
            nickname = JOptionPane.showInputDialog(null, "Chat SOCKETS PINARGO / LOJA", "CHAT SOCKETS", JOptionPane.INFORMATION_MESSAGE);
        }

        listaNombres.add(nickname);

        guardarArrayListEnArchivo(listaNombres);

        //Crear socket para conectarse con el servidor
        Socket socket = new Socket("localhost", 6500);
        //Crear un hilo de ejecución para el cliente
        ClienteHilo clienteHilo = new ClienteHilo(socket, nickname, listaNombres);

        Thread threadCliente = new Thread(clienteHilo);
        threadCliente.start();

    }

    //metodo para guardar el arraylist actual en el fichero
    public static void guardarArrayListEnArchivo(ArrayList<String> lista) {
        try {
            File archivo = new File("nombresClientes.txt");
            FileWriter fw = new FileWriter(archivo);
            for (String item : lista) {
                fw.write(item + System.lineSeparator());
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("Error: no se pudo escribir en el archivo");
            e.printStackTrace();
        }
    }

    //comprueba que exista el nombre en la lista mediante el metodo contains
    public static boolean existeNombre(ArrayList<String> listaNombres, String nickname) {
        for (String nombre : listaNombres) {
            if (nombre.equals(nickname)) {
                return true;
            }
        }
        return false;
    }

    //lee el fichero de texto que tiene el contenido de la lista y lo carga en un objeto de tipo arraylist
    public static void cargarArrayListDesdeArchivo(ArrayList<String> lista) {
        try {
            File f = new File("nombresClientes.txt");
            BufferedReader br = new BufferedReader(new FileReader(f));
            String linea;
            while ((linea = br.readLine()) != null) {
                lista.add(linea);
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("No se encontró el archivo");
        } catch (IOException e) {
            System.out.println("Error: no se pudo leer el archivo");
            e.printStackTrace();
        }
    }

}