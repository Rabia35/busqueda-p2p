
package busqueda.jxta.chat;

import jade.wrapper.StaleProxyException;
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
import net.jxta.pipe.OutputPipe;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PipeAdvertisement;

/**
 *
 * @author almunoz
 */
public class ChatCliente {
    // ChatPeer
    private ChatPeer peerChat;
    // Advertisement para el pipe de los mensajes
    private PipeAdvertisement pipeAdvertisement;
    // Canales de Comunicacion (Pipes)
    private Vector<OutputPipe> outputPipes;
    private long tiempoEspera;
    // Timer, para buscar advertisements remotos cada cierto tiempo
    private static int TIEMPO = 30000; // 30 segundos
    private Timer timerBusqueda;
    // Advertisement para el pipe de los mensajes
    private PipeAdvertisement pipeServidor;

    public ChatCliente(ChatPeer peerChat, PipeAdvertisement pipeServidor) {
        this.peerChat = peerChat;
        this.pipeAdvertisement = null;
        this.outputPipes = null;
        this.tiempoEspera = 5000L; // 5 segundos
        this.timerBusqueda = null;
        this.pipeServidor = pipeServidor;
    }

    private OutputPipe crearOuputPipe(PipeAdvertisement advertisement) throws IOException {
        OutputPipe pipe = null;
        pipe = peerChat.getNetPeerGroup().getPipeService().createOutputPipe(advertisement, tiempoEspera);
        return pipe;
    }

    public void buscarAdvertisement(String busqueda, String nombre) throws IOException {
        buscarAdvertisementChat(busqueda, nombre);
        this.timerBusqueda = new Timer(TIEMPO, new TimerBusquedaListener(busqueda, nombre));
        if (!timerBusqueda.isRunning()) {
            timerBusqueda.start();
        }
    }

    private void buscarAdvertisementChat(String busqueda, String nombre) throws IOException {
        this.outputPipes = new Vector<OutputPipe>(0,10);
        // Busca advertisements remotos y actualiza los advertisements
        // locales, de eso se encarga el metodo getRemoteAdvertisements()
        BusquedaListener busquedaListener = new BusquedaListener();
        String peerId = null; // Busca todos los peers
        int numeroAdvertisements = 1;
        peerChat.getNetPeerGroup().getDiscoveryService().getRemoteAdvertisements(peerId, DiscoveryService.ADV, busqueda, nombre, numeroAdvertisements, busquedaListener);
        // Busca advertisements locales
        Enumeration<Advertisement> en = peerChat.getNetPeerGroup().getDiscoveryService().getLocalAdvertisements(DiscoveryService.ADV, busqueda, nombre);
        if (en != null) {
            while (en.hasMoreElements()) {
                pipeAdvertisement = (PipeAdvertisement) en.nextElement();
                if (pipeServidor.getID() != pipeAdvertisement.getID()) {
                    // Crear el canal de comunicacion (outputPipe) y agregarlo al vector
                    OutputPipe outputPipe = crearOuputPipe(pipeAdvertisement);
                    if (outputPipe != null) {
                        outputPipes.addElement(outputPipe);
                    }
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
                System.out.println("El OutputPipe es null, no se puede enviar el mensaje");
            }
        }
    }

    public String getOutputPipeAdvs() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("======================\n");
        buffer.append("OutPipe Advertisements\n");
        buffer.append("\n");
        for (OutputPipe outputPipe : outputPipes) {
            Advertisement adv = outputPipe.getAdvertisement();
            buffer.append(adv.toString());
            buffer.append("\n");
        }
        buffer.append("======================");
        buffer.append("\n");
        return buffer.toString();
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
        private final String busqueda;
        private final String nombre;

        public TimerBusquedaListener(String busqueda, String nombre) {
            this.busqueda = busqueda;
            this.nombre = nombre;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                buscarAdvertisementChat(busqueda, nombre);
            } catch (IOException ioex) {
                System.out.println("IOException: " + ioex.getMessage());
            }
        }
    }

}
