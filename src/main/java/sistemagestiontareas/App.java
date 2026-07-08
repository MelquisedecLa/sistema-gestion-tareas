package sistemagestiontareas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class App extends Application {

    private static Stage stagePrincipal;

    @Override
    public void start(Stage stage) throws Exception {
        stagePrincipal = stage;
        stagePrincipal.getIcons().add(new Image(getClass().getResourceAsStream("icons/app_icon.png")));
        cambiarEscena("login.fxml", "Iniciar Sesión");
    }

    public static void cambiarEscena(String fxml, String titulo) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());
        stagePrincipal.setTitle(titulo);
        stagePrincipal.setScene(scene);
        stagePrincipal.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}