
package ChatServicePropagate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.XMLDocument;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.Message.ElementIterator;
import net.jxta.endpoint.MessageElement;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.platform.ModuleClassID;
import net.jxta.platform.ModuleSpecID;
import net.jxta.protocol.ModuleClassAdvertisement;
import net.jxta.protocol.ModuleSpecAdvertisement;
import net.jxta.protocol.PipeAdvertisement;

/**
 *
 * @author almunoz
 */
public class ChatServidor {
    // ChatPeer
    private ChatPeer chatPeer;
    // Grupo
    private PeerGroup netPeerGroup;
    // Servicios
    private String nombreServicio;
    private DiscoveryService discoveryService;
    private PipeService pipeService;
    // Advertisement para el pipe de los mensajes
    private PipeAdvertisement pipeAdvertisement;
    // Canales de Comunicacion (Pipes)
    private InputPipe inputPipe;

    public ChatServidor(ChatPeer chatPeer, PeerGroup netPeerGroup, String nombreServicio) {
        this.chatPeer = chatPeer;
        this.netPeerGroup = netPeerGroup;
        this.nombreServicio = nombreServicio;
        this.discoveryService = this.netPeerGroup.getDiscoveryService();
        this.pipeService = this.netPeerGroup.getPipeService();
        this.pipeAdvertisement = null;
        this.inputPipe = null;
    }

    /**
     * @return the netPeerGroup
     */
    public PeerGroup getNetPeerGroup() {
        return netPeerGroup;
    }

    /**
     * @param netPeerGroup the netPeerGroup to set
     */
    public void setNetPeerGroup(PeerGroup netPeerGroup) {
        this.netPeerGroup = netPeerGroup;
        this.discoveryService = this.netPeerGroup.getDiscoveryService();
        this.pipeService = this.netPeerGroup.getPipeService();
    }

    /**
     * @return the nombreServicio
     */
    public String getNombreServicio() {
        return nombreServicio;
    }

    /**
     * @param nombreServicio the nombreServicio to set
     */
    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    /**
     * @return the discoveryService
     */
    public DiscoveryService getDiscoveryService() {
        return discoveryService;
    }

    /**
     * @return the pipeService
     */
    public PipeService getPipeService() {
        return pipeService;
    }

    /**
     * @return the pipeAdvertisement
     */
    public PipeAdvertisement getPipeAdvertisement() {
        return pipeAdvertisement;
    }

    /**
     * @return the inputPipe
     */
    public InputPipe getInputPipe() {
        return inputPipe;
    }

    /****************************************************/

    public PipeAdvertisement crearPipeAdvertisement(String tipo) {
        if (tipo.equals(PipeService.UnicastType) ||
            tipo.equals(PipeService.UnicastSecureType) ||
            tipo.equals(PipeService.PropagateType)) {
            PipeID pipeID = (PipeID) IDFactory.newPipeID(netPeerGroup.getPeerGroupID());
            PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
            advertisement.setPipeID(pipeID);
            advertisement.setType(tipo);
            advertisement.setName("Pipe Chat");
            advertisement.setDescription("JXTA Chat Pipe");
            return advertisement;
        } else {
            return null;
        }
    }

    public PipeAdvertisement crearPipeAdvertisementFile() throws NoSuchElementException, FileNotFoundException, IOException {
        FileInputStream file = new FileInputStream("advertisements/ChatPipeAdv.xml");
        XMLDocument xml = (XMLDocument) StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, file);
        PipeAdvertisement advertisement = (PipeAdvertisement)AdvertisementFactory.newAdvertisement(xml);
        return advertisement;
    }

    public void publicarServicio() throws IOException {
        // Crear el ModuleClassAdvertisement
        ModuleClassAdvertisement moduleClass = (ModuleClassAdvertisement) AdvertisementFactory.newAdvertisement(ModuleClassAdvertisement.getAdvertisementType());
        ModuleClassID mcID = IDFactory.newModuleClassID();
        moduleClass.setModuleClassID(mcID);
        moduleClass.setName("jxta-chat-mca");
        moduleClass.setDescription("JXTA Chat");
        discoveryService.publish(moduleClass);
        discoveryService.remotePublish(moduleClass);
        // Crear el Pipe Advertisements
        pipeAdvertisement = this.crearPipeAdvertisementFile();
        // Crear el ModuleSpecAdvertisement
        ModuleSpecAdvertisement moduleSpec = (ModuleSpecAdvertisement) AdvertisementFactory.newAdvertisement(ModuleSpecAdvertisement.getAdvertisementType());
        ModuleSpecID msID = IDFactory.newModuleSpecID(mcID);
        moduleSpec.setModuleSpecID(msID);
        moduleSpec.setVersion("Version 1.0");
        moduleSpec.setName(nombreServicio);
        moduleSpec.setDescription("JXTA Chat Spec");
        moduleSpec.setCreator("almunoz");
        moduleSpec.setSpecURI("http://jxta.org/chat");
        moduleSpec.setPipeAdvertisement(pipeAdvertisement);
        discoveryService.publish(moduleSpec);
        discoveryService.remotePublish(moduleSpec);
        // Crear el canal de comunicacion (inputPipe)
        PipeInputListener pipeInputListener = new PipeInputListener();
        inputPipe = pipeService.createInputPipe(pipeAdvertisement, pipeInputListener);
        System.out.println("Advertisement del InputPipe Creado");
        System.out.println(pipeAdvertisement.toString());
    }

    // Clase para escuchar los mensajes de entrada
    public class PipeInputListener implements PipeMsgListener {
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
                chatPeer.recibirMensaje("Mensaje: " + mensajeElement.toString());
            } else {
                chatPeer.recibirMensaje("No se encuentra el elemento <mensaje>.");
            }
        }
    }

}
