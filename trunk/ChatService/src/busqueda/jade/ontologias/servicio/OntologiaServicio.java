
package busqueda.jade.ontologias.servicio;

import jade.content.onto.BeanOntology;
import jade.content.onto.BeanOntologyException;
import jade.content.onto.Ontology;

/**
 *
 * @author almunoz
 */
public class OntologiaServicio extends BeanOntology {
    public static final String ONTOLOGY_NAME = "Ontologia Servicio";
    private static Ontology instacia;

    public static Ontology getInstance() {
        if (instacia == null) {
            instacia = new OntologiaServicio();
        }
        return instacia;
    }

    private OntologiaServicio() {
        super(ONTOLOGY_NAME);
        try {
            // Concepto
            this.add(Servicio.class);
            // Predicados
            this.add(PublicarServicio.class);
            this.add(DespublicarServicio.class);
        } catch (BeanOntologyException ex) {
            System.out.println("OntologyException: " + ex.getMessage());
        }        
    }

}
