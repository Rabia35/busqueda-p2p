/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package busqueda.utilidades;

import java.io.File;

/**
 *
 * @author almunoz
 */
public abstract class Utilidades {
    // Directorio del Cache de JXTA
    public static final String JXTA_CACHE = ".jxta";
    // Archivos del Cache de JADE
    public static final String JADE_MTP = "MTPs-Main-Container.txt";
    public static final String JADE_AP = "APDescription.txt";

    // Elimina recursivamente el directorio pasado como parametro
    public static void eliminarCache(String directorio) {
        try{
            File file = new File(directorio);
            if (file.exists()) {
                if (file.isDirectory()) {
                    File[] archivos = file.listFiles();
                    for (int index=0; index<archivos.length ; index++) {
                        if (archivos[index].isDirectory()) {
                            eliminarCache(archivos[index].getPath());
                        } else {
                            System.out.println("Eliminando: " + archivos[index].getPath());
                            archivos[index].delete();
                        }
                    }
                }
                System.out.println("Eliminando: " + file.getPath());
                file.delete();
            }
        } catch (NullPointerException ex) {
            System.out.println("NullPointerException: " + ex.getMessage());
        }
    }

}
