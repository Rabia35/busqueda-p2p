
package busqueda.jade.ontologias.servicio;

import jade.content.Predicate;

/**
 *
 * @author almunoz
 */
public class PublicarServicio implements Predicate {
    private Servicio servicio;

    public PublicarServicio() {
        this.servicio = new Servicio();
    }

    /**
     * @return the servicio
     */
    public Servicio getServicio() {
        return servicio;
    }

    /**
     * @param servicio the servicio to set
     */
    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

}
