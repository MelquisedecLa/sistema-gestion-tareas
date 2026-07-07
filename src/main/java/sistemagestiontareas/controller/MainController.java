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
import sistemagestiontareas.Sesion;
import sistemagestiontareas.dao.RecordatorioDAO;
import sistemagestiontareas.dao.RecordatorioDAOImpl;
import sistemagestiontareas.dao.TareaDAO;
import sistemagestiontareas.dao.TareaDAOImpl;
import sistemagestiontareas.dao.ElementoCompartidoDAO;
import sistemagestiontareas.dao.ElementoCompartidoDAOImpl;
import sistemagestiontareas.dao.UsuarioDAO;
import sistemagestiontareas.dao.UsuarioDAOImpl;
import sistemagestiontareas.enums.Estado;
import sistemagestiontareas.model.Elemento;
import sistemagestiontareas.model.Recordatorio;
import sistemagestiontareas.model.Tarea;
import sistemagestiontareas.model.Usuario;
import sistemagestiontareas.model.UsuarioClasico;
import sistemagestiontareas.model.UsuarioPremium;

import java.time.LocalDate;
import java.util.Optional;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainController {

    @FXML private Label labelBienvenida;
    @FXML private Label labelTipoUsuario;
    @FXML private ListView<Elemento> listViewElementos;

    private final ObservableList<Elemento> elementos = FXCollections.observableArrayList();
    private final TareaDAO tareaDAO = new TareaDAOImpl();
    private final RecordatorioDAO recordatorioDAO = new RecordatorioDAOImpl();
    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private final ElementoCompartidoDAO compartidoDAO = new ElementoCompartidoDAOImpl();

    @FXML
    public void initialize() {
        Usuario usuario = Sesion.getUsuarioActual();
        if (usuario == null) return;

        labelBienvenida.setText("Hola, " + usuario.getNombre());
        labelTipoUsuario.setText(usuario instanceof UsuarioPremium ? "Premium" : "Clásico");

        cargarElementosDesdeBD(usuario.getId());

        listViewElementos.setItems(elementos);
        listViewElementos.setCellFactory(lv -> new ElementoListCell(
                this::eliminarElemento,
                this::cambiarEstadoTarea,
                this::reprogramarRecordatorio,
                this::editarElemento
        ));
    }

    private void cargarElementosDesdeBD(int usuarioId) {
        try {
            elementos.clear();
            elementos.addAll(tareaDAO.buscarPorUsuario(usuarioId));
            elementos.addAll(recordatorioDAO.buscarPorUsuario(usuarioId));

            for (int idCompartido : compartidoDAO.buscarIdsCompartidosConUsuario(usuarioId)) {
                Tarea tarea = tareaDAO.buscarPorId(idCompartido);
                if (tarea != null) {
                    elementos.add(tarea);
                    continue;
                }
                Recordatorio recordatorio = recordatorioDAO.buscarPorId(idCompartido);
                if (recordatorio != null) {
                    elementos.add(recordatorio);
                }
            }
        } catch (RuntimeException e) {
            mostrarError("No se pudieron cargar tus tareas y recordatorios.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onCrearTarea() {
        Usuario usuario = Sesion.getUsuarioActual();
        if (usuario == null) return;

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
                if (usuario instanceof UsuarioClasico clasico
                        && contarElementosDeUsuario(usuario.getId()) >= clasico.getLimiteElementos()) {
                    mostrarAdvertencia("Alcanzaste el límite de elementos permitidos para tu cuenta Clásica.");
                    return;
                }

                Tarea tarea = new Tarea(0, controller.getTitulo(),
                        controller.getDescripcion(), controller.getPrioridad(),
                        Estado.PENDIENTE, controller.getFecha());

                int idGenerado = tareaDAO.guardar(tarea, usuario.getId());
                Tarea tareaGuardada = tareaDAO.buscarPorId(idGenerado);
                elementos.add(tareaGuardada);
                usuario.getElementos().add(tareaGuardada);
            }
        } catch (RuntimeException e) {
            mostrarError("No se pudo guardar la tarea en la base de datos.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCrearRecordatorio() {
        Usuario usuario = Sesion.getUsuarioActual();
        if (usuario == null) return;

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
                if (usuario instanceof UsuarioClasico clasico
                        && contarElementosDeUsuario(usuario.getId()) >= clasico.getLimiteElementos()) {
                    mostrarAdvertencia("Alcanzaste el límite de elementos permitidos para tu cuenta Clásica.");
                    return;
                }

                Recordatorio recordatorio = new Recordatorio(0, controller.getTitulo(),
                        controller.getDescripcion(), controller.getPrioridad(), controller.getFecha());

                int idGenerado = recordatorioDAO.guardar(recordatorio, usuario.getId());
                Recordatorio recordatorioGuardado = recordatorioDAO.buscarPorId(idGenerado);
                elementos.add(recordatorioGuardado);
                usuario.getElementos().add(recordatorioGuardado);
            }
        } catch (RuntimeException e) {
            mostrarError("No se pudo guardar el recordatorio en la base de datos.");
            e.printStackTrace();
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
                    tareaDAO.actualizar(tarea);
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
                    recordatorioDAO.actualizar(recordatorio);
                    listViewElementos.refresh();
                }
            }
        } catch (RuntimeException e) {
            mostrarError("No se pudo actualizar en la base de datos.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void eliminarElemento(Elemento elemento) {
        try {
            boolean eliminado = (elemento instanceof Tarea)
                    ? tareaDAO.eliminar(elemento.getId())
                    : recordatorioDAO.eliminar(elemento.getId());

            if (eliminado) {
                elementos.remove(elemento);
                Usuario usuario = Sesion.getUsuarioActual();
                if (usuario != null) usuario.getElementos().remove(elemento);
            }
        } catch (RuntimeException e) {
            mostrarError("No se pudo eliminar de la base de datos.");
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
            try {
                tareaDAO.actualizarEstado(tarea.getId(), nuevoEstado);
                tarea.cambiarEstado(nuevoEstado);
                listViewElementos.refresh();
            } catch (RuntimeException e) {
                mostrarError("No se pudo actualizar el estado en la base de datos.");
                e.printStackTrace();
            }
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
                mostrarAdvertencia("La fecha no puede ser en el pasado.");
                return;
            }
            recordatorio.reprogramarFecha(nuevaFecha);
            recordatorioDAO.actualizar(recordatorio);
            listViewElementos.refresh();
        });
    }

    @FXML
    private void onCompartir() {
        Elemento seleccionado = listViewElementos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarInfo("Selecciona primero una tarea o recordatorio de la lista para compartir.");
            return;
        }

        Usuario actual = Sesion.getUsuarioActual();
        List<Usuario> candidatos = usuarioDAO.buscarTodos();
        candidatos.removeIf(u -> u.getId() == actual.getId());

        if (candidatos.isEmpty()) {
            mostrarInfo("No hay otros usuarios registrados todavía para compartir.");
            return;
        }

        Map<String, Usuario> opciones = new LinkedHashMap<>();
        for (Usuario u : candidatos) {
            opciones.put(u.getNombre() + " (" + u.getEmail() + ")", u);
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(opciones.keySet().iterator().next(), opciones.keySet());
        dialog.setTitle("Compartir elemento");
        dialog.setHeaderText("Compartir \"" + seleccionado.getTitulo() + "\" con:");
        dialog.setContentText("Usuario:");

        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(seleccionUsuario -> {
            Usuario destino = opciones.get(seleccionUsuario);

            // Validar limite de compartir para UsuarioClasico (maximo 1 usuario por elemento)
            if (actual instanceof UsuarioClasico) {
                if (compartidoDAO.contarUsuariosCompartidos(seleccionado.getId()) >= 1) {
                    mostrarAdvertencia("Como usuario Clásico solo puedes compartir cada elemento con un máximo de 1 usuario.");
                    return;
                }
            }

            if (destino instanceof UsuarioClasico clasico) {
                if (contarElementosDeUsuario(destino.getId()) >= clasico.getLimiteElementos()) {
                    mostrarAdvertencia(destino.getNombre() + " ya tiene " + clasico.getLimiteElementos() +
                            " elementos (su límite como usuario Clásico). Debe eliminar alguno antes de recibir más.");
                    return;
                }
            }

            try {
                compartidoDAO.compartir(seleccionado.getId(), destino.getId());
                mostrarInfo("Compartido con " + destino.getNombre() + " correctamente.");
            } catch (RuntimeException e) {
                mostrarError("No se pudo compartir el elemento.");
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void onCerrarSesion() {
        Sesion.cerrarSesion();
        try {
            App.cambiarEscena("login.fxml", "Iniciar Sesión");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING, mensaje);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION, mensaje);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
    private int contarElementosDeUsuario(int usuarioId) {
        return tareaDAO.buscarPorUsuario(usuarioId).size()
                + recordatorioDAO.buscarPorUsuario(usuarioId).size()
                + compartidoDAO.buscarIdsCompartidosConUsuario(usuarioId).size();
    }
}