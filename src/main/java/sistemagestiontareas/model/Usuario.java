package sistemagestiontareas.model;

/**
 * Clase que representa un usuario del sistema.
 */
public class Usuario {

    private int id;
    private String nombre;
    private String email;
    private String password;

    /** Constructor vacío */
    public Usuario() {}

    /**
     * Constructor con parámetros.
     *
     * @param nombre nombre del usuario
     * @param id identificador del usuario
     * @param email correo electrónico del usuario
     * @param password contraseña del usuario
     */
    public Usuario(String nombre, int id, String email, String password) {
        this.nombre = nombre;
        this.id = id;
        this.email = email;
        this.password = password;
    }

    /** @return correo electrónico del usuario */
    public String getEmail() {
        return email;
    }

    /** @param email nuevo correo electrónico */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return id del usuario */
    public int getId() {
        return id;
    }

    /** @param id nuevo id del usuario */
    public void setId(int id) {
        this.id = id;
    }

    /** @return nombre del usuario */
    public String getNombre() {
        return nombre;
    }

    /** @param nombre nuevo nombre del usuario */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** @return contraseña del usuario */
    public String getPassword() {
        return password;
    }

    /** @param password nueva contraseña */
    public void setPassword(String password) {
        this.password = password;
    }
}