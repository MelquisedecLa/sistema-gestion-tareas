package sistemagestiontareas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import sistemagestiontareas.App;
import sistemagestiontareas.Sesion;
import sistemagestiontareas.dao.UsuarioDAO;
import sistemagestiontareas.dao.UsuarioDAOImpl;
import sistemagestiontareas.model.Usuario;

public class LoginController {

    @FXML private TextField campoEmail;
    @FXML private PasswordField campoPassword;
    @FXML private Label labelError;

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    @FXML
    private void onIniciarSesion() {
        String email = campoEmail.getText();
        String password = campoPassword.getText();

        if (email.isBlank() || password.isBlank()) {
            labelError.setText("Debes completar todos los campos.");
            return;
        }

        Usuario usuario;
        try {
            usuario = usuarioDAO.buscarPorEmail(email);
        } catch (Exception e) {
            labelError.setText("Error al conectar con la base de datos.");
            e.printStackTrace();
            return;
        }

        // No existe ningún usuario registrado con ese correo.
        if (usuario == null) {
            labelError.setText("No existe una cuenta con ese correo.");
            return;
        }

        // Existe el correo, pero la contraseña no coincide.
        if (!usuario.iniciarSesion(email, password)) {
            labelError.setText("Contraseña incorrecta.");
            return;
        }

        // Credenciales correctas: guardamos la sesión y avanzamos.
        Sesion.setUsuarioActual(usuario);

        try {
            App.cambiarEscena("main.fxml", "Sistema de Gestión de Tareas");
        } catch (Exception e) {
            labelError.setText("Error al cargar la pantalla principal.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onIrARegistro() {
        try {
            App.cambiarEscena("registro.fxml", "Registro de Usuario");
        } catch (Exception e) {
            labelError.setText("Error al cargar la pantalla de registro.");
            e.printStackTrace();
        }
    }
}