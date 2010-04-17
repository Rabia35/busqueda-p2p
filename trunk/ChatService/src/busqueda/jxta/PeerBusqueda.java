
package busqueda.jxta;

import busqueda.JXTACommunicator;
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
    public static final String NOMBRE = "Network JXTA-JADE";
    public static final String NOMBRE_PEER = "Peer JXTA-JADE";
    public static boolean SERVIDOR_CHAT = false;

    private JXTACommunicator jxtaCommunicator;
    // Red
    private NetworkManager manager;
    private NetworkConfigurator configurator;
    private PeerGroup netPeerGroup;
    private String puerto;
    // Chat
    private ChatNuevo chat;
    
    public PeerBusqueda(JXTACommunicator jxtaCommunicator) {
        this.jxtaCommunicator = jxtaCommunicator;
        this.manager = null;
        this.configurator = null;
        this.puerto = "9701";
        this.netPeerGroup = null;
        this.chat = null;
    }

    /**
     * @return the netPeerGroup
     */
    public PeerGroup getNetPeerGroup() {
        return netPeerGroup;
    }

    public void iniciarJXTA(boolean server) {
        iniciarJXTA(puerto, server);
    }

    public void iniciarJXTA(String puerto, boolean servidor) {
        try {
            PeerBusqueda.SERVIDOR_CHAT = servidor;
            configurarJXTA(puerto);
            // Iniciar la Red y Obtener el grupo
            netPeerGroup = manager.startNetwork();
            //netPeerGroup = PeerGroupFactory.newNetPeerGroup();
            //esperarConexionRendezvous();
        } catch (PeerGroupException ex) {
            System.out.println("PeerGroupException: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
        }
    }

    private void configurarJXTA(String puerto) throws IOException {
        this.puerto = puerto;
        // Configurar el Nodo dentro de la Red JXTA
        manager = new NetworkManager(NetworkManager.ConfigMode.EDGE, NOMBRE);
        // Crear un nuevo Peer
        // El nombre debe pasarse com parametro y ser diferente para cado nodo
        //PeerID peerID = UtilidadesJXTA.crearPeerID(NOMBRE_PEER);
        //manager.setPeerID(peerID);
        // Configuracion de la RED
        configurator = manager.getConfigurator();
        configurator.setName(NOMBRE_PEER);
        // Configuracion TCP
        configurator.setTcpPort(Integer.valueOf(this.puerto));
        configurator.setTcpEnabled(true);
        configurator.setTcpOutgoing(true);
        configurator.setTcpIncoming(true);
        configurator.setUseMulticast(true);
        // Configuracion HTTP
        configurator.setHttpEnabled(true);
        configurator.setHttpOutgoing(true);
        //configurator.setHttpIncoming(true);
        // Guardar la Configuracion
        //configurator.save();
    }

    private void esperarConexionRendezvous() {
        // Espera una conexion a un Rendezvous
        System.out.println("Esperando una conexion a un Rendezvous...");
        if (manager.waitForRendezvousConnection(1 * 1000)) {
            System.out.println("Conexion a Rendezvous establecida.");
        } else {
            System.out.println("Conexion a Rendezvous no establecida.");
        }
    }

    public void terminarJXTA() {
        chat.terminar();
        manager.stopNetwork();
    }
    
    /* Para el chat*/

    public void iniciarChat(String tipo, String descripcion) {
        try {
            chat = new ChatNuevo(this);
            chat.iniciar(tipo, descripcion);
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
        }
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
