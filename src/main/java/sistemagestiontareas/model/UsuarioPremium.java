package sistemagestiontareas.model;

import java.time.LocalDate;

public class UsuarioPremium extends Usuario {

    //representa cuándo vence la membresía mensual del usuario premium.
    private LocalDate fechaExpiracion;

    public UsuarioPremium(String nombre, int id, String email, String password, LocalDate fechaExpiracion) {
        super(nombre, id, email, password);
        // La fecha de expiración es un mes desde que se registra
        this.fechaExpiracion = fechaExpiracion;
    }

    // Método ÚNICO de UsuarioPremium — verifica si la membresía
    // del usuario premium sigue vigente comparando con la fecha actual.
    public boolean verificarMembresia() {
        return LocalDate.now().isBefore(fechaExpiracion);
    }

    public LocalDate getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(LocalDate fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
}