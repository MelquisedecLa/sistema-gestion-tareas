package sistemagestiontareas.model;

import sistemagestiontareas.enums.Estado;
import sistemagestiontareas.enums.Prioridad;
import java.time.LocalDate;

/**
 * Clase que representa una tarea dentro del sistema.
 */
public class Tarea extends Elemento {

    private Estado estado;
    private LocalDate fechaLimite;

    /**
     * Constructor de la clase Tarea.
     *
     * @param id          identificador de la tarea
     * @param titulo      título de la tarea
     * @param descripcion descripción de la tarea
     * @param prioridad   prioridad de la tarea
     * @param estado      estado actual de la tarea
     * @param fechaLimite fecha límite de entrega
     */
    public Tarea(int id, String titulo, String descripcion,
                 Prioridad prioridad, Estado estado, LocalDate fechaLimite) {
        super(id, titulo, descripcion, prioridad);
        this.estado = estado;
        this.fechaLimite = fechaLimite;
    }

    /** @return estado actual de la tarea */
    public Estado getEstado() { return estado; }

    /** @param estado nuevo estado */
    public void setEstado(Estado estado) { this.estado = estado; }

    /** @return fecha límite de la tarea */
    public LocalDate getFechaLimite() { return fechaLimite; }

    /** @param fechaLimite nueva fecha límite */
    public void setFechaLimite(LocalDate fechaLimite) { this.fechaLimite = fechaLimite; }

    /**
     * Cambia el estado de la tarea.
     *
     * @param nuevoEstado nuevo estado asignado
     */
    public void cambiarEstado(Estado nuevoEstado) {
        this.estado = nuevoEstado;
        System.out.println("El estado de la tarea cambió a: " + nuevoEstado);
    }

    @Override
    public void mostrarInfo() {
        super.mostrarInfo();
        System.out.println("Estado: " + estado);
        System.out.println("Fecha límite: " + fechaLimite);
    }
}