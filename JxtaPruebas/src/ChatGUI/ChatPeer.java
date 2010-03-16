
package ChatGUI;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredTextDocument;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupFactory;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeService;
import net.jxta.platform.ModuleClassID;
import net.jxta.platform.ModuleSpecID;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.ModuleClassAdvertisement;
import net.jxta.protocol.ModuleSpecAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.protocol.PipeAdvertisement;

/**
 *
 * @author almunoz
 */
public class ChatPeer {
    // Red
    private NetworkManager manager;
    private NetworkConfigurator configurator;
    // Grupo
    private PeerGroup netPeerGroup;
    private PeerGroupAdvertisement netPeerGroupAdvertisement;
    // Servicios
    private DiscoveryService discoveryService;
    private PipeService pipeService;
    // Advertisements
    private PipeAdvertisement pipeAdvertisement;    
    private ModuleClassAdvertisement moduleClassAdvertisement;
    private ModuleSpecAdvertisement moduleSpecAdvertisement;
    // Canales de Comunicacion (Pipes)
    private InputPipe inputPipe;
    private OutputPipe outputPipe;

    public ChatPeer() {
        this.manager = null;
        this.configurator = null;
        this.netPeerGroup = null;
        this.netPeerGroupAdvertisement = null;
        this.discoveryService = null;
        this.pipeService = null;
        this.pipeAdvertisement = null;
        this.moduleClassAdvertisement = null;
        this.moduleSpecAdvertisement = null;
        this.inputPipe = null;
        this.outputPipe = null;
    }

    public void iniciarJXTA() {
        try {
            System.out.println("Iniciando Red JXTA");
            manager = new NetworkManager(NetworkManager.ConfigMode.ADHOC, "jxta-chat");
            manager.startNetwork();
            // Grupo
            netPeerGroup = manager.getNetPeerGroup();
            netPeerGroupAdvertisement = netPeerGroup.getPeerGroupAdvertisement();
            // Servicios
            discoveryService = netPeerGroup.getDiscoveryService();
            pipeService = netPeerGroup.getPipeService();
            // Advertisements
            pipeAdvertisement = this.crearPipeAdvertisement();
            
            System.out.println("Red JXTA Iniciada");
        } catch (IOException ioex) {
            System.out.println("IOException: " + ioex.getMessage());
        } catch (PeerGroupException pgex) {
            System.out.println("PGException: " + pgex.getMessage());
        }
    }

    public void terminarJXTA() {
        System.out.println("Terminando Red JXTA");
        manager.stopNetwork();
        System.out.println("Red JXTA Terminada");
    }

    public PipeAdvertisement crearPipeAdvertisement() {
        PipeID pipeID = null;
        pipeID = (PipeID) IDFactory.newPipeID(netPeerGroup.getPeerGroupID());
        PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
        advertisement.setPipeID(pipeID);
        advertisement.setType(PipeService.UnicastType);
        advertisement.setName("Pipe Chat");
        return advertisement;
    }
    
    public void publicarServicio() {
        try {
            // Crear el ModuleClassAdvertisement
            moduleClassAdvertisement = (ModuleClassAdvertisement) AdvertisementFactory.newAdvertisement(ModuleClassAdvertisement.getAdvertisementType());
            ModuleClassID mcID = IDFactory.newModuleClassID();
            moduleClassAdvertisement.setModuleClassID(mcID);
            moduleClassAdvertisement.setName("JXTAMCA:jxta-chat");
            moduleClassAdvertisement.setDescription("JXTA Chat");
            discoveryService.publish(moduleClassAdvertisement);
            discoveryService.remotePublish(moduleClassAdvertisement);
            // Crear el ModuleSpecAdvertisement
            moduleSpecAdvertisement = (ModuleSpecAdvertisement) AdvertisementFactory.newAdvertisement(ModuleSpecAdvertisement.getAdvertisementType());
            ModuleSpecID msID = IDFactory.newModuleSpecID(mcID);
            moduleSpecAdvertisement.setModuleSpecID(msID);
            moduleSpecAdvertisement.setName("JXTAMSA:jxta-chat");
            moduleSpecAdvertisement.setVersion("Version 1.0");
            moduleSpecAdvertisement.setSpecURI("http://www.jxta.org/chat");
            moduleSpecAdvertisement.setPipeAdvertisement(pipeAdvertisement);
            discoveryService.publish(moduleSpecAdvertisement);
            discoveryService.remotePublish(moduleSpecAdvertisement);
            // Crear el canal de comunicacion (inputPipe)
            inputPipe = pipeService.createInputPipe(pipeAdvertisement);
            // Display the advertisement as a plain text document.
            StructuredTextDocument doc = (StructuredTextDocument) moduleSpecAdvertisement.getDocument(MimeMediaType.XMLUTF8);
            StringWriter out = new StringWriter();
            doc.sendToWriter(out);
            System.out.println(out.toString());
            out.close();
        } catch (IOException ioex) {
            System.out.println("IOException: " + ioex.getMessage());
        }
    }
    
}
