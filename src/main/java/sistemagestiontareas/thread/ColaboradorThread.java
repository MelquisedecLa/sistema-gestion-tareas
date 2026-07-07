package sistemagestiontareas.thread;

import sistemagestiontareas.enums.Estado;
import sistemagestiontareas.model.Tarea;
import sistemagestiontareas.model.Usuario;

import java.util.Random;

/**
 * Hilo que simula un usuario colaborando en una tarea compartida.
 */
public class ColaboradorThread extends Thread {
    private Tarea tarea;
    private Usuario usuario;
    private int operaciones;
    private Random random = new Random();

    public ColaboradorThread(String nombre, Tarea tarea, Usuario usuario, int operaciones) {
        super(nombre);
        this.tarea = tarea;
        this.usuario = usuario;
        this.operaciones = operaciones;
    }

    @Override
    public void run() {
        System.out.println(usuario.getNombre() + " comenzó a colaborar en: " +
                tarea.getTitulo());

        for (int i = 0; i < operaciones; i++) {
            try {
                // Esperar entre 1-3 segundos
                Thread.sleep(1000 + random.nextInt(2000));

                // Realizar una operación aleatoria
                int operacion = random.nextInt(4);
                switch (operacion) {
                    case 0 -> cambiarEstado();
                    case 1 -> actualizarDescripcion();
                    case 2 -> verTarea();
                    case 3 -> compartirConOtro();
                }

            } catch (InterruptedException e) {
                System.out.println(usuario.getNombre() + " fue interrumpido.");
                break;
            }
        }

        System.out.println(usuario.getNombre() + " terminó de colaborar en la tarea.");
    }

    private void cambiarEstado() {
        Estado[] estados = Estado.values();
        Estado nuevoEstado = estados[random.nextInt(estados.length)];

        // Usar el método sincronizado de Tarea
        tarea.operacionConcurrente("Cambiando estado a: " + nuevoEstado, usuario);
        tarea.cambiarEstado(nuevoEstado);

        System.out.println("   ✓ " + usuario.getNombre() + " cambió estado a " + nuevoEstado);
    }

    private void actualizarDescripcion() {
        String nuevaDesc = "Actualizada por " + usuario.getNombre() +
                " en " + System.currentTimeMillis();

        tarea.operacionConcurrente("Actualizando descripción", usuario);
        tarea.setDescripcion(nuevaDesc);

        System.out.println(usuario.getNombre() + " actualizó la descripción");
    }

    private void verTarea() {
        tarea.operacionConcurrente("Visualizando tarea", usuario);

        System.out.println("      " + usuario.getNombre() + " visualizó la tarea");
        System.out.println("      Título: " + tarea.getTitulo());
        System.out.println("      Estado: " + tarea.getEstado());
        System.out.println("      Colaboradores: " + tarea.getCantidadColaboradores());
    }

    private void compartirConOtro() {
        // Simular que comparte la tarea con otro usuario (solo para demostración)
        tarea.operacionConcurrente("Compartiendo tarea", usuario);
        System.out.println("   ✓ " + usuario.getNombre() + " compartió la tarea con otros");
    }
}