
package ChatGUI;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Vector;
import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.StructuredTextDocument;
import net.jxta.document.XMLDocument;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.Message.ElementIterator;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.NetPeerGroupFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupFactory;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.platform.ModuleClassID;
import net.jxta.platform.ModuleSpecID;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.ModuleClassAdvertisement;
import net.jxta.protocol.ModuleSpecAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.protocol.PipeAdvertisement;

/**
 *
 * @author almunoz
 */
public class ChatPeer {
    // Interfaz Grafica
    private ChatGUI gui;
    // Red
    private NetworkManager manager;
    private NetworkConfigurator configurator;
    // Peer
    private static int peerNumber = 0;
    private int puerto;
    // Grupo
    private static PeerGroup netPeerGroup;
    private static PeerGroupAdvertisement netPeerGroupAdvertisement;
    // Servicios
    private String nombreCampo;
    private String nombreServicio;
    private DiscoveryService discoveryService;
    private PipeService pipeService;
    // Advertisements para el servicio
    private PipeAdvertisement pipeAdvertisement;
    private ModuleClassAdvertisement moduleClassAdvertisement;
    private ModuleSpecAdvertisement moduleSpecAdvertisement;
    // Canales de Comunicacion (Pipes)
    private long tiempoEspera;
    private InputPipe inputPipe;
    private OutputPipe outputPipe;
    private PipeAdvertisement outputPipeAdvertisement;
    private Vector<OutputPipe> outputPipes;
    
    public ChatPeer(ChatGUI chatGUI) {
        this.gui = chatGUI;
        this.manager = null;
        this.configurator = null;
        peerNumber = peerNumber + 1;
        this.puerto = 9701;
        netPeerGroup = null;
        netPeerGroupAdvertisement = null;
        this.nombreCampo = "Name";
        this.nombreServicio = "jxta-chat";
        this.discoveryService = null;
        this.pipeService = null;
        this.pipeAdvertisement = null;
        this.moduleClassAdvertisement = null;
        this.moduleSpecAdvertisement = null;
        this.tiempoEspera = 60000;
        this.inputPipe = null;
        this.outputPipe = null;
        this.outputPipeAdvertisement = null;
        this.outputPipes = new Vector<OutputPipe>(0, 10);
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }
    
    public void iniciarJXTA() {
        try {
            gui.recibirMensaje("Iniciando Red JXTA");
            manager = new NetworkManager(NetworkManager.ConfigMode.RENDEZVOUS, "jxta-net-chat");
            configurator = manager.getConfigurator();
            configurator.setName("chat" + peerNumber);
            configurator.setPrincipal("chat-principal");
            configurator.setPassword("chat-password");
            configurator.setTcpPort(puerto);
            manager.startNetwork();
            // Grupo
            netPeerGroup = manager.getNetPeerGroup();
            //netPeerGroup = PeerGroupFactory.newNetPeerGroup();
            netPeerGroupAdvertisement = netPeerGroup.getPeerGroupAdvertisement();
            // Servicios
            discoveryService = netPeerGroup.getDiscoveryService();
            pipeService = netPeerGroup.getPipeService();
            
            // Publicar el servicio de Chat
            publicarServicio();
            // Buscar el servicio de Chat
            buscarServicio();
            gui.recibirMensaje("Red JXTA Iniciada");
            gui.recibirMensaje(manager.getPeerID().toString());
        } catch (IOException ioex) {
            gui.recibirMensaje("IOException: " + ioex.getMessage());
        } catch (PeerGroupException pgex) {
            gui.recibirMensaje("PGException: " + pgex.getMessage());
        }
    }

    public void terminarJXTA() {
        gui.recibirMensaje("Terminando Red JXTA");
        manager.stopNetwork();
        gui.recibirMensaje("Red JXTA Terminada");
    }

    public PipeAdvertisement crearPipeAdvertisement() {
        PipeID pipeID = null;
        pipeID = (PipeID) IDFactory.newPipeID(netPeerGroup.getPeerGroupID());
        PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
        advertisement.setPipeID(pipeID);
        advertisement.setType(PipeService.UnicastType);
        //advertisement.setType(PipeService.PropagateType);
        advertisement.setName("Pipe Chat");
        advertisement.setDescription("JXTA Chat Pipe");
        return advertisement;
    }

    public PipeAdvertisement crearPipeAdvertisementFile() {
        try {
            FileInputStream file = new FileInputStream("advertisements/ChatService.adv");
            XMLDocument xml = (XMLDocument) StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, file);
            PipeAdvertisement advertisement = (PipeAdvertisement)AdvertisementFactory.newAdvertisement(xml);
            return advertisement;
        } catch (IOException ioex) {
            gui.recibirMensaje("IOException: " + ioex.getMessage());
        }
        return null;
    }
    
    public void publicarServicio() {
        try {
            // Crear el ModuleClassAdvertisement
            moduleClassAdvertisement = (ModuleClassAdvertisement) AdvertisementFactory.newAdvertisement(ModuleClassAdvertisement.getAdvertisementType());
            ModuleClassID mcID = IDFactory.newModuleClassID();
            moduleClassAdvertisement.setModuleClassID(mcID);
            moduleClassAdvertisement.setName("jxta-chat-mca");
            moduleClassAdvertisement.setDescription("JXTA Chat");
            discoveryService.publish(moduleClassAdvertisement);
            discoveryService.remotePublish(moduleClassAdvertisement);
            // Crear el Pipe Advertisements
            pipeAdvertisement = this.crearPipeAdvertisement();
            // Crear el ModuleSpecAdvertisement
            moduleSpecAdvertisement = (ModuleSpecAdvertisement) AdvertisementFactory.newAdvertisement(ModuleSpecAdvertisement.getAdvertisementType());
            ModuleSpecID msID = IDFactory.newModuleSpecID(mcID);
            moduleSpecAdvertisement.setModuleSpecID(msID);
            moduleSpecAdvertisement.setVersion("Version 1.0");
            moduleSpecAdvertisement.setName(nombreServicio);
            moduleSpecAdvertisement.setDescription("JXTA Chat Spec");
            moduleSpecAdvertisement.setCreator("almunoz");
            moduleSpecAdvertisement.setSpecURI("http://jxta.org/chat");
            moduleSpecAdvertisement.setPipeAdvertisement(pipeAdvertisement);
            discoveryService.publish(moduleSpecAdvertisement);
            discoveryService.remotePublish(moduleSpecAdvertisement);
            // Crear el canal de comunicacion (inputPipe)
            PipeInputListener pipeInputListener = new PipeInputListener();
            inputPipe = pipeService.createInputPipe(pipeAdvertisement, pipeInputListener);
            // Display the advertisement as a plain text document.
            StructuredTextDocument doc = (StructuredTextDocument) moduleSpecAdvertisement.getDocument(MimeMediaType.XMLUTF8);
            StringWriter out = new StringWriter();
            doc.sendToWriter(out);
            gui.recibirMensaje(out.toString());
            out.close();
        } catch (IOException ioex) {
            gui.recibirMensaje("IOException: " + ioex.getMessage());
        }
    }

    public void buscarServicio() {
        try {
            Enumeration en = discoveryService.getLocalAdvertisements(DiscoveryService.ADV,
                                                                     nombreCampo, nombreServicio);
            if ( (en != null) && en.hasMoreElements()) {
                /*ModuleSpecAdvertisement moduleSpec = (ModuleSpecAdvertisement)en.nextElement();
                outputPipeAdvertisement = moduleSpec.getPipeAdvertisement();
                // Crear el canal de comunicacion (outputPipe)
                outputPipe = crearOuputPipe(outputPipeAdvertisement);
                */
                crearOuputPipes(en);
            } else {
                BusquedaListener busquedaListener = new BusquedaListener();
                String peerId = null; // Busca todos los peers
                int numeroAdvertisements = 10;
                discoveryService.getRemoteAdvertisements(peerId, DiscoveryService.ADV,
                                                         nombreCampo, nombreServicio,
                                                         numeroAdvertisements, busquedaListener);
            }
        } catch (IOException ioex) {
            gui.recibirMensaje("IOException: " + ioex.getMessage());
        }        
    }

    public OutputPipe crearOuputPipe(PipeAdvertisement advertisement) {
        OutputPipe pipe = null;
        try {
            pipe = pipeService.createOutputPipe(advertisement, tiempoEspera);
        } catch (IOException ioex) {
            gui.recibirMensaje("IOException: " + ioex.getMessage());
        }
        return pipe;
    }

    public void crearOuputPipes(Enumeration en) {
        if (en != null) {
            outputPipes = new Vector<OutputPipe>(0, 10);
            while (en.hasMoreElements()) {
                ModuleSpecAdvertisement moduleSpec = (ModuleSpecAdvertisement) en.nextElement();
                outputPipeAdvertisement = moduleSpec.getPipeAdvertisement();
                // Crear el canal de comunicacion (outputPipe) y agregarlo a la lista de canales
                outputPipes.add(crearOuputPipe(outputPipeAdvertisement));
            }
        }
    }

    public void enviarMensaje(String mensaje) {
        try {
            Message message = new Message();
            StringMessageElement mensajeElement = new StringMessageElement("mensaje", mensaje, null);
            message.addMessageElement(mensajeElement);
            //outputPipe.send(message);
            for (OutputPipe out : outputPipes) {
                if (out != null) {
                    gui.recibirMensaje("PIPE: " + out.toString());
                    out.send(message);
                }
            }
        } catch (IOException ioex) {
            gui.recibirMensaje("IOException: " + ioex.getMessage());
        }
    }    

    // Clase para escuchar los mensajes de entrada
    public class PipeInputListener implements PipeMsgListener {
        @Override
        public void pipeMsgEvent(PipeMsgEvent pmevent) {
            Message message = pmevent.getMessage();
            // Todos los elementos del mensaje
            ElementIterator elementos = message.getMessageElements();
            if (!elementos.hasNext()) {
                gui.recibirMensaje("El mensaje no tiene elementos.");
                return;
            }
            while (elementos.hasNext()) {
                MessageElement elemento = elementos.next();
                gui.recibirMensaje("Elemento: " + elemento.toString());
            }
            // Por nombre del elemento
            MessageElement mensajeElement  = message.getMessageElement("mensaje");
            if (mensajeElement.toString() != null) {
                gui.recibirMensaje("Elemento solo: " + mensajeElement.toString());
            } else {
                gui.recibirMensaje("No se encuentra el elemento <mensaje>.");
            }
        }
    }

    public class BusquedaListener implements DiscoveryListener {
        @Override
        public void discoveryEvent(DiscoveryEvent devent) {
            gui.recibirMensaje("Found Remote Advertisement...\n");
            DiscoveryResponseMsg message = devent.getResponse();
            Enumeration en = message.getAdvertisements();// Responses();
            if ( (en != null) && en.hasMoreElements()) {
                /*ModuleSpecAdvertisement moduleSpec = (ModuleSpecAdvertisement)en.nextElement();
                outputPipeAdvertisement = moduleSpec.getPipeAdvertisement();
                // Crear el canal de comunicacion (outputPipe)
                outputPipe = crearOuputPipe(outputPipeAdvertisement);
                */
                crearOuputPipes(en);
            }            
        }
    }
    
}
