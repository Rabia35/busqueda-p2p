
package busqueda;

import busqueda.jxta.JXTAManager;
import gui.ChatGUI;
import jade.wrapper.StaleProxyException;
import java.io.IOException;

/**
 *
 * @author almunoz
 */
public class JXTACommunicator {
    // JADE Communicator
    private JADECommunicator jadeCommunicator;
    // Manager de JXTA
    private JXTAManager jxtaManager;
    // Interfaz Grafica
    private ChatGUI gui;

    public JXTACommunicator(ChatGUI gui) {
        this.jadeCommunicator = null;
        this.jxtaManager = new JXTAManager(this);
        this.gui = gui;
    }

    /**
     * @return the jadeCommunicator
     */
    public JADECommunicator getJadeCommunicator() {
        return jadeCommunicator;
    }

    /**
     * @param jadeCommunicator the jadeCommunicator to set
     */
    public void setJadeCommunicator(JADECommunicator jadeCommunicator) {
        this.jadeCommunicator = jadeCommunicator;
    }

    public void iniciarJXTA(String puerto) {
        if (puerto != null ){
            jxtaManager.iniciar(puerto);
        } else {
            jxtaManager.iniciar();
        }
    }

    public void terminarJXTA() throws IOException {
        jxtaManager.terminar();
    }

    /* METODOS PARA EL CHAT */

    public void iniciarChat(String nombre, String descripcion) {
        jxtaManager.iniciarChat(nombre, descripcion);
    }

    public void detenerChat() {
        jxtaManager.detenerChat();
    }

    public void enviarMensajeChat(final String mensaje) {
        jxtaManager.enviarMensajeChat(mensaje);
    }

    public void mostrarMensajeChat(String mensaje) throws StaleProxyException {
        jadeCommunicator.recibirMensajeChat(mensaje);
    }

    public String getAdvertisementsChat() {
        return jxtaManager.getAdvertisementsChat();
    }

}
