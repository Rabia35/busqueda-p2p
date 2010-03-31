
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
public class AgenteChat extends Agent {
    // Servicio
    public static String NOMBRE_SERVICIO = "chat-service";
    public static String TIPO_SERVICIO = "chat";
    public static String DESCRIPCION_SERVICIO = "chat-descripcion";
    // EL agente de la Interfaz Grafica
    private AID agenteGUI;
    // Para la Interfaz JXTA
    private AID agenteJXTA;

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
            // Crea la descripcion del agente
            DFAgentDescription dfad = new DFAgentDescription();
            dfad.setName(getAID());
            // Crea la descripcion del servicio
            ServiceDescription sd = new ServiceDescription();
            sd.setName(AgenteChat.NOMBRE_SERVICIO);
            sd.setType(AgenteChat.TIPO_SERVICIO);
            dfad.addServices(sd);
            // Registrar la descripcion del agente en el DF
            DFService.register(this, dfad);
            System.out.println("El servicio " + AgenteChat.NOMBRE_SERVICIO + " se ha registrado.");
        } catch (FIPAException fex) {
            System.out.println("FIPAException: " + fex.getMessage());
        }
    }

    private void deregistrarServicio() {
        try {
            DFService.deregister(this);
            System.out.println("El servicio " + AgenteChat.NOMBRE_SERVICIO + " ya no esta registrado.");
        } catch (FIPAException fex) {
            System.out.println("FIPAException: " + fex.getMessage());
        }
    }

    private boolean buscarAgenteGUI() {
        try {
            // Build the description used as template for the search
            DFAgentDescription templateDfad = new DFAgentDescription();
            ServiceDescription templateSd = new ServiceDescription();
            templateSd.setType(AgenteGUI.TIPO_SERVICIO);
            templateDfad.addServices(templateSd);

            SearchConstraints sc = new SearchConstraints();
            sc.setMaxResults(new Long(1));

            DFAgentDescription[] results = DFService.search(this, templateDfad, sc);
            if (results.length > 0) {
                agenteGUI = results[0].getName();
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
            templateSd.setType(AgenteJXTA.TIPO_SERVICIO);
            templateDfad.addServices(templateSd);

            SearchConstraints sc = new SearchConstraints();
            sc.setMaxResults(new Long(1));

            DFAgentDescription[] results = DFService.search(this, templateDfad, sc);
            if (results.length > 0) {
                agenteJXTA = results[0].getName();
                return true;
            }
        } catch (FIPAException fex) {
            System.out.println("FIPAException: " + fex.getMessage());
        }
        System.out.println("El agente " + getLocalName() + " no encontro ningun servicio de JXTA.");
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
                // Envia el mensaje para que publique el servicio
                ACLMessage acl = new ACLMessage(ACLMessage.REQUEST);
                acl.addReceiver(agenteJXTA);
                acl.setContent(AgenteChat.NOMBRE_SERVICIO);
                myAgent.send(acl);
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
                System.out.println("Procesar: " + mensaje);
                if (acl.getPerformative() == ACLMessage.REQUEST) {
                    ACLMessage aclJXTA = new ACLMessage(ACLMessage.INFORM);
                    aclJXTA.addReceiver(agenteJXTA);
                    aclJXTA.setContent(mensaje);
                    myAgent.send(aclJXTA);
                } else if (acl.getPerformative() == ACLMessage.INFORM) {
                    ACLMessage aclGUI = new ACLMessage(ACLMessage.INFORM);
                    aclGUI.addReceiver(agenteGUI);
                    aclGUI.setContent(mensaje);
                    myAgent.send(aclGUI);
                } else {
                    // Crea la respuesta
                    myAgent.addBehaviour(new EnviarRespuestaBehaviour(acl));
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
            reply.setContent("No se pudo procesar el mensaje.");
            myAgent.send(reply);
        }
    }

}
