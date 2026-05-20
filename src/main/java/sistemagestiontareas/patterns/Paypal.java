package sistemagestiontareas.patterns;

public class Paypal implements FormaPago {

    // Atributos privados de Paypal
    private String email;
    private String contrasena;

    public Paypal(String email, String contrasena) {
        this.email = email;
        this.contrasena = contrasena;
    }

    // Método "+ procesarPago(): void" heredado de FormaPago
    // Esta clase lo implementa con la lógica de PayPal.
    @Override
    public void procesarPago() {
        System.out.println("Procesando pago con PayPal.");
        System.out.println("Cuenta: " + email);
    }

    public String getEmail() { return email; }
}