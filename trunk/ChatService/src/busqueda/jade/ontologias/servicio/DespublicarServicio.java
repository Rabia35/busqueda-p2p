
package busqueda.jade.ontologias.servicio;

import jade.content.Predicate;

/**
 *
 * @author almunoz
 */
public class DespublicarServicio implements Predicate {
    private Servicio servicio;

    public DespublicarServicio() {
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
