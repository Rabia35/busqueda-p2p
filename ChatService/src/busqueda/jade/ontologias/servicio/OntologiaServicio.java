
package busqueda.jade.ontologias.servicio;

import jade.content.onto.BasicOntology;
import jade.content.onto.BeanOntology;
import jade.content.onto.BeanOntologyException;
import jade.content.onto.Ontology;

/**
 *
 * @author almunoz
 */
public class OntologiaServicio extends BeanOntology {
    public static final String ONTOLOGY_NAME = "Ontologia Servicio";
    private static Ontology instacia = new OntologiaServicio();

    public static Ontology getInstance() {
        return instacia;
    }

    private OntologiaServicio() {
        super(ONTOLOGY_NAME, BasicOntology.getInstance());
        try {
            // Concepto
            this.add(Servicio.class);
            // Acciones
            this.add(Publicar.class);
            this.add(Despublicar.class);
        } catch (BeanOntologyException ex) {
            System.out.println("BeanOntologyException: " + ex.getMessage());
        }
        
    }

}
