package sistemagestiontareas.model;

import java.util.ArrayList;
import java.util.List;
import sistemagestiontareas.interfaces.Autenticable;
import sistemagestiontareas.patterns.FormaPago;

public abstract class Usuario implements Autenticable {

    private int id;
    private String nombre;
    private String email;
    private String password;


    private ValidadorCorreo validador;

    // Esta lista es la que "guarda" los elementos que el usuario crea.
    private List<Elemento> elementos;

    // Usuario no sabe si es PayPal, Débito o Crédito, solo llama procesarPago().
    private FormaPago metodoPago;

    public Usuario() {
        this.elementos = new ArrayList<>();
        this.validador = new ValidadorCorreo();
    }

    /**
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

    // Viene de implementar la interfaz Autenticable
    @Override
    public boolean iniciarSesion(String email, String clave) {
        return this.email.equals(email) && this.password.equals(clave);
    }

    @Override
    public void cerrarSesion() {
        System.out.println("Sesión cerrada para: " + nombre);
    }

    // Método público de Usuario. Agrega el elemento a la lista
    public void crearElemento(Elemento elemento) {
        if (elemento != null) {
            elementos.add(elemento);
            System.out.println("Elemento creado y guardado: " + elemento.getTitulo());
        }
    }

    public void compartirElemento(Elemento elemento, Usuario usuario) {
        elemento.compartir(usuario);
    }

    /**
     * Muestra todos los elementos del usuario.
     * Método auxiliar, no está en diagrama pero necesario para el código.
     */
    public void mostrarElementos() {
        System.out.println("Elementos de " + nombre + ":");
        for (Elemento e : elementos) {
            e.mostrarInfo();
            System.out.println("-------------------");
        }
    }

    public void setMetodoPago(FormaPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public FormaPago getMetodoPago() { return metodoPago; }

    public List<Elemento> getElementos() { return elementos; }
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}