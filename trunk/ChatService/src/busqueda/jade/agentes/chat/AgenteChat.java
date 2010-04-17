
package busqueda.jade.agentes.chat;

import busqueda.jade.UtilidadesJADE;
import busqueda.jade.agentes.AgenteJXTA;
import busqueda.jade.agentes.AgenteGUI;
import busqueda.jade.comportamientos.PublicarServicioJXTABehaviour;
import busqueda.jade.comportamientos.RegistrarServicioDFBehaviour;
import busqueda.jade.ontologias.mensaje.EnviarMensaje;
import busqueda.jade.ontologias.mensaje.MostrarMensaje;
import busqueda.jade.ontologias.mensaje.OntologiaMensaje;
import busqueda.jade.ontologias.servicio.OntologiaServicio;
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
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author almunoz
 */
public class AgenteChat extends Agent {
    // Servicio
    public static final String TIPO_SERVICIO = "chat-service";
    public static final String DESCRIPCION_SERVICIO = "Servicio de Chat";
    // El agente de la Interfaz Grafica
    private AID agenteGUI;
    // Para la Interfaz JXTA
    private AID agenteJXTA;
    // Codec del Lenguaje de Contenido
    private Codec codec;
    // Ontologias
    private Ontology ontologiaServicio;
    private Ontology ontologiaMensaje;

    @Override
    protected void setup() {
        // Content Language
        codec = new SLCodec();
        this.getContentManager().registerLanguage(codec);
        // Ontologias
        ontologiaServicio = OntologiaServicio.getInstance();
        ontologiaMensaje = OntologiaMensaje.getInstance();
        this.getContentManager().registerOntology(ontologiaServicio);
        this.getContentManager().registerOntology(ontologiaMensaje);
        // Mensaje de inicio
        System.out.println("El agente " + this.getName() + " se ha iniciado.");
        // Comportamientos
        this.addBehaviour(new RegistrarServicioDFBehaviour(this, AgenteChat.TIPO_SERVICIO, AgenteChat.DESCRIPCION_SERVICIO));
        this.addBehaviour(new BuscarAgenteGUIBehaviour(this, 5000));
        this.addBehaviour(new BuscarAgenteJXTABehaviour(this, 5000));
        this.addBehaviour(new PublicarServicioJXTABehaviour(this, 5000, AgenteChat.TIPO_SERVICIO, AgenteChat.DESCRIPCION_SERVICIO));
        this.addBehaviour(new RecibirMensajeBehaviour(this));
    }

    @Override
    protected void takeDown() {
        UtilidadesJADE.deregistrarServicio(this, AgenteChat.TIPO_SERVICIO);
        System.out.println("El agente " + this.getName() + " ha terminado.");
    }

    /*******************/
    /* COMPORTAMIENTOS */
    /*******************/

    private class BuscarAgenteGUIBehaviour extends TickerBehaviour {

        public BuscarAgenteGUIBehaviour(Agent agente, long periodo) {
            super(agente, periodo);
        }

        @Override
        protected void onTick() {
            agenteGUI = UtilidadesJADE.buscarAgente(myAgent, AgenteGUI.TIPO_SERVICIO);
            if (agenteGUI != null) {
                this.stop();
            }
        }
    }

    private class BuscarAgenteJXTABehaviour extends TickerBehaviour {

        public BuscarAgenteJXTABehaviour(Agent agente, long periodo) {
            super(agente, periodo);
        }

        @Override
        protected void onTick() {
            agenteJXTA = UtilidadesJADE.buscarAgente(myAgent, AgenteJXTA.TIPO_SERVICIO);
            if (agenteJXTA != null) {
                this.stop();
            }
        }
    }

    private class RecibirMensajeBehaviour extends CyclicBehaviour {

        public RecibirMensajeBehaviour(Agent agente) {
            super(agente);
        }
        
        @Override
        public void action() {
            // Para recibir el mensaje, excepto el que el envia
            //MessageTemplate mt = MessageTemplate.not(MessageTemplate.MatchSender(myAgent.getAID()));
            //ACLMessage acl = receive(mt);
            // Para recibir el mensaje, dependiendo el tipo realizo la accion
            ACLMessage acl = receive();
            if (acl != null) {
                if (acl.getPerformative() == ACLMessage.REQUEST) {
                    try {
                        ContentElement elemento = myAgent.getContentManager().extractContent(acl);
                        if (elemento instanceof EnviarMensaje) {
                            EnviarMensaje enviar = (EnviarMensaje) elemento;
                            ACLMessage aclJXTA = new ACLMessage(ACLMessage.REQUEST);
                            aclJXTA.setSender(myAgent.getAID());
                            aclJXTA.addReceiver(agenteJXTA);
                            aclJXTA.setLanguage(codec.getName());
                            aclJXTA.setOntology(ontologiaMensaje.getName());
                            // Rellena el contenido
                            myAgent.getContentManager().fillContent(aclJXTA, enviar);
                            myAgent.send(aclJXTA);
                        } else if (elemento instanceof MostrarMensaje) {
                            MostrarMensaje mostrar = (MostrarMensaje) elemento;
                            ACLMessage aclGUI = new ACLMessage(ACLMessage.REQUEST);
                            aclGUI.setSender(myAgent.getAID());
                            aclGUI.addReceiver(agenteGUI);
                            aclGUI.setLanguage(codec.getName());
                            aclGUI.setOntology(ontologiaMensaje.getName());
                            // Rellena el contenido
                            myAgent.getContentManager().fillContent(aclGUI, mostrar);
                            myAgent.send(aclGUI);
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
                this.block();
            }
        }
    }

}
