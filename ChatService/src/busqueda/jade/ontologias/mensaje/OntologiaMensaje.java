
package busqueda.jade.ontologias.mensaje;

import jade.content.onto.BeanOntology;
import jade.content.onto.BeanOntologyException;
import jade.content.onto.Ontology;

/**
 *
 * @author almunoz
 */
public class OntologiaMensaje extends BeanOntology {
    public static final String ONTOLOGY_NAME = "Ontologia Mensaje";
    private static Ontology instacia;

    public static Ontology getInstance() {
        if (instacia == null) {
            instacia = new OntologiaMensaje();
        }
        return instacia;
    }

    private OntologiaMensaje() {
        super(ONTOLOGY_NAME);
        try {
            // Concepto
            this.add(Mensaje.class);
            // Predicados
            this.add(EnviarMensaje.class);
            this.add(MostrarMensaje.class);
        } catch (BeanOntologyException ex) {
            System.out.println("BeanOntologyException: " + ex.getMessage());
        }
        
    }

}
