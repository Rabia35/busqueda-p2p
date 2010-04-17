
package busqueda.jade.agentes;

import busqueda.jade.JADEContainer;
import busqueda.jade.UtilidadesJADE;
import busqueda.jade.agentes.chat.AgenteChat;
import busqueda.jade.comportamientos.RegistrarServicioDFBehaviour;
import busqueda.jade.ontologias.mensaje.EnviarMensaje;
import busqueda.jade.ontologias.mensaje.Mensaje;
import busqueda.jade.ontologias.mensaje.MostrarMensaje;
import busqueda.jade.ontologias.mensaje.OntologiaMensaje;
import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author almunoz
 */
public class AgenteGUI extends Agent {
    public static final String TIPO_SERVICIO = "gui-service";
    public static final String DESCRIPCION_SERVICIO = "Servicio de Interfaz Grafica";
    // El container que realiza la conexion con el JADE Communicator
    private JADEContainer jadeContainer;
    // Agente del Chat
    private AID agenteChat;
    // Codec del Lenguaje de Contenido
    private SLCodec codec;
    // Ontologias
    private Ontology ontologiaMensaje;

    @Override
    protected void setup() {
        // Contenedor JADE
        jadeContainer = JADEContainer.getInstance();
        // Content Language
        codec = new SLCodec();
        this.getContentManager().registerLanguage(codec);
        // Ontologias
        ontologiaMensaje = OntologiaMensaje.getInstance();
        this.getContentManager().registerOntology(ontologiaMensaje);
        // Permite la comunicacion O2A: Object to Agent
        this.setEnabledO2ACommunication(true, 0);
        // Mensaje de inicio
        System.out.println("El agente " + this.getName() + " se ha iniciado.");
        // Comportamientos
        this.addBehaviour(new RegistrarServicioDFBehaviour(this, AgenteGUI.TIPO_SERVICIO, AgenteGUI.DESCRIPCION_SERVICIO));
        this.addBehaviour(new BuscarAgenteChatBehaviour(this, 5000));
        this.addBehaviour(new EnviarMensajeO2ABehaviour(this));
        this.addBehaviour(new RecibirMensajeBehaviour(this));
    }

    @Override
    protected void takeDown() {
        UtilidadesJADE.deregistrarServicio(this, AgenteGUI.TIPO_SERVICIO);
        System.out.println("El agente " + this.getName() + " ha terminado.");
    }

    /*******************/
    /* COMPORTAMIENTOS */
    /*******************/

    private class BuscarAgenteChatBehaviour extends TickerBehaviour {
        
        public BuscarAgenteChatBehaviour(Agent agente, long periodo) {
            super(agente, periodo);
        }

        @Override
        protected void onTick() {
            agenteChat = UtilidadesJADE.buscarAgente(myAgent, AgenteChat.TIPO_SERVICIO);
            if (agenteChat != null) {
                this.stop();
            }
        }
    }

    private class EnviarMensajeO2ABehaviour extends CyclicBehaviour {

        public EnviarMensajeO2ABehaviour(Agent agente) {
            super(agente);
        }

        @Override
        public void action() {
            String texto = (String) myAgent.getO2AObject();
            if (texto != null) {
                try {
                    ACLMessage acl = new ACLMessage(ACLMessage.REQUEST);
                    acl.setSender(myAgent.getAID());
                    acl.addReceiver(agenteChat);
                    acl.setLanguage(codec.getName());
                    acl.setOntology(ontologiaMensaje.getName());
                    // Mensaje
                    Mensaje mensaje = new Mensaje();
                    mensaje.setRemitente(myAgent.getContainerController().getName());
                    mensaje.setMensaje(texto);
                    // Predicado
                    EnviarMensaje enviar = new EnviarMensaje();
                    enviar.setMensaje(mensaje);
                    // Rellena el contenido
                    myAgent.getContentManager().fillContent(acl, enviar);
                    myAgent.send(acl);
                } catch (CodecException ex) {
                    System.out.println("CodecException: " + ex.getMessage());
                } catch (OntologyException ex) {
                    System.out.println("OntologyException: " + ex.getMessage());
                }
            } else {
                this.block();
            }
        }
    }

    private class RecibirMensajeBehaviour extends CyclicBehaviour {

        public RecibirMensajeBehaviour(Agent agente) {
            super(agente);
        }

        @Override
        public void action() {
            // Para recibir el mensaje
            ACLMessage acl = receive();
            if (acl != null) {
                if (acl.getPerformative() == ACLMessage.REQUEST) {
                    try {
                        ContentElement elemento = myAgent.getContentManager().extractContent(acl);
                        if (elemento instanceof MostrarMensaje) {
                            MostrarMensaje mostrar = (MostrarMensaje) elemento;
                            Mensaje mensaje = mostrar.getMensaje();
                            jadeContainer.mostrarMensajeChat(mensaje.getRemitente(), mensaje.getMensaje());
                        }
                    } catch (CodecException ex) {
                        System.out.println("CodecException: " + ex.getMessage());
                    } catch (UngroundedException ex) {
                        System.out.println("UngroundedException: " + ex.getMessage());
                    } catch (OntologyException ex) {
                        System.out.println("OntologyException: " + ex.getMessage());
                    }
                }
            } else {
                block();
            }
        }
    }

}
