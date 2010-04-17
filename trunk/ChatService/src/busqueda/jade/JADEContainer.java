
package busqueda.jade;

import busqueda.JADECommunicator;
import busqueda.jade.ontologias.mensaje.Mensaje;
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
    // La instacia de la clase
    private static JADEContainer instancia;
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

    public static JADEContainer getInstance() {
        if (instancia == null) {
            instancia = new JADEContainer();
        }
        return instancia;
    }

    private JADEContainer() {
        this.jadeCommunicator = null;
        this.profile = null;
        this.puerto = "1099";
        this.mainContainer = null;
        this.agenteRMA = null;
        this.agenteGUI = null;
        this.agenteChat = null;
        this.agenteJXTA = null;
    }

    public void setJadeCommunicator(JADECommunicator jadeCommunicator) {
        this.jadeCommunicator = jadeCommunicator;
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
        // Crea e inicia el agenteGUI
        agenteGUI = crearAgente("gui", "busqueda.jade.agentes.AgenteGUI", null);
        // Crea e inicia el agenteJXTA
        agenteJXTA = crearAgente("jxta", "busqueda.jade.agentes.AgenteJXTA", null);
        // Crea e inicia el agenteChat
        agenteChat = crearAgente("chat", "busqueda.jade.agentes.chat.AgenteChat", null);
    }

    /* METODOS PARA EL CHAT */
    public void iniciarChat(String tipo, String descripcion) {
        jadeCommunicator.iniciarChat(tipo, descripcion);
    }

    public void enviarMensajeChatJXTA(String remitente, String mensaje) {
        jadeCommunicator.enviarMensajeChatJXTA(remitente, mensaje);
    }

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
    
}
