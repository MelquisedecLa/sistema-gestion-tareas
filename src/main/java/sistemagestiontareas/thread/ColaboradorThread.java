package sistemagestiontareas.thread;

import sistemagestiontareas.dao.TareaDAO;
import sistemagestiontareas.dao.TareaDAOImpl;
import sistemagestiontareas.enums.Estado;
import sistemagestiontareas.model.Tarea;
import sistemagestiontareas.model.Usuario;

import java.util.Random;

/**
 * Hilo que simula un usuario colaborando en una tarea compartida al mismo tiempo que el dueño.
 */
public class ColaboradorThread extends Thread {

    private final Tarea tarea;
    private final Usuario usuario;
    private final int operaciones;
    private final Random random = new Random();
    private final TareaDAO tareaDAO = new TareaDAOImpl();
    private Runnable alTerminarOperacion;

    public ColaboradorThread(String nombre, Tarea tarea, Usuario usuario, int operaciones) {
        super(nombre);
        this.tarea = tarea;
        this.usuario = usuario;
        this.operaciones = operaciones;
    }

    // Permite que la UI se refresque cada vez que el hilo hace un cambio
    public void setAlTerminarOperacion(Runnable callback) {
        this.alTerminarOperacion = callback;
    }

    @Override
    public void run() {
        tarea.operacionConcurrente("comenzó a colaborar en \"" + tarea.getTitulo() + "\"", usuario);

        for (int i = 0; i < operaciones; i++) {
            try {
                Thread.sleep(1500 + random.nextInt(2000));

                if (random.nextBoolean()) {
                    cambiarEstado();
                } else {
                    verTarea();
                }

                if (alTerminarOperacion != null) {
                    alTerminarOperacion.run();
                }

            } catch (InterruptedException e) {
                tarea.operacionConcurrente(usuario.getNombre() + " fue interrumpido", usuario);
                break;
            }
        }

        tarea.operacionConcurrente("terminó de colaborar", usuario);
    }

    private void cambiarEstado() {
        Estado[] estados = Estado.values();
        Estado nuevoEstado = estados[random.nextInt(estados.length)];

        tarea.operacionConcurrente("cambiando estado a " + nuevoEstado, usuario);
        tarea.cambiarEstado(nuevoEstado);
        tareaDAO.actualizarEstado(tarea.getId(), nuevoEstado);
    }

    private void verTarea() {
        tarea.operacionConcurrente("visualizando la tarea (estado actual: " + tarea.getEstado() + ")", usuario);
    }
}