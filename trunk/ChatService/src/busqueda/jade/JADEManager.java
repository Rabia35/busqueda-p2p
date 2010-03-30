
package busqueda.jade;

import busqueda.jxta.JXTAManager;
import gui.ChatGUI;
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
public class JADEManager {
    // Interfaz
    private ChatGUI gui;
    private String puerto;
    // Para iniciar la plataforma
    private Profile profile;
    private ContainerController mainContainer;
    private AgentController rma;
    // Agentes
    private AgentController interfaz;
    private AgentController chat;
    private AgentController jxta;
    // JXTA Manager
    private JXTAManager jxtaManager;

    public JADEManager(ChatGUI gui, JXTAManager jxtaManager) {
        this.gui = gui;
        this.mainContainer = null;
        this.rma = null;
        this.jxtaManager = jxtaManager;
    }

    public void iniciar() throws StaleProxyException {
        iniciar("1099");
    }

    public void iniciar(String port) throws StaleProxyException {
        this.puerto = port;
        Runtime runtime = Runtime.instance();
        profile = new ProfileImpl();        
        profile.setParameter(Profile.MAIN_PORT, this.puerto);
        profile.setParameter(Profile.LOCAL_PORT, this.puerto);
        mainContainer = runtime.createMainContainer(profile);
        // Crea e inicia el agente RMA: Remote Management Agent
        rma = mainContainer.createNewAgent("RMA", "jade.tools.rma.rma", null);
        rma.start();
        // Crea e inicia el agente chat
        chat = crearAgente("chat", "busqueda.jade.chat.ChatAgent", null);
        Object[] argumentos = {gui};
        // Crea e inicia el agente interfaz de usuario
        interfaz = crearAgente("gui", "busqueda.jade.chat.GUIAgent", argumentos);
        // Crea e inicia el agente jxta
        Object[] argumentosJXTA = {jxtaManager};
        jxta = crearAgente("jxta", "busqueda.jade.chat.JXTAAgent", argumentosJXTA);
    }

    public void terminar() throws StaleProxyException {
        chat.kill();
        interfaz.kill();
        jxta.kill();
        mainContainer.kill();
    }

    public AgentController crearAgente(String nombre, String clase, Object[] argumentos) throws StaleProxyException {
        AgentController agente = mainContainer.createNewAgent(nombre, clase, argumentos);
        agente.start();
        return agente;
    }

    public void detenerAgente(AgentController agente) throws StaleProxyException {
        agente.kill();
    }

    public void enviarMensaje(String mensaje) throws StaleProxyException {
        interfaz.putO2AObject(mensaje, AgentController.ASYNC);
    }
    
}
