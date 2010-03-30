
package busqueda.jade.chat;

import busqueda.jxta.JXTAManager;
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
public class JXTAAgent extends Agent {
    public static String NOMBRE_SERVICIO = "chat-jxta-service";
    public static String TIPO_SERVICIO = "chat-jxta";
    public static String DESCRIPCION_SERVICIO = "chat-jxta-descripcion";
    // Para el nodo JXTA
    private JXTAManager jxta;
    // Agente del Chat
    private AID chat;    

    @Override
    protected void setup() {
        // Argumentos
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            jxta = (JXTAManager) args[0];
        } else {
            doDelete();
        }
        System.out.println("El agente " + this.getName() + " se ha iniciado.");
        // Permite la comunicacion O2A: Object to Agent
        this.setEnabledO2ACommunication(true, 0);
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
            sd.setName(JXTAAgent.NOMBRE_SERVICIO);
            sd.setType(JXTAAgent.TIPO_SERVICIO);
            dfad.addServices(sd);
            // Registrar la descripcion en el DF
            DFService.register(this, dfad);
            System.out.println("El servicio " + JXTAAgent.NOMBRE_SERVICIO + " se ha registrado.");
        } catch (FIPAException fex) {
            System.out.println("FIPAException: " + fex.getMessage());
        }
    }

    private void deregistrarServicio() {
        try {
            DFService.deregister(this);
            System.out.println("El servicio " + JXTAAgent.NOMBRE_SERVICIO + " ya no esta registrado.");
        } catch (FIPAException fex) {
            System.out.println("FIPAException: " + fex.getMessage());
        }
    }

    private boolean buscarAgenteChat() {
        try {
            // Build the description used as template for the search
            DFAgentDescription templateDfad = new DFAgentDescription();
            ServiceDescription templateSd = new ServiceDescription();
            templateSd.setType(ChatAgent.TIPO_SERVICIO);
            templateDfad.addServices(templateSd);

            SearchConstraints sc = new SearchConstraints();
            sc.setMaxResults(new Long(1));

            DFAgentDescription[] results = DFService.search(this, templateDfad, sc);
            if (results.length > 0) {
                chat = results[0].getName();
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
                myAgent.addBehaviour(new EnviarMensajeBehaviour(mensaje));
            } else {
                this.block();
            }
        }
    }

    public class EnviarMensajeBehaviour extends OneShotBehaviour {
        private String mensaje;

        public EnviarMensajeBehaviour(String mensaje) {
            this.mensaje = mensaje;
        }

        @Override
        public void action() {
            ACLMessage acl = new ACLMessage(ACLMessage.INFORM);
            acl.addReceiver(chat);
            acl.setContent(mensaje);
            myAgent.send(acl);
        }
    }

    public class RecibirMensajeBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // Para recibir el mensaje, dependiendo el tipo realizo la accion en JXTA
            ACLMessage acl = receive();
            if (acl != null) {
                String mensaje = acl.getContent();
                jxta.enviarMensajeChat(acl.getSender().getName() + " dice: " + mensaje);
            } else {
                block();
            }
        }
    }

}
