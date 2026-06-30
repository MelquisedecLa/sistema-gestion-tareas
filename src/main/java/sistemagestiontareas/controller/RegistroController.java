package sistemagestiontareas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import sistemagestiontareas.App;
import sistemagestiontareas.model.ValidadorCorreo;
import sistemagestiontareas.patterns.FormaPago;
import sistemagestiontareas.patterns.Paypal;
import sistemagestiontareas.patterns.TarjetaCredito;
import sistemagestiontareas.patterns.TarjetaDebito;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class RegistroController {

    @FXML private TextField campoNombre;
    @FXML private TextField campoEmail;
    @FXML private PasswordField campoPassword;
    @FXML private RadioButton radioClasico;
    @FXML private RadioButton radioPremium;
    @FXML private Label labelLimiteClasico;
    @FXML private Label labelError;

    @FXML private VBox panelPago;
    @FXML private ComboBox<String> comboMetodoPago;

    @FXML private VBox panelTarjeta;
    @FXML private TextField campoNumeroTarjeta;
    @FXML private TextField campoFechaExpiracion;
    @FXML private TextField campoTitular;
    @FXML private TextField campoCvv;

    @FXML private VBox panelPaypal;
    @FXML private TextField campoEmailPaypal;
    @FXML private PasswordField campoPasswordPaypal;

    private final ValidadorCorreo validador = new ValidadorCorreo();

    @FXML
    public void initialize() {
        comboMetodoPago.getItems().addAll("Tarjeta de Crédito", "Tarjeta de Débito", "PayPal");

        radioPremium.selectedProperty().addListener((obs, antes, esPremium) -> {
            panelPago.setVisible(esPremium);
            panelPago.setManaged(esPremium);
            labelLimiteClasico.setVisible(!esPremium);
            labelLimiteClasico.setManaged(!esPremium);
        });

        comboMetodoPago.valueProperty().addListener((obs, antes, metodo) -> {
            boolean esTarjeta = "Tarjeta de Crédito".equals(metodo) || "Tarjeta de Débito".equals(metodo);
            boolean esPaypal = "PayPal".equals(metodo);
            panelTarjeta.setVisible(esTarjeta);
            panelTarjeta.setManaged(esTarjeta);
            panelPaypal.setVisible(esPaypal);
            panelPaypal.setManaged(esPaypal);
        });
    }

    @FXML
    private void onRegistrar() {
        String nombre = campoNombre.getText();
        String email = campoEmail.getText();
        String password = campoPassword.getText();

        if (nombre == null || nombre.isBlank()) {
            labelError.setText("El nombre es obligatorio.");
            return;
        }
        if (!validador.validarFormato(email) || !validador.validarDominio(email)) {
            labelError.setText("Correo inválido. Debe contener '@' y un dominio válido.");
            return;
        }
        if (password == null || password.length() < 8) {
            labelError.setText("La contraseña debe tener al menos 8 caracteres.");
            return;
        }

        if (radioClasico.isSelected()) {
            // Usuario clásico: sin método de pago, límite de 3 elementos ya lo maneja UsuarioClasico.
            System.out.println("Registrando usuario CLASICO: " + nombre + " (límite: 3 elementos)");
            volverALogin();
            return;
        }

        // --- Usuario Premium: requiere método de pago válido ---
        String metodo = comboMetodoPago.getValue();
        if (metodo == null) {
            labelError.setText("Selecciona un método de pago.");
            return;
        }

        FormaPago metodoPago;
        try {
            metodoPago = switch (metodo) {
                case "Tarjeta de Crédito" -> construirTarjeta(true);
                case "Tarjeta de Débito" -> construirTarjeta(false);
                case "PayPal" -> construirPaypal();
                default -> null;
            };
        } catch (IllegalArgumentException e) {
            labelError.setText(e.getMessage());
            return;
        }

        LocalDate fechaExpiracionMembresia = LocalDate.now().plusMonths(1);
        System.out.println("Registrando usuario PREMIUM: " + nombre
                + " | Método de pago: " + metodo
                + " | Membresía válida hasta: " + fechaExpiracionMembresia);

        volverALogin();
    }

    private FormaPago construirTarjeta(boolean esCredito) {
        String numero = campoNumeroTarjeta.getText();
        if (numero == null || !numero.matches("\\d{16}")) {
            throw new IllegalArgumentException("El número de tarjeta debe tener exactamente 16 dígitos.");
        }

        String fechaTexto = campoFechaExpiracion.getText();
        if (fechaTexto == null || !fechaTexto.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            throw new IllegalArgumentException("Formato de vencimiento inválido. Usa MM/YY, ejemplo: 08/27");
        }
        int mes = Integer.parseInt(fechaTexto.split("/")[0]);
        int anio = 2000 + Integer.parseInt(fechaTexto.split("/")[1]);
        LocalDate fechaExpiracion = LocalDate.of(anio, mes, 1);
        if (fechaExpiracion.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La tarjeta está vencida.");
        }

        String titular = campoTitular.getText();
        if (titular == null || titular.isBlank()) {
            throw new IllegalArgumentException("El nombre del titular es obligatorio.");
        }

        String cvvTexto = campoCvv.getText();
        if (cvvTexto == null || !cvvTexto.matches("\\d{3}")) {
            throw new IllegalArgumentException("El CVV debe tener exactamente 3 dígitos.");
        }
        int cvv = Integer.parseInt(cvvTexto);

        return esCredito
                ? new TarjetaCredito(numero, fechaExpiracion, titular, cvv)
                : new TarjetaDebito(numero, fechaExpiracion, titular, cvv);
    }

    private FormaPago construirPaypal() {
        String correoPaypal = campoEmailPaypal.getText();
        if (!validador.validarFormato(correoPaypal) || !validador.validarDominio(correoPaypal)) {
            throw new IllegalArgumentException("Correo de PayPal inválido.");
        }
        String passwordPaypal = campoPasswordPaypal.getText();
        if (passwordPaypal == null || passwordPaypal.isBlank()) {
            throw new IllegalArgumentException("La contraseña de PayPal es obligatoria.");
        }
        return new Paypal(correoPaypal, passwordPaypal);
    }

    private void volverALogin() {
        try {
            App.cambiarEscena("login.fxml", "Iniciar Sesión");
        } catch (Exception e) {
            labelError.setText("Error al volver al login.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onIrALogin() {
        try {
            App.cambiarEscena("login.fxml", "Iniciar Sesión");
        } catch (Exception e) {
            labelError.setText("Error al cargar el login.");
            e.printStackTrace();
        }
    }
}