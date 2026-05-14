package sistemagestiontareas.model;

/**
 * Representa a un usuario clásico dentro del sistema.
 * Tiene un límite máximo de elementos que puede crear.
 */
public class UsuarioClasico extends Usuario {

    private static final int LIMITE_ELEMENTOS = 3;

    public UsuarioClasico(String nombre, int id, String email, String password) {
        super(nombre, id, email, password);
    }

    /**
     * Verifica si el usuario aún puede crear más elementos.
     *
     * @return true si no ha alcanzado el límite
     */
    public boolean verificarLimiteTareas() {
        return getElementos().size() < LIMITE_ELEMENTOS;
    }

    /**
     * Crea un elemento si no se ha alcanzado el límite permitido.
     *
     * @param elemento elemento a crear
     */
    @Override
    public void crearElemento(Elemento elemento) {
        if (verificarLimiteTareas()) {
            agregarElemento(elemento);
            System.out.println("Elemento creado por usuario clásico.");
        } else {
            System.out.println("El usuario clásico alcanzó el límite de elementos.");
        }
    }
}