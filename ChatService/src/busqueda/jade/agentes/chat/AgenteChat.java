
package busqueda.jade.agentes.chat;

import busqueda.jade.JADEContainer;
import busqueda.jade.agentes.AgenteJXTA;
import busqueda.jade.agentes.AgenteGUI;
import busqueda.jade.comportamientos.RegistrarServicioBehaviour;
import busqueda.jade.ontologias.mensaje.Enviar;
import busqueda.jade.ontologias.mensaje.Mostrar;
import busqueda.jade.ontologias.mensaje.OntologiaMensaje;
import busqueda.jade.ontologias.servicio.OntologiaServicio;
import busqueda.jade.ontologias.servicio.Publicar;
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
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author almunoz
 */
public class AgenteChat extends Agent {
    // Servicio
    public static final String NOMBRE_SERVICIO = "chat-service";
    public static final String TIPO_SERVICIO = "chat";
    public static final String DESCRIPCION_SERVICIO = "chat-descripcion";
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
        this.addBehaviour(new RegistrarServicioBehaviour(this, AgenteChat.NOMBRE_SERVICIO, AgenteChat.TIPO_SERVICIO));
        this.addBehaviour(new BuscarAgenteGUIBehaviour(this, 5000));
        this.addBehaviour(new BuscarAgenteJXTABehaviour(this, 5000));
        this.addBehaviour(new RecibirMensajeBehaviour(this));
    }

    @Override
    protected void takeDown() {
        deregistrarServicio();
        System.out.println("El agente " + this.getName() + " ha terminado.");
    }

    private void deregistrarServicio() {
        try {
            DFService.deregister(this);
            System.out.println("El servicio " + AgenteChat.NOMBRE_SERVICIO + " ya no esta registrado.");
        } catch (FIPAException fex) {
            System.out.println("FIPAException: " + fex.getMessage());
        }
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
            agenteGUI = JADEContainer.buscarAgente(myAgent, AgenteGUI.TIPO_SERVICIO);
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
            agenteJXTA = JADEContainer.buscarAgente(myAgent, AgenteJXTA.TIPO_SERVICIO);
            if (agenteJXTA != null) {
                try {
                    // Envia el mensaje para publicar el servicio
                    ACLMessage acl = new ACLMessage(ACLMessage.REQUEST);
                    acl.setSender(myAgent.getAID());
                    acl.addReceiver(agenteJXTA);
                    acl.setLanguage(codec.getName());
                    acl.setOntology(ontologiaServicio.getName());
                    // Concepto
                    Servicio servicio = new Servicio();
                    servicio.setNombre(AgenteChat.NOMBRE_SERVICIO);
                    servicio.setTipo(AgenteChat.TIPO_SERVICIO);
                    servicio.setDescripcion(AgenteChat.DESCRIPCION_SERVICIO);
                    // Predicado
                    Publicar publicar = new Publicar();
                    publicar.setServicio(servicio);
                    // Coloca el predicado en el mensaje
                    myAgent.getContentManager().fillContent(acl, publicar);
                    myAgent.send(acl);
                    this.stop();
                } catch (CodecException ex) {
                    System.out.println("CodecException: " + ex.getMessage());
                } catch (OntologyException ex) {
                    System.out.println("OntologyException: " + ex.getMessage());
                }
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
                        if (elemento instanceof Enviar) {
                            Enviar enviar = (Enviar) elemento;
                            ACLMessage aclJXTA = new ACLMessage(ACLMessage.REQUEST);
                            aclJXTA.setSender(myAgent.getAID());
                            aclJXTA.addReceiver(agenteJXTA);
                            aclJXTA.setLanguage(codec.getName());
                            aclJXTA.setOntology(ontologiaMensaje.getName());
                            // Rellena el contenido
                            myAgent.getContentManager().fillContent(aclJXTA, enviar);
                            myAgent.send(aclJXTA);
                        } else if (elemento instanceof Mostrar) {
                            Mostrar mostrar = (Mostrar) elemento;
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
