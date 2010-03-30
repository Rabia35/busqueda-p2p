
package busqueda.jxta.chat;

import jade.wrapper.StaleProxyException;
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
    private ChatPeer peerChat;
    // Advertisements
    private PipeAdvertisement pipeAdvertisement;
    // Canales de Comunicacion (Pipes)
    private InputPipe inputPipe;

    public ChatServidor(ChatPeer peerChat) {
        this.peerChat = peerChat;
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
        PipeID pipeID = (PipeID) IDFactory.newPipeID(peerChat.getNetPeerGroup().getPeerGroupID());
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
        peerChat.getNetPeerGroup().getDiscoveryService().publish(getPipeAdvertisement());
        peerChat.getNetPeerGroup().getDiscoveryService().remotePublish(getPipeAdvertisement());
        // Crear el canal de comunicacion (inputPipe)
        PipeInputListener pipeInputListener = new PipeInputListener();
        inputPipe = peerChat.getNetPeerGroup().getPipeService().createInputPipe(getPipeAdvertisement(),pipeInputListener);
    }

    public void despublicarAdvertisement() throws IOException {
        // Elimina el advertisement del servicio
        peerChat.getNetPeerGroup().getDiscoveryService().flushAdvertisement(getPipeAdvertisement());
    }

    public String getInputPipeAdvs() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("=======================\n");
        buffer.append("InputPipe Advertisement\n");
        buffer.append("\n");
        buffer.append(getPipeAdvertisement().toString());
        buffer.append("\n");
        buffer.append("=======================");
        buffer.append("\n");
        return buffer.toString();
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
                try {
                    // Procesa el elemento del mensaje
                    peerChat.mostrarMensaje(mensajeElement.toString());
                } catch (StaleProxyException ex) {
                    System.out.println("StaleProxyException: " + ex.getMessage());
                }
            } else {
                try {
                    peerChat.mostrarMensaje("No se encuentra el elemento <mensaje>.");
                } catch (StaleProxyException ex) {
                    System.out.println("StaleProxyException: " + ex.getMessage());
                }
            }
        }
    }

}
