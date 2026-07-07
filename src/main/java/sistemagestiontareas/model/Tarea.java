package sistemagestiontareas.model;

import sistemagestiontareas.enums.Estado;
import sistemagestiontareas.enums.Prioridad;
import sistemagestiontareas.model.Usuario;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa una tarea dentro del sistema.
 */
public class Tarea extends Elemento {

    private Estado estado;
    private List<Usuario> colaboradores;
    private final Object lock = new Object();

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
        super(id, titulo, descripcion, prioridad, fechaLimite);
        this.estado = estado;
        this.colaboradores = new ArrayList<>();
    }

    /** @return estado actual de la tarea */
    public Estado getEstado() { return estado; }

    /** @param estado nuevo estado */
    public void setEstado(Estado estado) { this.estado = estado; }

    /** @return lista de colaboradores de la tarea */
    public List<Usuario> getColaboradores() { return colaboradores; }

    /**
     * Cambia el estado de la tarea de forma sincronizada (segura para hilos).
     *
     * @param nuevoEstado nuevo estado asignado
     */
    public synchronized void cambiarEstado(Estado nuevoEstado) {
        Estado estadoAnterior = this.estado;
        this.estado = nuevoEstado;
        System.out.println("El estado de la tarea cambió a: " + nuevoEstado);

        String mensaje = String.format(
                "[Thread-%s] Estado de tarea '%s' cambiado de %s a %s",
                Thread.currentThread().getName(),
                getTitulo(),
                estadoAnterior,
                nuevoEstado
        );
    }
    /**
     * Agrega un colaborador a la tarea de forma sincronizada.
     *
     * @param usuario usuario a agregar como colaborador
     */
    public synchronized void agregarColaborador(Usuario usuario) {
        if (!colaboradores.contains(usuario)) {
            colaboradores.add(usuario);
            System.out.println("[Thread-" + Thread.currentThread().getName() +
                    "] Usuario " + usuario.getNombre() +
                    " agregado como colaborador de la tarea: " + getTitulo());
        }
    }

    /**
     * Simula una operación concurrente en la tarea.
     * Útil para demostrar el acceso concurrente con hilos.
     *
     * @param operacion descripción de la operación
     * @param usuario usuario que realiza la operación
     */
    public void operacionConcurrente(String operacion, Usuario usuario) {
        synchronized (lock) {
            System.out.println("\n[Thread-" + Thread.currentThread().getName() +
                    "] " + usuario.getNombre() + " ejecutando: " + operacion);
            try {
                // Simular tiempo de procesamiento (500ms - 2000ms)
                Thread.sleep(500 + (int)(Math.random() * 1500));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("[Thread-" + Thread.currentThread().getName() +
                    "] ✓ " + usuario.getNombre() + " completó: " + operacion);
        }
    }

    /**
     * Obtiene el número de colaboradores.
     *
     * @return cantidad de colaboradores
     */
    public int getCantidadColaboradores() {
        return colaboradores.size();
    }

    public boolean esColaborador(Usuario usuario) {
        return colaboradores.contains(usuario);
    }


    @Override
    public void mostrarInfo() {
        super.mostrarInfo();
        System.out.println("Estado: " + estado);
        System.out.println("Fecha límite: " + getFechaLimite());
        System.out.println("Colaboradores (" + colaboradores.size() + "):");
        if (colaboradores.isEmpty()) {
            System.out.println("  Sin colaboradores");
        } else {
            for (Usuario u : colaboradores) {
                System.out.println("  - " + u.getNombre() + " (" + u.getEmail() + ")");
            }
        }
    }
}