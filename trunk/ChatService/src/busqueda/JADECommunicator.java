
package busqueda;

import busqueda.jade.JADEManager;
import gui.ChatGUI;
import jade.wrapper.StaleProxyException;

/**
 *
 * @author almunoz
 */
public class JADECommunicator {
    // JXTA Communicator
    private JXTACommunicator jxtaCommunicator;
    // Manager de Agentes
    private JADEManager jadeManager;
    // Interfaz Grafica
    private ChatGUI gui;

    public JADECommunicator(JXTACommunicator jxtaCommunicator, ChatGUI gui) {
        this.jxtaCommunicator = jxtaCommunicator;
        this.jadeManager = new JADEManager(this);
        this.gui = gui;
    }

    /**
     * @return the jxtaCommunicator
     */
    public JXTACommunicator getJxtaCommunicator() {
        return jxtaCommunicator;
    }

    /**
     * @param jxtaCommunicator the jxtaCommunicator to set
     */
    public void setJxtaCommunicator(JXTACommunicator jxtaCommunicator) {
        this.jxtaCommunicator = jxtaCommunicator;
    }

    public void iniciarJADE(String puerto) throws StaleProxyException {
        if (puerto != null ){
            jadeManager.iniciar(puerto);
        } else {
            jadeManager.iniciar();
        }
        jadeManager.crearAgentes();
        jadeManager.crearAgentesJXTA(jxtaCommunicator);
    }

    public void terminarJADE() throws StaleProxyException {
        jadeManager.terminar();
    }

    /* METODOS PARA EL CHAT */

    public void enviarMensajeChat(String mensaje) throws StaleProxyException {
        jadeManager.enviarMensajeChat(mensaje);
    }
    
    public void recibirMensajeChat(String mensaje) throws StaleProxyException {
        jadeManager.recibirMensajeChat(mensaje);
    }

    public void mostrarMensajeChat(final String mensaje) {
        gui.mostrarMensaje(mensaje);
    }
    
}
