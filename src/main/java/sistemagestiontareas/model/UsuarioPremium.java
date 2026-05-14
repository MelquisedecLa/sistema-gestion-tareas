package sistemagestiontareas.model;

/**
 * Representa a un usuario premium dentro del sistema.
 * Tiene acceso completo a todas las funcionalidades sin restricciones.
 */
public class UsuarioPremium extends Usuario {

    public UsuarioPremium(String nombre, int id, String email, String password) {
        super(nombre, id, email, password);
    }

    /**
     * Indica que el usuario tiene acceso completo al sistema.
     */
    public void accesoCompleto() {
        System.out.println("Acceso completo habilitado.");
    }

    /**
     * Crea un elemento sin restricciones.
     *
     * @param elemento elemento a crear
     */
    @Override
    public void crearElemento(Elemento elemento) {
        agregarElemento(elemento);
        System.out.println("Elemento creado por usuario premium.");
    }
}