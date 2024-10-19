package chisli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import chisli.utils.matrix.Matrix;
import chisli.utils.matrix.MatrixSteps; 
import chisli.utils.spl.Cramer;
import chisli.utils.spl.Gauss;
import chisli.utils.spl.GaussJordan;
import chisli.utils.spl.SplInverse;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;

public class SistemPersamaanLinearController {

    @FXML
    private Button primaryButton;

    @FXML
    private ToggleButton standardModeToggle;

    @FXML
    private ToggleButton adjointModeToggle;

    @FXML
    private Label selectedModeLabel;

    private boolean isDeterminantModeAdjoint;

    private void updateModeLabel() {
        String mode = isDeterminantModeAdjoint ? "Adjoint" : "Standard";
        selectedModeLabel.setText("Selected Mode: " + mode);
    }

    // Initialize method (optional)
    @FXML
    public void initialize() {
        // Set up the toggle buttons
        standardModeToggle.setSelected(true); // Set the default selection
        updateModeLabel(); // Update the label initially
        
        standardModeToggle.setOnAction(event -> {
            if (standardModeToggle.isSelected()) {
                adjointModeToggle.setSelected(false);
                isDeterminantModeAdjoint = false;
                updateModeLabel();
            }
        });

        adjointModeToggle.setOnAction(event -> {
            if (adjointModeToggle.isSelected()) {
                standardModeToggle.setSelected(false);
                isDeterminantModeAdjoint = true;
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

        int rows = Integer.parseInt(rowsInput.getText());
        int columns = Integer.parseInt(columnsInput.getText());

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
                double value = Double.parseDouble(field.getText());
                matrix.set(row, col, value);
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
        matrix = matrix.getCleanedMatrix();

        double[][] matrixData1 = new double[columns-1][columns-1];
        double[][] matrixData2 = new double[columns-1][1];

        // set matrixData1 and matrixData2 from matrix
        for (int row = 0; row < columns-1; row++) {
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
            double[] solution = Cramer.solve(matrix1, matrix2, isDeterminantModeAdjoint);
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
        matrix = matrix.getCleanedMatrix();

        double[][] matrixData1 = new double[columns-1][columns-1];
        double[][] matrixData2 = new double[columns-1][1];

        // set matrixData1 and matrixData2 from matrix
        for (int row = 0; row < columns-1; row++) {
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
            double[] solution = SplInverse.solve(matrix1, matrix2, isDeterminantModeAdjoint); // Get solution
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
}
