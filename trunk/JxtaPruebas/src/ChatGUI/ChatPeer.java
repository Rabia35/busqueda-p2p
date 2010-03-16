
package ChatGUI;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupFactory;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
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
    private PipeAdvertisement pipeAdvertisement;
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
            pipeAdvertisement = this.getPipeAdvertisement();
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

    public PipeAdvertisement getPipeAdvertisement() {
        PipeID pipeID = null;
        pipeID = (PipeID) IDFactory.newPipeID(netPeerGroup.getPeerGroupID());
        PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
        advertisement.setPipeID(pipeID);
        advertisement.setType(PipeService.UnicastType);
        advertisement.setName("Pipe Chat");
        return advertisement;
    }

}
