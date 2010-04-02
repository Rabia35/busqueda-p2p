
package busqueda.jade.ontologias.servicio;

import jade.content.Concept;

/**
 *
 * @author almunoz
 */
public class Servicio implements Concept {
    private String nombre;
    private String tipo;
    private String descripcion;

    public Servicio() {
        this.nombre = "tipo-service";
        this.tipo = "tipo";
        this.descripcion = "tipo-descripcion";
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
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
