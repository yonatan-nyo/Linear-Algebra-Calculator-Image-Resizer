package chisli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import chisli.utils.interpolasiPolinomial.InterpolasiPolinomial;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class InterpolasiPolinomialController {
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
            String inputData = dataInputField.getText().trim();
            
            // Check if the input field is empty
            if (inputData.isEmpty()) {
                resultLabel1.setText("Error: Input field is empty.");
                resultLabel2.setText("");
                return;
            }

            String[] lines = inputData.split("\n");

            // Check if each row from 0 to lines.length - 2 has exactly 2 columns
            for (int i = 0; i < lines.length - 1; i++) {
                String[] columns = lines[i].trim().split("\\s+");
                if (columns.length != 2) {
                    resultLabel1.setText("Error: Row " + (i + 1) + " must contain exactly 2 columns.");
                    resultLabel2.setText("");
                    return;
                }
            }

            // Check if the last row has exactly 1 column
            String[] lastLineNumbers = lines[lines.length - 1].trim().split("\\s+");
            if (lastLineNumbers.length != 1) {
                resultLabel1.setText("Error: The last row must contain exactly 1 column.");
                resultLabel2.setText("");
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

            // Check if xToEvaluate is within the range of xValues
            double minX = Arrays.stream(xValues).min().orElse(Double.NaN);
            double maxX = Arrays.stream(xValues).max().orElse(Double.NaN);

            if (xToEvaluate < minX || xToEvaluate > maxX) {
                resultLabel1.setText("Error: The last row value must be between " + minX + " and " + maxX + ".");
                resultLabel2.setText("");
                return;
            }

            // Call the interpolation method
            List<String> result = InterpolasiPolinomial.solve(xValues, yValues, xToEvaluate);
            resultLabel1.setText(result.get(0));
            resultLabel2.setText(result.get(1));
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