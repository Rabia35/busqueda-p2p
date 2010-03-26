
package ChatService;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.Timer;
import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.ModuleSpecAdvertisement;
import net.jxta.protocol.PipeAdvertisement;

/**
 *
 * @author almunoz
 */
public class ChatCliente {
    // ChatPeer
    private ChatPeer chatPeer;
    // Grupo
    private PeerGroup netPeerGroup;
    // Servicios
    private String nombreBusqueda;
    private String nombreServicio;
    private DiscoveryService discoveryService;
    private PipeService pipeService;
    // Advertisement para el pipe de los mensajes
    private PipeAdvertisement pipeAdvertisement;
    // Canales de Comunicacion (Pipes)
    private Vector<OutputPipe> outputPipes;
    private long tiempoEspera;
    // Timer, para buscar advertisements remotos cada cierto tiempo
    private static int TIEMPO = 30000; // 30 segundos
    private Timer timerBusqueda;

    public ChatCliente(ChatPeer chatPeer, PeerGroup netPeerGroup, String nombreBusqueda, String nombreServicio) {
        this.chatPeer = chatPeer;
        this.netPeerGroup = netPeerGroup;
        this.nombreBusqueda = nombreBusqueda;
        this.nombreServicio = nombreServicio;
        this.discoveryService = this.netPeerGroup.getDiscoveryService();
        this.pipeService = this.netPeerGroup.getPipeService();
        this.pipeAdvertisement = null;
        this.outputPipes = new Vector<OutputPipe>(0,10);
        this.tiempoEspera = 5000L; // 5 segundos
        this.timerBusqueda = new Timer(TIEMPO, new TimerBusquedaListener());
    }

    public OutputPipe crearOuputPipe(PipeAdvertisement advertisement) throws IOException {
        OutputPipe pipe = null;
        pipe = pipeService.createOutputPipe(advertisement, tiempoEspera);
        return pipe;
    }

    public void buscarServicio() throws IOException {
        buscarServicioChat();
        if (!timerBusqueda.isRunning()) {
            timerBusqueda.start();
        }
    }

    public void buscarServicioChat() throws IOException {
        this.outputPipes = new Vector<OutputPipe>(0,10);
        // Busca advertisements remotos y actualiza los advertisements
        // locales, de eso se encarga el metodo getRemoteAdvertisements()
        BusquedaListener busquedaListener = new BusquedaListener();
        String peerId = null; // Busca todos los peers
        int numeroAdvertisements = 1;
        discoveryService.getRemoteAdvertisements(peerId, DiscoveryService.ADV, nombreBusqueda, nombreServicio, numeroAdvertisements, busquedaListener);
        // Busca advertisements locales
        Enumeration<Advertisement> en = discoveryService.getLocalAdvertisements(DiscoveryService.ADV, nombreBusqueda, nombreServicio);
        if (en != null) {
            while (en.hasMoreElements()) {
                ModuleSpecAdvertisement moduleSpec = (ModuleSpecAdvertisement) en.nextElement();
                pipeAdvertisement = moduleSpec.getPipeAdvertisement();
                // Crear el canal de comunicacion (outputPipe) y agregarlo al vector
                OutputPipe outputPipe = crearOuputPipe(pipeAdvertisement);
                if (outputPipe != null) {
                    outputPipes.addElement(outputPipe);
                }
            }
        }
    }

    public void enviarMensaje(String mensaje) throws IOException {
        Message message = new Message();
        StringMessageElement mensajeElement = new StringMessageElement("mensaje", mensaje, null);
        message.addMessageElement(mensajeElement);
        for (OutputPipe outputPipe : outputPipes) {
            if (outputPipe != null) {
                outputPipe.send(message);
            } else {
                chatPeer.recibirMensaje("El OutputPipe es null, no se puede enviar el mensaje");
            }
        }
    }

    public void mostrarOutputPipeAdvs() {
        chatPeer.recibirMensaje("======================");
        chatPeer.recibirMensaje("OutPipe Advertisements\n");
        for (OutputPipe outputPipe : outputPipes) {
            Advertisement adv = outputPipe.getAdvertisement();
            chatPeer.recibirMensaje(adv.toString());
        }
        chatPeer.recibirMensaje("======================");
    }

    /*************/
    /* Listeners */
    /*************/

    public class BusquedaListener implements DiscoveryListener {
        @Override
        public void discoveryEvent(DiscoveryEvent devent) {
            DiscoveryResponseMsg message = devent.getResponse();
            Enumeration<Advertisement> responses = message.getAdvertisements();
            if ( responses != null) {
                while (responses.hasMoreElements()) {
                    // Muestro el advertisement encontrado
                    Advertisement adv = responses.nextElement();
                    System.out.println("Se encontro un advertisement remoto " +
                                       "de tipo: " + adv.getAdvType());
                }
            }            
        }
    }

    public class TimerBusquedaListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                buscarServicioChat();
            } catch (IOException ioex) {
                chatPeer.recibirMensaje("IOException: " + ioex.getMessage());
            }
        }
    }

}
