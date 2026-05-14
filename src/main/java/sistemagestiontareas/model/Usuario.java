package sistemagestiontareas.model;

import java.util.ArrayList;
import java.util.List;
import sistemagestiontareas.interfaces.Autenticable;

/**
 * Clase abstracta que representa un usuario del sistema.
 * Solo puede instanciarse a través de UsuarioClasico o UsuarioPremium.
 */
public abstract class Usuario implements Autenticable {

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
     * @param nombre   nombre del usuario
     * @param id       identificador del usuario
     * @param email    correo electrónico del usuario
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

    /**
     * Crea un elemento y lo agrega al usuario.
     * Cada subclase define sus propias restricciones.
     *
     * @param elemento elemento a crear
     */
    public abstract void crearElemento(Elemento elemento);

    /**
     * Agrega un elemento a la lista del usuario.
     *
     * @param elemento elemento a agregar
     */
    public void agregarElemento(Elemento elemento) {
        if (elemento != null) {
            elementos.add(elemento);
            System.out.println("Elemento agregado al usuario: " + nombre);
        }
    }

    /**
     * Muestra todos los elementos del usuario.
     */
    public void mostrarElementos() {
        System.out.println("Elementos de " + nombre + ":");
        for (Elemento elemento : elementos) {
            elemento.mostrarInfo();
            System.out.println("-------------------");
        }
    }

    /** @return lista de elementos del usuario */
    public List<Elemento> getElementos() {
        return elementos;
    }

    /** @return id del usuario */
    public int getId() {
        return id;
    }

    /** @param id nuevo id */
    public void setId(int id) {
        this.id = id;
    }

    /** @return nombre del usuario */
    public String getNombre() {
        return nombre;
    }

    /** @param nombre nuevo nombre */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** @return email del usuario */
    public String getEmail() {
        return email;
    }

    /** @param email nuevo email */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return contraseña del usuario */
    public String getPassword() {
        return password;
    }

    /** @param password nueva contraseña */
    public void setPassword(String password) {
        this.password = password;
    }

    public void compartirElemento(Elemento elemento, Usuario usuario) {
        elemento.compartir(usuario);
    }
}