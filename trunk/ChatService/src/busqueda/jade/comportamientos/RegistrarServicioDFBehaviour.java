
package busqueda.jade.comportamientos;

import busqueda.jade.UtilidadesJADE;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

/**
 *
 * @author almunoz
 */
public class RegistrarServicioDFBehaviour extends OneShotBehaviour {
    private String tipo;
    private String descripcion;

    public RegistrarServicioDFBehaviour(Agent agente, String tipo, String descripcion) {
        super(agente);
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    @Override
    public void action() {
        UtilidadesJADE.registrarServicio(myAgent, tipo, descripcion);
    }

}
