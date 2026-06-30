package sistemagestiontareas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sistemagestiontareas.App;
import sistemagestiontareas.enums.Estado;
import sistemagestiontareas.model.Elemento;
import sistemagestiontareas.model.Recordatorio;
import sistemagestiontareas.model.Tarea;

import java.time.LocalDate;
import java.util.Optional;

public class MainController {

    @FXML private Label labelBienvenida;
    @FXML private Label labelTipoUsuario;
    @FXML private ListView<Elemento> listViewElementos;

    private final ObservableList<Elemento> elementos = FXCollections.observableArrayList();
    private int siguienteId = 1;

    @FXML
    public void initialize() {
        // TODO: cuando exista la sesión/DAO real, aquí se carga el usuario y sus elementos reales.
        labelBienvenida.setText("Bienvenido");
        listViewElementos.setItems(elementos);
        listViewElementos.setCellFactory(lv -> new ElementoListCell(
                elementos::remove,
                this::cambiarEstadoTarea,
                this::reprogramarRecordatorio,
                this::editarElemento
        ));
    }

    @FXML
    private void onCrearTarea() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("crear_tarea.fxml"));
            Parent root = loader.load();
            CrearTareaController controller = loader.getController();

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Nueva Tarea");
            modal.setScene(new Scene(root));
            modal.showAndWait();

            if (controller.isGuardado()) {
                Tarea tarea = new Tarea(siguienteId++, controller.getTitulo(),
                        controller.getDescripcion(), controller.getPrioridad(),
                        Estado.PENDIENTE, controller.getFecha());
                elementos.add(tarea);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCrearRecordatorio() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("crear_recordatorio.fxml"));
            Parent root = loader.load();
            CrearRecordatorioController controller = loader.getController();

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Nuevo Recordatorio");
            modal.setScene(new Scene(root));
            modal.showAndWait();

            if (controller.isGuardado()) {
                Recordatorio recordatorio = new Recordatorio(siguienteId++, controller.getTitulo(),
                        controller.getDescripcion(), controller.getPrioridad(), controller.getFecha());
                elementos.add(recordatorio);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editarElemento(Elemento elemento) {
        try {
            if (elemento instanceof Tarea tarea) {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("crear_tarea.fxml"));
                Parent root = loader.load();
                CrearTareaController controller = loader.getController();
                controller.precargarDatos(tarea.getTitulo(), tarea.getDescripcion(),
                        tarea.getPrioridad(), tarea.getFechaLimite());

                Stage modal = new Stage();
                modal.initModality(Modality.APPLICATION_MODAL);
                modal.setTitle("Editar Tarea");
                modal.setScene(new Scene(root));
                modal.showAndWait();

                if (controller.isGuardado()) {
                    tarea.setTitulo(controller.getTitulo());
                    tarea.setDescripcion(controller.getDescripcion());
                    tarea.setPrioridad(controller.getPrioridad());
                    tarea.setFechaLimite(controller.getFecha());
                    listViewElementos.refresh();
                }
            } else if (elemento instanceof Recordatorio recordatorio) {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("crear_recordatorio.fxml"));
                Parent root = loader.load();
                CrearRecordatorioController controller = loader.getController();
                controller.precargarDatos(recordatorio.getTitulo(), recordatorio.getDescripcion(),
                        recordatorio.getPrioridad(), recordatorio.getFechaLimite());

                Stage modal = new Stage();
                modal.initModality(Modality.APPLICATION_MODAL);
                modal.setTitle("Editar Recordatorio");
                modal.setScene(new Scene(root));
                modal.showAndWait();

                if (controller.isGuardado()) {
                    recordatorio.setTitulo(controller.getTitulo());
                    recordatorio.setDescripcion(controller.getDescripcion());
                    recordatorio.setPrioridad(controller.getPrioridad());
                    recordatorio.setFechaLimite(controller.getFecha());
                    listViewElementos.refresh();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cambiarEstadoTarea(Tarea tarea) {
        ChoiceDialog<Estado> dialog = new ChoiceDialog<>(tarea.getEstado(), Estado.values());
        dialog.setTitle("Cambiar estado");
        dialog.setHeaderText("Tarea: " + tarea.getTitulo());
        dialog.setContentText("Nuevo estado:");

        Optional<Estado> resultado = dialog.showAndWait();
        resultado.ifPresent(nuevoEstado -> {
            tarea.cambiarEstado(nuevoEstado);
            listViewElementos.refresh();
        });
    }

    private void reprogramarRecordatorio(Recordatorio recordatorio) {
        Dialog<LocalDate> dialog = new Dialog<>();
        dialog.setTitle("Reprogramar recordatorio");
        dialog.setHeaderText("Recordatorio: " + recordatorio.getTitulo());

        DatePicker datePicker = new DatePicker(recordatorio.getFechaLimite());
        dialog.getDialogPane().setContent(datePicker);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(boton -> boton == ButtonType.OK ? datePicker.getValue() : null);

        Optional<LocalDate> resultado = dialog.showAndWait();
        resultado.ifPresent(nuevaFecha -> {
            if (nuevaFecha.isBefore(LocalDate.now())) {
                Alert alerta = new Alert(Alert.AlertType.WARNING, "La fecha no puede ser en el pasado.");
                alerta.showAndWait();
                return;
            }
            recordatorio.reprogramarFecha(nuevaFecha);
            listViewElementos.refresh();
        });
    }

    @FXML
    private void onCompartir() {
        Elemento seleccionado = listViewElementos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION, "Selecciona primero una tarea o recordatorio de la lista para compartir.");
            alerta.setHeaderText(null);
            alerta.showAndWait();
            return;
        }

        // TODO: cuando exista el DAO real, aquí se listarán los usuarios reales de la BD para elegir con quién compartir.
        Alert alerta = new Alert(Alert.AlertType.INFORMATION,
                "\"" + seleccionado.getTitulo() + "\" está listo para compartirse. " +
                        "Esta función se activará cuando haya usuarios reales registrados en la base de datos.");
        alerta.setHeaderText("Compartir elemento");
        alerta.showAndWait();
    }

    @FXML
    private void onCerrarSesion() {
        try {
            App.cambiarEscena("login.fxml", "Iniciar Sesión");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}