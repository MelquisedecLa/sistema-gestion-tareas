package sistemagestiontareas.model;

public class UsuarioClasico extends Usuario {

    public UsuarioClasico(String nombre, int id, String email, String password) {
        super(nombre, id, email, password);
    }

    //@Override
    public void verificarLimiteTareas() {
        System.out.println("Verificando límite de tareas...");
    }

    //@Override
    public void crearElemento() {
        System.out.println("Usuario clásico crea elemento con restricciones.");
    }
}
