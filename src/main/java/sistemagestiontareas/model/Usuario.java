package sistemagestiontareas.model;

public class Usuario {


    private int id;
    private String nombre;
    private String email;
    private String password;

    public Usuario() {}

    public Usuario(String nombre, int id, String email, String password) {
        this.nombre = nombre;
        this.id = id;
        this.email = email;
        this.password = password;
    }

    //public abstract void crearElemento();

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
