
package busqueda.jade;

import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

/**
 *
 * @author almunoz
 */
public abstract class UtilidadesJADE {

    public static void registrarServicio(Agent agente, String tipo, String descripcion) {
        try {
            // Crea la descripcion del agente
            DFAgentDescription dfad = new DFAgentDescription();
            dfad.setName(agente.getAID());
            // Crea la descripcion del servicio
            ServiceDescription sd = new ServiceDescription();
            sd.setType(tipo);
            sd.setName(descripcion);
            // Agrega la descripcion del servicio a la descripcion del agente
            dfad.addServices(sd);
            // Registrar la descripcion del agente en el DF
            DFService.register(agente, dfad);
            System.out.println("El servicio '" + tipo + "' se ha registrado.");
        } catch (FIPAException ex) {
            System.out.println("FIPAException: " + ex.getMessage());
        }
    }

    public static void deregistrarServicio(Agent agente, String tipo) {
        try {
            DFService.deregister(agente);
            System.out.println("El servicio '" + tipo + "' ya no esta registrado.");
        } catch (FIPAException fex) {
            System.out.println("FIPAException: " + fex.getMessage());
        }
    }

    public static void registrarOntologia(Agent agente, Ontology ontologia) {
        agente.getContentManager().registerOntology(ontologia);
        System.out.println("El agente '" + agente.getLocalName() + "' ha registrado la ontologia '" + ontologia.getName() + "'");
    }

    /**
     * Busca un agente por el tipo de servicio que presta
     * @param agente El agente que realiza la busqueda
     * @param tipoServicio El tipo de servicio que se desea buscar
     * @return El AID del agente encontrado, o null si no se encuentra
     */
    public static AID buscarAgente(Agent agente, String tipoServicio) {
        AID resultado = null;
        try {
            // Crea una descripcion como plantilla para la busqueda
            DFAgentDescription templateDfad = new DFAgentDescription();
            ServiceDescription templateSd = new ServiceDescription();
            templateSd.setType(tipoServicio);
            templateDfad.addServices(templateSd);
            // Los criterios de busqueda
            SearchConstraints sc = new SearchConstraints();
            sc.setMaxResults(new Long(1));
            // Busca los agentes basadose en la plantilla y en los criterios
            DFAgentDescription[] results = DFService.search(agente, templateDfad, sc);
            if (results.length > 0) {
                resultado = results[0].getName();
                return resultado;
            }
        } catch (FIPAException ex) {
            System.out.println("FIPAException: " + ex.getMessage());
        }
        System.out.println("El agente " + agente.getLocalName() + " no encontro el servicio " + tipoServicio);
        return resultado;
    }

}
