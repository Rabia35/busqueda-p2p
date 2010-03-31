
package busqueda;

import busqueda.jade.JADEContainer;
import jade.wrapper.StaleProxyException;

/**
 *
 * @author almunoz
 */
public class JADECommunicator {
    // JXTA Communicator
    private JXTACommunicator jxtaCommunicator;
    // Manager de Agentes
    private JADEContainer jadeContainer;
    // Interfaz Grafica
    private GUICommunicator guiCommunicator;

    public JADECommunicator(JXTACommunicator jxtaCommunicator, GUICommunicator guiCommunicator) {
        this.jxtaCommunicator = jxtaCommunicator;
        this.jadeContainer = new JADEContainer(this);
        this.guiCommunicator = guiCommunicator;
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
            jadeContainer.iniciar(puerto);
        } else {
            jadeContainer.iniciar();
        }
        jadeContainer.crearAgentes();
        jadeContainer.crearAgentesJXTA(jxtaCommunicator);
    }

    public void terminarJADE() throws StaleProxyException {
        jadeContainer.terminar();
    }

    /* METODOS PARA EL CHAT */

    public void enviarMensajeChat(String mensaje) throws StaleProxyException {
        jadeContainer.enviarMensajeChat(mensaje);
    }
    
    public void recibirMensajeChat(String mensaje) throws StaleProxyException {
        jadeContainer.recibirMensajeChat(mensaje);
    }

    public void mostrarMensajeChat(final String mensaje) {
        guiCommunicator.mostrarMensaje(mensaje);
    }
    
}
