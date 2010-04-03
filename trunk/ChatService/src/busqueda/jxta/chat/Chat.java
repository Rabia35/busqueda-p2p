
package busqueda.jxta.chat;

import busqueda.jxta.PeerBusqueda;
import java.io.IOException;

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

    public Chat(PeerBusqueda peer) {
        this.peer = peer;
        this.servidor = new ChatServidor(this.peer);
        this.cliente = new ChatCliente(this.peer);
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

}
