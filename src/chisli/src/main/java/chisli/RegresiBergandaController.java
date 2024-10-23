package chisli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import chisli.utils.regresi.RegresiKuadratik;
import chisli.utils.regresi.RegresiLinier;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;


public class RegresiBergandaController {
    @FXML
    private Button primaryButton;

    @FXML
    private TextArea dataInputField; 

    @FXML
    private Text resultLabel1;

    @FXML
    private Text resultLabel2;

    @FXML
    private Button fileUploadButton;

    // Initialize method (optional)
    @FXML
    public void initialize() {
        // Any initialization if needed
        resultLabel1.setFill(Color.GREENYELLOW);
        resultLabel2.setFill(Color.GREENYELLOW);
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
    private void switchToImageResize() {
        try {
            Router.navigateToImageResize();
        } catch (IOException e) {
            e.printStackTrace();
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
    public void performRegresiLinier() {
        try {
            String inputData = dataInputField.getText().trim();
            
            // Check if the input field is empty
            if (inputData.isEmpty()) {
                resultLabel1.setText("Error: Input field is empty.");
                resultLabel2.setText("");
                return;
            }
    
            String[] lines = inputData.split("\n");
    
            // Check if each row from 0 to lines.length - 2 has the same number of columns as the first row
            int expectedColumns = lines[0].trim().split("\\s+").length;
            for (int i = 0; i < lines.length - 1; i++) {
                String[] columns = lines[i].trim().split("\\s+");
                if (columns.length != expectedColumns) {
                    resultLabel1.setText("Error: Row " + (i + 1) + " must contain exactly " + expectedColumns + " columns.");
                    resultLabel2.setText("");
                    return;
                }
            }
    
            // Check if the last row has one less column than the first row
            String[] lastLineNumbers = lines[lines.length - 1].trim().split("\\s+");
            if (lastLineNumbers.length != expectedColumns - 1) {
                resultLabel1.setText("Error: The last row must contain exactly " + (expectedColumns - 1) + " columns.");
                resultLabel2.setText("");
                return;
            }
    
            List<double[]> xValuesList = new ArrayList<>();
            List<Double> yValuesList = new ArrayList<>();
    
            // Extract x values and y values from input
            for (int i = 0; i < lines.length - 1; i++) {
                String[] numbers = lines[i].trim().split("\\s+");
                double[] numbersArray = Arrays.stream(numbers).mapToDouble(this::parseFraction).toArray();
                double[] xValuesRow = Arrays.copyOfRange(numbersArray, 0, numbersArray.length - 1);
                xValuesList.add(xValuesRow);
                yValuesList.add(numbersArray[numbersArray.length - 1]);
            }
    
            // Convert xValuesList and yValuesList to arrays
            double[][] xValues = xValuesList.toArray(new double[0][]);
            double[] yValues = yValuesList.stream().mapToDouble(Double::doubleValue).toArray();
    
            // Parse xk from the last row
            double[] xk = Arrays.stream(lastLineNumbers).mapToDouble(this::parseFraction).toArray();
    
            // Call the regression method to get the function and predicted value
            RegresiLinier regresiLinier = new RegresiLinier();
            double[] coefficients = regresiLinier.solve(xValues, yValues); // solve sets the b coefficients
            double prediction = regresiLinier.predict(xk); // predict uses the updated b values
    
            // Construct the regression equation string
            StringBuilder equation = new StringBuilder("f(x) = ");
            equation.append(String.format("%.4f", coefficients[0])); // Intercept (b0)
    
            for (int i = 1; i < coefficients.length; i++) {
                if (coefficients[i] < 0) {
                    equation.append(" - ");
                    equation.append(String.format("%.4f", Math.abs(coefficients[i])));
                } else {
                    equation.append(" + ");
                    equation.append(String.format("%.4f", coefficients[i]));
                }
                equation.append("x").append(i); // x1, x2, ..., xn
            }
    
            // Check if all coefficients are zero
            if (Arrays.stream(coefficients).allMatch(coef -> coef == 0)) {
                resultLabel1.setText(String.format("The system has free variables. Please try making at least %d dependent equations.", coefficients.length));
                resultLabel2.setText("");
                return; // Exit the method
            }
    
            // Construct the prediction equation for f(xk)
            StringBuilder predictionEquation = new StringBuilder("Solution = ");
            predictionEquation.append(String.format("%.4f", coefficients[0])); // Intercept
    
            for (int i = 0; i < xk.length; i++) {
                if (coefficients[i + 1] < 0) {
                    predictionEquation.append(" - ");
                    predictionEquation.append(String.format("%.4f", Math.abs(coefficients[i + 1])));
                } else {
                    predictionEquation.append(" + ");
                    predictionEquation.append(String.format("%.4f", coefficients[i + 1]));
                }
                predictionEquation.append(" * ").append(String.format("%.4f", xk[i])); // b1 * xk1, b2 * xk2, ..., bn * xkn
            }
    
            predictionEquation.append(" = ").append(String.format("%.4f", prediction));
    
            // Display results
            resultLabel1.setText(equation.toString());
            resultLabel2.setText(predictionEquation.toString());
        } catch (NumberFormatException e) {
            resultLabel1.setText("Error: Please enter valid numbers.");
            resultLabel2.setText("");
        } catch (IllegalArgumentException e) {
            resultLabel1.setText(e.getMessage());
            resultLabel2.setText("");
        }
    }
    
    @FXML
    public void performRegresiKuadratik() {
        try {
            String inputData = dataInputField.getText().trim();
            
            // Check if the input field is empty
            if (inputData.isEmpty()) {
                resultLabel1.setText("Error: Input field is empty.");
                resultLabel2.setText("");
                return;
            }
    
            String[] lines = inputData.split("\n");
    
            // Check if each row from 0 to lines.length - 2 has exactly 3 columns
            for (int i = 0; i < lines.length - 1; i++) {
                String[] columns = lines[i].trim().split("\\s+");
                if (columns.length != 3) {
                    resultLabel1.setText("Error: Row " + (i + 1) + " must contain exactly 3 columns.");
                    resultLabel2.setText("");
                    return; 
                }
            }
    
            // Check if the last row has exactly 2 columns
            String[] lastLineNumbers = lines[lines.length - 1].trim().split("\\s+");
            if (lastLineNumbers.length != 2) {
                resultLabel1.setText("Error: The last row must contain exactly 2 columns.");
                resultLabel2.setText("");
                return;
            }
    
            List<double[]> xValuesList = new ArrayList<>();
            List<Double> yValuesList = new ArrayList<>();
    
            // Extract x values and y values from input
            for (int i = 0; i < lines.length - 1; i++) {
                String[] numbers = lines[i].trim().split("\\s+");
                double[] numbersArray = Arrays.stream(numbers).mapToDouble(this::parseFraction).toArray();
                double[] xValuesRow = Arrays.copyOfRange(numbersArray, 0, numbersArray.length - 1);
                xValuesList.add(xValuesRow);
                yValuesList.add(numbersArray[numbersArray.length - 1]);
            }
    
            // Convert xValuesList and yValuesList to arrays
            double[][] xValues = xValuesList.toArray(new double[0][]);
            double[] yValues = yValuesList.stream().mapToDouble(Double::doubleValue).toArray();
    
            // Parse xk from the last row
            double[] xk = Arrays.stream(lastLineNumbers).mapToDouble(this::parseFraction).toArray();
    
            // Call the regression method to get the function and predicted value
            RegresiKuadratik regresiKuadratik = new RegresiKuadratik();
            double[] coefficients = regresiKuadratik.solve(xValues, yValues); // solve sets the b coefficients
            double prediction = regresiKuadratik.predict(xk); // predict uses the updated b values
    
            // Check if all coefficients are zero
            if (Arrays.stream(coefficients).allMatch(coef -> coef == 0)) {
                resultLabel1.setText("The system has free variables. Please try making at least 6 dependent equations.");
                resultLabel2.setText("");
                return; // Exit the method
            }
    
            // Construct the regression equation string
            StringBuilder equation = new StringBuilder("f(x1,x2) = ");
            equation.append(String.format("%.4f", coefficients[0])); // Intercept (b0)
    
            // Construct the terms based on the coefficients
            for (int i = 1; i < coefficients.length; i++) {
                if (coefficients[i] < 0) {
                    equation.append(" - ");
                    equation.append(String.format("%.4f", Math.abs(coefficients[i])));
                } else {
                    equation.append(" + ");
                    equation.append(String.format("%.4f", coefficients[i]));
                }
    
                // Determine which term to append
                if (i == 1) {
                    equation.append("x1"); // First linear term
                } else if (i == 2) {
                    equation.append("x2"); // Second linear term
                } else if (i == 3) {
                    equation.append(" * x1^2"); // Quadratic term for x1
                } else if (i == 4) {
                    equation.append(" * x2^2"); // Quadratic term for x2
                } else if (i == 5) {
                    equation.append(" * x1 * x2"); // Interaction term
                }
            }
    
            // Construct the prediction equation for f(xk)
            StringBuilder predictionEquation = new StringBuilder(String.format("f(%d,%d) = ", (int) xk[0], (int) xk[1]));
            predictionEquation.append(String.format("%.4f", coefficients[0])); // Intercept
    
            // Add terms for the prediction equation
            for (int i = 0; i < xk.length; i++) {
                if (coefficients[i + 1] < 0) {
                    predictionEquation.append(" - ");
                    predictionEquation.append(String.format("%.4f", Math.abs(coefficients[i + 1])));
                } else {
                    predictionEquation.append(" + ");
                    predictionEquation.append(String.format("%.4f", coefficients[i + 1]));
                }
    
                // Append the corresponding xk value
                predictionEquation.append(" * ").append(String.format("%.4f", xk[i])); // b1 * xk1, b2 * xk2, ..., bn * xkn
            }
    
            // Add quadratic and interaction terms for prediction
            predictionEquation.append(" + ").append(String.format("%.4f", coefficients[3])).append(" * ").append(String.format("%.4f", xk[0] * xk[0])); // b3 * x1^2
            predictionEquation.append(" + ").append(String.format("%.4f", coefficients[4])).append(" * ").append(String.format("%.4f", xk[1] * xk[1])); // b4 * x2^2
            predictionEquation.append(" + ").append(String.format("%.4f", coefficients[5])).append(" * ").append(String.format("%.4f", xk[0] * xk[1])); // b5 * x1 * x2
            predictionEquation.append(" = ").append(String.format("%.4f", prediction)); // Final prediction
    
            // Display results
            resultLabel1.setText(equation.toString());
            resultLabel2.setText(predictionEquation.toString());
        } catch (NumberFormatException e) {
            resultLabel1.setText("Error: Please enter valid numbers.");
            resultLabel2.setText("");
        } catch (IllegalArgumentException e) {
            resultLabel1.setText(e.getMessage());
            resultLabel2.setText("");
        }
    }

    @FXML
    private void handleFileUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        
        // Open the file chooser
        File file = fileChooser.showOpenDialog(primaryButton.getScene().getWindow());
        
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                // Set the content to the TextArea
                dataInputField.setText(content.toString());
            } catch (IOException e) {
                resultLabel1.setText("Error reading file: " + e.getMessage());
                resultLabel2.setText("");
            }
        }
    }
}
