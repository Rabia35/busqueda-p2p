
package busqueda.jade;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

/**
 *
 * @author almunoz
 */
public abstract class Servicio {
    
    public static void registrar(Agent agente, String nombreServicio, String tipoServicio) {
        try {
            DFAgentDescription dfad = new DFAgentDescription();
            dfad.setName(agente.getAID());

            ServiceDescription sd = new ServiceDescription();
            sd.setName(nombreServicio);
            sd.setType(tipoServicio);
            dfad.addServices(sd);

            DFService.register(agente, dfad);
            System.out.println("El servicio " + nombreServicio + " se ha registrado.");
        } catch (FIPAException fex) {
            System.out.println("FIPAException: " + fex.getMessage());
        }
    }

}
