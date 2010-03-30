
package busqueda.jxta.chat;

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
    // ChatPeer
    private ChatPeer chatPeer;
    // Advertisements
    private PipeAdvertisement pipeAdvertisement;
    // Canales de Comunicacion (Pipes)
    private InputPipe inputPipe;

    public ChatServidor(ChatPeer chatPeer) {
        this.chatPeer = chatPeer;
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
        PipeID pipeID = (PipeID) IDFactory.newPipeID(chatPeer.getNetPeerGroup().getPeerGroupID());
        PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
        advertisement.setPipeID(pipeID);
        advertisement.setType(PipeService.UnicastType);
        advertisement.setName(nombre);
        advertisement.setDescription(descripcion);
        return advertisement;
    }

    public void publicarAdvertisement(String nombre, String descripcion) throws IOException {
        // Crear el Pipe Advertisements
        pipeAdvertisement = crearPipeAdvertisement(nombre, descripcion);
        // Publicar el Pipe
        chatPeer.getNetPeerGroup().getDiscoveryService().publish(getPipeAdvertisement());
        chatPeer.getNetPeerGroup().getDiscoveryService().remotePublish(getPipeAdvertisement());
        // Crear el canal de comunicacion (inputPipe)
        PipeInputListener pipeInputListener = new PipeInputListener();
        inputPipe = chatPeer.getNetPeerGroup().getPipeService().createInputPipe(getPipeAdvertisement(),pipeInputListener);
    }

    public void despublicarAdvertisement() throws IOException {
        // Elimina el advertisement del servicio
        chatPeer.getNetPeerGroup().getDiscoveryService().flushAdvertisement(getPipeAdvertisement());
    }

    public void mostrarInputPipeAdvs() {
        chatPeer.mostrarMensaje("=======================");
        chatPeer.mostrarMensaje("InputPipe Advertisement\n");
        chatPeer.mostrarMensaje(getPipeAdvertisement().toString());
        chatPeer.mostrarMensaje("=======================");
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
                // Procesa el elemento del mensaje
                chatPeer.mostrarMensaje(mensajeElement.toString());
            } else {
                chatPeer.mostrarMensaje("No se encuentra el elemento <mensaje>.");
            }
        }
    }

}
