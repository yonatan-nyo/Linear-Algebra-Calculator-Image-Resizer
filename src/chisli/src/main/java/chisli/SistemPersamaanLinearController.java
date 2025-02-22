package chisli;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import chislib.matrix.Matrix;
import chislib.matrix.MatrixSteps; 
import chislib.spl.Cramer;
import chislib.spl.Gauss;
import chislib.spl.GaussJordan;
import chislib.spl.SplInverse;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class SistemPersamaanLinearController {

    @FXML
    private Button primaryButton;

    @FXML
    private ToggleButton standardModeToggle;

    @FXML
    private ToggleButton cofactorModeToggle;

    @FXML
    private Label selectedModeLabel;

    @FXML
    private Button fileUploadButton;

    private boolean isDeterminantModeCofactorExpansion;

    private void updateModeLabel() {
        String mode = isDeterminantModeCofactorExpansion ? "Cofactor Expansion" : "OBE";
        selectedModeLabel.setText("Selected Mode for Cramer and Inverse: " + mode);
    }

    // Initialize method (optional)
    @FXML
    public void initialize() {
        // Set up the toggle buttons
        standardModeToggle.setSelected(true); // Set the default selection
        updateModeLabel(); // Update the label initially
        
        standardModeToggle.setOnAction(event -> {
            if (standardModeToggle.isSelected()) {
                cofactorModeToggle.setSelected(false);
                isDeterminantModeCofactorExpansion = false;
                updateModeLabel();
            }
        });

        cofactorModeToggle.setOnAction(event -> {
            if (cofactorModeToggle.isSelected()) {
                standardModeToggle.setSelected(false);
                isDeterminantModeCofactorExpansion = true;
                updateModeLabel();
            }
        });
    }

    @FXML
    private void switchToSistemPersamaanLinear() {
        try {
            Router.navigateToSistemPersamaanLinear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToInterpolasiPolinomial() {
        try {
            Router.navigateToInterpolasiPolinomial();
        } catch (IOException e) {
            e.printStackTrace();
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
            Router.navigateToBicubicSplineInterpolation();
        } catch (IOException e) {
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
    private TextField rowsInput;

    @FXML
    private TextField columnsInput;

    @FXML
    private TextArea stepsTextArea;

    @FXML
    private GridPane matrixGrid;

    @FXML
    private GridPane outputGrid;

    @FXML
    private ComboBox<String> determinantModeComboBox;

    private List<TextField> matrixFields = new ArrayList<>();
    
    @FXML
    public void generateMatrix() {
        matrixGrid.getChildren().clear();
        matrixFields.clear();
    
        String rowsInputString = rowsInput.getText().trim();
        String columnsInputString = columnsInput.getText().trim();
    
        // Check if the input fields are empty
        if (rowsInputString.isEmpty() || columnsInputString.isEmpty()) {
            displayError("Invalid matrix: Row size and column size must be filled");
            return;
        }
    
        int rows = Integer.parseInt(rowsInputString);
        int columns = Integer.parseInt(columnsInputString);
    
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                TextField field = new TextField();
                field.setPrefWidth(50);
                matrixFields.add(field);
                matrixGrid.add(field, col, row);
            }
        }
    }

    // Utility function to check input validity
    private boolean isValidInput() {
        String rowsInpuString = rowsInput.getText();
        String columnsInputString = columnsInput.getText();
        if (rowsInpuString.isEmpty() || columnsInputString.isEmpty()) {
            displayError("Invalid matrix: Rows and columns must be filled");
            return false;
        }
        int columns = Integer.parseInt(columnsInputString);
        if (columns < 2) {
            displayError("Invalid matrix: At least 2 columns are required");
            return false;
        }

        return true;
    }

    // Utility function to parse a fraction string
    private double parseFraction(String input) {
        if (input.contains("/")) {
            String[] parts = input.split("/");
            if (parts.length == 2) {
                try {
                    double numerator = Double.parseDouble(parts[0]);
                    double denominator = Double.parseDouble(parts[1]);
                    return numerator / denominator;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid fraction format");
                }
            } else {
                throw new IllegalArgumentException("Invalid fraction format");
            }
        } else {
            return Double.parseDouble(input);
        }
    }

    // Utility function to prepare the matrix
    private Matrix prepareMatrix(int rows, int columns) {
        double[][] matrixData = new double[rows][columns];
        Matrix matrix = new Matrix(matrixData);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (matrixFields.size() != rows * columns) {
                    displayError("Invalid matrix: Matrix fields have not been generated");
                    return null;
                }
                TextField field = matrixFields.get(row * columns + col);
                if (field.getText().isEmpty()) {
                    displayError("Invalid matrix: All fields must be filled");
                    return null;
                }
                try {
                    double value = parseFraction(field.getText());
                    matrix.set(row, col, value);
                } catch (IllegalArgumentException e) {
                    displayError("Invalid input: Please enter valid fractions or numbers");
                    return null;
                }
            }
        }
        return matrix;
    }

    @FXML
    public void solveGauss() {
        if (!isValidInput()) return;
        
        int rows = Integer.parseInt(rowsInput.getText());
        int columns = Integer.parseInt(columnsInput.getText());

        Matrix matrix = prepareMatrix(rows, columns);
        if (matrix == null) return;

        // Solve using Gaussian elimination
        try {
            String[] solution = Gauss.solve(matrix);
            displayStringSolution(solution);
        } catch (IllegalArgumentException e) {
            displayError("Error: " + e.getMessage());
        } finally {
            MatrixSteps matrixSteps = Gauss.getMatrixSteps(); 
            displaySteps(matrixSteps.getSteps());
        }
    }

    @FXML
    public void solveGaussJordan() {
        if (!isValidInput()) return;
        
        int rows = Integer.parseInt(rowsInput.getText());
        int columns = Integer.parseInt(columnsInput.getText());

        Matrix matrix = prepareMatrix(rows, columns);
        if (matrix == null) return;

        // Solve using Gauss-Jordan elimination
        try {
            String[] solution = GaussJordan.solve(matrix); // Get solution
            displayStringSolution(solution);
        } catch (IllegalArgumentException e) {
            displayError("Error: " + e.getMessage());
        } finally {
            MatrixSteps matrixSteps = GaussJordan.getMatrixSteps(); 
            displaySteps(matrixSteps.getSteps());
        }
    }
    
    @FXML
    public void solveCramer(){
        if (!isValidInput()) return;
        
        int rows = Integer.parseInt(rowsInput.getText());
        int columns = Integer.parseInt(columnsInput.getText());
        
        if(rows < (columns-1)){
            displayError("Invalid matrix: Number of rows must ≥ (columns-1)");
            return;
        }

        // take from matrix first then split into matrix1 and matrix2
        Matrix matrix = prepareMatrix(rows, columns);
        if (matrix == null) return;

        double[][] matrixData1 = new double[rows][columns-1];
        double[][] matrixData2 = new double[rows][1];

        // set matrixData1 and matrixData2 from matrix
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (col == columns-1) {
                    matrixData2[row][0] = matrix.get(row, col);
                } else {
                    matrixData1[row][col] = matrix.get(row, col);
                }
            }
        }

        Matrix matrix1 = new Matrix(matrixData1);
        Matrix matrix2 = new Matrix(matrixData2);

        try {
            double[] solution = Cramer.solve(matrix1, matrix2, isDeterminantModeCofactorExpansion);
            displaySolution(solution, columns - 1);
        } catch (IllegalArgumentException e) {
            displayError("Error: " + e.getMessage());
        } finally {
            MatrixSteps matrixSteps = Cramer.getMatrixSteps(); 
            displaySteps(matrixSteps.getSteps());
        }
    }
    
    @FXML
    public void inverse(){
        if (!isValidInput()) return;
        
        int rows = Integer.parseInt(rowsInput.getText());
        int columns = Integer.parseInt(columnsInput.getText());
        
        if(rows < (columns-1)){
            displayError("Invalid matrix: Number of rows must ≥ (columns-1)");
            return;
        }

         // take from matrix first then split into matrix1 and matrix2
        Matrix matrix = prepareMatrix(rows, columns);
        if (matrix == null) return;

        double[][] matrixData1 = new double[rows][columns-1];
        double[][] matrixData2 = new double[rows][1];

        // set matrixData1 and matrixData2 from matrix
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (col == columns-1) {
                    matrixData2[row][0] = matrix.get(row, col);
                } else {
                    matrixData1[row][col] = matrix.get(row, col);
                }
            }
        }

        Matrix matrix1 = new Matrix(matrixData1);
        Matrix matrix2 = new Matrix(matrixData2);

        try {
            double[] solution = SplInverse.solve(matrix1, matrix2, isDeterminantModeCofactorExpansion); // Get solution
            displaySolution(solution, columns - 1);
        } catch (IllegalArgumentException e) {
            displayError("Error: " + e.getMessage());
        } finally {
            MatrixSteps matrixSteps = SplInverse.getMatrixSteps(); 
            displaySteps(matrixSteps.getSteps());
        }
    }

    private void displaySteps(List<String> steps) {
        stepsTextArea.clear();
        for (String step : steps) {
            stepsTextArea.appendText(step + "\n");
        }
    }

    private void displaySolution(double[] solution, int numVariables) {
        outputGrid.getChildren().clear();
        for (int i = 0; i < numVariables; i++) {
            Label resultLabel = new Label(String.format("x%d = %.4f", i + 1, solution[i]));
            outputGrid.add(resultLabel, 0, i);
        }
    }

    private void displayStringSolution(String[] solution){
        outputGrid.getChildren().clear();
        for (int i = 0; i < solution.length; i++) {
            Label resultLabel = new Label(solution[i]);
            outputGrid.add(resultLabel, 0, i);
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
                
                // Set the rows and columns input fields
                rowsInput.setText(String.valueOf(rows));
                columnsInput.setText(String.valueOf(columns));
                
                // Generate the matrix grid
                generateMatrix();
                
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
    private void downloadSolution() {
        // Retrieve the solution from the outputGrid
        StringBuilder solutionBuilder = new StringBuilder();
        for (int i = 0; i < outputGrid.getChildren().size(); i++) {
            Label label = (Label) outputGrid.getChildren().get(i);
            solutionBuilder.append(label.getText()).append("\n");
        }
        String solution = solutionBuilder.toString();

        // Open a FileChooser to select the save location
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialFileName("solution.txt");
        File file = fileChooser.showSaveDialog(primaryButton.getScene().getWindow());

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
                writer.write(solution);
            } catch (IOException e) {
                displayError("Error saving file: " + e.getMessage());
            }
        }
    }
}
