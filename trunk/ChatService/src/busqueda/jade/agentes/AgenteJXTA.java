
package busqueda.jade.agentes;

import busqueda.jade.JADEContainer;
import busqueda.jade.UtilidadesJADE;
import busqueda.jade.agentes.chat.AgenteChat;
import busqueda.jade.comportamientos.RegistrarServicioDFBehaviour;
import busqueda.jade.ontologias.mensaje.EnviarMensaje;
import busqueda.jade.ontologias.mensaje.Mensaje;
import busqueda.jade.ontologias.mensaje.MostrarMensaje;
import busqueda.jade.ontologias.mensaje.OntologiaMensaje;
import busqueda.jade.ontologias.servicio.OntologiaServicio;
import busqueda.jade.ontologias.servicio.PublicarServicio;
import busqueda.jade.ontologias.servicio.Servicio;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author almunoz
 */
public class AgenteJXTA extends Agent {
    public static final String TIPO_SERVICIO = "jxta-service";
    public static final String DESCRIPCION_SERVICIO = "Servicio de Comunicacion JXTA";
    // El container que realiza la conexion con el JADE Communicator
    private JADEContainer jadeContainer;
    // Agente del Chat
    private AID agenteChat;
    // Codec del Lenguaje de Contenido
    private Codec codec;
    // Ontologias
    private Ontology ontologiaServicio;
    private Ontology ontologiaMensaje;

    @Override
    protected void setup() {
        // Contenedor JADE
        jadeContainer = JADEContainer.getInstance();
        // Content Language
        codec = new SLCodec();
        this.getContentManager().registerLanguage(codec);
        // Ontologias
        ontologiaServicio = OntologiaServicio.getInstance();
        ontologiaMensaje = OntologiaMensaje.getInstance();
        this.getContentManager().registerOntology(ontologiaServicio);
        this.getContentManager().registerOntology(ontologiaMensaje);
        // Permite la comunicacion O2A: Object to Agent
        this.setEnabledO2ACommunication(true, 0);
        // Mensaje de inicio
        System.out.println("El agente " + this.getName() + " se ha iniciado.");
        // Comportamientos
        this.addBehaviour(new RegistrarServicioDFBehaviour(this, AgenteJXTA.TIPO_SERVICIO, AgenteJXTA.DESCRIPCION_SERVICIO));
        this.addBehaviour(new BuscarAgenteChatBehaviour(this, 5000));
        this.addBehaviour(new EnviarMensajeO2ABehaviour(this));
        this.addBehaviour(new RecibirMensajeBehaviour(this));
    }

    @Override
    protected void takeDown() {
        UtilidadesJADE.deregistrarServicio(this, AgenteJXTA.TIPO_SERVICIO);
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
            Mensaje mensaje = (Mensaje) myAgent.getO2AObject();
            if (mensaje != null) {
                try {
                    ACLMessage acl = new ACLMessage(ACLMessage.REQUEST);
                    acl.setSender(myAgent.getAID());
                    acl.addReceiver(agenteChat);
                    acl.setLanguage(codec.getName());
                    acl.setOntology(ontologiaMensaje.getName());
                    MostrarMensaje mostrar = new MostrarMensaje();
                    mostrar.setMensaje(mensaje);
                    myAgent.getContentManager().fillContent(acl, mostrar);
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
            // Para recibir el mensaje, dependiendo el tipo realizo la accion en JXTA
            ACLMessage acl = receive();
            if (acl != null) {
                if (acl.getPerformative() == ACLMessage.REQUEST) {
                    try {
                        ContentElement elemento = myAgent.getContentManager().extractContent(acl);
                        if (elemento instanceof PublicarServicio) {
                            PublicarServicio publicar = (PublicarServicio) elemento;
                            Servicio servicio = publicar.getServicio();
                            jadeContainer.iniciarChat(servicio.getTipo(), servicio.getDescripcion());
                            //myAgent.addBehaviour(new IniciarChatBehaviour(myAgent, servicio.getTipo(), servicio.getDescripcion()));
                        } else if (elemento instanceof EnviarMensaje) {
                            EnviarMensaje enviar = (EnviarMensaje) elemento;
                            Mensaje mensaje = enviar.getMensaje();
                            jadeContainer.enviarMensajeChatJXTA(mensaje.getRemitente(), mensaje.getMensaje());
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

    private class IniciarChatBehaviour extends OneShotBehaviour {
        private String tipo;
        private String descripcion;

        public IniciarChatBehaviour(Agent agente, String tipo, String descripcion) {
            super(agente);
            this.tipo = tipo;
            this.descripcion = descripcion;
        }

        @Override
        public void action() {
            jadeContainer.iniciarChat(tipo, descripcion);
        }

    }

}
