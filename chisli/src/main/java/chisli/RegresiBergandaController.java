package chisli;

import java.io.IOException;
import java.util.Arrays;

import chisli.utils.regresi.RegresiLinier;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

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
    private void switchToRegresiLinierBerganda() {
        try {
            Router.navigateToRegresiLinierBerganda(); // Change to the desired navigation method
        } catch (IOException e) {
            e.printStackTrace(); // Handle exception
        }
    }
    @FXML
    private void switchToRegresiKuadratikBerganda() {
        try {
            Router.navigateToRegresiKuadratikBerganda(); // Change to the desired navigation method
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
            int n = lines.length - 1; // Number of samples (excluding the last line for xk)
            String[] firstLineParts = lines[0].trim().split("\\s+"); // Split by any whitespace sequence
            int m = firstLineParts.length - 1; // Number of features (exclude the last value, which is y)

            double[][] xValues = new double[n][m]; // 2D array for multiple features
            double[] yValues = new double[n];

            // Parse each line to get x and y values (excluding the last line for xk)
            for (int i = 0; i < n; i++) {
                String[] parts = lines[i].trim().split("\\s+"); // Split by any whitespace sequence
                if (parts.length != m + 1) {
                    throw new IllegalArgumentException("Baris " + (i + 1) + " harus berisi " + (m + 1) + " angka (fitur dan y), pastikan tidak ada spasi berlebih.");
                }

                // Parse x values (features)
                for (int j = 0; j < m; j++) {
                    xValues[i][j] = Double.parseDouble(parts[j]);
                }

                // Parse y value
                yValues[i] = Double.parseDouble(parts[m]);
            }

            // Parse the last line as xk values
            String[] xkParts = lines[lines.length - 1].trim().split("\\s+"); // Split by any whitespace sequence
            if (xkParts.length != m) {
                throw new IllegalArgumentException("Baris terakhir harus berisi " + m + " angka (fitur untuk xk), pastikan tidak ada spasi berlebih.");
            }
            double[] xk = new double[m]; // Array for xk
            for (int i = 0; i < m; i++) {
                xk[i] = Double.parseDouble(xkParts[i]);
            }

            // Call the regression method
            double[][] result = RegresiLinier.solve(xValues, yValues);

            // Format the result for f(x)
            StringBuilder resultText = new StringBuilder("Hasil regresi: ");
            resultText.append("f(x) = ");
            for (int i = 0; i < result.length; i++) {
                resultText.append(String.format("%.4f", result[i][0]));  // Append coefficient with 4 decimal precision
                if (i > 0) {
                    resultText.append("x").append(i);  // Append x1, x2, etc. for variables
                }
                if (i < result.length - 1) {
                    resultText.append(" + ");
                }
            }

            // Calculate the estimated value for xk
            double estimatedValue = result[0][0];  // Start with the intercept (b0)
            for (int i = 1; i < result.length; i++) {
                estimatedValue += result[i][0] * xk[i - 1];  // Calculate b1*x1 + b2*x2 ...
            }

            resultText.append(", f(xk) = ").append(String.format("%.4f", estimatedValue));

            resultLabel.setText(resultText.toString());
        } catch (NumberFormatException e) {
            resultLabel.setText("Error: Pastikan semua nilai adalah angka yang valid.");
        } catch (IllegalArgumentException e) {
            resultLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void performRegresiKuadratik() {
        
    }
}
