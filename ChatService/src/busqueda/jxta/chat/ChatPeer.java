
package busqueda.jxta.chat;

import busqueda.jxta.JXTAManager;
import jade.wrapper.StaleProxyException;
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
    // JXTA Manager
    private JXTAManager jxtaManager;
    // Red
    private NetworkManager manager;
    private NetworkConfigurator configurator;
    // Servidor y Cliente de Chat
    private ChatServidor servidor;
    private ChatCliente cliente;
    // Puerto TCP
    private String puertoTCP;
    // Grupo
    private PeerGroup netPeerGroup;
        
    public ChatPeer(JXTAManager jxtaManager) {
        this.jxtaManager = jxtaManager;
        this.manager = null;
        this.configurator = null;
        this.servidor = null;
        this.cliente = null;
        this.puertoTCP = "9701";
        this.netPeerGroup = null;        
    }

    public PeerGroup getNetPeerGroup() {
        return netPeerGroup;
    }

    public void iniciarJXTA() {
        iniciarJXTA(puertoTCP);
    }
    
    public void iniciarJXTA(String port) {
        try {
            this.puertoTCP = port;
            // Configurar el Nodo dentro de la Red JXTA
            manager = new NetworkManager(NetworkManager.ConfigMode.RENDEZVOUS, "chat-nodo");
            configurator = manager.getConfigurator();
            // Configuracion TCP
            configurator.setTcpEnabled(true);
            configurator.setTcpPort(Integer.valueOf(this.puertoTCP));
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
            System.out.println("PeerGroupException: " + pgex.getMessage());
        } catch (IOException ioex) {
            System.out.println("IOException: " + ioex.getMessage());
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
            System.out.println("IOException: " + ioex.getMessage());
        }
    }

    public void buscarAdvertisement(String nombre) {
        try {
            cliente.buscarAdvertisement(ChatPeer.BUSQUEDA, nombre);
        } catch (IOException ioex) {
            System.out.println("IOException: " + ioex.getMessage());
        }
    }

    public void enviarMensaje(String mensaje) {
        try {
            cliente.enviarMensaje(mensaje);
        } catch (IOException ioex) {
            System.out.println("IOException: " + ioex.getMessage());
        }
    }

    public void mostrarMensaje(String mensaje) throws StaleProxyException {
        jxtaManager.mostrarMensajeChat(mensaje);
    }

    public String getAdvertisements() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(servidor.getInputPipeAdvs());
        buffer.append("\n");
        buffer.append(cliente.getOutputPipeAdvs());
        buffer.append("\n");
        return buffer.toString();
    }

    public void mostrarAdvertisement(Advertisement adv) {
        System.out.println("Advertisement\n");
        System.out.println(adv.toString());
    }
    
}
