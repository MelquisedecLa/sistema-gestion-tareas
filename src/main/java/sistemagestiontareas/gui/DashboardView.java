package sistemagestiontareas.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import sistemagestiontareas.enums.Estado;
import sistemagestiontareas.enums.Prioridad;
import sistemagestiontareas.model.Elemento;
import sistemagestiontareas.model.Recordatorio;
import sistemagestiontareas.model.Tarea;
import sistemagestiontareas.model.Usuario;
import sistemagestiontareas.model.UsuarioClasico;
import sistemagestiontareas.model.UsuarioPremium;
import sistemagestiontareas.persistence.RecordatorioDAO;
import sistemagestiontareas.persistence.RecordatorioDAOImpl;
import sistemagestiontareas.persistence.TareaDAO;
import sistemagestiontareas.persistence.TareaDAOImpl;
import sistemagestiontareas.persistence.UsuarioDAO;
import sistemagestiontareas.persistence.UsuarioDAOImpl;
import sistemagestiontareas.thread.AutoGuardarThread;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Pantalla principal del sistema una vez que el usuario ha iniciado sesión.
 *
 * <p>Muestra los elementos del usuario (tareas y recordatorios) en un
 * {@link ListView} con colores por prioridad e iconos distintivos, y expone
 * operaciones para crear, cambiar estado, reprogramar y compartir elementos.</p>
 *
 * <p>Implementa el patrón <em>fx:root</em> de JavaFX FXML: la clase extiende
 * {@link BorderPane} y se registra como raíz y controlador de {@code dashboard.fxml}.
 * La lógica de negocio (límites de {@link UsuarioClasico}, {@link AutoGuardarThread},
 * {@link TareaDAOImpl}, {@link RecordatorioDAOImpl}, etc.) permanece completamente intacta respecto a la version
 * de consola.</p>
 */
public class DashboardView extends BorderPane {

    // ── Campos inyectados desde dashboard.fxml ────────────────────────────────

    /** Etiqueta con el nombre y tipo de cuenta del usuario activo. */
    @FXML private Label etiquetaNombre;

    /** Etiqueta con información de membresía (Premium) o límite de elementos (Clásico). */
    @FXML private Label etiquetaInfo;

    /** Lista visual de tareas y recordatorios del usuario. */
    @FXML private ListView<Elemento> listaElementos;

    // ── Estado de la vista ────────────────────────────────────────────────────

    private final Usuario usuarioActual;
    private final Runnable alCerrarSesion;
    private final TareaDAO        tareaDAO        = new TareaDAOImpl();
    private final RecordatorioDAO recordatorioDAO = new RecordatorioDAOImpl();
    private final UsuarioDAO      usuarioDAO      = new UsuarioDAOImpl();
    private final ObservableList<Elemento> elementosObservable;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Construye la vista del panel principal y carga {@code dashboard.fxml} con
     * el patrón {@code fx:root}. Los campos del usuario deben estar inicializados
     * antes de que {@link FXMLLoader#load()} invoque a {@link #initialize()}.
     *
     * @param usuarioActual   usuario que acaba de iniciar sesión
     * @param alCerrarSesion  callback invocado al presionar "Cerrar sesion"
     * @throws RuntimeException si {@code dashboard.fxml} no se encuentra en el classpath
     */
    public DashboardView(Usuario usuarioActual, Runnable alCerrarSesion) {
        this.usuarioActual      = usuarioActual;
        this.alCerrarSesion     = alCerrarSesion;
        this.elementosObservable = FXCollections.observableArrayList(usuarioActual.getElementos());

        var url = getClass().getResource("dashboard.fxml");
        if (url == null) {
            throw new RuntimeException("No se encontro dashboard.fxml en el classpath");
        }
        FXMLLoader loader = new FXMLLoader(url);
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar dashboard.fxml", e);
        }
    }

    // ── Inicialización post-FXML ─────────────────────────────────────────────

    /**
     * Invocado automáticamente por {@link FXMLLoader} tras inyectar los campos
     * {@code @FXML}. Configura el {@link ListView} y las etiquetas del encabezado
     * en función del tipo de usuario.
     */
    @FXML
    private void initialize() {
        // Configurar ListView con celda personalizada (colores + iconos)
        listaElementos.setItems(elementosObservable);
        listaElementos.setCellFactory(lv -> new ElementoListCell());

        // Encabezado dinámico según tipo de usuario
        String tipo = (usuarioActual instanceof UsuarioPremium) ? "Premium" : "Clasico";
        etiquetaNombre.setText("Bienvenido, " + usuarioActual.getNombre() + " (" + tipo + ")");

        if (usuarioActual instanceof UsuarioPremium premium) {
            String estado = premium.verificarMembresia() ? "activa" : "vencida";
            etiquetaInfo.setText("Membresia " + estado + " hasta " + premium.getFechaExpiracion());
        } else if (usuarioActual instanceof UsuarioClasico clasico) {
            etiquetaInfo.setText(
                    "Elementos: " + clasico.getElementos().size() + " / " + clasico.getLimiteElementos());
        }
    }

    // ── Métodos de acción FXML (puentes hacia la lógica de negocio) ───────────

    /** @see #crearElemento(boolean) */
    @FXML private void accionNuevaTarea()         { crearElemento(true);  }

    /** @see #crearElemento(boolean) */
    @FXML private void accionNuevoRecordatorio()  { crearElemento(false); }

    /** @see #cambiarEstadoSeleccionado() */
    @FXML private void accionCambiarEstado()      { cambiarEstadoSeleccionado(); }

    /** @see #reprogramarSeleccionado() */
    @FXML private void accionReprogramar()        { reprogramarSeleccionado();   }

    /** @see #compartirSeleccionado() */
    @FXML private void accionCompartir()          { compartirSeleccionado();     }

    /** @see #recargarElementos() */
    @FXML private void accionActualizar()         { recargarElementos();         }

    /** Cierra la sesión del usuario y regresa a la pantalla de login. */
    @FXML
    private void accionCerrarSesion() {
        usuarioActual.cerrarSesion();
        alCerrarSesion.run();
    }

    // ── Lógica de negocio ────────────────────────────────────────────────────

    /**
     * Abre un diálogo para capturar los datos de una nueva tarea o recordatorio,
     * lo persiste en la base de datos y lo agrega a la lista visual.
     *
     * <p>Verifica el límite de {@link UsuarioClasico} antes de mostrar el diálogo.
     * Lanza un hilo {@link AutoGuardarThread} (patrón FORK/JOIN del entregable 2)
     * mientras el usuario completa el formulario.</p>
     *
     * @param esTarea {@code true} para crear una Tarea; {@code false} para un Recordatorio
     */
    private void crearElemento(boolean esTarea) {
        if (usuarioActual instanceof UsuarioClasico clasico && !clasico.verificarLimiteTareas()) {
            mostrarAlerta(AlertType.WARNING, "Limite alcanzado",
                    "Llegaste al limite de " + clasico.getLimiteElementos() + " elementos para cuentas Clasico.");
            return;
        }

        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle(esTarea ? "Nueva tarea" : "Nuevo recordatorio");
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField campoTitulo       = new TextField();
        TextArea  campoDescripcion  = new TextArea();
        campoDescripcion.setPrefRowCount(3);
        ComboBox<Prioridad> comboPrioridad =
                new ComboBox<>(FXCollections.observableArrayList(Prioridad.values()));
        comboPrioridad.setValue(Prioridad.MEDIA);
        DatePicker selectorFecha = new DatePicker(LocalDate.now().plusDays(1));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.addRow(0, new Label("Titulo:"),       campoTitulo);
        grid.addRow(1, new Label("Descripcion:"),  campoDescripcion);
        grid.addRow(2, new Label("Prioridad:"),    comboPrioridad);
        grid.addRow(3, new Label("Fecha limite:"), selectorFecha);
        dialogo.getDialogPane().setContent(grid);

        // Hilo de autoguardado (mismo patrón FORK/JOIN que en Main.java de consola)
        AutoGuardarThread autoGuardar = new AutoGuardarThread(esTarea ? "Tarea" : "Recordatorio");
        autoGuardar.start();

        Button botonOk = (Button) dialogo.getDialogPane().lookupButton(ButtonType.OK);
        botonOk.addEventFilter(ActionEvent.ACTION, evento -> {
            String tituloTexto = campoTitulo.getText() == null ? "" : campoTitulo.getText().trim();
            LocalDate fecha = selectorFecha.getValue();
            if (tituloTexto.isEmpty()) {
                mostrarAlerta(AlertType.ERROR, "Dato invalido", "El titulo no puede estar vacio.");
                evento.consume();
                return;
            }
            if (fecha == null || fecha.isBefore(LocalDate.now())) {
                mostrarAlerta(AlertType.ERROR, "Dato invalido", "La fecha limite no puede ser en el pasado.");
                evento.consume();
            }
        });

        Optional<ButtonType> resultado = dialogo.showAndWait();
        autoGuardar.detener();  // JOIN: detiene el hilo de autoguardado

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            String    tituloTexto = campoTitulo.getText().trim();
            String    descripcion = campoDescripcion.getText();
            Prioridad prioridad   = comboPrioridad.getValue();
            LocalDate fecha       = selectorFecha.getValue();

            try {
                if (esTarea) {
                    Tarea borrador = new Tarea(0, tituloTexto, descripcion, prioridad, Estado.PENDIENTE, fecha.atStartOfDay());
                    int id = tareaDAO.guardar(borrador, usuarioActual.getId());
                    Tarea tarea = new Tarea(id, tituloTexto, descripcion, prioridad, Estado.PENDIENTE, fecha.atStartOfDay());
                    usuarioActual.crearElemento(tarea);
                    elementosObservable.add(tarea);
                } else {
                    Recordatorio borrador = new Recordatorio(0, tituloTexto, descripcion, prioridad, fecha.atStartOfDay());
                    int id = recordatorioDAO.guardar(borrador, usuarioActual.getId());
                    Recordatorio rec = new Recordatorio(id, tituloTexto, descripcion, prioridad, fecha.atStartOfDay());
                    usuarioActual.crearElemento(rec);
                    elementosObservable.add(rec);
                }
            } catch (SQLException ex) {
                mostrarAlerta(AlertType.ERROR, "Error de base de datos",
                        "No se pudo guardar: " + ex.getMessage());
            }
        }
    }

    /**
     * Abre un diálogo de selección para cambiar el estado de la tarea seleccionada
     * y persiste el cambio en la base de datos.
     */
    private void cambiarEstadoSeleccionado() {
        Elemento seleccionado = listaElementos.getSelectionModel().getSelectedItem();
        if (!(seleccionado instanceof Tarea tarea)) {
            mostrarAlerta(AlertType.INFORMATION, "Selecciona una tarea",
                    "Elige una tarea de la lista (los recordatorios no tienen estado).");
            return;
        }

        ChoiceDialog<Estado> dialogo = new ChoiceDialog<>(tarea.getEstado(), Estado.values());
        dialogo.setTitle("Cambiar estado");
        dialogo.setHeaderText(tarea.getTitulo());
        dialogo.setContentText("Nuevo estado:");

        dialogo.showAndWait().ifPresent(nuevoEstado -> {
            tarea.cambiarEstado(nuevoEstado);
            try {
                tareaDAO.actualizarEstado(tarea.getId(), nuevoEstado);
                listaElementos.refresh();
            } catch (SQLException ex) {
                mostrarAlerta(AlertType.ERROR, "Error de base de datos",
                        "No se pudo actualizar: " + ex.getMessage());
            }
        });
    }

    /**
     * Abre un diálogo con un {@link DatePicker} para reprogramar la fecha del
     * recordatorio seleccionado y persiste el cambio.
     */
    private void reprogramarSeleccionado() {
        Elemento seleccionado = listaElementos.getSelectionModel().getSelectedItem();
        if (!(seleccionado instanceof Recordatorio recordatorio)) {
            mostrarAlerta(AlertType.INFORMATION, "Selecciona un recordatorio",
                    "Elige un recordatorio de la lista para reprogramarlo.");
            return;
        }

        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Reprogramar recordatorio");
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        DatePicker selectorFecha = new DatePicker(recordatorio.getFechaLimite().toLocalDate());
        VBox contenido = new VBox(10,
                new Label("Nueva fecha para: " + recordatorio.getTitulo()), selectorFecha);
        contenido.setPadding(new Insets(20));
        dialogo.getDialogPane().setContent(contenido);

        Button botonOk = (Button) dialogo.getDialogPane().lookupButton(ButtonType.OK);
        botonOk.addEventFilter(ActionEvent.ACTION, evento -> {
            LocalDate fecha = selectorFecha.getValue();
            if (fecha == null || fecha.isBefore(LocalDate.now())) {
                mostrarAlerta(AlertType.ERROR, "Dato invalido", "La fecha no puede ser en el pasado.");
                evento.consume();
            }
        });

        Optional<ButtonType> resultado = dialogo.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            LocalDate nuevaFecha = selectorFecha.getValue();
            recordatorio.reprogramarFecha(nuevaFecha.atStartOfDay());
            try {
                recordatorioDAO.actualizar(recordatorio);
                listaElementos.refresh();
            } catch (SQLException ex) {
                mostrarAlerta(AlertType.ERROR, "Error de base de datos",
                        "No se pudo actualizar: " + ex.getMessage());
            }
        }
    }

    /**
     * Abre un diálogo con un {@link ComboBox} de usuarios para compartir el elemento
     * seleccionado, y registra el acceso compartido en la base de datos.
     */
    private void compartirSeleccionado() {
        Elemento seleccionado = listaElementos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(AlertType.INFORMATION, "Nada seleccionado",
                    "Elige un elemento de la lista para compartir.");
            return;
        }

        List<Usuario> otros;
        try {
            otros = usuarioDAO.buscarTodos();
        } catch (SQLException ex) {
            mostrarAlerta(AlertType.ERROR, "Error de base de datos",
                    "No se pudo cargar la lista de usuarios: " + ex.getMessage());
            return;
        }
        otros.removeIf(u -> u.getId() == usuarioActual.getId());

        if (otros.isEmpty()) {
            mostrarAlerta(AlertType.INFORMATION, "Sin destinatarios",
                    "No hay otros usuarios registrados todavia.");
            return;
        }

        Dialog<Usuario> dialogo = new Dialog<>();
        dialogo.setTitle("Compartir elemento");
        dialogo.setHeaderText("Compartir: " + seleccionado.getTitulo());
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ComboBox<Usuario> comboUsuarios =
                new ComboBox<>(FXCollections.observableArrayList(otros));
        comboUsuarios.setValue(otros.get(0));
        comboUsuarios.setConverter(new StringConverter<Usuario>() {
            @Override public String toString(Usuario u) {
                return u == null ? "" : u.getNombre() + " (" + u.getEmail() + ")";
            }
            @Override public Usuario fromString(String texto) {
                return comboUsuarios.getValue();
            }
        });

        VBox contenido = new VBox(10,
                new Label("Selecciona el usuario destino:"), comboUsuarios);
        contenido.setPadding(new Insets(20));
        dialogo.getDialogPane().setContent(contenido);
        dialogo.setResultConverter(boton -> boton == ButtonType.OK ? comboUsuarios.getValue() : null);

        dialogo.showAndWait().ifPresent(usuarioDestino -> {
            usuarioActual.compartirElemento(seleccionado, usuarioDestino);
            seleccionado.getUsuariosCompartidos().add(usuarioActual);
            usuarioDestino.crearElemento(seleccionado);
            try {
                if (seleccionado instanceof Tarea t) {
                    tareaDAO.compartir(t.getId(), usuarioDestino.getId());
                } else if (seleccionado instanceof Recordatorio r) {
                    recordatorioDAO.compartir(r.getId(), usuarioDestino.getId());
                }
                listaElementos.refresh();
                mostrarAlerta(AlertType.INFORMATION, "Compartido",
                        "Elemento compartido con " + usuarioDestino.getNombre() + ".");
            } catch (SQLException ex) {
                mostrarAlerta(AlertType.ERROR, "Error de base de datos",
                        "No se pudo registrar el compartido: " + ex.getMessage());
            }
        });
    }

    /**
     * Recarga la lista de elementos desde la base de datos y actualiza la vista.
     */
    private void recargarElementos() {
        try {
            List<Elemento> elementos = new java.util.ArrayList<>();
            elementos.addAll(tareaDAO.buscarPorUsuario(usuarioActual.getId()));
            elementos.addAll(recordatorioDAO.buscarPorUsuario(usuarioActual.getId()));
            usuarioActual.getElementos().clear();
            usuarioActual.getElementos().addAll(elementos);
            elementosObservable.setAll(elementos);
        } catch (SQLException ex) {
            mostrarAlerta(AlertType.ERROR, "Error de base de datos",
                    "No se pudo actualizar la lista: " + ex.getMessage());
        }
    }

    /**
     * Muestra un {@link Alert} modal con el tipo, título y mensaje indicados.
     *
     * @param tipo    tipo de alerta (INFO, WARNING, ERROR, etc.)
     * @param titulo  título de la ventana de alerta
     * @param mensaje cuerpo del mensaje mostrado al usuario
     */
    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
