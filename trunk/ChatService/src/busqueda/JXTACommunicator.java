
package busqueda;

import busqueda.jxta.PeerBusqueda;
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
    // Peer
    private PeerBusqueda peer;
    // Interfaz Grafica
    private ChatGUI gui;

    public JXTACommunicator(ChatGUI gui) {
        this.jadeCommunicator = null;
        this.peer = new PeerBusqueda(this);
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
            peer.iniciarJXTA(puerto);
        } else {
            peer.iniciarJXTA();
        }
    }

    public void terminarJXTA() throws IOException {
        peer.terminarJXTA();
    }

    /* METODOS PARA EL CHAT */

    public void iniciarChat(String nombre, String descripcion) {
        peer.iniciarChat(nombre, descripcion);
    }

    public void detenerChat() {
        peer.detenerChat();
    }

    public void enviarMensajeChat(final String mensaje) {
        peer.enviarMensajeChat(mensaje);
    }

    public void mostrarMensajeChat(String mensaje) throws StaleProxyException {
        jadeCommunicator.recibirMensajeChat(mensaje);
    }

    public String getAdvertisementsChat() {
        return peer.getAdvertisementsChat();
    }

}
