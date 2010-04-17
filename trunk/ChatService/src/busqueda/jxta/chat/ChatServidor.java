
package busqueda.jxta.chat;

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
public class ChatServidor {
    // Peer
    private Chat chat;
    // Advertisement
    private PipeAdvertisement inputPipeAdvertisement;
    // Canal de Comunicacion (InputPipe)
    private InputPipe inputPipe;
    // La lista de clientes, Canales de Comunicacion (OutputPipes)
    private Vector<OutputPipe> clientes;
    
    public ChatServidor(Chat chat) {
        this.chat = chat;
        this.inputPipeAdvertisement = UtilidadesJXTA.crearPipeAdvertisement(Chat.grupoChat, Chat.NOMBRE_SERVIDOR, Chat.DESCRIPCION_SERVIDOR);
        PipeInputListener listener = new PipeInputListener();
        this.inputPipe = UtilidadesJXTA.crearInputPipe(Chat.grupoChat, inputPipeAdvertisement, listener);
        UtilidadesJXTA.publicarAdvertisement(Chat.grupoChat, inputPipeAdvertisement);
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
            UtilidadesJXTA.eliminarAdvertisement(Chat.grupoChat, inputPipeAdvertisement);
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

    private void guardarCliente(PipeAdvertisement advertisement) {
        boolean encontrado = false;
        for (OutputPipe pipe : clientes) {
            if (pipe != null) {
                if (pipe.getAdvertisement().getID() == advertisement.getID()) {
                    encontrado = true;
                }
            }
        }
        if (!encontrado) {
            OutputPipe cliente = UtilidadesJXTA.crearOuputPipe(Chat.grupoChat, advertisement);
            clientes.addElement(cliente);
            chat.mostrarMensajeChat("Servidor", "El cliente '" + advertisement.getName() + "' se ha conectado.");
            chat.mostrarMensajeChat("Servidor", "El ID del cliente es: " + advertisement.getID().toString() + "\n");
        }
    }

    // Clase para escuchar los mensajes de entrada
    private class PipeInputListener implements PipeMsgListener {
        @Override
        public void pipeMsgEvent(PipeMsgEvent pmevent) {
            PipeAdvertisement advertisement = null;
            Message message = pmevent.getMessage();
            if (!message.getMessageElements().hasNext()) {
                return;
            }
            // Por nombre del elemento
            MessageElement remitente = message.getMessageElement("remitente");
            MessageElement mensaje  = message.getMessageElement("mensaje");
            MessageElement advertisementID  = message.getMessageElement("advertisementID");
            MessageElement cliente  = message.getMessageElement("cliente");
            if (remitente != null && mensaje != null && advertisementID != null) {
                chat.mostrarMensajeChat(remitente.toString(), mensaje.toString());
                // Envia el mensaje a todos los demas clientes
                for (OutputPipe outputPipe : clientes) {
                    if ( (outputPipe != null) && !outputPipe.getAdvertisement().getID().toString().equals(advertisementID.toString())) {
                        Message msg = new Message();
                        StringMessageElement remitenteElement = new StringMessageElement("remitente", remitente.toString(), null);
                        StringMessageElement mensajeElement = new StringMessageElement("mensaje", mensaje.toString(), null);
                        msg.addMessageElement(remitenteElement);
                        msg.addMessageElement(mensajeElement);
                        try {
                            outputPipe.send(msg);
                        } catch (IOException ex) {
                            System.out.println("IOException: No se pudo enviar el mensaje en el servidor.");
                        }
                    }
                }
            } else if (cliente != null) {
                advertisement = UtilidadesJXTA.crearPipeAdvertisementFromString(cliente.toString());
                guardarCliente(advertisement);
            }else {
                chat.mostrarMensajeChat("Error", "No se puede descifrar el mensaje.");
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
