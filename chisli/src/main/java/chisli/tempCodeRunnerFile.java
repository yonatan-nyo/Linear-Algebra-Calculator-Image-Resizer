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
public class tempCodeRunnerFile extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println();
        scene = new Scene(loadFXML("sistem-persamaan-linear"), 640, 480);
        loadCSS("sistem-persamaan-linear");
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        loadCSS(fxml);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(tempCodeRunnerFile.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    private static void loadCSS(String fxml) {
        String cssFile = fxml + ".css";
        scene.getStylesheets().clear();
        scene.getStylesheets().add(tempCodeRunnerFile.class.getResource("global.css").toExternalForm());
        scene.getStylesheets().add(tempCodeRunnerFile.class.getResource(cssFile).toExternalForm());
    }

    public static void main(String[] args) {
        launch();
    }

}