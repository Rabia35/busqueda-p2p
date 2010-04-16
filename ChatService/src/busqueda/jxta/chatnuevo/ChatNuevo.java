
package busqueda.jxta.chatnuevo;

import busqueda.jxta.PeerBusqueda;
import busqueda.jxta.UtilidadesJXTA;
import java.io.IOException;
import net.jxta.peergroup.PeerGroup;

/**
 *
 * @author almunoz
 */
public class ChatNuevo {
    public static final int PIPE_TIMEOUT  = 5 * 1000;
    public static final int DELAY_BUSQUEDA  = 10 * 1000;
    public static final String NOMBRE_GRUPO = "Chat PeerGroup";
    public static final String DESCRIPCION_GRUPO = "PeerGroup del Chat";
    public static final String NOMBRE_CLIENTE = "Chat Cliente";
    public static final String DESCRIPCION_CLIENTE = "Cliente del Chat";
    public static final String NOMBRE_SERVIDOR = "Chat Servidor";
    public static final String DESCRIPCION_SERVIDOR = "Servidor del Chat";
    // Peer
    private PeerBusqueda peer;
    // Servidor
    private ChatServidorNuevo servidor;
    // Cliente
    private ChatClienteNuevo cliente;
    // Grupos
    private static PeerGroup netPeerGroup;
    public static PeerGroup grupoChat;

    public ChatNuevo(PeerBusqueda peer) {
        this.peer = peer;
        this.servidor = null;
        this.cliente = null;
        ChatNuevo.netPeerGroup = peer.getNetPeerGroup();
        ChatNuevo.grupoChat = null;
    }

    public void iniciar(String nombre, String descripcion) throws IOException {
        System.out.println("Iniciando el Chat");
        ChatNuevo.grupoChat = UtilidadesJXTA.crearGrupo(ChatNuevo.netPeerGroup, ChatNuevo.NOMBRE_GRUPO, ChatNuevo.DESCRIPCION_GRUPO);
        if (ChatNuevo.grupoChat != null) {
            UtilidadesJXTA.iniciarGrupo(ChatNuevo.grupoChat);
        }
        if (PeerBusqueda.SERVIDOR_CHAT) {
            System.out.println("Iniciando el Servidor");
            this.servidor = new ChatServidorNuevo(this);
        } else {
            System.out.println("Iniciando el Cliente");
            this.cliente = new ChatClienteNuevo(this);
        }
    }

    public void terminar() {
        System.out.println("Deteniendo el Chat");
        if (PeerBusqueda.SERVIDOR_CHAT) {
            System.out.println("Deteniendo el Servidor");
            this.servidor.detener();
        } else {
            System.out.println("Deteniendo el Cliente");
            this.cliente.detener();
        }
        UtilidadesJXTA.terminarGrupo(ChatNuevo.grupoChat);
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
