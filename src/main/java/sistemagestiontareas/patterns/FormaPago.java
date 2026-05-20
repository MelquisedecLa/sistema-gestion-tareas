package sistemagestiontareas.patterns;

// Cada método de pago la implementa distinto.
public interface FormaPago {

    // DIAGRAMA: Método "+ procesarPago(): void" de la interfaz FormaPago
    // Cada clase concreta (TarjetaCredito, TarjetaDebito, Paypal)
    // implementará este método a su manera.
    void procesarPago();
}