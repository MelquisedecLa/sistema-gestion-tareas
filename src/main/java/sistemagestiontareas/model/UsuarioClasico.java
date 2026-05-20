package sistemagestiontareas.model;

public class UsuarioClasico extends Usuario {

    // el usuario tiene un límite máximo de elementos que puede crear.
    private int limiteElementos;

    public UsuarioClasico(String nombre, int id, String email, String password) {
        super(nombre, id, email, password);
        this.limiteElementos = 3; // límite por defecto
    }

    // verifica si el usuario todavía puede crear más elementos sin pasarse del límite.
    public boolean verificarLimiteTareas() {
        return getElementos().size() < limiteElementos;
    }
    // pero en UsuarioClasico verificamos el límite antes de crear.
    @Override
    public void crearElemento(Elemento elemento) {
        if (verificarLimiteTareas()) {
            super.crearElemento(elemento);
        } else {
            System.out.println("Límite de " + limiteElementos + " elementos alcanzado. No se puede crear más.");
        }
    }

    public int getLimiteElementos() { return limiteElementos; }
}