
package busqueda.jade.ontologias.mensaje;

import jade.content.Predicate;

/**
 *
 * @author almunoz
 */
public class Enviar implements Predicate {
    private Mensaje mensaje;

    public Enviar() {
        this.mensaje = new Mensaje();
    }

    /**
     * @return the mensaje
     */
    public Mensaje getMensaje() {
        return mensaje;
    }

    /**
     * @param mensaje the mensaje to set
     */
    public void setMensaje(Mensaje mensaje) {
        this.mensaje = mensaje;
    }

}
