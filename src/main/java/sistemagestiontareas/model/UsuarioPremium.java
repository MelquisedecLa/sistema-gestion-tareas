package sistemagestiontareas.model;

/**
 * Representa a un usuario premium dentro del sistema.
 * Este tipo de usuario tiene acceso completo a las funcionalidades.
 */
public class UsuarioPremium extends Usuario {

    /**
     * Constructor de la clase UsuarioPremium.
     *
     * @param nombre nombre del usuario
     * @param id identificador del usuario
     * @param email correo electrónico del usuario
     * @param password contraseña del usuario
     */
    public UsuarioPremium(String nombre, int id, String email, String password) {
        super(nombre, id, email, password);
    }

    /**
     * Indica que el usuario tiene acceso completo.
     */
    public void accesoCompleto() {
        System.out.println("Acceso completo habilitado.");
    }

    /**
     * Simula la creación de un elemento sin restricciones.
     */
    public void crearElemento() {
        System.out.println("Usuario premium crea elemento sin restricciones.");
    }
}