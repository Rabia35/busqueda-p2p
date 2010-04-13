
package busqueda.jxta;

import busqueda.JXTACommunicator;
//import busqueda.jxta.chat.Chat;
import busqueda.jxta.chatnuevo.ChatNuevo;
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
    public static final String ATRIBUTO_BUSQUEDA = "Name";
    // Timeut de los OutputPipes
    public static final long TIMEOUT = 5 * 1000; // 5 segundos
    // Tiempo de Busqueda
    public static final int TIEMPO_BUSQUEDA = 10 * 1000; // 10 segundos
    // JXTA Communicator
    private JXTACommunicator jxtaCommunicator;
    // Red
    private NetworkManager manager;
    private NetworkConfigurator configurator;
    private String puerto;
    // Grupo
    private PeerGroup netPeerGroup;
    // Chat
    private ChatNuevo chat;
    // Servidor de chat
    private boolean server;

    public PeerBusqueda(JXTACommunicator jxtaCommunicator) {
        this.jxtaCommunicator = jxtaCommunicator;
        this.manager = null;
        this.configurator = null;
        this.puerto = "9701";
        this.netPeerGroup = null;
        this.chat = null;
        this.server = false;
    }

    /**
     * @return the netPeerGroup
     */
    public PeerGroup getNetPeerGroup() {
        return netPeerGroup;
    }

    private void configurarJXTA(String puerto, boolean server) throws IOException {
        // Puerto
        this.puerto = puerto;
        // Servidor
        this.server = server;
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

    public void iniciarJXTA(boolean server) {
        iniciarJXTA(puerto, server);
    }

    public void iniciarJXTA(String puerto, boolean server) {
        try {
            // Configurar JXTA
            configurarJXTA(puerto, server);
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
        detenerChat();
        manager.stopNetwork();
    }
    
    /* Para el chat*/

    public void iniciarChat(String nombre, String descripcion) {
        try {
            chat = new ChatNuevo(this);
            chat.iniciar(nombre, descripcion, this.server);
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
        }
    }

    public void detenerChat() {
        chat.terminar(this.server);
    }

    public void mostrarMensajeChat(String remitente, String mensaje) {
        try {
            jxtaCommunicator.mostrarMensajeChat(remitente, mensaje);
        } catch (StaleProxyException ex) {
            System.out.println("StaleProxyException: " + ex.getMessage());
        }
    }

    public void enviarMensajeChat(String remitente, String mensaje) {
        try {
            chat.enviarMensaje(remitente, mensaje);
        } catch (IOException ioex) {
            System.out.println("IOException: " + ioex.getMessage());
        }
    }

    public String getAdvertisementsChat() {
        return chat.getAdvertisements();
    }

}
