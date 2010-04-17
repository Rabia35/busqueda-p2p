
package busqueda.jade.ontologias.servicio;

import jade.content.Concept;

/**
 *
 * @author almunoz
 */
public class Servicio implements Concept {
    private String tipo;
    private String descripcion;

    public Servicio() {
        this.tipo = "tipo";
        this.descripcion = "tipo-descripcion";
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
