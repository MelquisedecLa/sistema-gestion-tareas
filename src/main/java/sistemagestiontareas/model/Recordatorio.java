package sistemagestiontareas.model;

import sistemagestiontareas.enums.Prioridad;
import java.time.LocalDate;

public class Recordatorio extends Elemento {

    public Recordatorio(int id, String titulo, String descripcion,
                        Prioridad prioridad, LocalDate fechaLimite) {
        super(id, titulo, descripcion, prioridad, fechaLimite);
    }

    // Método "+ reprogramarFecha(LocalDate nuevaFecha)"
    // Método público de Recordatorio — permite cambiar la fecha límite.
    public void reprogramarFecha(LocalDate nuevaFecha) {
        setFechaLimite(nuevaFecha);
        System.out.println("La fecha del recordatorio fue cambiada a: " + nuevaFecha);
    }

    // Método "+ MostrarInfo(): void"
    @Override
    public void mostrarInfo() {
        super.mostrarInfo();
        System.out.println("Fecha límite del recordatorio: " + getFechaLimite());
    }


}