
package busqueda.jxta.chatnuevo;

import busqueda.jxta.UtilidadesJXTA;
import java.io.IOException;
import java.util.Vector;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.protocol.PipeAdvertisement;

/**
 *
 * @author almunoz
 */
public class ChatServidorNuevo {
    // Peer
    private ChatNuevo chat;
    // Advertisement
    private PipeAdvertisement inputPipeAdvertisement;
    // Canal de Comunicacion (InputPipe)
    private InputPipe inputPipe;
    // La lista de clientes, Canales de Comunicacion (OutputPipes)
    private Vector<OutputPipe> clientes;
    
    public ChatServidorNuevo(ChatNuevo chat) {
        this.chat = chat;
        this.inputPipeAdvertisement = UtilidadesJXTA.crearPipeAdvertisement(chat.getGrupoChat(), ChatNuevo.NOMBRE_SERVIDOR, ChatNuevo.DESCRIPCION_SERVIDOR);
        PipeInputListener listener = new PipeInputListener();
        this.inputPipe = UtilidadesJXTA.crearInputPipe(chat.getGrupoChat(), inputPipeAdvertisement, listener);
        UtilidadesJXTA.publicarAdvertisement(chat.getGrupoChat(), inputPipeAdvertisement);
        this.clientes = new Vector<OutputPipe>(0, 10);
    }

    /**
     * @return the pipeAdvertisement
     */
    public PipeAdvertisement getPipeAdvertisement() {
        return inputPipeAdvertisement;
    }
    
    private void cerrarPipes() {
        if (inputPipe != null) {
            inputPipe.close();
        }
        if (inputPipeAdvertisement != null) {
            UtilidadesJXTA.eliminarAdvertisement(chat.getGrupoChat(), inputPipeAdvertisement);
        }
        for (OutputPipe outputPipe : clientes) {
            if (outputPipe != null) {
                outputPipe.close();
            }
        }
    }

    public void detener() {
        enviarMensajeCerrar();
        cerrarPipes();
        System.out.println("Servidor de chat terminado.");
    }

    private void enviarMensajeCerrar() {
        // Envia el mensaje a todos los clientes
        for (OutputPipe outputPipe : clientes) {
            if (outputPipe != null) {
                Message msg = new Message();
                StringMessageElement cerrarElement = new StringMessageElement("cerrar", inputPipeAdvertisement.toString(), null);
                msg.addMessageElement(cerrarElement);
                try {
                    outputPipe.send(msg);
                } catch (IOException ex) {
                    System.out.println("IOException: No se pudo enviar el mensaje");
                }
            }
        }
    }

    // Clase para escuchar los mensajes de entrada
    private class PipeInputListener implements PipeMsgListener {
        @Override
        public void pipeMsgEvent(PipeMsgEvent pmevent) {
            boolean encontrado = false;
            PipeAdvertisement advertisement = null;
            Message message = pmevent.getMessage();
            if (!message.getMessageElements().hasNext()) {
                return;
            }
            // Por nombre del elemento
            MessageElement remitente = message.getMessageElement("remitente");
            MessageElement mensaje  = message.getMessageElement("mensaje");
            MessageElement cliente  = message.getMessageElement("cliente");
            if (remitente != null && mensaje != null && cliente != null) {
                advertisement = UtilidadesJXTA.crearPipeAdvertisementFromString(cliente.toString());
                chat.mostrarMensajeChat(remitente.toString(), mensaje.toString());
            } else {
                chat.mostrarMensajeChat("Error", "No se pudo mostrar el mensaje");
                return;
            }
            // Envia el mensaje a todos los clientes
            for (OutputPipe outputPipe : clientes) {
                if (outputPipe != null) {
                    if (outputPipe.getAdvertisement().getID() == advertisement.getID()) {
                        encontrado = true;
                    } else {
                        Message msg = new Message();
                        StringMessageElement remitenteElement = new StringMessageElement("remitente", remitente.toString(), null);
                        StringMessageElement mensajeElement = new StringMessageElement("mensaje", mensaje.toString(), null);
                        msg.addMessageElement(remitenteElement);
                        msg.addMessageElement(mensajeElement);
                        try {
                            outputPipe.send(msg);
                        } catch (IOException ex) {
                            System.out.println("IOException: No se pudo enviar el mensaje");
                        }
                    }
                }
            }
            if (!encontrado) {
                OutputPipe clienteOutputPipe = UtilidadesJXTA.crearOuputPipe(chat.getGrupoChat(), advertisement);
                clientes.addElement(clienteOutputPipe);
            }
        }
    }

    public String getAdvertisements() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("=================================\n");
        buffer.append("Servidor: InputPipe Advertisement\n");
        buffer.append("\n");
        buffer.append(inputPipeAdvertisement.toString());
        buffer.append("\n");
        buffer.append("=================================\n");
        if (clientes != null && clientes.size()>0) {
            buffer.append("Servidor: Lista de Clientes\n");
            buffer.append("\n");
            for (OutputPipe pipe : clientes) {
                if (pipe != null) {
                    buffer.append("Cliente Advertisement\n");
                    buffer.append("\n");
                    buffer.append(pipe.getAdvertisement().toString());
                    buffer.append("\n");
                }
            }
            buffer.append("=================================\n");
        }
        buffer.append("\n");
        return buffer.toString();
    }

}
