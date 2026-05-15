package sistemagestiontareas.thread;

import sistemagestiontareas.model.Elemento;
import sistemagestiontareas.model.Usuario;

/**
 * Hilo que simula el acceso concurrente de un usuario al sistema.
 */
public class GestorTareasThread extends Thread {

    private Usuario usuario;
    private Elemento elemento;

    /**
     * Constructor del hilo.
     *
     * @param usuario  usuario que realizará la acción
     * @param elemento elemento que se intentará crear
     */
    public GestorTareasThread(Usuario usuario, Elemento elemento) {
        this.usuario = usuario;
        this.elemento = elemento;
    }

    /**
     * Asigna un usuario al gestor.
     *
     * @param usuario usuario a asignar
     */
    public void asignarUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Ejecuta la creación del elemento de forma concurrente.
     */
    @Override
    public void run() {
        synchronized (usuario) {
            System.out.println("[Thread " + Thread.currentThread().getName() +
                    "] " + usuario.getNombre() + " creando: " + elemento.getTitulo());
            usuario.crearElemento(elemento);
            System.out.println("[Thread " + Thread.currentThread().getName() + "] Finalizado.");
        }
    }
}