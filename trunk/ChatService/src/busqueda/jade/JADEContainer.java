
package busqueda.jade;

import busqueda.JADECommunicator;
import busqueda.JXTACommunicator;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
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
        agenteChat = crearAgente("chat", "busqueda.jade.chat.AgenteChat", null);
        // Crea e inicia el agenteGUI
        Object[] argumentos = {this};
        agenteGUI = crearAgente("gui", "busqueda.jade.chat.AgenteGUI", argumentos);
    }

    public void crearAgentesJXTA(JXTACommunicator jxtaCommunicator) throws StaleProxyException {
        Object[] argumentos = {jxtaCommunicator};
        // Crea e inicia el agenteJXTA
        agenteJXTA = crearAgente("jxta", "busqueda.jade.chat.AgenteJXTA", argumentos);
    }

    /* METODOS PARA EL CHAT */

    public void enviarMensajeChat(String mensaje) throws StaleProxyException {
        agenteGUI.putO2AObject(mensaje, AgentController.ASYNC);
    }

    public void mostrarMensajeChat(String mensaje) {
        jadeCommunicator.mostrarMensajeChat(mensaje);
    }

    public void recibirMensajeChat(String mensaje) throws StaleProxyException {
        agenteJXTA.putO2AObject(mensaje, AgentController.ASYNC);
    }
    
}
