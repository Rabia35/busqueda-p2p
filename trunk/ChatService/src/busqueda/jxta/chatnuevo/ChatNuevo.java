
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
    public static String NOMBRE_GRUPO = "peergroup";
    public static String DESCRIPCION_GRUPO = "PeerGroup del ";
    public static String NOMBRE_CLIENTE = "cliente";
    public static String DESCRIPCION_CLIENTE = "Cliente del ";
    public static String NOMBRE_SERVIDOR = "servidor";
    public static String DESCRIPCION_SERVIDOR = "Servidor del ";
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

    public void iniciar(String tipo, String descripcion) throws IOException {
        ChatNuevo.NOMBRE_GRUPO = tipo + "-" + ChatNuevo.NOMBRE_GRUPO;
        ChatNuevo.DESCRIPCION_GRUPO = ChatNuevo.DESCRIPCION_GRUPO + descripcion;
        ChatNuevo.NOMBRE_CLIENTE = tipo + "-" + ChatNuevo.NOMBRE_CLIENTE;
        ChatNuevo.DESCRIPCION_CLIENTE = ChatNuevo.DESCRIPCION_CLIENTE + descripcion;
        ChatNuevo.NOMBRE_SERVIDOR = tipo + "-" + ChatNuevo.NOMBRE_SERVIDOR;
        ChatNuevo.DESCRIPCION_SERVIDOR = ChatNuevo.DESCRIPCION_SERVIDOR + descripcion;
        System.out.println("Iniciando el Chat");
        ChatNuevo.grupoChat = UtilidadesJXTA.crearGrupo(ChatNuevo.netPeerGroup, ChatNuevo.NOMBRE_GRUPO, ChatNuevo.DESCRIPCION_GRUPO);
        if (ChatNuevo.grupoChat != null) {
            UtilidadesJXTA.iniciarGrupo(ChatNuevo.grupoChat);
        }
        if (PeerBusqueda.SERVIDOR_CHAT) {
            System.out.println("Iniciando el servidor '" + ChatNuevo.NOMBRE_SERVIDOR + "'");
            this.servidor = new ChatServidorNuevo(this);
        } else {
            System.out.println("Iniciando el cliente '" + ChatNuevo.NOMBRE_CLIENTE + "'");
            this.cliente = new ChatClienteNuevo(this);
        }
    }

    public void terminar() {
        System.out.println("Deteniendo el Chat");
        if (PeerBusqueda.SERVIDOR_CHAT) {
            System.out.println("Deteniendo el servidor '" + ChatNuevo.NOMBRE_SERVIDOR + "'");
            this.servidor.detener();
        } else {
            System.out.println("Deteniendo el cliente '" + ChatNuevo.NOMBRE_CLIENTE + "'");
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
