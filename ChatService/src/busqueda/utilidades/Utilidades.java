
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

    /**
     * Elimina el archivo pasado como parametro, si el parametro es un
     * directorio elimina todo el contenido recursivamente
     * @param archivo El archivo a eliminar
     */
    private static void eliminarCache(String archivo) {
        try{
            File file = new File(archivo);
            if (file.exists()) {
                if (file.isDirectory()) {
                    File[] archivos = file.listFiles();
                    for (int index=0; index<archivos.length ; index++) {
                        eliminarCache(archivos[index].getPath());
                    }
                }
                System.out.println("Eliminando: " + file.getPath());
                file.delete();
            }
        } catch (NullPointerException ex) {
            System.out.println("NullPointerException: " + ex.getMessage());
        }
    }

    /**
     * Elimina la cache de JADE y JXTA recursivamente
     */
    public static void eliminarCache() {
        Utilidades.eliminarCache(Utilidades.JADE_MTP);
        Utilidades.eliminarCache(Utilidades.JADE_AP);
        Utilidades.eliminarCache(Utilidades.JXTA_CACHE);
    }

}
