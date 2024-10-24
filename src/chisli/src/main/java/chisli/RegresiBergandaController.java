package chisli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

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
    private void switchToMatrixDeterminant() {
        try {
            Router.navigateToMatrixDeterminant();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToInverseMatrix(){
        try {
            Router.navigateToInverseMatrix();
        } catch (Exception e) {
            e.printStackTrace();
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
                resultLabel1.setText("The system leads to a trivial solution, the result of any input is 0");
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
    
            // Check if each row from 0 to lines.length - 2 has the same number of columns
            int numColumns = lines[0].trim().split("\\s+").length;
            for (int i = 0; i < lines.length - 1; i++) {
                String[] columns = lines[i].trim().split("\\s+");
                if (columns.length != numColumns) {
                    resultLabel1.setText("Error: Row " + (i + 1) + " must contain exactly " + numColumns + " columns.");
                    resultLabel2.setText("");
                    return; 
                }
            }
    
            // Check if the last row has exactly numColumns - 1 columns
            String[] lastLineNumbers = lines[lines.length - 1].trim().split("\\s+");
            if (lastLineNumbers.length != numColumns - 1) {
                resultLabel1.setText("Error: The last row must contain exactly " + (numColumns - 1) + " columns.");
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
                resultLabel1.setText("The system leads to a trivial solution, the result of any input is 0");
                resultLabel2.setText("");
                return; // Exit the method
            }
    
            // Construct the regression equation string
            StringBuilder equation = new StringBuilder("f(x1,x2,...,xn) = ");
            equation.append(String.format("%.4f", coefficients[0])); // Intercept (b0)
    
            // Construct the terms based on the coefficients
            int coefIndex = 1;
            int numFeatures = xValues[0].length;
    
            // Linear terms
            for (int i = 0; i < numFeatures; i++) {
                if (coefficients[coefIndex] < 0) {
                    equation.append(" - ");
                    equation.append(String.format("%.4f", Math.abs(coefficients[coefIndex])));
                } else {
                    equation.append(" + ");
                    equation.append(String.format("%.4f", coefficients[coefIndex]));
                }
                equation.append("x").append(i + 1);
                coefIndex++;
            }
    
            // Quadratic terms
            for (int i = 0; i < numFeatures; i++) {
                if (coefficients[coefIndex] < 0) {
                    equation.append(" - ");
                    equation.append(String.format("%.4f", Math.abs(coefficients[coefIndex])));
                } else {
                    equation.append(" + ");
                    equation.append(String.format("%.4f", coefficients[coefIndex]));
                }
                equation.append("x").append(i + 1).append("^2");
                coefIndex++;
            }
    
            // Interaction terms
            for (int i = 0; i < numFeatures; i++) {
                for (int j = i + 1; j < numFeatures; j++) {
                    if (coefficients[coefIndex] < 0) {
                        equation.append(" - ");
                        equation.append(String.format("%.4f", Math.abs(coefficients[coefIndex])));
                    } else {
                        equation.append(" + ");
                        equation.append(String.format("%.4f", coefficients[coefIndex]));
                    }
                    equation.append("x").append(i + 1).append("*x").append(j + 1);
                    coefIndex++;
                }
            }
    
            // Construct the prediction equation for f(xk)
            StringBuilder predictionEquation = new StringBuilder("f(");
            for (int i = 0; i < xk.length; i++) {
                predictionEquation.append(String.format("%.4f", xk[i]));
                if (i < xk.length - 1) {
                    predictionEquation.append(",");
                }
            }
            predictionEquation.append(") = ");
            predictionEquation.append(String.format("%.4f", coefficients[0])); // Intercept
    
            // Add terms for the prediction equation
            coefIndex = 1;
            for (int i = 0; i < xk.length; i++) {
                if (coefficients[coefIndex] < 0) {
                    predictionEquation.append(" - ");
                    predictionEquation.append(String.format("%.4f", Math.abs(coefficients[coefIndex])));
                } else {
                    predictionEquation.append(" + ");
                    predictionEquation.append(String.format("%.4f", coefficients[coefIndex]));
                }
                predictionEquation.append(" * ").append(String.format("%.4f", xk[i])); // b1 * xk1, b2 * xk2, ..., bn * xkn
                coefIndex++;
            }
    
            // Add quadratic and interaction terms for prediction
            for (int i = 0; i < xk.length; i++) {
                if (coefficients[coefIndex] < 0) {
                    predictionEquation.append(" - ");
                    predictionEquation.append(String.format("%.4f", Math.abs(coefficients[coefIndex])));
                } else {
                    predictionEquation.append(" + ");
                    predictionEquation.append(String.format("%.4f", coefficients[coefIndex]));
                }
                predictionEquation.append(" * ").append(String.format("%.4f", xk[i] * xk[i])); // b3 * x1^2, b4 * x2^2, ...
                coefIndex++;
            }
    
            for (int i = 0; i < xk.length; i++) {
                for (int j = i + 1; j < xk.length; j++) {
                    if (coefficients[coefIndex] < 0) {
                        predictionEquation.append(" - ");
                        predictionEquation.append(String.format("%.4f", Math.abs(coefficients[coefIndex])));
                    } else {
                        predictionEquation.append(" + ");
                        predictionEquation.append(String.format("%.4f", coefficients[coefIndex]));
                    }
                    predictionEquation.append(" * ").append(String.format("%.4f", xk[i] * xk[j])); // b5 * x1 * x2, ...
                    coefIndex++;
                }
            }
    
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

    @FXML
    public void downloadSolution() {
        String result1 = resultLabel1.getText();
        String result2 = resultLabel2.getText();

        // Create a file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Solution");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

        // Show save dialog
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fileWriter = new FileWriter(fileChooser.getSelectedFile() + ".txt")) {
                fileWriter.write("Result 1: " + result1 + "\n");
                fileWriter.write("Result 2: " + result2 + "\n");
                resultLabel1.setText("Solution downloaded successfully.");
                resultLabel2.setText("");
            } catch (IOException e) {
                resultLabel1.setText("Error: Unable to save the file.");
                resultLabel2.setText("");
            }
        }
    }
}
