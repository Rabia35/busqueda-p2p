
package busqueda.jxta.chatnuevo;

import busqueda.jxta.UtilidadesJXTA;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Enumeration;
import javax.swing.Timer;
import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PipeAdvertisement;

/**
 *
 * @author almunoz
 */
public class ChatClienteNuevo {
    // El objeto del Chat
    private ChatNuevo chat;
    // Servidor: Advertisement, canal y datos de busqueda
    private OutputPipe servidor;
    // Cliente: Advertisement y Canal de Comunicacion (InputPipe)
    private PipeAdvertisement inputPipeAdvertisement;
    private InputPipe inputPipe;
    // Timer, para buscar advertisements remotos cada cierto tiempo
    
    private Timer timerBusqueda;

    public ChatClienteNuevo(ChatNuevo chat) {
        this.chat = chat;
        this.servidor = null;
        this.inputPipeAdvertisement = UtilidadesJXTA.crearPipeAdvertisement(chat.getGrupoChat(), ChatNuevo.NOMBRE_CLIENTE, ChatNuevo.DESCRIPCION_CLIENTE);
        PipeInputListener listener = new PipeInputListener();
        this.inputPipe = UtilidadesJXTA.crearInputPipe(chat.getGrupoChat(), inputPipeAdvertisement, listener);
        UtilidadesJXTA.publicarAdvertisement(chat.getGrupoChat(), inputPipeAdvertisement);
        TimerBusquedaListener timerListener = new TimerBusquedaListener();
        this.timerBusqueda = new Timer(UtilidadesJXTA.DELAY_BUSQUEDA, timerListener);
        iniciarBusquedaServidor();
    }

    public void detener() {
        terminarBusquedaServidor();
        cerrarPipes();
        System.out.println("Cliente de chat terminado.");
    }

    private void iniciarBusquedaServidor() {
        cerrarPipeServidor();
        System.out.print("Buscando servidor...");
        System.out.flush();
        if (!timerBusqueda.isRunning()) {
            timerBusqueda.start();
        }
    }
    
    public void terminarBusquedaServidor() {
        if (timerBusqueda.isRunning()) {
            timerBusqueda.stop();
        }
    }

    private void cerrarPipes() {
        if (inputPipe != null) {
            inputPipe.close();
        }
        if (inputPipeAdvertisement != null) {
            UtilidadesJXTA.eliminarAdvertisement(chat.getGrupoChat(), inputPipeAdvertisement);
        }
        cerrarPipeServidor();
    }

    private void cerrarPipeServidor() {
        if (servidor != null) {
            servidor.close();
            servidor = null;
        }
        Advertisement adv = null;
        Enumeration<Advertisement> en = null;
        try {
            en = chat.getGrupoChat().getDiscoveryService().getLocalAdvertisements(DiscoveryService.ADV, UtilidadesJXTA.ATRIBUTO_BUSQUEDA, ChatNuevo.NOMBRE_SERVIDOR);
        } catch (IOException ex) {
            System.out.println("IOException: No se encontraron advertisements locales");
        }
        if (en != null) {
            while (en.hasMoreElements()) {
                adv = en.nextElement();
                UtilidadesJXTA.eliminarAdvertisement(chat.getGrupoChat(), adv);
            }
        }
    }

    /* Busca advertisements remotos y actualiza los advertisements
     * locales, de eso se encarga el metodo getRemoteAdvertisements()
     */
    private void buscarAdvertisementServidor() {
        System.out.print(".");
        System.out.flush();
        PipeAdvertisement adv = null;
        BusquedaListener busquedaListener = new BusquedaListener();
        String peerId = null; // Busca todos los peers
        int numeroAdvertisements = 1; // un advertisement por Peer
        chat.getGrupoChat().getDiscoveryService().getRemoteAdvertisements(peerId, DiscoveryService.ADV, UtilidadesJXTA.ATRIBUTO_BUSQUEDA, ChatNuevo.NOMBRE_SERVIDOR, numeroAdvertisements, busquedaListener);
        Enumeration<Advertisement> en = null;
        try {
            en = chat.getGrupoChat().getDiscoveryService().getLocalAdvertisements(DiscoveryService.ADV, UtilidadesJXTA.ATRIBUTO_BUSQUEDA, ChatNuevo.NOMBRE_SERVIDOR);
        } catch (IOException ex) {
            System.out.println("IOException: No se encontraron advertisements locales");
        }
        if (en != null) {
            while (en.hasMoreElements()) {
                adv = (PipeAdvertisement) en.nextElement();
                servidor = UtilidadesJXTA.crearOuputPipe(chat.getGrupoChat(), adv);
                if (servidor != null) {
                    terminarBusquedaServidor();
                    break;
                }
            }
        }
    }

    public void enviarMensaje(String remitente, String mensaje) {
        if (servidor != null) {
            try {
                Message message = new Message();
                StringMessageElement remitenteElement = new StringMessageElement("remitente", remitente, null);
                StringMessageElement mensajeElement = new StringMessageElement("mensaje", mensaje, null);
                StringMessageElement clienteElement = new StringMessageElement("cliente", inputPipeAdvertisement.toString(), null);
                message.addMessageElement(remitenteElement);
                message.addMessageElement(mensajeElement);
                message.addMessageElement(clienteElement);
                servidor.send(message);
            } catch (IOException ex) {
                System.out.println("IOException: No se puede enviar el mensaje");
                iniciarBusquedaServidor();
            }
        } else {
            System.out.println("El OutputPipe es null");
            iniciarBusquedaServidor();
        }
    }
    
    // Clase para escuchar los mensajes de entrada
    private class PipeInputListener implements PipeMsgListener {
        @Override
        public void pipeMsgEvent(PipeMsgEvent pmevent) {
            Message message = pmevent.getMessage();
            if (!message.getMessageElements().hasNext()) {
                return;
            }
            // Por nombre del elemento
            MessageElement remitente = message.getMessageElement("remitente");
            MessageElement mensaje  = message.getMessageElement("mensaje");
            MessageElement cerrar  = message.getMessageElement("cerrar");
            if (remitente != null && mensaje != null) {
                chat.mostrarMensajeChat(remitente.toString(), mensaje.toString());
            } else if (cerrar != null) {
                PipeAdvertisement advertisement = UtilidadesJXTA.crearPipeAdvertisementFromString(cerrar.toString());
                UtilidadesJXTA.eliminarAdvertisement(chat.getGrupoChat(), advertisement);
                iniciarBusquedaServidor();
            } else {
                chat.mostrarMensajeChat("Error", "No se pudo mostrar el mensaje");
            }
        }
    }

    public class TimerBusquedaListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            buscarAdvertisementServidor();
        }
    }

    public class BusquedaListener implements DiscoveryListener {
        @Override
        public void discoveryEvent(DiscoveryEvent devent) {
            DiscoveryResponseMsg message = devent.getResponse();
            Enumeration<Advertisement> responses = message.getAdvertisements();
            if (responses != null) {
                int cont = 1;
                while (responses.hasMoreElements()) {
                    // Muestro el advertisement encontrado
                    Advertisement adv = responses.nextElement();
                    System.out.println(String.valueOf(cont) + ": Se encontro un advertisement remoto: " + adv.getID());
                    cont++;
                }
            }
        }
    }

    public String getAdvertisements() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("================================\n");
        buffer.append("Cliente: InputPipe Advertisement\n");
        buffer.append("\n");
        buffer.append(inputPipeAdvertisement.toString());
        buffer.append("\n");
        buffer.append("================================\n");
        if (servidor != null ) {
            buffer.append("Cliente: Advertisement Servidor Actual\n");
            buffer.append("\n");
            buffer.append(servidor.getAdvertisement().toString());
            buffer.append("\n");
            buffer.append("================================\n");
        }
        buffer.append("\n");
        return buffer.toString();
    }

}
