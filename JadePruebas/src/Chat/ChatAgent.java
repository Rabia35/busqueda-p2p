package Chat;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;
import jade.util.leap.Iterator;

/**
 *
 * @author almunoz
 */
public class ChatAgent extends Agent {
    private String serviceName;
    private String serviceType;
    // Para la busqueda
    private DFAgentDescription[] searchResults;
    private AID[] searchAgents;
    // Para la subscripcion
    private DFAgentDescription[] subscribeResults;
    private AID[] subscribeAgents;
    
    @Override
    protected void setup() {
        serviceName = "JADE-Chat";
        serviceType = "chat";
        System.out.println("El agente " + getAID().getName() + " se ha iniciado.");
        addBehaviour(new RegisterServiceBehaviour());
        addBehaviour(new SearchBehaviour());
        addBehaviour(new SubscribeBehaviour());
    }

    @Override
    protected void takeDown() {
        deregisterService();
        System.out.println("El servicio " + serviceName + " ya no esta registrado.");
        System.out.println("El agente " + getAID().getName() + " ha terminado.");
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
        } catch (FIPAException fex) {
            System.out.println("FIPAException: " + fex.getMessage());
        }
    }

    private void deregisterService() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fex) {
            System.out.println("FIPAException: " + fex.getMessage());
        }
    }

    private void searchService() {
        try {
            // Build the description used as template for the search
            DFAgentDescription templateDfad = new DFAgentDescription();
            ServiceDescription templateSd = new ServiceDescription();
            templateSd.setType(serviceType);
            templateDfad.addServices(templateSd);
            SearchConstraints sc = new SearchConstraints();
            // We want to receive 10 results at most
            sc.setMaxResults(new Long(10));
            DFAgentDescription[] results = DFService.search(this, templateDfad, sc);
            if (results.length > 0) {
                System.out.println("El agente " + getLocalName() + " encontro los siguientes servicios de chat: ");
                for (int i = 0; i < results.length; ++i) {
                    DFAgentDescription dfad = results[i];
                    AID provider = dfad.getName();
                    // The same agent may provide several services; we are only interested
                    // in the chat one
                    Iterator it = dfad.getAllServices();
                    while (it.hasNext()) {
                        ServiceDescription sd = (ServiceDescription) it.next();
                        if (sd.getType().equals(serviceType)) {
                            System.out.println("El servicio " + sd.getName() + " lo provee el agente " + provider.getName());
                        }
                    }
                }
            } else {
                System.out.println("El agente " + getLocalName() + " no encontro ningun servicio de chat.");
            }
        } catch (FIPAException fex) {
            System.out.println("FIPAException: " + fex.getMessage());
        }
    }

    private void subscribeService() {
        // Build the description used as template for the subscription
        DFAgentDescription templateDfad = new DFAgentDescription();
        ServiceDescription templateSd = new ServiceDescription();
        templateSd.setType(serviceType);
        templateDfad.addServices(templateSd);

        SearchConstraints sc = new SearchConstraints();
        // We want to receive 10 results at most
        sc.setMaxResults(new Long(10));

        addBehaviour(new SubscriptionInitiator(this, DFService.createSubscriptionMessage(this, getDefaultDF(), templateDfad, sc)) {

            @Override
            protected void handleInform(ACLMessage inform) {
                System.out.println("Agente " + getLocalName() + ": Notificacion recibida del Agente DF");
                try {
                    DFAgentDescription[] results = DFService.decodeNotification(inform.getContent());
                    if (results.length > 0) {
                        for (int i = 0; i < results.length; ++i) {
                            DFAgentDescription dfd = results[i];
                            AID provider = dfd.getName();
                            // The same agent may provide several services; we are only interested
                            // in the chat one
                            Iterator it = dfd.getAllServices();
                            while (it.hasNext()) {
                                ServiceDescription sd = (ServiceDescription) it.next();
                                if (sd.getType().equals(serviceType)) {
                                    System.out.println("El servicio " + sd.getName() + " lo provee el agente " + provider.getName());
                                }
                            }
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
            System.out.println("Register Service Behaviour");
            registerService();
            System.out.println("El servicio " + serviceName + " se ha registrado.");
        }

    }

    public class DeregisterServiceBehaviour extends OneShotBehaviour {

        @Override
        public void action() {
            System.out.println("Deregister Service Behaviour");
            deregisterService();
            System.out.println("El servicio " + serviceName + " ya no esta registrado.");
        }

    }

    public class SearchBehaviour extends OneShotBehaviour {

        @Override
        public void action() {
            System.out.println("Search Behaviour");
            searchService();
        }
        
    }

    public class SubscribeBehaviour extends OneShotBehaviour {

        @Override
        public void action() {
            System.out.println("Subscribe Behaviour");
            subscribeService();
        }

    }

    public class SendBehaviour extends OneShotBehaviour {

        @Override
        public void action() {
            System.out.println("Send Behaviour");
        }

    }

    public class ReceiveBehaviour extends OneShotBehaviour {

        @Override
        public void action() {
            System.out.println("Receive Behaviour");
        }

    }

}
