
package busqueda.jxta;

import busqueda.JXTACommunicator;
import busqueda.jxta.chat.ChatPeer;
import jade.wrapper.StaleProxyException;
import java.io.IOException;

/**
 *
 * @author almunoz
 */
public class JXTAManager {
    // JXTA Communicator
    private JXTACommunicator jxtaCommunicator;
    // Puerto
    private String puerto;
    // Chat Peer
    private ChatPeer peerChat;

    public JXTAManager(JXTACommunicator jxtaCommunicator) {
        this.jxtaCommunicator = jxtaCommunicator;
        this.puerto = "9701";
        this.peerChat = new ChatPeer(this);
    }

    public void iniciar() {
        iniciar(this.puerto);
    }

    public void iniciar(String port) {
        this.puerto = port;
        peerChat.iniciarJXTA(this.puerto);
    }

    public void terminar() throws IOException {
        peerChat.terminarJXTA();
    }

    /* CHAT */

    public void publicarAdvertisementChat(String nombre, String descripcion) {
        peerChat.publicarAdvertisement(nombre, descripcion);
    }

    public void buscarAdvertisementChat(String nombre) {
        peerChat.buscarAdvertisement(nombre);
    }

    public void enviarMensajeChat(String mensaje) {
        peerChat.enviarMensaje(mensaje);
    }

    public void mostrarMensajeChat(String mensaje) throws StaleProxyException {
        jxtaCommunicator.mostrarMensajeChat(mensaje);
    }

    public String getAdvertisementsChat() {
        return peerChat.getAdvertisements();
    }

}
