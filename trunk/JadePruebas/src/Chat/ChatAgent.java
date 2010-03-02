package Chat;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;

/**
 *
 * @author almunoz
 */
public class ChatAgent extends Agent {
    private String serviceName;
    private String serviceType;
    // Para la busqueda
    private AID[] agents;
    private AID[] subscribeAgents;
    // Para el mensaje
    public static int numero = 1;
        
    @Override
    protected void setup() {
        serviceName = "jade-chat";
        serviceType = "chat";
        System.out.println("El agente " + this.getName() + " se ha iniciado.");

        addBehaviour(new RegisterServiceBehaviour());
        addBehaviour(new ReceiveBehaviour());
        
        addBehaviour(new SendBehaviour("Hola soy " + this.getLocalName()));
    }

    @Override
    protected void takeDown() {
        deregisterService();
        System.out.println("El agente " + this.getName() + " ha terminado.");
    }

    private void registerService() {
        try {
            DFAgentDescription dfad = new DFAgentDescription();
            dfad.setName(getAID());

            ServiceDescription sd = new ServiceDescription();
            sd.setName(serviceName);
            sd.setType(serviceType);
            dfad.addServices(sd);

            DFService.register(this, dfad);
            System.out.println(this.getLocalName() + ": El servicio " + serviceName + " se ha registrado.");
        } catch (FIPAException fex) {
            System.out.println("FIPAException: " + fex.getMessage());
        }
    }

    private void deregisterService() {
        try {
            DFService.deregister(this);
            System.out.println(this.getLocalName() + ": El servicio " + serviceName + " ya no esta registrado.");
        } catch (FIPAException fex) {
            System.out.println("FIPAException: " + fex.getMessage());
        }
    }

    private AID[] searchService() {
        try {
            // Build the description used as template for the search
            DFAgentDescription templateDfad = new DFAgentDescription();
            ServiceDescription templateSd = new ServiceDescription();
            templateSd.setType(serviceType);
            templateDfad.addServices(templateSd);

            SearchConstraints sc = new SearchConstraints();
            sc.setMaxResults(new Long(-1));
            
            DFAgentDescription[] results = DFService.search(this, templateDfad, sc);
            if (results.length > 0) {
                AID[] searchAgents = new AID[results.length];
                for (int i = 0; i < results.length; ++i) {
                    searchAgents[i] = results[i].getName();
                }
                return searchAgents;
            }
        } catch (FIPAException fex) {
            System.out.println("FIPAException: " + fex.getMessage());
        }
        System.out.println("El agente " + getLocalName() + " no encontro ningun servicio de chat.");
        return null;
    }

    private void subscribeService() {
        // Build the description used as template for the subscription
        DFAgentDescription templateDfad = new DFAgentDescription();
        ServiceDescription templateSd = new ServiceDescription();
        templateSd.setType(serviceType);
        templateDfad.addServices(templateSd);

        SearchConstraints sc = new SearchConstraints();
        sc.setMaxResults(new Long(-1));

        addBehaviour(new SubscriptionInitiator(this, DFService.createSubscriptionMessage(this, getDefaultDF(), templateDfad, sc)) {

            @Override
            protected void handleInform(ACLMessage inform) {
                System.out.println("Agente " + myAgent.getLocalName() + ": Notificacion recibida del Agente DF");
                try {
                    DFAgentDescription[] results = DFService.decodeNotification(inform.getContent());
                    if (results.length > 0) {
                        subscribeAgents = new AID[results.length];
                        for (int i = 0; i < results.length; ++i) {
                            subscribeAgents[i] = results[i].getName();
                        }
                    }
                } catch (FIPAException fex) {
                    System.out.println("FIPAException: " + fex.getMessage());
                }
            }
        });
    }

    public class RegisterServiceBehaviour extends OneShotBehaviour {

        @Override
        public void action() {
            registerService();
        }

    }

    public class DeregisterServiceBehaviour extends OneShotBehaviour {

        @Override
        public void action() {
            deregisterService();
        }

    }

    public class SearchBehaviour extends OneShotBehaviour {

        @Override
        public void action() {
            agents = searchService();
        }
        
    }

    public class SubscribeBehaviour extends OneShotBehaviour {

        @Override
        public void action() {
            subscribeService();
        }

    }

    public class SendAgentBehaviour extends OneShotBehaviour {
        private String mensaje;
        private AID destino;

        public SendAgentBehaviour(String mensaje, AID agent) {
            this.mensaje = mensaje;
            this.destino = agent;
        }

        @Override
        public void action() {
            ACLMessage acl = new ACLMessage(ACLMessage.INFORM);
            acl.addReceiver(destino);
            acl.setContent(mensaje);
            send(acl);
        }

    }

    public class SendBehaviour extends OneShotBehaviour {
        private String mensaje;

        public SendBehaviour(String mensaje) {
            this.mensaje = mensaje;
        }
        
        @Override
        public void action() {
            agents = searchService();
            ACLMessage acl = new ACLMessage(ACLMessage.INFORM);
            for (int index = 0; index < agents.length; index++) {
                // Para agregar los agentes que reciben el mensaje, excepto a el
                //if (!agents[index].getName().equals(myAgent.getName())) {
                //    acl.addReceiver(agents[index]);
                //}
                // Para agregar los agentes que reciben el mensaje
                acl.addReceiver(agents[index]);
            }
            acl.setContent(mensaje);
            send(acl);
        }

    }

    public class ReceiveBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            // Para recibir el mensaje, excepto el que el envia
            //MessageTemplate mt = MessageTemplate.not(MessageTemplate.MatchSender(myAgent.getAID()));
            //ACLMessage acl = receive(mt);
            // Para recibir el mensaje
            ACLMessage acl = receive();
            if (acl != null) {
                System.out.println(myAgent.getLocalName() + " -> " + acl.getSender().getLocalName() + " dice: " + acl.getContent());
            }
            else {
                block();
            }
        }

    }

}
