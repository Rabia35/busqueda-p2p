
package busqueda.jade.ontologias.servicio;

import jade.content.AgentAction;

/**
 *
 * @author almunoz
 */
public class Despublicar implements AgentAction {
    private Servicio servicio;

    public Despublicar() {
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
