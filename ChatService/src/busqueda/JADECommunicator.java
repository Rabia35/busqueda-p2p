
package busqueda;

import busqueda.jade.JADEContainer;
import jade.wrapper.StaleProxyException;

/**
 *
 * @author almunoz
 */
public class JADECommunicator {
    // La instacia de la clase
    private static JADECommunicator instancia;
    // JXTA Communicator
    private JXTACommunicator jxtaCommunicator;
    // Manager de Agentes
    private JADEContainer jadeContainer;
    // Interfaz Grafica
    private GUICommunicator guiCommunicator;

    public static JADECommunicator getInstance() {
        if (instancia == null) {
            instancia = new JADECommunicator();
        }
        return instancia;
    }

    private JADECommunicator() {
        this.jxtaCommunicator = null;
        this.jadeContainer = new JADEContainer(this);
        this.guiCommunicator = null;
    }

    /**
     * @param jxtaCommunicator the jxtaCommunicator to set
     */
    public void setJxtaCommunicator(JXTACommunicator jxtaCommunicator) {
        this.jxtaCommunicator = jxtaCommunicator;
    }

    /**
     * @param guiCommunicator the guiCommunicator to set
     */
    public void setGuiCommunicator(GUICommunicator guiCommunicator) {
        this.guiCommunicator = guiCommunicator;
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
    
    public void recibirMensajeChat(String remitente, String mensaje) throws StaleProxyException {
        jadeContainer.recibirMensajeChat(remitente, mensaje);
    }

    public void mostrarMensajeChat(String remitente, String mensaje) {
        guiCommunicator.mostrarMensaje(remitente, mensaje);
    }
    
}
