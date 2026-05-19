package sistemagestiontareas.model;

import java.util.ArrayList;
import java.util.List;
import sistemagestiontareas.interfaces.Autenticable;
import sistemagestiontareas.patterns.EstrategiaCreacion;

/**
 * Clase abstracta que representa un usuario del sistema.
 * Usa el patrón Strategy para delegar la lógica de creación de elementos.
 */
public abstract class Usuario implements Autenticable {

    private int id;
    private String nombre;
    private String email;
    private String password;
    private List<Elemento> elementos;
    private EstrategiaCreacion estrategia;
    private ValidadorCorreo validador;

    /** Constructor vacío. */
    public Usuario() {
        this.elementos = new ArrayList<>();
        this.validador = new ValidadorCorreo();
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
        this.validador = new ValidadorCorreo();
        if (!validador.validarFormato(email) || !validador.validarDominio(email)) {
            throw new IllegalArgumentException("El correo no es válido: " + email);
        }
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
     * Crea un elemento delegando la lógica a la estrategia asignada.
     *
     * @param elemento elemento a crear
     */
    public void crearElemento(Elemento elemento) {
        if (estrategia == null) {
            System.out.println("No hay estrategia asignada para: " + nombre);
            return;
        }
        boolean creado = estrategia.crearElemento(elemento, elementos);
        if (creado) {
            agregarElemento(elemento);
        }
    }

    /**
     * Agrega un elemento a la lista del usuario.
     *
     * @param elemento elemento a agregar
     */
    public void agregarElemento(Elemento elemento) {
        if (elemento != null) {
            elementos.add(elemento);
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

    /**
     * Comparte un elemento con otro usuario.
     *
     * @param elemento elemento as compartir
     * @param usuario  usuario destino
     */
    public void compartirElemento(Elemento elemento, Usuario usuario) {
        elemento.compartir(usuario);
    }

    /** @param estrategia nueva estrategia a asignar */
    public void setEstrategia(EstrategiaCreacion estrategia) {
        this.estrategia = estrategia;
    }

    /** @return estrategia actual */
    public EstrategiaCreacion getEstrategia() {
        return estrategia;
    }

    /** @return lista de elementos */
    public List<Elemento> getElementos() { return elementos; }

    /** @return id del usuario */
    public int getId() { return id; }

    /** @param id nuevo id */
    public void setId(int id) { this.id = id; }

    /** @return nombre del usuario */
    public String getNombre() { return nombre; }

    /** @param nombre nuevo nombre */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /** @return email del usuario */
    public String getEmail() { return email; }

    /** @param email nuevo email */
    public void setEmail(String email) { this.email = email; }

    /** @return contraseña del usuario */
    public String getPassword() { return password; }

    /** @param password nueva contraseña */
    public void setPassword(String password) { this.password = password; }
}