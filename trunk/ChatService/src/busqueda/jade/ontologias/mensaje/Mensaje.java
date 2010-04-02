
package busqueda.jade.ontologias.mensaje;

import jade.content.Concept;

/**
 *
 * @author almunoz
 */
public class Mensaje implements Concept {
    private String remitente;
    private String mensaje;
    
    public Mensaje() {
        this.remitente = "remitente";
        this.mensaje = "mensaje";
    }

    /**
     * @return the remitente
     */
    public String getRemitente() {
        return remitente;
    }

    /**
     * @param remitente the remitente to set
     */
    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    /**
     * @return the mensaje
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * @param mensaje the mensaje to set
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
