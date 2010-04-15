
package busqueda.jxta.chat;

import busqueda.jxta.PeerBusqueda;
import busqueda.jxta.UtilidadesJXTA;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.Message.ElementIterator;
import net.jxta.endpoint.MessageElement;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.protocol.PipeAdvertisement;

/**
 *
 * @author almunoz
 */
public class ChatServidor {
    // Peer
    private PeerBusqueda peer;
    // Advertisement
    private PipeAdvertisement pipeAdvertisement;
    // Canal de Comunicacion (Pipe)
    private InputPipe inputPipe;

    public ChatServidor(PeerBusqueda peer) {
        this.peer = peer;
        this.pipeAdvertisement = null;
        this.inputPipe = null;
    }

    /**
     * @return the pipeAdvertisement
     */
    public PipeAdvertisement getPipeAdvertisement() {
        return pipeAdvertisement;
    }

    public void iniciar(String nombre, String descripcion) {
        pipeAdvertisement = UtilidadesJXTA.crearPipeAdvertisement(peer.getNetPeerGroup(), nombre, descripcion);
        PipeInputListener listener = new PipeInputListener();
        inputPipe = UtilidadesJXTA.crearInputPipe(peer.getNetPeerGroup(), pipeAdvertisement, listener);
        UtilidadesJXTA.publicarAdvertisement(peer.getNetPeerGroup(), pipeAdvertisement);
    }

    public void detener() {
        inputPipe.close();
        UtilidadesJXTA.eliminarAdvertisement(peer.getNetPeerGroup(), pipeAdvertisement);
    }

    // Clase para escuchar los mensajes de entrada
    private class PipeInputListener implements PipeMsgListener {
        @Override
        public void pipeMsgEvent(PipeMsgEvent pmevent) {
            Message message = pmevent.getMessage();
            // Todos los elementos del mensaje
            ElementIterator elementos = message.getMessageElements();
            if (!elementos.hasNext()) {
                return;
            }
            // Por nombre del elemento
            MessageElement remitenteElement = message.getMessageElement("remitente");
            MessageElement mensajeElement  = message.getMessageElement("mensaje");
            if (remitenteElement.toString() != null && mensajeElement.toString() != null) {
                peer.mostrarMensajeChat(remitenteElement.toString(), mensajeElement.toString());
            } else {
                peer.mostrarMensajeChat("Error", "No se encuentra el elemento <mensaje>");
            }
        }
    }

    public String getInputPipeAdvs() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("=======================\n");
        buffer.append("InputPipe Advertisement\n");
        buffer.append("\n");
        if (pipeAdvertisement != null ) {
            buffer.append(pipeAdvertisement.toString());
        } else {
            buffer.append("No se encontro el Pipe Advertisement.");
        }
        buffer.append("\n");
        buffer.append("=======================");
        buffer.append("\n");
        return buffer.toString();
    }

}
