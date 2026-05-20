package sistemagestiontareas.patterns;

import java.time.LocalDate;


public class TarjetaDebito implements FormaPago {

    private String numeroTarjeta;
    private LocalDate fechaExpiracion;
    private String titular;
    private int cvv;

    public TarjetaDebito(String numeroTarjeta, LocalDate fechaExpiracion, String titular, int cvv) {
        this.numeroTarjeta = numeroTarjeta;
        this.fechaExpiracion = fechaExpiracion;
        this.titular = titular;
        this.cvv = cvv;
    }

    @Override
    public void procesarPago() {
        System.out.println("Procesando pago con Tarjeta de Débito.");
        System.out.println("Titular: " + titular);
        System.out.println("Tarjeta: **** **** **** " + numeroTarjeta.substring(numeroTarjeta.length() - 4));
    }

    public String getNumeroTarjeta() { return numeroTarjeta; }
    public LocalDate getFechaExpiracion() { return fechaExpiracion; }
    public String getTitular() { return titular; }
    public int getCvv() { return cvv; }
}