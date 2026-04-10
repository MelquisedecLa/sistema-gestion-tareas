package sistemagestiontareas.model;

import sistemagestiontareas.enums.Estado;
import sistemagestiontareas.enums.Prioridad;

/**
 * Clase que representa una tarea dentro del sistema.
 */
public class Tarea extends Elemento {

    private Estado estado;

    /**
     * Constructor de la clase Tarea.
     *
     * @param id identificador de la tarea
     * @param titulo título de la tarea
     * @param descripcion descripción de la tarea
     * @param prioridad prioridad de la tarea
     * @param estado estado actual de la tarea
     */
    public Tarea(
            int id,
            String titulo,
            String descripcion,
            Prioridad prioridad,
            Estado estado) {
        super(id, titulo, descripcion, prioridad);
        this.estado = estado;
    }

    /** @return estado actual de la tarea */
    public Estado getEstado() {
        return estado;
    }

    /** @param estado nuevo estado de la tarea */
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    /**
     * Permite cambiar el estado de la tarea.
     *
     * @param nuevoEstado nuevo estado asignado
     */
    public void cambiarEstado(Estado nuevoEstado) {
        this.estado = nuevoEstado;
        System.out.println("El estado de la tarea cambió a: " + nuevoEstado);
    }

    /**
     * Muestra la información de la tarea.
     */
    @Override
    public void mostrarInfo() {
        super.mostrarInfo();
        System.out.println("Estado: " + estado);
    }
}