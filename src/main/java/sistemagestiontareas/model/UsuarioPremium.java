package sistemagestiontareas.model;

public class UsuarioPremium extends Usuario {

    public UsuarioPremium(String nombre, int id, String email, String password) {
        super(nombre, id , email, password);
    }

    public void accesoCompleto() {
        System.out.println("Acceso completo habilitado.");
    }

    //@Override
    public void crearElemento() {
        System.out.println("Usuario premium crea elemento sin restricciones.");
    }
//}
}
