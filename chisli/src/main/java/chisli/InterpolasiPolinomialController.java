package chisli;

import java.io.IOException;

import chisli.utils.interpolasiPolinomial.InterpolasiPolinomial;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class InterpolasiPolinomialController {
    @FXML
    private Button primaryButton;

    @FXML
    private TextArea dataInputField; 

    @FXML
    private Label resultLabel;

    // Initialize method (optional)
    @FXML
    public void initialize() {
        // Any initialization if needed
    }

    @FXML
    private void switchToSistemPersamaanLinear() {
        try {
            Router.navigateToSistemPersamaanLinear(); // Change to the desired navigation method
        } catch (IOException e) {
            e.printStackTrace(); // Handle exception
        }
    }    
    
    @FXML
    private void switchToInterpolasiPolinomial() {
        try {
            Router.navigateToInterpolasiPolinomial(); // Change to the desired navigation method
        } catch (IOException e) {
            e.printStackTrace(); // Handle exception
        }
    }

    @FXML
    private void switchToRegresiLinierBerganda() {
        try {
            Router.navigateToRegresiLinierBerganda();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToRegresiKuadratikBerganda() {
        try {
            Router.navigateToRegresiKuadratikBerganda();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToBicubicSplineInterpolation() {
        try {
            Router.navigateToBicubicSplineInterpolation(); // Change to the desired navigation method
        } catch (IOException e) {
            e.printStackTrace(); // Handle exception
        }
    }
    

    @FXML
    public void performInterpolation() {
        try {
            String[] lines = dataInputField.getText().split("\n");
            double[] xValues = new double[lines.length];
            double[] yValues = new double[lines.length];

            // Parse each line to get x and y values
            for (int i = 0; i < lines.length-1; i++) {
                String[] parts = lines[i].trim().split(" ");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Setiap baris harus berisi dua angka (x dan y).");
                }
                xValues[i] = Double.parseDouble(parts[0]);
                yValues[i] = Double.parseDouble(parts[1]);
            }

            double xToEvaluate = Double.parseDouble(lines[lines.length-1].trim());

            // Call the interpolation method
            double result = InterpolasiPolinomial.solve(xValues, yValues, xToEvaluate);
            resultLabel.setText("Hasil interpolasi: " + result);
        } catch (NumberFormatException e) {
            resultLabel.setText("Error: Please enter valid numbers.");
        } catch (IllegalArgumentException e) {
            resultLabel.setText(e.getMessage());
        }
    }
}
