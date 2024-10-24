package chisli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import chisli.utils.bicubicSplineInterpolation.BicubicSplineInterpolation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

public class BicubicSplineInterpolationController {
    // Initialize method (optional)
    @FXML
    public void initialize() {
        generateSpline();
    }

    @FXML
    private Button fileUploadButton;

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

    @FXML
    private Button primaryButton;

    @FXML
    private GridPane matrixGrid;

    @FXML
    private GridPane outputGrid;

    @FXML
    private TextArea stepsTextArea;

    @FXML
    private TextField xInput;  // Input for x-coordinate

    @FXML
    private TextField yInput;  // Input for y-coordinate

    private List<TextField> matrixFields = new ArrayList<>();

    @FXML

    public void generateSpline() {
        matrixGrid.getChildren().clear();
        matrixFields.clear();
        
        int rows = 4;
        int columns = 4;
        
        // Add column headers (0 to 4)
        for (int col = 0; col <= columns; col++) {
            Label colLabel = new Label(col == 0 ? "" : String.valueOf(col - 2));
            colLabel.setPrefWidth(50);
            colLabel.setStyle("-fx-text-fill: white;");
            matrixGrid.add(colLabel, col, 0);
        }
    
        // Add row headers (0 to 4) and text fields
        for (int row = 0; row < rows; row++) {
            Label rowLabel = new Label(String.valueOf(row-1));
            rowLabel.setPrefWidth(50);
            rowLabel.setStyle("-fx-text-fill: white;");
            matrixGrid.add(rowLabel, 0, row + 1);
        
            for (int col = 0; col < columns; col++) {
                TextField field = new TextField();
                field.setPrefWidth(50);
                field.setStyle("-fx-text-fill: white;");
                matrixFields.add(field);
                matrixGrid.add(field, col + 1, row + 1);
            }
        }
    }

    @FXML
    public void solveSpline() {
        double[][] matrixData = new double[4][4];

        // Collect the data from the input fields
        try {
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 4; col++) {
                    TextField field = matrixFields.get(row * 4 + col);
                    double value = Double.parseDouble(field.getText());
                    matrixData[row][col] = value;
                }
            }

            // Get the x and y values from the input
            double x = Double.parseDouble(xInput.getText());
            double y = Double.parseDouble(yInput.getText());

            if(x < 0 || x > 1 || y < 0 || y > 1) {
                throw new IllegalArgumentException("inputted x and y must be in range 0..1");
            }

            // Perform bicubic spline interpolation
            BicubicSplineInterpolation spline = new BicubicSplineInterpolation(matrixData);
            double interpolatedValue = spline.interpolate(x, y);  // Interpolate at user-defined point

            displaySolution(interpolatedValue, x, y);

            // Retrieve and display steps
            displaySteps(spline.getMatrixSteps().getSteps());

        } catch (NumberFormatException e) {
            displayError("Please fill all matrix fields and enter valid x and y values.");
        } catch (IllegalArgumentException e) {
            displayError("Error: " + e.getMessage());
        }
    }

    private void displaySolution(double value, double x, double y) {
        outputGrid.getChildren().clear();
        Label resultLabel = new Label(String.format("Interpolated value at (%.2f, %.2f): %.4f", x, y, value));
        outputGrid.add(resultLabel, 0, 0);
    }

    private void displaySteps(List<String> steps) {
        stepsTextArea.clear();
        for (String step : steps) {
            stepsTextArea.appendText(step + "\n");
        }
    }

    private void displayError(String message) {
        stepsTextArea.clear();
        outputGrid.getChildren().clear();
        Label errorLabel = new Label(message);
        outputGrid.add(errorLabel, 0, 0);
    }

    @FXML
    private void handleFileUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        
        // Open the file chooser
        File file = fileChooser.showOpenDialog(primaryButton.getScene().getWindow());
        
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                List<String> lines = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                
                // Determine the number of rows and columns
                int rows = lines.size();
                int columns = lines.get(0).trim().split("\\s+").length;
                
                // Clear existing grid and fields
                matrixGrid.getChildren().clear();
                matrixFields.clear();
                
                // Generate the matrix grid
                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < columns; col++) {
                        TextField field = new TextField();
                        field.setPrefWidth(50);
                        field.setStyle("-fx-text-fill: white;");
                        matrixFields.add(field);
                        matrixGrid.add(field, col, row);
                    }
                }
                
                // Fill the matrix grid with values from the file
                for (int row = 0; row < rows; row++) {
                    String[] values = lines.get(row).trim().split("\\s+");
                    for (int col = 0; col < columns; col++) {
                        TextField field = matrixFields.get(row * columns + col);
                        field.setText(values[col]);
                    }
                }
            } catch (IOException e) {
                displayError("Error reading file: " + e.getMessage());
            }
        }
    }

    @FXML
    public void downloadSolution() {
        StringBuilder solutionText = new StringBuilder();

        // Retrieve text from outputGrid
        outputGrid.getChildren().forEach(node -> {
            if (node instanceof Label) {
                Label label = (Label) node;
                solutionText.append(label.getText()).append("\n");
            }
        });

        // Create a file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Solution");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

        // Show save dialog
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fileWriter = new FileWriter(fileChooser.getSelectedFile() + ".txt")) {
                fileWriter.write(solutionText.toString());
            } catch (IOException e) {
                displayError("Error: Unable to save the file.");
            }
        }
    }
}
