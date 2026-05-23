package sistemagestiontareas.interfaces;

/**
 * Define las operaciones básicas de autenticación de un usuario.
 */
public interface Autenticable {

    /**
     * Permite iniciar sesión con las credenciales proporcionadas.
     *
     * @param email correo del usuario
     * @param clave contraseña del usuario
     * @return true si las credenciales son correctas, false en caso contrario
     */
    boolean iniciarSesion(String email, String clave);

    /**
     * Cierra la sesión del usuario actual.
     */
    void cerrarSesion();
}