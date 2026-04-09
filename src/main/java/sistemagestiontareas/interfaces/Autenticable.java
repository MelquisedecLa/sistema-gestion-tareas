package sistemagestiontareas.interfaces;

public interface Autenticable {
    boolean iniciarSesion(String usuario, String contrasena);
    void cerrarSesion();
}