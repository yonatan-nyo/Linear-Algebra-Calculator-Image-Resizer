package chisli;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class InterpolasiPolinomialController {

    @FXML
    private Button primaryButton;

    // Initialize method (optional)
    @FXML
    public void initialize() {
        // Any initialization if needed
    }

    @FXML
    private void switchToRegresiBerganda() {
        try {
            Router.navigateToRegresiBerganda(); // Change to the desired navigation method
        } catch (IOException e) {
            e.printStackTrace(); // Handle exception
        }
    }
}
