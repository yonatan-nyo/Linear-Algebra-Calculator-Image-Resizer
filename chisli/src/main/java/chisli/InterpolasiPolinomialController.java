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
    

        // Helper method to parse fractions
    private double parseFraction(String input) {
        if (input.contains("/")) {
            String[] parts = input.split("/");
            if (parts.length == 2) {
                double numerator = Double.parseDouble(parts[0]);
                double denominator = Double.parseDouble(parts[1]);
                return numerator / denominator;
            } else {
                throw new IllegalArgumentException("Invalid fraction format");
            }
        } else {
            return Double.parseDouble(input);
        }
    }
    
    @FXML
    public void performInterpolation() {
        try {
            String[] lines = dataInputField.getText().split("\n");
    
            // Check if each row from 0 to lines.length - 2 has exactly 2 columns
            for (int i = 0; i < lines.length - 1; i++) {
                String[] columns = lines[i].trim().split("\\s+");
                if (columns.length != 2) {
                    resultLabel.setText("Error: Row " + (i + 1) + " must contain exactly 2 columns.");
                    return;
                }
            }
    
            // Check if the last row has exactly 1 column
            String[] lastLineNumbers = lines[lines.length - 1].trim().split("\\s+");
            if (lastLineNumbers.length != 1) {
                resultLabel.setText("Error: The last row must contain exactly 1 column.");
                return;
            }
    
            double[] xValues = new double[lines.length - 1];
            double[] yValues = new double[lines.length - 1];
    
            // Parse each line to get x and y values
            for (int i = 0; i < lines.length - 1; i++) {
                String[] parts = lines[i].trim().split(" ");
                xValues[i] = parseFraction(parts[0]);
                yValues[i] = parseFraction(parts[1]);
            }
    
            double xToEvaluate = parseFraction(lines[lines.length - 1].trim());
    
            // Call the interpolation method
            String result = InterpolasiPolinomial.solve(xValues, yValues, xToEvaluate);
            resultLabel.setText(result);
        } catch (NumberFormatException e) {
            resultLabel.setText("Error: Please enter valid numbers.");
        } catch (IllegalArgumentException e) {
            resultLabel.setText(e.getMessage());
        }
    }
}
