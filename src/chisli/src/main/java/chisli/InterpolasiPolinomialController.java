package chisli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

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
}