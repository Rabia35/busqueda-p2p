
package ChatService;

import java.io.IOException;
import java.io.StringWriter;
import net.jxta.document.Advertisement;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredTextDocument;
import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;

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
    // Servidor y Cliente de Chat
    private ChatServidor servidor;
    private ChatCliente cliente;
    // Puerto TCP
    private int puertoTCP;
    // Grupo
    private static PeerGroup netPeerGroup;
    // Datos del Servicios
    private String nombreBusqueda;
    private String nombreServicio;
        
    public ChatPeer(ChatGUI chatGUI) {
        this.gui = chatGUI;
        this.manager = null;
        this.configurator = null;
        this.servidor = null;
        this.cliente = null;
        this.puertoTCP = 9701;
        netPeerGroup = null;
        this.nombreBusqueda = "Name";
        this.nombreServicio = "jxta-chat";
    }

    public void setPuerto(int puerto) {
        this.puertoTCP = puerto;
    }
    
    public void iniciarJXTA() {
        try {
            // Configurar el Nodo dentro de la Red JXTA
            manager = new NetworkManager(NetworkManager.ConfigMode.RENDEZVOUS, "chat-nodo");
            configurator = manager.getConfigurator();
            // Configuracion TCP
            configurator.setTcpEnabled(true);
            configurator.setTcpPort(puertoTCP);
            configurator.setTcpOutgoing(true);
            configurator.setTcpIncoming(true);
            configurator.setUseMulticast(true);
            // Configuracion HTTP
            configurator.setHttpEnabled(true);
            configurator.setHttpOutgoing(true);
            // Iniciar la Red
            manager.startNetwork();
            // Grupo
            netPeerGroup = manager.getNetPeerGroup();
            //netPeerGroup = PeerGroupFactory.newNetPeerGroup();
            // Publicar el servicio de Chat
            this.servidor = new ChatServidor(this, netPeerGroup, nombreServicio);
            this.servidor.publicarServicio();
            // Buscar el servicio de Chat
            this.cliente = new ChatCliente(this, netPeerGroup, nombreBusqueda, nombreServicio);
            this.cliente.buscarServicio();
        } catch (PeerGroupException pgex) {
            gui.recibirMensaje("PeerGroupException: " + pgex.getMessage());
        } catch (IOException ioex) {
            gui.recibirMensaje("IOException: " + ioex.getMessage());
        }
        
    }

    public void terminarJXTA() throws IOException {
        servidor.despublicarServicio();
        manager.stopNetwork();
    }

    public void enviarMensaje(String mensaje) {
        try {
            this.cliente.enviarMensaje(mensaje);
        } catch (IOException ioex) {
            gui.recibirMensaje("IOException: " + ioex.getMessage());
        }
    }

    public void recibirMensaje(String mensaje) {
        gui.recibirMensaje(mensaje);
    }

    public void mostrarAdvs() {
        servidor.mostrarInputPipeAdvs();
        cliente.mostrarOutputPipeAdvs();
    }

    public void mostrarAdvertisement(Advertisement adv) {
        gui.recibirMensaje("Advertisement\n");
        gui.recibirMensaje(adv.toString());
    }

    public void displayAdvertisement(Advertisement adv) {
        try {
            // Display the advertisement as a plain text document.
            StructuredTextDocument doc = (StructuredTextDocument) adv.getDocument(MimeMediaType.XMLUTF8);
            StringWriter out = new StringWriter();
            doc.sendToWriter(out);
            gui.recibirMensaje(out.toString());
            out.close();
        } catch (IOException ioex) {
            gui.recibirMensaje("IOException: " + ioex.getMessage());
        }
    }

    

}
