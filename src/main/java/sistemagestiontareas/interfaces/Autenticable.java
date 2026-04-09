package sistemagestiontareas.interfaces;

/**
 * Define las operaciones básicas de autenticación de un usuario en el sistema.
 */
public interface Autenticable {

    /**
     * Permite iniciar sesión con las credenciales proporcionadas.
     *
     * @param username nombre de usuario
     * @param clave contraseña del usuario
     * @return si las credenciales son correctas, false en caso contrario
     */
    boolean iniciarSesion(String username, String clave);

    /**
     * Cierra la sesión del usuario actual.
     */
    void cerrarSesion();
}