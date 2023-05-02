
package chat;

import java.util.ArrayList;

/**
 *
 * @author elvis, daniel
 */
public class Nombres {
    //usamos patron singleton para que solo haya una instancia de la clase nombres
    private static ArrayList<String> listaNombres = new ArrayList<String>();

    public static ArrayList<String> getListaNombres() {
        return listaNombres;
    }

    public static void agregarNombre(String nombre) {
        listaNombres.add(nombre);
    }
  
}