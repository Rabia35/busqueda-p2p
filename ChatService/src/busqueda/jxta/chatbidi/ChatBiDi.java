
package busqueda.jxta.chatbidi;

import busqueda.jxta.PeerBusqueda;
import busqueda.jxta.UtilidadesJXTA;
import java.io.IOException;
import net.jxta.document.AdvertisementFactory;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;

/**
 *
 * @author almunoz
 */
public class ChatBiDi {
    public static final int PIPE_TIMEOUT  = 10 * 1000;
    public static final int DELAY_BUSQUEDA  = 15 * 1000;
    public static final String NOMBRE_GRUPO = "Chat PeerGroup";
    public static final String DESCRIPCION_GRUPO = "PeerGroup del Chat";
    public static final String NOMBRE_CLIENTE = "Chat Cliente";
    public static final String DESCRIPCION_CLIENTE = "Cliente del Chat";
    public static final String NOMBRE_SERVIDOR = "Chat Servidor";
    public static final String DESCRIPCION_SERVIDOR = "Servidor del Chat";
    public static final String NOMBRE_BIDIPIPE = "Chat BiDiPipe";
    public static final String DESCRIPCION_BIDIPIPE = "BiDiPipe del Chat";
    // Peer
    private PeerBusqueda peer;
    // Servidor
    private ChatServidorBiDi servidor;
    // Cliente
    private ChatClienteBiDi cliente;
    // Grupos
    private static PeerGroup netPeerGroup;
    public static PeerGroup grupoChat;

    public static PipeAdvertisement getBiDiPipeAdvertisement() {
        PipeID pipeID = (PipeID) IDFactory.newPipeID(grupoChat.getPeerGroupID(), NOMBRE_BIDIPIPE.getBytes());
        PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
        advertisement.setPipeID(pipeID);
        advertisement.setType(PipeService.UnicastType);
        advertisement.setName(NOMBRE_BIDIPIPE);
        advertisement.setDescription(DESCRIPCION_BIDIPIPE);
        //System.out.println("BiDiPipe Advertisement: \n" + advertisement.toString());
        return advertisement;
    }


    public ChatBiDi(PeerBusqueda peer) {
        this.peer = peer;
        this.servidor = null;
        this.cliente = null;
        ChatBiDi.netPeerGroup = peer.getNetPeerGroup();
        ChatBiDi.grupoChat = null;
    }

    public void iniciar(String nombre, String descripcion) throws IOException {
        System.out.println("Iniciando el Chat");
        //grupoChat = netPeerGroup;
        ChatBiDi.grupoChat = UtilidadesJXTA.crearGrupo(ChatBiDi.netPeerGroup, NOMBRE_GRUPO, DESCRIPCION_GRUPO);
        if (ChatBiDi.grupoChat != null) {
            UtilidadesJXTA.iniciarGrupo(ChatBiDi.grupoChat);
        }
        if (PeerBusqueda.SERVIDOR_CHAT) {
            System.out.println("Iniciando el Servidor");
            this.servidor = new ChatServidorBiDi(this);
        } else {
            System.out.println("Iniciando el Cliente");
            this.cliente = new ChatClienteBiDi(this);
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
        UtilidadesJXTA.terminarGrupo(ChatBiDi.grupoChat);
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
