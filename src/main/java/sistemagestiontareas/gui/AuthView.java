package sistemagestiontareas.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import sistemagestiontareas.model.Elemento;
import sistemagestiontareas.model.Usuario;
import sistemagestiontareas.model.UsuarioClasico;
import sistemagestiontareas.model.UsuarioPremium;
import sistemagestiontareas.model.ValidadorCorreo;
import sistemagestiontareas.patterns.FormaPago;
import sistemagestiontareas.patterns.Paypal;
import sistemagestiontareas.patterns.TarjetaCredito;
import sistemagestiontareas.patterns.TarjetaDebito;
import sistemagestiontareas.persistence.RecordatorioDAO;
import sistemagestiontareas.persistence.RecordatorioDAOImpl;
import sistemagestiontareas.persistence.TareaDAO;
import sistemagestiontareas.persistence.TareaDAOImpl;
import sistemagestiontareas.persistence.UsuarioDAO;
import sistemagestiontareas.persistence.UsuarioDAOImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

/**
 * Pantalla de autenticación del sistema: inicio de sesión y registro de nuevos usuarios.
 *
 * <p>Implementa el patrón <em>fx:root</em> de JavaFX FXML: la clase extiende {@code VBox}
 * y se registra simultáneamente como raíz y controlador del archivo {@code auth.fxml},
 * de modo que todos los campos anotados con {@link FXML} son inyectados automáticamente
 * por {@link FXMLLoader} al momento de la carga.</p>
 *
 * <p>Reutiliza integralmente las clases de dominio existentes
 * ({@link Usuario}, {@link UsuarioClasico}, {@link UsuarioPremium}, {@link ValidadorCorreo}
 * y las implementaciones de {@link FormaPago}) sin modificarlas; únicamente cambia
 * el medio de entrada (formulario JavaFX en lugar de {@code Scanner}) y el medio de
 * persistencia (PostgreSQL a través de {@link UsuarioDAOImpl}, {@link TareaDAOImpl} y {@link RecordatorioDAOImpl}).</p>
 */
public class AuthView extends VBox {

    // ── Campos inyectados desde auth.fxml ────────────────────────────────────

    /** Campo de correo en la pestaña de inicio de sesión. */
    @FXML private TextField campoEmailLogin;

    /** Campo de contraseña en la pestaña de inicio de sesión. */
    @FXML private PasswordField campoPasswordLogin;

    /** Etiqueta de error/estado en la pestaña de inicio de sesión. */
    @FXML private Label mensajeLogin;

    /** Campo de nombre completo en el formulario de registro. */
    @FXML private TextField campoNombre;

    /** Campo de correo en el formulario de registro. */
    @FXML private TextField campoEmailReg;

    /** Campo de contraseña en el formulario de registro. */
    @FXML private PasswordField campoPasswordReg;

    /** Grupo de selección exclusiva Clásico/Premium (definido en {@code fx:define}). */
    @FXML private ToggleGroup grupoTipo;

    /** RadioButton para cuenta Clásica. */
    @FXML private RadioButton radioClasico;

    /** RadioButton para cuenta Premium. */
    @FXML private RadioButton radioPremium;

    /** Contenedor del formulario de método de pago (visible solo para cuentas Premium). */
    @FXML private VBox panelPago;

    /** Selector del tipo de método de pago. */
    @FXML private ComboBox<String> comboMetodoPago;

    /** Formulario de datos de tarjeta bancaria (crédito o débito). */
    @FXML private GridPane panelTarjeta;

    /** Formulario de datos de cuenta PayPal. */
    @FXML private GridPane panelPaypal;

    /** Número de tarjeta bancaria (16 dígitos). */
    @FXML private TextField campoNumeroTarjeta;

    /** Fecha de vencimiento en formato MM/YY. */
    @FXML private TextField campoVencimiento;

    /** Nombre del titular de la tarjeta. */
    @FXML private TextField campoTitular;

    /** Código de seguridad CVV (3 dígitos). */
    @FXML private TextField campoCvv;

    /** Correo electrónico de la cuenta PayPal. */
    @FXML private TextField campoCorreoPaypal;

    /** Contraseña de la cuenta PayPal. */
    @FXML private PasswordField campoPasswordPaypal;

    /** Etiqueta de error/estado en el formulario de registro. */
    @FXML private Label mensajeRegistro;

    // ── Dependencias ─────────────────────────────────────────────────────────

    private final Consumer<Usuario> alIniciarSesion;
    private final UsuarioDAO      usuarioDAO      = new UsuarioDAOImpl();
    private final TareaDAO        tareaDAO        = new TareaDAOImpl();
    private final RecordatorioDAO recordatorioDAO = new RecordatorioDAOImpl();
    private final ValidadorCorreo validador       = new ValidadorCorreo();

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Construye la vista y carga {@code auth.fxml} con el patrón {@code fx:root}:
     * este objeto actúa a la vez como raíz del grafo de escena y como controlador FXML.
     *
     * @param alIniciarSesion callback invocado con el {@link Usuario} autenticado
     * @throws RuntimeException si {@code auth.fxml} no se encuentra en el classpath
     */
    public AuthView(Consumer<Usuario> alIniciarSesion) {
        this.alIniciarSesion = alIniciarSesion;

        var url = getClass().getResource("auth.fxml");
        if (url == null) {
            throw new RuntimeException("No se encontro auth.fxml en el classpath");
        }
        FXMLLoader loader = new FXMLLoader(url);
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar auth.fxml", e);
        }
    }

    // ── Inicialización post-FXML ─────────────────────────────────────────────

    /**
     * Invocado automáticamente por {@link FXMLLoader} tras inyectar todos los campos
     * {@code @FXML}. Configura los listeners dinámicos del formulario de registro.
     */
    @FXML
    private void initialize() {
        comboMetodoPago.getItems().addAll("Tarjeta de credito", "Tarjeta de debito", "PayPal");
        comboMetodoPago.setValue("Tarjeta de credito");

        // Mostrar/ocultar panel de pago según tipo de usuario
        grupoTipo.selectedToggleProperty().addListener((obs, anterior, nuevo) -> {
            boolean esPremium = nuevo == radioPremium;
            panelPago.setVisible(esPremium);
            panelPago.setManaged(esPremium);
        });

        // Mostrar tarjeta o PayPal según método seleccionado
        comboMetodoPago.valueProperty().addListener((obs, anterior, nuevo) -> {
            boolean esPaypal = "PayPal".equals(nuevo);
            panelTarjeta.setVisible(!esPaypal);
            panelTarjeta.setManaged(!esPaypal);
            panelPaypal.setVisible(esPaypal);
            panelPaypal.setManaged(esPaypal);
        });
    }

    // ── Acciones FXML ────────────────────────────────────────────────────────

    /**
     * Manejador del botón "Iniciar sesion". Valida credenciales contra la base de datos,
     * carga los elementos del usuario e invoca el callback de navegación.
     */
    @FXML
    private void accionIniciarSesion() {
        mensajeLogin.setText("");
        String email    = campoEmailLogin.getText()    == null ? "" : campoEmailLogin.getText().trim();
        String password = campoPasswordLogin.getText() == null ? "" : campoPasswordLogin.getText();

        if (email.isEmpty() || password.isEmpty()) {
            mensajeLogin.setText("Completa correo y contrasena.");
            return;
        }

        try {
            Usuario usuario = usuarioDAO.buscarPorEmail(email);
            if (usuario == null || !usuario.iniciarSesion(email, password)) {
                mensajeLogin.setText("Credenciales incorrectas.");
                return;
            }
            // Carga elementos directamente en la lista interna para no activar
            // el limite de UsuarioClasico al restaurar datos ya persistidos.
            List<Elemento> elementos = new java.util.ArrayList<>();
            elementos.addAll(tareaDAO.buscarPorUsuario(usuario.getId()));
            elementos.addAll(recordatorioDAO.buscarPorUsuario(usuario.getId()));
            usuario.getElementos().clear();
            usuario.getElementos().addAll(elementos);
            alIniciarSesion.accept(usuario);
        } catch (SQLException ex) {
            mensajeLogin.setText("No se pudo conectar a la base de datos: " + ex.getMessage());
        }
    }

    /**
     * Manejador del botón "Registrarse". Delega la validación en
     * {@link #validarYRegistrar} y muestra el mensaje de error si corresponde.
     */
    @FXML
    private void accionRegistrar() {
        String error = validarYRegistrar(
                campoNombre.getText(),        campoEmailReg.getText(),
                campoPasswordReg.getText(),   radioPremium.isSelected(),
                comboMetodoPago.getValue(),   campoNumeroTarjeta.getText(),
                campoVencimiento.getText(),   campoTitular.getText(),
                campoCvv.getText(),           campoCorreoPaypal.getText(),
                campoPasswordPaypal.getText());
        mensajeRegistro.setText(error == null ? "" : error);
    }

    // ── Lógica de validación y registro ──────────────────────────────────────

    /**
     * Valida los datos del formulario de registro, construye el {@link Usuario}
     * correspondiente y lo persiste en la base de datos.
     *
     * @param nombreTexto           nombre completo
     * @param emailTexto            correo electrónico
     * @param password              contraseña
     * @param esPremium             {@code true} si se seleccionó cuenta Premium
     * @param metodoPagoSeleccionado método de pago elegido
     * @param numeroTarjeta         número de tarjeta (16 dígitos)
     * @param vencimiento           fecha de vencimiento (MM/YY)
     * @param titular               nombre del titular de la tarjeta
     * @param cvvTexto              código CVV (3 dígitos)
     * @param correoPaypal          correo de la cuenta PayPal
     * @param passwordPaypal        contraseña de la cuenta PayPal
     * @return {@code null} si el registro fue exitoso; mensaje de error si falló
     */
    private String validarYRegistrar(String nombreTexto, String emailTexto, String password,
                                     boolean esPremium, String metodoPagoSeleccionado,
                                     String numeroTarjeta, String vencimiento,
                                     String titular, String cvvTexto,
                                     String correoPaypal, String passwordPaypal) {

        String nombre = nombreTexto == null ? "" : nombreTexto.trim();
        String email  = emailTexto  == null ? "" : emailTexto.trim();

        if (nombre.isEmpty()) return "El nombre no puede estar vacio.";
        if (!validador.validarFormato(email) || !validador.validarDominio(email))
            return "Correo invalido. Debe contener '@' y un dominio valido.";
        if (password == null || password.length() < 8)
            return "La contrasena debe tener al menos 8 caracteres.";

        FormaPago metodoPago = null;
        if (esPremium) {
            if ("PayPal".equals(metodoPagoSeleccionado)) {
                String correo = correoPaypal == null ? "" : correoPaypal.trim();
                if (!validador.validarFormato(correo) || !validador.validarDominio(correo))
                    return "Correo de PayPal invalido.";
                if (passwordPaypal == null || passwordPaypal.isEmpty())
                    return "Ingresa la contrasena de PayPal.";
                metodoPago = new Paypal(correo, passwordPaypal);
            } else {
                String numeroLimpio = numeroTarjeta == null ? "" : numeroTarjeta.replaceAll("\\s+", "");
                if (!numeroLimpio.matches("\\d{16}"))
                    return "El numero de tarjeta debe tener exactamente 16 digitos.";
                String vencimientoLimpio = vencimiento == null ? "" : vencimiento.trim();
                if (!vencimientoLimpio.matches("(0[1-9]|1[0-2])/\\d{2}"))
                    return "Formato de vencimiento invalido. Usa MM/YY, ejemplo: 08/27.";
                int mes  = Integer.parseInt(vencimientoLimpio.split("/")[0]);
                int anio = 2000 + Integer.parseInt(vencimientoLimpio.split("/")[1]);
                LocalDate fechaVencimiento = LocalDate.of(anio, mes, 1);
                if (fechaVencimiento.isBefore(LocalDate.now()))
                    return "La tarjeta esta vencida.";
                if (titular == null || titular.trim().isEmpty())
                    return "Ingresa el nombre del titular.";
                String cvvLimpio = cvvTexto == null ? "" : cvvTexto.trim();
                if (!cvvLimpio.matches("\\d{3}"))
                    return "El CVV debe tener exactamente 3 digitos.";
                int cvv = Integer.parseInt(cvvLimpio);
                if ("Tarjeta de credito".equals(metodoPagoSeleccionado)) {
                    metodoPago = new TarjetaCredito(numeroLimpio, fechaVencimiento, titular.trim(), cvv);
                } else {
                    metodoPago = new TarjetaDebito(numeroLimpio, fechaVencimiento, titular.trim(), cvv);
                }
            }
        }

        try {
            if (usuarioDAO.existeEmail(email)) return "Ese correo ya esta registrado. Usa uno diferente.";

            Usuario guardado;
            if (esPremium) {
                LocalDate fechaExpiracion = LocalDate.now().plusMonths(1);
                UsuarioPremium borrador = new UsuarioPremium(nombre, 0, email, password, fechaExpiracion);
                borrador.setMetodoPago(metodoPago);
                int id = usuarioDAO.guardar(borrador);
                guardado = new UsuarioPremium(nombre, id, email, password, fechaExpiracion);
                guardado.setMetodoPago(metodoPago);
            } else {
                Usuario borrador = new UsuarioClasico(nombre, 0, email, password);
                int id = usuarioDAO.guardar(borrador);
                guardado = new UsuarioClasico(nombre, id, email, password);
            }
            alIniciarSesion.accept(guardado);
            return null;
        } catch (IllegalArgumentException ex) {
            return "Error: " + ex.getMessage();
        } catch (SQLException ex) {
            return "No se pudo guardar en la base de datos: " + ex.getMessage();
        }
    }
}
