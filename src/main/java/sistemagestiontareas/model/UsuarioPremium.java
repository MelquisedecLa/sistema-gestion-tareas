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
     * Permite crear elementos sin restricciones.
     *
     * @param elemento elemento que será agregado
     */
    public void crearElemento(Elemento elemento) {
        agregarElemento(elemento);
        System.out.println("Elemento creado por usuario premium.");
    }
}