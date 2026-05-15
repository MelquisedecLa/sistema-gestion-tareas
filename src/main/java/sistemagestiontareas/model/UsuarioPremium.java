package sistemagestiontareas.model;

import sistemagestiontareas.patterns.EstrategiaPremium;

/**
 * Representa a un usuario premium dentro del sistema.
 * Tiene acceso completo y sin restricciones de creación de elementos.
 */
public class UsuarioPremium extends Usuario {

    private String nivelAcceso;
    private boolean accesoCompleto;
    private String metodoPago;

    public UsuarioPremium(String nombre, int id, String email, String password, String metodoPago) {
        super(nombre, id, email, password);
        this.nivelAcceso = "PREMIUM";
        this.accesoCompleto = true;
        this.metodoPago = metodoPago;
        setEstrategia(new EstrategiaPremium());
    }

    /**
     * Retorna el nivel de acceso del usuario premium.
     *
     * @return nivel de acceso
     */
    public String getNivelAcceso() {
        return nivelAcceso;
    }

    /**
     * Indica si el usuario tiene acceso completo al sistema.
     *
     * @return true si tiene acceso completo
     */
    public boolean isAccesoCompleto() {
        return accesoCompleto;
    }

    /** @return método de pago registrado */
    public String getMetodoPago() {
        return metodoPago;
    }

    /** @param metodoPago nuevo método de pago */
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
}