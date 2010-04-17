
package busqueda.jade.comportamientos;

import busqueda.jade.UtilidadesJADE;
import busqueda.jade.agentes.AgenteJXTA;
import busqueda.jade.ontologias.servicio.OntologiaServicio;
import busqueda.jade.ontologias.servicio.PublicarServicio;
import busqueda.jade.ontologias.servicio.Servicio;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author almunoz
 */
public class PublicarServicioJXTABehaviour extends TickerBehaviour {
    private AID agenteJXTA;
    private Codec codec;
    private Ontology ontologiaServicio;
    private String tipo;
    private String descripcion;

    public PublicarServicioJXTABehaviour(Agent agente, long periodo, String tipo, String descripcion) {
        super(agente, periodo);
        this.agenteJXTA = null;
        this.codec = new SLCodec();
        this.ontologiaServicio = OntologiaServicio.getInstance();
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    @Override
    protected void onTick() {
        agenteJXTA = UtilidadesJADE.buscarAgente(myAgent, AgenteJXTA.TIPO_SERVICIO);
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
                servicio.setTipo(tipo);
                servicio.setDescripcion(descripcion);
                // Predicado
                PublicarServicio publicar = new PublicarServicio();
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
