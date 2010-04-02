
package busqueda.jade.ontologias.servicio;

import jade.content.AgentAction;

/**
 *
 * @author almunoz
 */
public class Publicar implements AgentAction {
    private Servicio servicio;

    public Publicar() {
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
