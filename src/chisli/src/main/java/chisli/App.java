package chisli;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println();
        stage.getIcons().add(new Image(App.class.getResourceAsStream("app-icon.png")));
        
        stage.setTitle("Chisli");
    
        scene = new Scene(loadFXML("sistem-persamaan-linear"), 640, 480);
        loadCSS("sistem-persamaan-linear");
        stage.setMaximized(true);
    
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