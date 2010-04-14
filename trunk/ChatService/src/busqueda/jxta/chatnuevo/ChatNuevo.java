
package busqueda.jxta.chatnuevo;

import busqueda.jxta.PeerBusqueda;
import java.io.IOException;
import net.jxta.peergroup.PeerGroup;

/**
 *
 * @author almunoz
 */
public class ChatNuevo {
    public static final String NOMBRE_CLIENTE = "chat-cliente";
    public static final String DESCRIPCION_CLIENTE = "Cliente del Chat";
    public static final String NOMBRE_SERVIDOR = "chat-servidor";
    public static final String DESCRIPCION_SERVIDOR = "Servidor del Chat";
    // Peer
    private PeerBusqueda peer;
    // Servidor
    private ChatServidorNuevo servidor;
    // Cliente
    private ChatClienteNuevo cliente;
    // Grupo
    private PeerGroup grupoChat;

    public ChatNuevo(PeerBusqueda peer) {
        this.peer = peer;
        this.grupoChat = peer.getNetPeerGroup();
        this.servidor = null;
        this.cliente = null;
    }

    public PeerGroup getGrupoChat() {
        return grupoChat;
    }

    public void iniciar(String nombre, String descripcion, boolean server) throws IOException {
        System.out.println("Iniciando el Chat");
        if (server) {
            System.out.println("Iniciando el Servidor");
            this.servidor = new ChatServidorNuevo(this);
        } else {
            System.out.println("Iniciando el Cliente");
            this.cliente = new ChatClienteNuevo(this);
        }
    }

    public void terminar(boolean server) {
        System.out.println("Deteniendo el Chat");
        if (server) {
            System.out.println("Deteniendo el Servidor");
            this.servidor.detener();
        } else {
            System.out.println("Deteniendo el Cliente");
            this.cliente.detener();
        }
    }

    public void enviarMensaje(String remitente, String mensaje) throws IOException {
        cliente.enviarMensaje(remitente, mensaje);
    }

    public String getAdvertisements() {
        StringBuffer buffer = new StringBuffer();
        if (servidor != null) {
            buffer.append(servidor.getAdvertisements());
        }
        if (cliente != null) {
            buffer.append(cliente.getAdvertisements());
        }
        return buffer.toString();
    }

    public void mostrarMensajeChat(String remitente, String mensaje) {
        peer.mostrarMensajeChat(remitente, mensaje);
    }

}
