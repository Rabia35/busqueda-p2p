
package busqueda;

import busqueda.jxta.PeerBusqueda;
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
    private GUICommunicator guiCommunicator;

    public JXTACommunicator(GUICommunicator guiCommunicator) {
        this.jadeCommunicator = null;
        this.peer = new PeerBusqueda(this);
        this.guiCommunicator = guiCommunicator;
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

    public void enviarMensajeChat(String remitente, String mensaje) {
        peer.enviarMensajeChat(remitente, mensaje);
    }

    public void mostrarMensajeChat(String remitente, String mensaje) throws StaleProxyException {
        jadeCommunicator.recibirMensajeChat(remitente, mensaje);
    }

    public String getAdvertisementsChat() {
        return peer.getAdvertisementsChat();
    }

}
