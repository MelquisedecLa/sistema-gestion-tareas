package sistemagestiontareas.model;

import sistemagestiontareas.enums.Prioridad;
import sistemagestiontareas.interfaces.Compartible;

import java.util.ArrayList;
import java.util.List;

public abstract class Elemento implements Compartible {

    private int id;
    private String titulo;
    private String descripcion;
    private Prioridad prioridad;
    private List<Usuario> usuariosCompartidos;

    public Elemento(int id, String titulo, String descripcion, Prioridad prioridad) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.usuariosCompartidos = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public List<Usuario> getUsuariosCompartidos() {
        return usuariosCompartidos;
    }

    @Override
    public void compartir(Usuario usuario) {
        if (usuario != null) {
            usuariosCompartidos.add(usuario);
            System.out.println("Elemento compartido con: " + usuario.getNombre());
        }
    }

    public void mostrarInfo() {
        System.out.println("ID: " + id);
        System.out.println("Título: " + titulo);
        System.out.println("Descripción: " + descripcion);
        System.out.println("Prioridad: " + prioridad);
        System.out.println("Cantidad de usuarios compartidos: " + usuariosCompartidos.size());
    }
}