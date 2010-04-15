
package busqueda.jxta.chatbidi;

import busqueda.jxta.UtilidadesJXTA;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Vector;
import javax.swing.Timer;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.util.JxtaBiDiPipe;
import net.jxta.util.JxtaServerPipe;

/**
 *
 * @author almunoz
 */
public class ChatServidorBiDi {
    // Peer
    private ChatBiDi chat;
    // BiDiPipe Advertisement
    private PipeAdvertisement pipeAdvertisement;
    // JXTA Server Pipe
    private JxtaServerPipe serverBiDiPipe;
    // BiDiPipe
    //private JxtaBiDiPipe pipe;
    private BiDiPipeInputListener pipeListener;
    // La lista de clientes, Canales de Comunicacion (BiDiPipes)
    private Vector<JxtaBiDiPipe> clientes;
    // Timer, para buscar advertisements remotos cada cierto tiempo
    private Timer timerConexion;
    
    public ChatServidorBiDi(ChatBiDi chat) {
        this.chat = chat;
        this.clientes = new Vector<JxtaBiDiPipe>(0, 10);
        this.pipeAdvertisement = ChatBiDi.getBiDiPipeAdvertisement();
        TimerConexionListener timerListener = new TimerConexionListener();
        this.pipeListener = new BiDiPipeInputListener();
        this.timerConexion = new Timer(ChatBiDi.DELAY_BUSQUEDA, timerListener);
        this.serverBiDiPipe = crearServerPipe();
        iniciarConexiones();
    }

    private JxtaServerPipe crearServerPipe() {
        try {
            JxtaServerPipe myPipe = new JxtaServerPipe(ChatBiDi.grupoChat, pipeAdvertisement, 1, 1 * 1000);
            System.out.println("JxtaServerPipe creado.");
            return myPipe;
        } catch (IOException ex) {
            System.out.println("IOException : No se pudo crear el JxtaServerPipe.");
            cerrarPipes();
            return null;
        }
    }

    private void cerrarPipes() {
        try {
            if (serverBiDiPipe != null) {
                serverBiDiPipe.close();
            }
            for (JxtaBiDiPipe myPipe : clientes) {
                if (myPipe!=null && myPipe.isBound()) {
                    myPipe.close();
                }
            }
        } catch (IOException ex) {
            System.out.println("IOException : No se pudo cerrar el BiDiPipe.");
        }
    }

    public void detener() {
        enviarMensajeCerrar();
        terminarConexiones();
        cerrarPipes();
        System.out.println("Servidor de chat terminado.");
    }

    private void iniciarConexiones() {
        System.out.print("Esperando conexiones al JxtaServerPipe...");
        System.out.flush();
        if (!timerConexion.isRunning()) {
            timerConexion.start();
        }
    }

    public void terminarConexiones() {
        if (timerConexion.isRunning()) {
            timerConexion.stop();
        }
    }

    /* Busca el JxtaServerPipe del BiDiPipe */
    private void buscarConexion() {
        try {
            System.out.print(".");
            System.out.flush();
            JxtaBiDiPipe myPipe = this.serverBiDiPipe.accept();
            if (myPipe != null) {
                if (buscarPipe(myPipe) == false) {
                    System.out.println("Agregando un cliente de chat.");
                    //BiDiPipeInputListener listener = new BiDiPipeInputListener();
                    myPipe.setMessageListener(pipeListener);
                    clientes.add(myPipe);
                }
            }
        } catch (IOException ex) {
            System.out.println("IOException : No se acepto la conexion del BiDiPipe en el servidor.");
        }
    }

    private void enviarMensajeCerrar() {
        // Envia el mensaje a todos los clientes
        for (JxtaBiDiPipe myPipe : clientes) {
            if (myPipe != null && myPipe.isBound()) {
                Message msg = new Message();
                StringMessageElement cerrarElement = new StringMessageElement("cerrar", "Cerrar BiDiPipe", null);
                msg.addMessageElement(cerrarElement);
                try {
                    myPipe.sendMessage(msg);
                } catch (IOException ex) {
                    System.out.println("IOException: No se pudo enviar el mensaje");
                }
            }
        }
    }

    private boolean buscarPipe(JxtaBiDiPipe pipeBusqueda) {
        for (JxtaBiDiPipe myPipe : clientes) {
            if (myPipe.getPipeAdvertisement().getID() == pipeBusqueda.getPipeAdvertisement().getID() &&
                myPipe.getRemotePipeAdvertisement().getID() == pipeBusqueda.getRemotePipeAdvertisement().getID()) {
                return true;
            }
        }
        return false;
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
            MessageElement cliente  = message.getMessageElement("cliente");
            if (remitente != null && mensaje != null && cliente != null) {
                chat.mostrarMensajeChat(remitente.toString(), mensaje.toString());
            } else {
                chat.mostrarMensajeChat("Error", "No se pudo mostrar el mensaje");
                return;
            }
            // Envia el mensaje a todos los clientes
            for (JxtaBiDiPipe myPipe : clientes) {
                if (myPipe != null && myPipe.isBound()) {
                    Message msg = new Message();
                    StringMessageElement remitenteElement = new StringMessageElement("remitente", remitente.toString(), null);
                    StringMessageElement mensajeElement = new StringMessageElement("mensaje", mensaje.toString(), null);
                    msg.addMessageElement(remitenteElement);
                    msg.addMessageElement(mensajeElement);
                    try {
                        myPipe.sendMessage(msg);
                    } catch (IOException ex) {
                        System.out.println("IOException: No se pudo enviar el mensaje");
                    }
                }
            }
        }
    }

    public class TimerConexionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            buscarConexion();
        }
    }

    public String getAdvertisements() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("=================================\n");
        buffer.append("Servidor: ServerBiDiPipe Advertisement\n");
        buffer.append("\n");
        buffer.append(serverBiDiPipe.getPipeAdv().toString());
        buffer.append("\n");
        buffer.append("=================================\n");

        buffer.append("\n");
        return buffer.toString();
    }

}
