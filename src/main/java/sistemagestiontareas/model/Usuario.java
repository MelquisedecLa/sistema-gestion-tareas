package sistemagestiontareas.model;

import java.util.ArrayList;
import java.util.List;
import sistemagestiontareas.interfaces.Autenticable;

/**
 * Clase que representa un usuario del sistema.
 */
public class Usuario implements Autenticable {

    private int id;
    private String nombre;
    private String email;
    private String password;
    private List<Elemento> elementos;

    /** Constructor vacío. */
    public Usuario() {
        this.elementos = new ArrayList<>();
    }

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
        this.elementos = new ArrayList<>();
    }

    @Override
    public boolean iniciarSesion(String email, String clave) {
        return this.email.equals(email) && this.password.equals(clave);
    }

    @Override
    public void cerrarSesion() {
        System.out.println("Sesión cerrada para: " + nombre);
    }

    public void agregarElemento(Elemento elemento) {
        if (elemento != null) {
            elementos.add(elemento);
            System.out.println("Elemento agregado al usuario: " + nombre);
        }
    }

    public void mostrarElementos() {
        System.out.println("Elementos de " + nombre + ":");

        for (Elemento elemento : elementos) {
            elemento.mostrarInfo();
            System.out.println("-------------------");
        }
    }

    public List<Elemento> getElementos() {
        return elementos;
    }

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