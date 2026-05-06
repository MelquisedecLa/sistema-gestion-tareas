package sistemagestiontareas.model;

/**
 * Representa a un usuario clásico dentro del sistema.
 * Este tipo de usuario tiene ciertas restricciones funcionales
 * en comparación con un usuario premium.
 */
public class UsuarioClasico extends Usuario {

    private static final int LIMITE_ELEMENTOS = 3;

    public UsuarioClasico(String nombre, int id, String email, String password) {
        super(nombre, id, email, password);
    }

    public boolean verificarLimiteTareas() {
        return getElementos().size() < LIMITE_ELEMENTOS;
    }

    public void crearElemento(Elemento elemento) {
        if (verificarLimiteTareas()) {
            agregarElemento(elemento);
            System.out.println("Elemento creado por usuario clásico.");
        } else {
            System.out.println("El usuario clásico alcanzó el límite de elementos.");
        }
    }
}