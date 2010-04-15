
package busqueda.utilidades;

import java.io.File;

/**
 *
 * @author almunoz
 */
public abstract class Utilidades {
    // Directorio del Cache de JXTA
    private static String JXTA_CACHE = ".jxta";
    // Archivos del Cache de JADE
    private static String JADE_MTP = "MTPs-Main-Container.txt";
    private static String JADE_AP = "APDescription.txt";

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
                    for (int index=0; index<archivos.length; index++) {
                        Utilidades.eliminarCache(archivos[index].getPath());
                    }
                }
                boolean eliminado = file.delete();
                if (eliminado) {
                    System.out.println("Eliminando: " + file.getPath());
                } else {
                    System.out.println("No se elimino: " + file.getPath());
                }
            }
        } catch (NullPointerException ex) {
            System.out.println("NullPointerException: " + ex.getMessage());
        }
    }

    /**
     * Elimina la cache de JADE y JXTA recursivamente
     */
    public static void eliminarCache() {
        System.out.println("Eliminando cache de JXTA y JADE");
        Utilidades.eliminarCache(Utilidades.JADE_MTP);
        Utilidades.eliminarCache(Utilidades.JADE_AP);
        Utilidades.eliminarCache(Utilidades.JXTA_CACHE);
    }

}
