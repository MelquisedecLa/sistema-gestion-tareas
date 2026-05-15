package sistemagestiontareas.model;

import sistemagestiontareas.patterns.EstrategiaClasica;

/**
 * Representa a un usuario clásico dentro del sistema.
 * Tiene un límite máximo de elementos y nivel de acceso básico.
 */
public class UsuarioClasico extends Usuario {

    private int limiteElementos;
    private String nivelAcceso;

    public UsuarioClasico(String nombre, int id, String email, String password) {
        super(nombre, id, email, password);
        this.limiteElementos = 3;
        this.nivelAcceso = "BASICO";
        setEstrategia(new EstrategiaClasica());
    }

    /**
     * Verifica si el usuario aún puede crear más elementos.
     *
     * @return true si no ha alcanzado el límite
     */
    public boolean verificarLimiteTareas() {
        return getElementos().size() < limiteElementos;
    }

    /** @return límite de elementos permitidos */
    public int getLimiteElementos() { return limiteElementos; }

    /** @return nivel de acceso del usuario */
    public String getNivelAcceso() { return nivelAcceso; }
}