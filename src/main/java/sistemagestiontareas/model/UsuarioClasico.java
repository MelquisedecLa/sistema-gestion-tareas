package sistemagestiontareas.model;

/**
 * Representa a un usuario clásico dentro del sistema.
 * Este tipo de usuario tiene ciertas restricciones funcionales
 * en comparación con un usuario premium.
 */
public class UsuarioClasico extends Usuario {

    /**
     * Constructor de la clase UsuarioClasico.
     *
     * @param nombre nombre del usuario
     * @param id identificador del usuario
     * @param email correo electrónico del usuario
     * @param password contraseña del usuario
     */
    public UsuarioClasico(String nombre, int id, String email, String password) {
        super(nombre, id, email, password);
    }

    /**
     * Verifica el límite de tareas permitido para un usuario clásico.
     */
    public void verificarLimiteTareas() {
        System.out.println("Verificando límite de tareas del usuario clásico...");
    }

    /**
     * Simula la creación de un elemento con restricciones propias del usuario clásico.
     */
    public void crearElemento() {
        System.out.println("Usuario clásico crea un elemento con restricciones.");
    }
}