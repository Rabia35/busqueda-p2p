
package busqueda.jxta;

import busqueda.jxta.chat.Chat;
import jade.wrapper.StaleProxyException;
import java.io.IOException;
import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;

/**
 *
 * @author almunoz
 */
public class PeerBusqueda {
    // Datos del Servicio
    public static String BUSQUEDA = "Name";
    // JXTA Manager
    private JXTAManager jxtaManager;
    // Red
    private NetworkManager manager;
    private NetworkConfigurator configurator;
    private String puerto;
    // Grupo
    private PeerGroup netPeerGroup;
    // Chat
    private Chat chat;


    public PeerBusqueda(JXTAManager jxtaManager) {
        this.jxtaManager = jxtaManager;
        this.manager = null;
        this.configurator = null;
        this.puerto = "9701";
        this.netPeerGroup = null;
        this.chat = new Chat(this);
    }

    /**
     * @return the netPeerGroup
     */
    public PeerGroup getNetPeerGroup() {
        return netPeerGroup;
    }

    public void configurarJXTA(String puerto) throws IOException {
        // Puerto
        this.puerto = puerto;
        // Configurar el Nodo dentro de la Red JXTA
        manager = new NetworkManager(NetworkManager.ConfigMode.EDGE, "peer-busqueda");
        configurator = manager.getConfigurator();
        // Configuracion TCP
        configurator.setTcpEnabled(true);
        configurator.setTcpPort(Integer.valueOf(this.puerto));
        configurator.setTcpOutgoing(true);
        configurator.setTcpIncoming(true);
        configurator.setUseMulticast(true);
        // Configuracion HTTP
        configurator.setHttpEnabled(true);
        configurator.setHttpOutgoing(true);
    }

    public void iniciarJXTA(String puerto) {
        try {
            // Configurar JXTA
            configurarJXTA(puerto);
            // Iniciar la Red
            manager.startNetwork();
            // Grupo
            netPeerGroup = manager.getNetPeerGroup();
            //netPeerGroup = PeerGroupFactory.newNetPeerGroup();
        } catch (PeerGroupException ex) {
            System.out.println("PeerGroupException: " + ex.getMessage());

        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
        }
    }

    public void terminarJXTA() {
        manager.stopNetwork();
    }

    public void iniciarChat(String nombre, String descripcion) {
        try {
            chat.iniciarServidor(nombre, descripcion);
            chat.iniciarCliente(PeerBusqueda.BUSQUEDA, nombre);
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
        }
    }

    public void detenerChat() {
        try {
            chat.terminarServidor();
            chat.terminarCliente();
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
        }
    }

    public void mostrarMensajeChat(String mensaje) {
        try {
            jxtaManager.mostrarMensajeChat(mensaje);
        } catch (StaleProxyException ex) {
            System.out.println("StaleProxyException: " + ex.getMessage());
        }
    }

    public void enviarMensajeChat(String mensaje) {
        try {
            chat.enviarMensaje(mensaje);
        } catch (IOException ioex) {
            System.out.println("IOException: " + ioex.getMessage());
        }
    }

    public String getAdvertisementsChat() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(chat.getAdvertisements());
        buffer.append("\n");
        return buffer.toString();
    }

}
