
package busqueda;

import busqueda.jxta.PeerBusqueda;
import jade.wrapper.StaleProxyException;
import java.io.IOException;

/**
 *
 * @author almunoz
 */
public class JXTACommunicator {
    // La instacia de la clase
    private static JXTACommunicator instancia;
    // JADE Communicator
    private JADECommunicator jadeCommunicator;
    // Peer
    private PeerBusqueda peer;
    // Interfaz Grafica
    private GUICommunicator guiCommunicator;

    public static JXTACommunicator getInstance() {
        if (instancia == null) {
            instancia = new JXTACommunicator();
        }
        return instancia;
    }

    private JXTACommunicator() {
        this.jadeCommunicator = null;
        this.peer = new PeerBusqueda(this);
        this.guiCommunicator = null;
    }

    /**
     * @param jadeCommunicator the jadeCommunicator to set
     */
    public void setJadeCommunicator(JADECommunicator jadeCommunicator) {
        this.jadeCommunicator = jadeCommunicator;
    }

    /**
     * @param guiCommunicator the guiCommunicator to set
     */
    public void setGuiCommunicator(GUICommunicator guiCommunicator) {
        this.guiCommunicator = guiCommunicator;
    }

    public void iniciarJXTA(String puerto, boolean server) {
        if (puerto != null ){
            peer.iniciarJXTA(puerto, server);
        } else {
            peer.iniciarJXTA(server);
        }
    }

    public void terminarJXTA() throws IOException {
        peer.terminarJXTA();
    }

    /* METODOS PARA EL CHAT */

    public void iniciarChat(final String nombre, final String descripcion) {
        peer.iniciarChat(nombre, descripcion);
    }

    public void enviarMensajeChat(final String remitente, final String mensaje) {
        peer.enviarMensajeChat(remitente, mensaje);
    }

    public void mostrarMensajeChat(String remitente, String mensaje) throws StaleProxyException {
        jadeCommunicator.recibirMensajeChat(remitente, mensaje);
    }

    public String getAdvertisementsChat() {
        return peer.getAdvertisementsChat();
    }

}
