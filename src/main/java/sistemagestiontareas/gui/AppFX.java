package sistemagestiontareas.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sistemagestiontareas.model.Usuario;

/**
 * Punto de entrada principal de la interfaz grafica JavaFX (Entregable 3).
 *
 * <p>Extiende {@link Application} y gestiona la navegacion entre las dos
 * pantallas del sistema: autenticacion ({@link AuthView}) y panel principal
 * ({@link DashboardView}). El cambio de escena se realiza reutilizando el
 * mismo {@link Stage} primario con una nueva {@link Scene}.</p>
 *
 * <p>El {@code Main.java} de consola (entregables 1 y 2) queda intacto;
 * para ejecutarlo basta pasar {@code -PmainClass=sistemagestiontareas.Main}
 * al invocar {@code gradle run}.</p>
 */
public class AppFX extends Application {

    /** Ventana principal de la aplicacion, reutilizada en cada cambio de pantalla. */
    private Stage escenarioPrincipal;

    /**
     * Punto de entrada del proceso JVM.
     * Invoca {@link Application#launch(String...)} que inicializa el toolkit
     * de JavaFX y llama a {@link #start(Stage)}.
     *
     * @param args argumentos de linea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Metodo del ciclo de vida de JavaFX invocado tras la inicializacion del toolkit.
     * Conserva la referencia al escenario principal y muestra la pantalla de login.
     *
     * @param stage escenario (ventana) principal creado por el framework
     */
    @Override
    public void start(Stage stage) {
        this.escenarioPrincipal = stage;
        mostrarLogin();
        stage.show();
    }

    /**
     * Muestra la pantalla de autenticacion ({@link AuthView}) en el escenario principal.
     * Se usa como callback al cerrar sesion desde {@link DashboardView}.
     */
    private void mostrarLogin() {
        AuthView vistaAuth = new AuthView(this::mostrarDashboard);
        escenarioPrincipal.setScene(new Scene(vistaAuth, 480, 520));
        escenarioPrincipal.setTitle("Gestion de Tareas Colaborativas - Iniciar sesion");
    }

    /**
     * Muestra el panel principal ({@link DashboardView}) para el usuario que acaba
     * de autenticarse exitosamente. Se usa como callback desde {@link AuthView}.
     *
     * @param usuario usuario autenticado cuya informacion y elementos se mostraran
     */
    private void mostrarDashboard(Usuario usuario) {
        DashboardView vistaDashboard = new DashboardView(usuario, this::mostrarLogin);
        escenarioPrincipal.setScene(new Scene(vistaDashboard, 800, 600));
        escenarioPrincipal.setTitle("Gestion de Tareas Colaborativas - " + usuario.getNombre());
    }
}
