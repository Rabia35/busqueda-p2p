
package busqueda.jxta.chat;

import busqueda.jxta.PeerBusqueda;
import java.io.IOException;
import net.jxta.document.AdvertisementFactory;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.Message.ElementIterator;
import net.jxta.endpoint.MessageElement;
import net.jxta.id.IDFactory;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
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

    private PipeAdvertisement crearPipeAdvertisement(String nombre, String descripcion) {
        PipeID pipeID = (PipeID) IDFactory.newPipeID(peer.getNetPeerGroup().getPeerGroupID());
        PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
        advertisement.setPipeID(pipeID);
        advertisement.setType(PipeService.UnicastType);
        advertisement.setName(nombre);
        advertisement.setDescription(descripcion);
        return advertisement;
    }

    private InputPipe crearInputPipe(PipeAdvertisement advertisement) throws IOException {
        PipeInputListener pipeInputListener = new PipeInputListener();
        InputPipe pipe = peer.getNetPeerGroup().getPipeService().createInputPipe(advertisement, pipeInputListener);
        return pipe;
    }

    private void publicarAdvertisement(PipeAdvertisement advertisement) throws IOException {
        peer.getNetPeerGroup().getDiscoveryService().publish(advertisement);
        peer.getNetPeerGroup().getDiscoveryService().remotePublish(advertisement);
    }

    public void iniciar(String nombre, String descripcion) {
        try {
            pipeAdvertisement = crearPipeAdvertisement(nombre, descripcion);
            inputPipe = crearInputPipe(pipeAdvertisement);
            publicarAdvertisement(pipeAdvertisement);
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
        }
    }

    public void detener() {
        try {
            inputPipe.close();
            peer.getNetPeerGroup().getDiscoveryService().flushAdvertisement(pipeAdvertisement);
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
        }
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
            MessageElement mensajeElement  = message.getMessageElement("mensaje");
            if (mensajeElement.toString() != null) {
                peer.mostrarMensajeChat(mensajeElement.toString());
            } else {
                peer.mostrarMensajeChat("No se encuentra el elemento <mensaje>.");
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
