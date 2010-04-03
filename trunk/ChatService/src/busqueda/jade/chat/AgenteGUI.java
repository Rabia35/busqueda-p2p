
package busqueda.jade.chat;

import busqueda.jade.JADEContainer;
import busqueda.jade.ontologias.mensaje.Enviar;
import busqueda.jade.ontologias.mensaje.Mensaje;
import busqueda.jade.ontologias.mensaje.Mostrar;
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
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author almunoz
 */
public class AgenteGUI extends Agent {
    public static String NOMBRE_SERVICIO = "chat-gui-service";
    public static String TIPO_SERVICIO = "chat-gui";
    public static String DESCRIPCION_SERVICIO = "chat-gui-descripcion";
    // El manager que realiza la conexion con el JADE Communicator
    private JADEContainer jadeManager;
    // Agente del Chat
    private AID agenteChat;
    // Codec del Lenguaje de Contenido
    private SLCodec codec;
    // Ontologias
    private Ontology ontologiaMensaje;

    @Override
    protected void setup() {
        // Argumentos
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            jadeManager = (JADEContainer) args[0];
        } else {
            doDelete();
        }
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
        this.addBehaviour(new RegistrarServicioBehaviour(this));
        this.addBehaviour(new BuscarAgenteChatBehaviour(this, 5000));
        this.addBehaviour(new EnviarMensajeO2ABehaviour(this));
        this.addBehaviour(new RecibirMensajeBehaviour(this));
    }

    @Override
    protected void takeDown() {
        deregistrarServicio();
        System.out.println("El agente " + this.getName() + " ha terminado.");
    }

    private void registrarServicio() {
        try {
            DFAgentDescription dfad = new DFAgentDescription();
            dfad.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setName(AgenteGUI.NOMBRE_SERVICIO);
            sd.setType(AgenteGUI.TIPO_SERVICIO);
            dfad.addServices(sd);
            // Registrar la descripcion en el DF
            DFService.register(this, dfad);
            System.out.println("El servicio " + AgenteGUI.NOMBRE_SERVICIO + " se ha registrado.");
        } catch (FIPAException fex) {
            System.out.println("FIPAException: " + fex.getMessage());
        }
    }

    private void deregistrarServicio() {
        try {
            DFService.deregister(this);
            System.out.println("El servicio " + AgenteGUI.NOMBRE_SERVICIO + " ya no esta registrado.");
        } catch (FIPAException fex) {
            System.out.println("FIPAException: " + fex.getMessage());
        }
    }

    private boolean buscarAgenteChat() {
        try {
            // Build the description used as template for the search
            DFAgentDescription templateDfad = new DFAgentDescription();
            ServiceDescription templateSd = new ServiceDescription();
            templateSd.setType(AgenteChat.TIPO_SERVICIO);
            templateDfad.addServices(templateSd);

            SearchConstraints sc = new SearchConstraints();
            sc.setMaxResults(new Long(1));

            DFAgentDescription[] results = DFService.search(this, templateDfad, sc);
            if (results.length > 0) {
                agenteChat = results[0].getName();
                return true;
            }
        } catch (FIPAException fex) {
            System.out.println("FIPAException: " + fex.getMessage());
        }
        System.out.println("El agente " + getLocalName() + " no encontro ningun servicio de chat.");
        return false;
    }

    /*******************/
    /* COMPORTAMIENTOS */
    /*******************/

    private class RegistrarServicioBehaviour extends OneShotBehaviour {

        public RegistrarServicioBehaviour(Agent agente) {
            super(agente);
        }
        
        @Override
        public void action() {
            registrarServicio();
        }
    }

    private class BuscarAgenteChatBehaviour extends TickerBehaviour {
        private boolean encontrado = false;

        public BuscarAgenteChatBehaviour(Agent agente, long periodo) {
            super(agente, periodo);
        }

        @Override
        protected void onTick() {
            encontrado = buscarAgenteChat();
            if (encontrado) {
                this.stop();
            }
        }
    }

    public class EnviarMensajeO2ABehaviour extends CyclicBehaviour {

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
                    Enviar enviar = new Enviar();
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

    public class RecibirMensajeBehaviour extends CyclicBehaviour {

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
                        if (elemento instanceof Mostrar) {
                            Mostrar mostrar = (Mostrar) elemento;
                            Mensaje mensaje = mostrar.getMensaje();
                            jadeManager.mostrarMensajeChat(mensaje.getRemitente(), mensaje.getMensaje());
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
