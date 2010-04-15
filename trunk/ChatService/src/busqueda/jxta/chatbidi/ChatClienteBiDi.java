
package busqueda.jxta.chatbidi;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.Timer;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.util.JxtaBiDiPipe;

/**
 *
 * @author almunoz
 */
public class ChatClienteBiDi {
    // El objeto del Chat
    private ChatBiDi chat;
    // BiDiPipe Advertisement
    private PipeAdvertisement pipeAdvertisement;
    // BiDiPipe
    private JxtaBiDiPipe pipe;
    // Timer, para buscar advertisements remotos cada cierto tiempo
    private Timer timerBusqueda;

    public ChatClienteBiDi(ChatBiDi chat) {
        this.chat = chat;
        this.pipeAdvertisement = ChatBiDi.getBiDiPipeAdvertisement();
        TimerBusquedaListener timerListener = new TimerBusquedaListener();
        this.timerBusqueda = new Timer(ChatBiDi.DELAY_BUSQUEDA, timerListener);
        iniciarBusquedaServidor();
    }

    public void detener() {
        terminarBusquedaServidor();
        cerrarPipes();
        System.out.println("Cliente de chat terminado.");
    }

    private void iniciarBusquedaServidor() {
        System.out.print("Buscando servidor...");
        System.out.flush();
        cerrarPipes();
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
        try {
            if (pipe != null) {
                pipe.close();
            }
        } catch (IOException ex) {
            System.out.println("IOException : No se pudo cerrar el BiDiPipe.");
        }
    }

    /* Busca el JxtaServerPipe del BiDiPipe */
    private void buscarServidor() {
        try {
            System.out.print(".");
            System.out.flush();
            BiDiPipeInputListener listener = new BiDiPipeInputListener();
            this.pipe = new JxtaBiDiPipe(ChatBiDi.grupoChat, pipeAdvertisement, ChatBiDi.PIPE_TIMEOUT, listener);
            if (pipe.isBound()) {
                terminarBusquedaServidor();
            }
        } catch (IOException ex) {
            System.out.println("IOException : No se pudo buscar el BiDiPipe en el cliente.");
        }
    }

    public void enviarMensaje(String remitente, String mensaje) {
        if (pipe!=null) {
            try {
                Message message = new Message();
                StringMessageElement remitenteElement = new StringMessageElement("remitente", remitente, null);
                StringMessageElement mensajeElement = new StringMessageElement("mensaje", mensaje, null);
                StringMessageElement clienteElement = new StringMessageElement("cliente", pipe.getPipeAdvertisement().toString(), null);
                message.addMessageElement(remitenteElement);
                message.addMessageElement(mensajeElement);
                message.addMessageElement(clienteElement);
                pipe.sendMessage(message);
            } catch (IOException ex) {
                System.out.println("IOException: No se puede enviar el mensaje");
                terminarBusquedaServidor();
                iniciarBusquedaServidor();
            }
        } else {
            System.out.println("El BiDiPipe esta desconectado.");
            terminarBusquedaServidor();
            iniciarBusquedaServidor();
        }
    }
    
    // Clase para escuchar los mensajes de entrada
    private class BiDiPipeInputListener implements PipeMsgListener {
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
                try {
                    pipe.close();
                } catch (IOException ex) {
                    System.out.println("IOException : No se pudo cerrar el BiDiPipe.");
                } finally {
                    iniciarBusquedaServidor();
                }
            } else {
                chat.mostrarMensajeChat("Error", "No se pudo mostrar el mensaje");
            }
        }
    }

    public class TimerBusquedaListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            buscarServidor();
        }
    }

    public String getAdvertisements() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("=================================\n");
        buffer.append("Cliente: BiDiPipe Advertisement\n");
        buffer.append("\n");
        buffer.append(pipe.getPipeAdvertisement().toString());
        buffer.append("\n");
        buffer.append("Cliente: BiDiPipe Remote Advertisement\n");
        buffer.append("\n");
        buffer.append(pipe.getRemotePipeAdvertisement().toString());
        buffer.append("\n");
        buffer.append("=================================\n");
        buffer.append("\n");
        return buffer.toString();
    }

}
