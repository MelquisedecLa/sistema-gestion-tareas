package sistemagestiontareas.model;

import sistemagestiontareas.enums.Prioridad;

import java.time.LocalDate;

/**
 * Clase que representa un recordatorio dentro del sistema.
 */
public class Recordatorio extends Elemento {

    private LocalDate fecha;

    /**
     * Constructor de la clase Recordatorio.
     *
     * @param id identificador del recordatorio
     * @param titulo título del recordatorio
     * @param descripcion descripción del recordatorio
     * @param prioridad prioridad del recordatorio
     * @param fecha fecha asignada al recordatorio
     */
    public Recordatorio(
            int id,
            String titulo,
            String descripcion,
            Prioridad prioridad,
            LocalDate fecha) {
        super(id, titulo, descripcion, prioridad);
        this.fecha = fecha;
    }

    /** @return fecha del recordatorio */
    public LocalDate getFecha() {
        return fecha;
    }

    /** @param fecha nueva fecha del recordatorio */
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    /**
     * Permite cambiar la fecha del recordatorio.
     *
     * @param nuevaFecha nueva fecha asignada
     */
    public void reprogramarFecha(LocalDate nuevaFecha) {
        this.fecha = nuevaFecha;
        System.out.println("La fecha del recordatorio fue cambiada a: " + nuevaFecha);
    }

    /**
     * Muestra la información del recordatorio.
     */
    @Override
    public void mostrarInfo() {
        super.mostrarInfo();
        System.out.println("Fecha del recordatorio: " + fecha);
    }
}