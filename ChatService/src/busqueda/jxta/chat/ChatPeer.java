
package busqueda.jxta.chat;

import gui.ChatGUI;
import java.io.IOException;
import net.jxta.document.Advertisement;
import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;

/**
 *
 * @author almunoz
 */
public class ChatPeer {
    // Datos del Servicio
    public static String BUSQUEDA = "Name";
    public static String TIPO_SERVICIO = "chat";
    public static String DESCRIPCION_SERVICIO = "chat-descripcion";
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
    private PeerGroup netPeerGroup;
        
    public ChatPeer(ChatGUI chatGUI) {
        this.gui = chatGUI;
        this.manager = null;
        this.configurator = null;
        this.servidor = null;
        this.cliente = null;
        this.puertoTCP = 9701;
        this.netPeerGroup = null;
    }

    public PeerGroup getNetPeerGroup() {
        return netPeerGroup;
    }

    public void iniciarJXTA() {
        iniciarJXTA("9701");
    }
    
    public void iniciarJXTA(String port) {
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
            servidor = new ChatServidor(this);
            servidor.publicarAdvertisement(ChatPeer.TIPO_SERVICIO, ChatPeer.DESCRIPCION_SERVICIO);
            // Buscar el servicio de Chat
            cliente = new ChatCliente(this, servidor.getPipeAdvertisement());
            cliente.buscarAdvertisement(ChatPeer.BUSQUEDA, ChatPeer.TIPO_SERVICIO);
        } catch (PeerGroupException pgex) {
            gui.mostrarMensaje("PeerGroupException: " + pgex.getMessage());
        } catch (IOException ioex) {
            gui.mostrarMensaje("IOException: " + ioex.getMessage());
        }
        
    }

    public void terminarJXTA() throws IOException {
        servidor.despublicarAdvertisement();
        manager.stopNetwork();
    }

    public void publicarAdvertisement(String nombre, String descripcion) {
        try {
            servidor.publicarAdvertisement(nombre, descripcion);
        } catch (IOException ioex) {
            gui.mostrarMensaje("IOException: " + ioex.getMessage());
        }
    }

    public void buscarAdvertisement(String nombre) {
        try {
            cliente.buscarAdvertisement(ChatPeer.BUSQUEDA, nombre);
        } catch (IOException ioex) {
            gui.mostrarMensaje("IOException: " + ioex.getMessage());
        }
    }

    public void enviarMensaje(String mensaje) {
        try {
            cliente.enviarMensaje(mensaje);
        } catch (IOException ioex) {
            gui.mostrarMensaje("IOException: " + ioex.getMessage());
        }
    }

    public void mostrarMensaje(String mensaje) {
        gui.mostrarMensaje(mensaje);
    }

    public void mostrarAdvertisements() {
        servidor.mostrarInputPipeAdvs();
        cliente.mostrarOutputPipeAdvs();
    }

    public void mostrarAdvertisement(Advertisement adv) {
        gui.mostrarMensaje("Advertisement\n");
        gui.mostrarMensaje(adv.toString());
    }
    
}
