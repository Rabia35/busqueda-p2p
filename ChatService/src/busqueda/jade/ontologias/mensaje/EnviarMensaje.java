
package busqueda.jade.ontologias.mensaje;

import jade.content.Predicate;

/**
 *
 * @author almunoz
 */
public class EnviarMensaje implements Predicate {
    private Mensaje mensaje;

    public EnviarMensaje() {
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
