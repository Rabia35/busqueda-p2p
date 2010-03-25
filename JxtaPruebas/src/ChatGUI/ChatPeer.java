
package ChatGUI;

import java.io.IOException;
import java.io.StringWriter;
import net.jxta.document.Advertisement;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredTextDocument;
import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupFactory;
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
    // Servidor y CLiente de Chat
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
            //manager = new NetworkManager(NetworkManager.ConfigMode.RENDEZVOUS, "jxta-net-chat");
            /*configurator = manager.getConfigurator();
            configurator.setName("chat" + peerNumber);
            configurator.setPassword("chat-password");
            configurator.setTcpPort(puerto);
            configurator.setMode(NetworkConfigurator.RELAY_OFF);*/
            //manager.startNetwork();
            // Grupo
            //netPeerGroup = manager.getNetPeerGroup();
            netPeerGroup = PeerGroupFactory.newNetPeerGroup();
            // Publicar el servicio de Chat
            this.servidor = new ChatServidor(this, netPeerGroup, nombreServicio);
            this.servidor.publicarServicio();
            // Buscar el servicio de Chat
            this.cliente = new ChatCliente(this, netPeerGroup, nombreBusqueda, nombreServicio);
            this.cliente.buscarServicio();
        } catch (IOException ioex) {
            gui.recibirMensaje("IOException: " + ioex.getMessage());
        } catch (PeerGroupException pgex) {
            gui.recibirMensaje("PGException: " + pgex.getMessage());
        }
    }

    public void terminarJXTA() {
        //manager.stopNetwork();
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
