
package busqueda.jade;

import busqueda.JADECommunicator;
import busqueda.JXTACommunicator;
import busqueda.jade.ontologias.mensaje.Mensaje;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

/**
 *
 * @author almunoz
 */
public class JADEContainer {
    // JADE Communicator
    private JADECommunicator jadeCommunicator;
    // Para iniciar la plataforma
    private Profile profile;
    private String puerto;
    private ContainerController mainContainer;
    // Agentes
    private AgentController agenteRMA;
    private AgentController agenteGUI;
    private AgentController agenteChat;
    private AgentController agenteJXTA;

    public JADEContainer(JADECommunicator jadeCommunicator) {
        this.jadeCommunicator = jadeCommunicator;
        this.profile = null;
        this.puerto = "1099";
        this.mainContainer = null;
        this.agenteRMA = null;
        this.agenteGUI = null;
        this.agenteChat = null;
        this.agenteJXTA = null;
    }

    public void iniciar() throws StaleProxyException {
        iniciar(this.puerto);
    }

    public void iniciar(String port) throws StaleProxyException {
        this.puerto = port;
        Runtime runtime = Runtime.instance();
        profile = new ProfileImpl();        
        profile.setParameter(Profile.MAIN_PORT, this.puerto);
        profile.setParameter(Profile.LOCAL_PORT, this.puerto);
        mainContainer = runtime.createMainContainer(profile);
        // Crea e inicia el agenteRMA: Remote Management Agent
        agenteRMA = crearAgente("RMA", "jade.tools.rma.rma", null);
    }

    public void terminar() throws StaleProxyException {
        agenteChat.kill();
        agenteGUI.kill();
        agenteJXTA.kill();
        agenteRMA.kill();
        mainContainer.kill();
    }

    public AgentController crearAgente(String nombre, String clase, Object[] argumentos) throws StaleProxyException {
        AgentController agente = mainContainer.createNewAgent(nombre, clase, argumentos);
        agente.start();
        return agente;
    }

    public void crearAgentes() throws StaleProxyException {
        // Crea e inicia el agenteChat
        agenteChat = crearAgente("chat", "busqueda.jade.agentes.chat.AgenteChat", null);
        // Crea e inicia el agenteGUI
        Object[] argumentos = {this};
        agenteGUI = crearAgente("gui", "busqueda.jade.agentes.AgenteGUI", argumentos);
    }

    public void crearAgentesJXTA(JXTACommunicator jxtaCommunicator) throws StaleProxyException {
        Object[] argumentos = {jxtaCommunicator};
        // Crea e inicia el agenteJXTA
        agenteJXTA = crearAgente("jxta", "busqueda.jade.agentes.AgenteJXTA", argumentos);
    }

    /* METODOS PARA EL CHAT */

    public void enviarMensajeChat(String mensaje) throws StaleProxyException {
        agenteGUI.putO2AObject(mensaje, AgentController.ASYNC);
    }

    public void mostrarMensajeChat(String remitente, String mensaje) {
        jadeCommunicator.mostrarMensajeChat(remitente, mensaje);
    }

    public void recibirMensajeChat(String remitente, String mensaje) throws StaleProxyException {
        Mensaje msg = new Mensaje();
        msg.setRemitente(remitente);
        msg.setMensaje(mensaje);
        agenteJXTA.putO2AObject(msg, AgentController.ASYNC);
    }

    /* Metodos de clase */
    
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
