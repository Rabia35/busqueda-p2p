
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
    // Peer
    private PeerBusqueda peer;
    // Servidor
    private ChatServidor servidor;
    // Cliente
    private ChatCliente cliente;
    // Grupo
    private PeerGroup netPeerGroup;

    public Chat(PeerBusqueda peer) {
        this.peer = peer;
        this.servidor = new ChatServidor(this.peer);
        this.cliente = new ChatCliente(this.peer);
        this.netPeerGroup = peer.getNetPeerGroup();
    }

    public PeerGroup getNetPeerGroup() {
        return netPeerGroup;
    }

    public void iniciar(String nombre, String descripcion) throws IOException {
        servidor.iniciar(nombre, descripcion);
        cliente.iniciar(UtilidadesJXTA.ATRIBUTO_BUSQUEDA, nombre, servidor.getPipeAdvertisement());
    }

    public void terminar() throws IOException {
        servidor.detener();
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

}
