
package busqueda.jade.ontologias.mensaje;

import jade.content.onto.BasicOntology;
import jade.content.onto.BeanOntology;
import jade.content.onto.BeanOntologyException;
import jade.content.onto.Ontology;

/**
 *
 * @author almunoz
 */
public class OntologiaMensaje extends BeanOntology {
    public static final String ONTOLOGY_NAME = "Ontologia Chat";
    private static Ontology instacia = new OntologiaMensaje();

    public static Ontology getInstance() {
        return instacia;
    }

    private OntologiaMensaje() {
        super(ONTOLOGY_NAME, BasicOntology.getInstance());
        try {
            // Concepto
            this.add(Mensaje.class);
            // Acciones
            this.add(Enviar.class);
            this.add(Mostrar.class);
        } catch (BeanOntologyException ex) {
            System.out.println("BeanOntologyException: " + ex.getMessage());
        }
        
    }

}
