package sistemagestiontareas;

import sistemagestiontareas.model.Usuario;

/**
 * Mantiene el estado del usuario que inició sesión, para que esté
 * disponible desde cualquier controlador de la aplicación JavaFX.
 */
public class Sesion {

    private static Usuario usuarioActual;

    private Sesion() {}

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }
}