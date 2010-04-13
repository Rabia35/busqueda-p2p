
package busqueda.jxta.chatnuevo;

import busqueda.jxta.PeerBusqueda;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.XMLDocument;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;

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
    private PeerGroup netPeerGroup;
    

    public ChatNuevo(PeerBusqueda peer) {
        this.peer = peer;
        this.netPeerGroup = peer.getNetPeerGroup();
        this.servidor = null;
        this.cliente = null;
    }

    public PeerGroup getNetPeerGroup() {
        return netPeerGroup;
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

    /*public void iniciarServidor(String nombre, String descripcion) throws IOException {
        servidor.iniciar(nombre, descripcion);
    }*/

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

    /*public void iniciarCliente(String busqueda, String nombre) throws IOException {
        cliente.iniciarBusquedaServidor(servidor.getPipeAdvertisement());
    }*/

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

    /* Metodos comunes en el cliente y en el servidor */

    public PipeAdvertisement crearPipeAdvertisement(String nombre, String descripcion) {
        PipeID pipeID = (PipeID) IDFactory.newPipeID(this.netPeerGroup.getPeerGroupID());
        PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
        advertisement.setPipeID(pipeID);
        advertisement.setType(PipeService.UnicastType);
        advertisement.setName(nombre);
        advertisement.setDescription(descripcion);
        return advertisement;
    }

    public InputPipe crearInputPipe(PipeAdvertisement advertisement, PipeMsgListener pipeMsgListener) throws IOException {
        InputPipe pipe = this.netPeerGroup.getPipeService().createInputPipe(advertisement, pipeMsgListener);
        return pipe;
    }

    public OutputPipe crearOuputPipe(PipeAdvertisement advertisement) throws IOException {
        OutputPipe pipe = null;
        pipe = this.netPeerGroup.getPipeService().createOutputPipe(advertisement, PeerBusqueda.TIMEOUT);
        return pipe;
    }

    public void publicarAdvertisement(Advertisement advertisement) throws IOException {
        this.netPeerGroup.getDiscoveryService().publish(advertisement);
        this.netPeerGroup.getDiscoveryService().remotePublish(advertisement);
    }

    public void eliminarAdvertisement(Advertisement advertisement) {
        try {
            this.netPeerGroup.getDiscoveryService().flushAdvertisement(advertisement);
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
        }
    }

    public PipeAdvertisement crearPipeAdvertisementFromString(String advString) throws IOException {
        ByteArrayInputStream advStream = new ByteArrayInputStream(advString.getBytes());
        XMLDocument xml = (XMLDocument) StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, advStream);
        PipeAdvertisement advertisement = (PipeAdvertisement)AdvertisementFactory.newAdvertisement(xml);
        return advertisement;
    }

}
