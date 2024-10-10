package chisli;

import java.io.IOException;
import java.util.Arrays;

import chisli.utils.interpolasiPolinomial.InterpolasiPolinomial;
import chisli.utils.regresi.RegresiLinier;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class RegresiBergandaController {

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
    private void switchToRegresiBerganda() {
        try {
            Router.navigateToRegresiBerganda(); // Change to the desired navigation method
        } catch (IOException e) {
            e.printStackTrace(); // Handle exception
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
    public void performRegresiLinier() {
            try {
                String[] lines = dataInputField.getText().split("\n");
            int n = lines.length; // Number of samples
            String[] firstLineParts = lines[0].trim().split(" ");
            int m = firstLineParts.length - 1; // Number of features (exclude the last value, which is y)

            double[][] xValues = new double[n][m]; // 2D array for multiple features
            double[] yValues = new double[n];

            // Parse each line to get x and y values
            for (int i = 0; i < lines.length; i++) {
                String[] parts = lines[i].trim().split(" ");
                if (parts.length != m + 1) {
                    throw new IllegalArgumentException("Setiap baris harus berisi " + (m + 1) + " angka (fitur dan y).");
                }

                // Parse x values (features)
                for (int j = 0; j < m; j++) {
                    xValues[i][j] = Double.parseDouble(parts[j]);
                }

                // Parse y value
                yValues[i] = Double.parseDouble(parts[m]);
            }
            
            // Call the regression method

            double[][] result = RegresiLinier.solve(xValues, yValues);

            // Format the result
            StringBuilder resultText = new StringBuilder("Hasil regresi: ");
            for (int i = 0; i < result.length; i++) {
                resultText.append("b").append(i).append("=").append(result[i][0]);
                if (i < result.length - 1) {
                    resultText.append(", ");
                }
            }
            resultLabel.setText(resultText.toString());
        } catch (NumberFormatException e) {
            resultLabel.setText("Error: Please enter valid numbers.");
        } catch (IllegalArgumentException e) {
            resultLabel.setText(e.getMessage());
        }
    }
}
