package sistemagestiontareas.model;

import sistemagestiontareas.enums.Prioridad;

import java.time.LocalDate;

public class Recordatorio extends Elemento {

    private LocalDate fecha;

    public Recordatorio(int id, String titulo, String descripcion, Prioridad prioridad, LocalDate fecha) {
        super(id, titulo, descripcion, prioridad);
        this.fecha = fecha;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void reprogramarFecha(LocalDate nuevaFecha) {
        this.fecha = nuevaFecha;
        System.out.println("La fecha del recordatorio fue cambiada a: " + nuevaFecha);
    }

    @Override
    public void mostrarInfo() {
        super.mostrarInfo();
        System.out.println("Fecha del recordatorio: " + fecha);
    }
}