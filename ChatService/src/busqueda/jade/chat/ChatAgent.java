
package busqueda.jade.chat;

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
public class ChatAgent extends Agent {
    // Servicio
    public static String NOMBRE_SERVICIO = "chat-service";
    public static String TIPO_SERVICIO = "chat";
    public static String DESCRIPCION_SERVICIO = "chat-descripcion";
    // Para la Interfaz Grafica
    public AID gui;
    // Para la Interfaz JXTA
    public AID jxta;

    @Override
    protected void setup() {
        System.out.println("El agente " + this.getName() + " se ha iniciado.");
        // Comportamientos
        this.addBehaviour(new RegistrarServicioBehaviour());
        this.addBehaviour(new BuscarAgenteGUIBehaviour(this, 5000));
        this.addBehaviour(new BuscarAgenteJXTABehaviour(this, 5000));
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
            sd.setName(ChatAgent.NOMBRE_SERVICIO);
            sd.setType(ChatAgent.TIPO_SERVICIO);
            dfad.addServices(sd);
            // Registrar la descrpcion en el DF
            DFService.register(this, dfad);
            System.out.println("El servicio " + ChatAgent.NOMBRE_SERVICIO + " se ha registrado.");
        } catch (FIPAException fex) {
            System.out.println("FIPAException: " + fex.getMessage());
        }
    }

    private void deregistrarServicio() {
        try {
            DFService.deregister(this);
            System.out.println("El servicio " + ChatAgent.NOMBRE_SERVICIO + " ya no esta registrado.");
        } catch (FIPAException fex) {
            System.out.println("FIPAException: " + fex.getMessage());
        }
    }

    private boolean buscarAgenteGUI() {
        try {
            // Build the description used as template for the search
            DFAgentDescription templateDfad = new DFAgentDescription();
            ServiceDescription templateSd = new ServiceDescription();
            templateSd.setType(GUIAgent.TIPO_SERVICIO);
            templateDfad.addServices(templateSd);

            SearchConstraints sc = new SearchConstraints();
            sc.setMaxResults(new Long(1));

            DFAgentDescription[] results = DFService.search(this, templateDfad, sc);
            if (results.length > 0) {
                gui = results[0].getName();
                return true;
            }
        } catch (FIPAException fex) {
            System.out.println("FIPAException: " + fex.getMessage());
        }
        System.out.println("El agente " + getLocalName() + " no encontro ningun servicio de interfaz grafica.");
        return false;
    }

    private boolean buscarAgenteJXTA() {
        try {
            // Build the description used as template for the search
            DFAgentDescription templateDfad = new DFAgentDescription();
            ServiceDescription templateSd = new ServiceDescription();
            templateSd.setType(JXTAAgent.TIPO_SERVICIO);
            templateDfad.addServices(templateSd);

            SearchConstraints sc = new SearchConstraints();
            sc.setMaxResults(new Long(1));

            DFAgentDescription[] results = DFService.search(this, templateDfad, sc);
            if (results.length > 0) {
                gui = results[0].getName();
                return true;
            }
        } catch (FIPAException fex) {
            System.out.println("FIPAException: " + fex.getMessage());
        }
        System.out.println("El agente " + getLocalName() + " no encontro ningun servicio de interfaz grafica.");
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

    private class BuscarAgenteGUIBehaviour extends TickerBehaviour {
        private boolean encontrado = false;

        public BuscarAgenteGUIBehaviour(Agent agente, long periodo) {
            super(agente, periodo);
        }

        @Override
        protected void onTick() {
            encontrado = buscarAgenteGUI();
            if (encontrado) {
                this.stop();
            }
        }
    }

    private class BuscarAgenteJXTABehaviour extends TickerBehaviour {
        private boolean encontrado = false;

        public BuscarAgenteJXTABehaviour(Agent agente, long periodo) {
            super(agente, periodo);
        }

        @Override
        protected void onTick() {
            encontrado = buscarAgenteJXTA();
            if (encontrado) {
                this.stop();
            }
        }
    }

    public class RecibirMensajeBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // Para recibir el mensaje, excepto el que el envia
            //MessageTemplate mt = MessageTemplate.not(MessageTemplate.MatchSender(myAgent.getAID()));
            //ACLMessage acl = receive(mt);
            // Para recibir el mensaje, dependiendo el tipo realizo la accion
            ACLMessage acl = receive();
            if (acl != null) {
                String mensaje = acl.getContent();
                System.out.println(acl.getSender().getLocalName() + " dice: " + mensaje);
                // Crea la respuesta
                //myAgent.addBehaviour(new EnviarRespuestaBehaviour(acl));
                if (acl.getSender() != jxta) {
                    ACLMessage aclJXTA = new ACLMessage(ACLMessage.INFORM);
                    aclJXTA.addReceiver(jxta);
                    aclJXTA.setContent(mensaje);
                    myAgent.send(aclJXTA);
                }
                if (acl.getSender() != gui) {
                    ACLMessage aclGUI = new ACLMessage(ACLMessage.INFORM);
                    aclGUI.addReceiver(gui);
                    aclGUI.setContent(mensaje);
                    myAgent.send(aclGUI);
                }
            } else {
                this.block();
            }
        }
    }

    public class EnviarRespuestaBehaviour extends OneShotBehaviour {
        private ACLMessage acl;

        public EnviarRespuestaBehaviour(ACLMessage acl) {
            this.acl = acl;
        }

        @Override
        public void action() {
            // Crea la respuesta
            ACLMessage reply = acl.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent("Gracias. Mensaje Recibido.");
            myAgent.send(reply);
        }
    }

}
