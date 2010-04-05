
package busqueda.jade.comportamientos;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

/**
 *
 * @author almunoz
 */
public class RegistrarServicioBehaviour extends OneShotBehaviour {
    private String nombreServicio;
    private String tipoServicio;

    public RegistrarServicioBehaviour(Agent agente, String nombre, String tipo) {
        super(agente);
        this.nombreServicio = nombre;
        this.tipoServicio = tipo;
    }

    @Override
    public void action() {
        try {
            // Crea la descripcion del agente
            DFAgentDescription dfad = new DFAgentDescription();
            dfad.setName(myAgent.getAID());
            // Crea la descripcion del servicio
            ServiceDescription sd = new ServiceDescription();
            sd.setName(nombreServicio);
            sd.setType(tipoServicio);
            // Agrega la descripcion del servicio a la descripcion del agente
            dfad.addServices(sd);
            // Registrar la descripcion del agente en el DF
            DFService.register(myAgent, dfad);
            System.out.println("El servicio " + nombreServicio + " se ha registrado.");
        } catch (FIPAException ex) {
            System.out.println("FIPAException: " + ex.getMessage());
        }
    }

}
