package sistemagestiontareas.model;

import sistemagestiontareas.enums.Prioridad;
import sistemagestiontareas.interfaces.Compartible;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

/**
 * Clase abstracta que representa un elemento del sistema.
 * Puede ser compartido con otros usuarios.
 */
public abstract class Elemento implements Compartible {

    private int id;
    private String titulo;
    private String descripcion;
    private Prioridad prioridad;
    private LocalDate fechaLimite;
    private List<Usuario> usuariosCompartidos;

    /**
     * Constructor de la clase Elemento.
     *
     * @param id identificador del elemento
     * @param titulo título del elemento
     * @param descripcion descripción del elemento
     * @param prioridad nivel de prioridad del elemento
     */
    public Elemento(int id, String titulo, String descripcion, Prioridad prioridad, LocalDate fechaLimite) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.fechaLimite = fechaLimite;
        this.usuariosCompartidos = new ArrayList<>();
    }

    /** @return id del elemento */
    public int getId() {
        return id;
    }

    /** @param id nuevo id del elemento */
    public void setId(int id) {
        this.id = id;
    }

    /** @return título del elemento */
    public String getTitulo() {
        return titulo;
    }

    /** @param titulo nuevo título del elemento */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /** @return descripción del elemento */
    public String getDescripcion() {
        return descripcion;
    }

    /** @param descripcion nueva descripción del elemento */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /** @return prioridad del elemento */
    public Prioridad getPrioridad() {
        return prioridad;
    }

    public LocalDate getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDate fechaLimite) { this.fechaLimite = fechaLimite; }

    /** @param prioridad nueva prioridad del elemento */
    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    /** @return lista de usuarios con quienes se ha compartido el elemento */
    public List<Usuario> getUsuariosCompartidos() {
        return usuariosCompartidos;
    }

    /**
     * Comparte el elemento con un usuario.
     *
     * @param usuario usuario con quien se comparte el elemento
     */
    @Override
    public void compartir(Usuario usuario) {
        if (usuario != null) {
            System.out.println("Elemento compartido con: " + usuario.getNombre());
        }
    }

    /**
     * Muestra la información del elemento en consola.
     */
    public void mostrarInfo() {
        System.out.println("ID: " + id);
        System.out.println("Título: " + titulo);
        System.out.println("Descripción: " + descripcion);
        System.out.println("Prioridad: " + prioridad);
        if (usuariosCompartidos.isEmpty()) {
            System.out.println("No compartido con nadie.");
        } else {
            System.out.println("Compartido por/con:");
            for (Usuario u : usuariosCompartidos) {
                System.out.println("  - " + u.getNombre() + " (" + u.getEmail() + ")");
            }
        }
    }
}