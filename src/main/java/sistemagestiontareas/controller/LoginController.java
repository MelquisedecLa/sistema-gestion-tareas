package sistemagestiontareas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import sistemagestiontareas.App;

public class LoginController {

    @FXML private TextField campoEmail;
    @FXML private PasswordField campoPassword;
    @FXML private Label labelError;

    @FXML
    private void onIniciarSesion() {
        String email = campoEmail.getText();
        String password = campoPassword.getText();

        if (email.isBlank() || password.isBlank()) {
            labelError.setText("Debes completar todos los campos.");
            return;
        }

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