package sistemagestiontareas.model;

import sistemagestiontareas.enums.Estado;
import sistemagestiontareas.enums.Prioridad;
import sistemagestiontareas.model.Tarea;
import sistemagestiontareas.model.Usuario;
import sistemagestiontareas.thread.ColaboradorThread;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de tareas con simulación de acceso concurrente.
 */
public class GestorTareas {
    private List<Thread> hilosActivos = new ArrayList<>();
    private Tarea tareaCompartida;
    private Usuario usuarioCreador;

    public GestorTareas(Usuario creador) {
        this.usuarioCreador = creador;
    }

    /**
     * Crea una tarea compartida y agrega colaboradores.
     */
    public void crearTareaCompartida(String titulo, String descripcion, Prioridad prioridad,
                                     LocalDate fechaLimite, List<Usuario> colaboradores) {
        int id = usuarioCreador.getElementos().size() + 1;
        tareaCompartida = new Tarea(id, titulo, descripcion, prioridad,
                Estado.PENDIENTE, fechaLimite);

        // Agregar al creador como colaborador
        tareaCompartida.agregarColaborador(usuarioCreador);
        usuarioCreador.crearElemento(tareaCompartida);

        // Agregar colaboradores
        for (Usuario colaborador : colaboradores) {
            tareaCompartida.agregarColaborador(colaborador);
            colaborador.crearElemento(tareaCompartida);
        }

        System.out.println("═══════════════════════════════════════");
        System.out.println("   TAREA COMPARTIDA CREADA");
        System.out.println("   Título: " + titulo);
        System.out.println("   Creador: " + usuarioCreador.getNombre());
        System.out.println("   Colaboradores: " + colaboradores.size());
        System.out.println("═══════════════════════════════════════");
    }

    /**
     * Simula acceso concurrente con múltiples hilos.
     */
    public void simularAccesoConcurrente() {
        if (tareaCompartida == null) {
            System.out.println(" No hay tarea compartida.");
            return;
        }

        System.out.println("\n🔄 INICIANDO SIMULACIÓN DE ACCESO CONCURRENTE");
        System.out.println("   Tarea: " + tareaCompartida.getTitulo());
        System.out.println("   Colaboradores: " + tareaCompartida.getCantidadColaboradores());
        System.out.println("─────────────────────────────────────────");

        // Crear y lanzar un hilo por cada colaborador
        for (Usuario colaborador : tareaCompartida.getColaboradores()) {
            ColaboradorThread hilo = new ColaboradorThread(
                    "Hilo-" + colaborador.getNombre(),
                    tareaCompartida,
                    colaborador,
                    4 // 4 operaciones por colaborador
            );
            hilosActivos.add(hilo);
            hilo.start();
            System.out.println("     Hilo iniciado para: " + colaborador.getNombre());
        }

        // Esperar a que todos los hilos terminen
        for (Thread hilo : hilosActivos) {
            try {
                hilo.join();
            } catch (InterruptedException e) {
                System.err.println("Error esperando hilo: " + e.getMessage());
            }
        }

        System.out.println("─────────────────────────────────────────");
        System.out.println("   SIMULACIÓN COMPLETADA");
        System.out.println("   Estado final: " + tareaCompartida.getEstado());
        System.out.println("   Colaboradores: " + tareaCompartida.getCantidadColaboradores());
        System.out.println("\n RESUMEN FINAL:");
        tareaCompartida.mostrarInfo();
    }

    public Tarea getTareaCompartida() {
        return tareaCompartida;
    }

    public void detenerSimulacion() {
        for (Thread hilo : hilosActivos) {
            if (hilo.isAlive()) {
                hilo.interrupt();
            }
        }
        hilosActivos.clear();
        System.out.println(" Simulación detenida.");
    }
}
