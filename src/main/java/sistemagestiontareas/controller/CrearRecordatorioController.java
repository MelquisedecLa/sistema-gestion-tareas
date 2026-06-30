package sistemagestiontareas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sistemagestiontareas.enums.Prioridad;

import java.time.LocalDate;

public class CrearRecordatorioController {

    @FXML private TextField campoTitulo;
    @FXML private TextArea campoDescripcion;
    @FXML private ComboBox<Prioridad> comboPrioridad;
    @FXML private DatePicker campoFecha;
    @FXML private Label labelError;

    private boolean guardado = false;

    @FXML
    public void initialize() {
        comboPrioridad.getItems().addAll(Prioridad.values());
        comboPrioridad.setValue(Prioridad.MEDIA);
    }

    /** Llamar este método antes de mostrar el modal, solo si se va a EDITAR un recordatorio existente. */
    public void precargarDatos(String titulo, String descripcion, Prioridad prioridad, LocalDate fecha) {
        campoTitulo.setText(titulo);
        campoDescripcion.setText(descripcion);
        comboPrioridad.setValue(prioridad);
        campoFecha.setValue(fecha);
    }

    @FXML
    private void onGuardar() {
        String titulo = campoTitulo.getText();
        LocalDate fecha = campoFecha.getValue();

        if (titulo == null || titulo.isBlank()) {
            labelError.setText("El título es obligatorio.");
            return;
        }
        if (fecha == null) {
            labelError.setText("Debes seleccionar una fecha límite.");
            return;
        }
        if (fecha.isBefore(LocalDate.now())) {
            labelError.setText("La fecha no puede ser en el pasado.");
            return;
        }

        guardado = true;
        cerrarVentana();
    }

    @FXML
    private void onCancelar() {
        guardado = false;
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) campoTitulo.getScene().getWindow();
        stage.close();
    }

    public boolean isGuardado() { return guardado; }
    public String getTitulo() { return campoTitulo.getText(); }
    public String getDescripcion() { return campoDescripcion.getText(); }
    public Prioridad getPrioridad() { return comboPrioridad.getValue(); }
    public LocalDate getFecha() { return campoFecha.getValue(); }
}