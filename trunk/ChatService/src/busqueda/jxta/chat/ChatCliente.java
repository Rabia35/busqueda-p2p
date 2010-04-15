
package busqueda.jxta.chat;

import busqueda.jxta.PeerBusqueda;
import busqueda.jxta.UtilidadesJXTA;
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
    // Peer
    private PeerBusqueda peer;
    // Advertisement del servidor local
    private PipeAdvertisement pipeAdvertisementServidor;
    // Canales de Comunicacion (Pipes)
    private Vector<OutputPipe> outputPipes;
    // Timer, para buscar advertisements remotos cada cierto tiempo
    private Timer timerBusqueda;

    public ChatCliente(PeerBusqueda peer) {
        this.peer = peer;
        this.pipeAdvertisementServidor = null;
        this.outputPipes = new Vector<OutputPipe>(0,10);
        this.timerBusqueda = null;
    }
    
    private void buscarAdvertisement(String busqueda, String nombre) throws IOException {
        buscarAdvertisementChat(busqueda, nombre);
        this.timerBusqueda = new Timer(UtilidadesJXTA.DELAY_BUSQUEDA, new TimerBusquedaListener(busqueda, nombre));
        if (!timerBusqueda.isRunning()) {
            timerBusqueda.start();
        }
    }

    /* Busca advertisements remotos y actualiza los advertisements
     * locales, de eso se encarga el metodo getRemoteAdvertisements()
     */
    private void buscarAdvertisementChat(String busqueda, String nombre) throws IOException {
        cerrarOutputPipes();
        this.outputPipes = new Vector<OutputPipe>(0,10);
        BusquedaListener busquedaListener = new BusquedaListener();
        String peerId = null; // Busca todos los peers
        int numeroAdvertisements = 1;
        peer.getNetPeerGroup().getDiscoveryService().getRemoteAdvertisements(peerId, DiscoveryService.ADV, busqueda, nombre, numeroAdvertisements, busquedaListener);
        Enumeration<Advertisement> en = peer.getNetPeerGroup().getDiscoveryService().getLocalAdvertisements(DiscoveryService.ADV, busqueda, nombre);
        if (en != null) {
            while (en.hasMoreElements()) {
                PipeAdvertisement adv = (PipeAdvertisement) en.nextElement();
                if (pipeAdvertisementServidor.getID() != adv.getID()) {
                    OutputPipe outputPipe = UtilidadesJXTA.crearOuputPipe(peer.getNetPeerGroup(), adv);
                    if (outputPipe != null) {
                        outputPipes.addElement(outputPipe);
                    }
                }
            }
        }
    }

    public void iniciar(String busqueda, String nombre, PipeAdvertisement pipeAdvertisementServidor) {
        this.pipeAdvertisementServidor = pipeAdvertisementServidor;
        try {
            buscarAdvertisement(busqueda, nombre);
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
        }
    }

    public void cerrarOutputPipes() {
        for (OutputPipe outputPipe : outputPipes) {
            if (outputPipe != null) {
                outputPipe.close();
            }
        }
    }

    public void detener() {
        cerrarOutputPipes();
        System.out.println("Cliente de chat terminado.");
    }
    
    public void enviarMensaje(String remitente, String mensaje) throws IOException {        
        for (OutputPipe outputPipe : outputPipes) {
            if (outputPipe != null) {
                Message message = new Message();
                StringMessageElement remitenteElement = new StringMessageElement("remitente", remitente, null);
                StringMessageElement mensajeElement = new StringMessageElement("mensaje", mensaje, null);
                message.addMessageElement(remitenteElement);
                message.addMessageElement(mensajeElement);
                outputPipe.send(message);
            } else {
                System.out.println("El OutputPipe es null, no se puede enviar el mensaje");
            }
        }
    }

    public class BusquedaListener implements DiscoveryListener {
        @Override
        public void discoveryEvent(DiscoveryEvent devent) {
            DiscoveryResponseMsg message = devent.getResponse();
            Enumeration<Advertisement> responses = message.getAdvertisements();
            if ( responses != null) {
                while (responses.hasMoreElements()) {
                    // Muestro el advertisement encontrado
                    Advertisement adv = responses.nextElement();
                    System.out.println("Se encontro un advertisement remoto: " + adv.getID().toString());
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

    public String getOutputPipeAdvs() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("======================\n");
        buffer.append("OutPipe Advertisements\n");
        buffer.append("\n");
        if (outputPipes != null ) {
            for (OutputPipe outputPipe : outputPipes) {
                Advertisement adv = outputPipe.getAdvertisement();
                buffer.append(adv.toString());
                buffer.append("\n");
            }
        } else {
            buffer.append("No se encontro el Pipe Advertisement.");
        }
        buffer.append("======================");
        buffer.append("\n");
        return buffer.toString();
    }

}
