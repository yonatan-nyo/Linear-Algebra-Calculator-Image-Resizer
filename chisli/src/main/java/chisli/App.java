package chisli;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println();
        scene = new Scene(loadFXML("primary"), 640, 480);
        loadCSS("primary");
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        loadCSS(fxml);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    private static void loadCSS(String fxml) {
        String cssFile = fxml + ".css";
        scene.getStylesheets().clear();
        scene.getStylesheets().add(App.class.getResource("global.css").toExternalForm());
        scene.getStylesheets().add(App.class.getResource(cssFile).toExternalForm());
    }

    public static void main(String[] args) {
        launch();
    }

}