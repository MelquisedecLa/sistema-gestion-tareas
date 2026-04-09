package sistemagestiontareas.model;

import sistemagestiontareas.enums.Estado;
import sistemagestiontareas.enums.Prioridad;

public class Tarea extends Elemento {

    private Estado estado;

    public Tarea(int id, String titulo, String descripcion, Prioridad prioridad, Estado estado) {
        super(id, titulo, descripcion, prioridad);
        this.estado = estado;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public void cambiarEstado(Estado nuevoEstado) {
        this.estado = nuevoEstado;
        System.out.println("El estado de la tarea cambió a: " + nuevoEstado);
    }

    @Override
    public void mostrarInfo() {
        super.mostrarInfo();
        System.out.println("Estado: " + estado);
    }
}