
package busqueda.jade.chat;

import busqueda.JXTACommunicator;
import busqueda.jade.ontologias.mensaje.OntologiaMensaje;
import busqueda.jade.ontologias.servicio.OntologiaServicio;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
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
public class AgenteJXTA extends Agent {
    public static String NOMBRE_SERVICIO = "chat-jxta-service";
    public static String TIPO_SERVICIO = "chat-jxta";
    public static String DESCRIPCION_SERVICIO = "chat-jxta-descripcion";
    // El JXTA Communicator, para comunicarse con el peer
    private JXTACommunicator jxtaCommunicator;
    // Agente del Chat
    private AID agenteChat;
    // Codec del Lenguaje de Contenido
    private SLCodec codec;
    // Ontologias
    private Ontology ontologiaServicio;
    private Ontology ontologiaChat;

    @Override
    protected void setup() {
        // Argumentos
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            jxtaCommunicator = (JXTACommunicator) args[0];
        } else {
            doDelete();
        }
        // Content Language
        codec = new SLCodec();
        this.getContentManager().registerLanguage(codec);
        // Ontologias
        ontologiaServicio = OntologiaServicio.getInstance();
        ontologiaChat = OntologiaMensaje.getInstance();
        this.getContentManager().registerOntology(ontologiaServicio);
        this.getContentManager().registerOntology(ontologiaChat);
        // Permite la comunicacion O2A: Object to Agent
        this.setEnabledO2ACommunication(true, 0);
        // Mensaje de inicio
        System.out.println("El agente " + this.getName() + " se ha iniciado.");
        // Comportamientos
        this.addBehaviour(new RegistrarServicioBehaviour());
        this.addBehaviour(new BuscarAgenteChatBehaviour(this, 5000));
        this.addBehaviour(new EnviarMensajeO2ABehaviour());
        this.addBehaviour(new RecibirMensajeBehaviour());
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
            //dfad.addLanguages(lenguaje);
            //dfad.addOntologies(ontologia);
            ServiceDescription sd = new ServiceDescription();
            sd.setName(AgenteJXTA.NOMBRE_SERVICIO);
            sd.setType(AgenteJXTA.TIPO_SERVICIO);
            dfad.addServices(sd);
            // Registrar la descripcion en el DF
            DFService.register(this, dfad);
            System.out.println("El servicio " + AgenteJXTA.NOMBRE_SERVICIO + " se ha registrado.");
        } catch (FIPAException fex) {
            System.out.println("FIPAException: " + fex.getMessage());
        }
    }

    private void deregistrarServicio() {
        try {
            DFService.deregister(this);
            System.out.println("El servicio " + AgenteJXTA.NOMBRE_SERVICIO + " ya no esta registrado.");
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
        @Override
        public void action() {
            String mensaje = (String) myAgent.getO2AObject();
            if (mensaje != null) {
                ACLMessage acl = new ACLMessage(ACLMessage.INFORM);
                acl.addReceiver(agenteChat);
                acl.setContent(mensaje);
                myAgent.send(acl);                
            } else {
                this.block();
            }
        }
    }

    public class RecibirMensajeBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // Para recibir el mensaje, dependiendo el tipo realizo la accion en JXTA
            ACLMessage acl = receive();
            if (acl != null) {
                String mensaje = acl.getContent();
                if (acl.getPerformative() == ACLMessage.REQUEST &&
                    mensaje.equals(AgenteChat.NOMBRE_SERVICIO)) {
                    jxtaCommunicator.iniciarChat(AgenteChat.TIPO_SERVICIO,
                                                 AgenteChat.DESCRIPCION_SERVICIO);
                } else if (acl.getPerformative() == ACLMessage.INFORM) {
                    jxtaCommunicator.enviarMensajeChat(acl.getSender().getName() + " dice: " + mensaje);
                }
            } else {
                block();
            }
        }
    }

}
