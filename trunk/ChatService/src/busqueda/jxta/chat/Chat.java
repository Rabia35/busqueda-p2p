
package busqueda.jxta.chat;

import busqueda.jxta.PeerBusqueda;
import busqueda.jxta.UtilidadesJXTA;
import java.io.IOException;
import net.jxta.peergroup.PeerGroup;

/**
 *
 * @author almunoz
 */
public class Chat {
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
    private ChatServidor servidor;
    // Cliente
    private ChatCliente cliente;
    // Grupos
    private static PeerGroup netPeerGroup;
    public static PeerGroup grupoChat;

    public Chat(PeerBusqueda peer) {
        this.peer = peer;
        this.servidor = null;
        this.cliente = null;
        Chat.netPeerGroup = peer.getNetPeerGroup();
        Chat.grupoChat = null;
    }

    public void iniciar(String tipo, String descripcion) throws IOException {
        Chat.NOMBRE_GRUPO = tipo + "-" + Chat.NOMBRE_GRUPO;
        Chat.DESCRIPCION_GRUPO = Chat.DESCRIPCION_GRUPO + descripcion;
        Chat.NOMBRE_CLIENTE = tipo + "-" + Chat.NOMBRE_CLIENTE;
        Chat.DESCRIPCION_CLIENTE = Chat.DESCRIPCION_CLIENTE + descripcion;
        Chat.NOMBRE_SERVIDOR = tipo + "-" + Chat.NOMBRE_SERVIDOR;
        Chat.DESCRIPCION_SERVIDOR = Chat.DESCRIPCION_SERVIDOR + descripcion;
        System.out.println("Iniciando el Chat");
        Chat.grupoChat = UtilidadesJXTA.crearGrupo(Chat.netPeerGroup, Chat.NOMBRE_GRUPO, Chat.DESCRIPCION_GRUPO);
        if (Chat.grupoChat != null) {
            UtilidadesJXTA.iniciarGrupo(Chat.grupoChat);
        }
        if (PeerBusqueda.SERVIDOR_CHAT) {
            System.out.println("Iniciando el servidor '" + Chat.NOMBRE_SERVIDOR + "'");
            this.servidor = new ChatServidor(this);
        } else {
            System.out.println("Iniciando el cliente '" + Chat.NOMBRE_CLIENTE + "'");
            this.cliente = new ChatCliente(this);
        }
    }

    public void terminar() {
        System.out.println("Deteniendo el Chat");
        if (PeerBusqueda.SERVIDOR_CHAT) {
            System.out.println("Deteniendo el servidor '" + Chat.NOMBRE_SERVIDOR + "'");
            this.servidor.detener();
        } else {
            System.out.println("Deteniendo el cliente '" + Chat.NOMBRE_CLIENTE + "'");
            this.cliente.detener();
        }
        UtilidadesJXTA.terminarGrupo(Chat.grupoChat);
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
