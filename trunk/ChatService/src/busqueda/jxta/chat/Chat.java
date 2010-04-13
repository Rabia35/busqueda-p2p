
package busqueda.jxta.chat;

import busqueda.jxta.PeerBusqueda;
import java.io.IOException;
import net.jxta.document.AdvertisementFactory;
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
public class Chat {
    //
    private PeerBusqueda peer;
    // Servidor
    private ChatServidor servidor;
    // Cliente
    private ChatCliente cliente;
    // Grupo
    private PeerGroup netPeerGroup;

    // Timeut de los OutputPipes
    private static final long TIMEOUT = 5000; // 5 segundos

    public Chat(PeerBusqueda peer) {
        this.peer = peer;
        this.servidor = new ChatServidor(this.peer);
        this.cliente = new ChatCliente(this.peer);
        this.netPeerGroup = peer.getNetPeerGroup();
    }

    public PeerGroup getNetPeerGroup() {
        return netPeerGroup;
    }

    public void iniciarServidor(String nombre, String descripcion) throws IOException {
        servidor.iniciar(nombre, descripcion);
    }

    public void terminarServidor() throws IOException {
        servidor.detener();
    }

    public void iniciarCliente(String busqueda, String nombre) throws IOException {
        cliente.iniciar(busqueda, nombre, servidor.getPipeAdvertisement());
    }

    public void terminarCliente() throws IOException {
        cliente.detener();
    }

    public void enviarMensaje(String remitente, String mensaje) throws IOException {
        cliente.enviarMensaje(remitente, mensaje);
    }

    public String getAdvertisements() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(servidor.getInputPipeAdvs());
        buffer.append("\n");
        buffer.append(cliente.getOutputPipeAdvs());
        buffer.append("\n");
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
        pipe = this.netPeerGroup.getPipeService().createOutputPipe(advertisement, TIMEOUT);
        return pipe;
    }

    public void publicarAdvertisement(PipeAdvertisement advertisement) throws IOException {
        this.netPeerGroup.getDiscoveryService().publish(advertisement);
        this.netPeerGroup.getDiscoveryService().remotePublish(advertisement);
    }

}
