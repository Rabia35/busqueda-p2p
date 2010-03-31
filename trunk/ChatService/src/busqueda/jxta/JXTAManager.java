
package busqueda.jxta;

import busqueda.JXTACommunicator;
import jade.wrapper.StaleProxyException;
import java.io.IOException;

/**
 *
 * @author almunoz
 */
public class JXTAManager {
    // JXTA Communicator
    private JXTACommunicator jxtaCommunicator;
    // Peer
    private PeerBusqueda peer;
    // Puerto
    private static String PUERTO_TCP = "9701";
    
    public JXTAManager(JXTACommunicator jxtaCommunicator) {
        this.jxtaCommunicator = jxtaCommunicator;
        this.peer = new PeerBusqueda(this);
    }

    public void iniciar() {
        peer.iniciarJXTA(PUERTO_TCP);
    }

    public void iniciar(String puerto) {
        peer.iniciarJXTA(puerto);
    }

    public void terminar() throws IOException {
        peer.terminarJXTA();
    }

    /* CHAT */

    public void iniciarChat(String nombre, String descripcion) {
        peer.iniciarChat(nombre, descripcion);
    }

    public void detenerChat() {
        peer.detenerChat();
    }
    
    public void enviarMensajeChat(String mensaje) {
        peer.enviarMensajeChat(mensaje);
    }

    public void mostrarMensajeChat(String mensaje) throws StaleProxyException {
        jxtaCommunicator.mostrarMensajeChat(mensaje);
    }

    public String getAdvertisementsChat() {
        return peer.getAdvertisementsChat();
    }

}
