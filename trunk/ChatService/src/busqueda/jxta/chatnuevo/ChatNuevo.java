
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
    public static final String NOMBRE_GRUPO = "Chat PeerGroup";
    public static final String DESCRIPCION_GRUPO = "Descripcion del Chat PeerGroup";
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
    // Grupos
    private PeerGroup netPeerGroup;
    private PeerGroup grupoChat;

    public ChatNuevo(PeerBusqueda peer) {
        this.peer = peer;
        this.netPeerGroup = peer.getNetPeerGroup();
        this.grupoChat = null;
        this.servidor = null;
        this.cliente = null;
    }

    public PeerGroup getGrupoChat() {
        return grupoChat;
    }

    public void iniciar(String nombre, String descripcion) throws IOException {
        System.out.println("Iniciando el Chat");
        //grupoChat = netPeerGroup;
        grupoChat = UtilidadesJXTA.crearGrupo(netPeerGroup, NOMBRE_GRUPO, DESCRIPCION_GRUPO);
        if (grupoChat != null) {
            UtilidadesJXTA.iniciarGrupo(grupoChat);
        }
        if (PeerBusqueda.SERVIDOR_CHAT == true) {
            System.out.println("Iniciando el Servidor");
            this.servidor = new ChatServidorNuevo(this);
        } else {
            System.out.println("Iniciando el Cliente");
            this.cliente = new ChatClienteNuevo(this);
        }
    }

    public void terminar() {
        System.out.println("Deteniendo el Chat");
        UtilidadesJXTA.terminarGrupo(grupoChat);
        if (PeerBusqueda.SERVIDOR_CHAT == true) {
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
