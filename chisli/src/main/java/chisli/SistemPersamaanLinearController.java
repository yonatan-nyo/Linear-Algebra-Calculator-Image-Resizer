package chisli;

import java.io.IOException;
import chisli.utils.matrix.Matrix;
import chisli.utils.spl.Gauss;
import chisli.utils.spl.GaussJordan; // Import GaussJordan class
import chisli.utils.spl.Cramer;
import chisli.utils.spl.SplInverse;
import chisli.utils.matrix.MatrixSteps;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.List;

public class SistemPersamaanLinearController {

    @FXML
    private Button primaryButton;

    // Initialize method (optional)
    @FXML
    public void initialize() {
        // Any initialization if needed
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
    private void switchToRegresiBerganda() {
        try {
            Router.navigateToRegresiBerganda();
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

    @FXML
    public void solveGauss() {
        int rows = Integer.parseInt(rowsInput.getText());
        int columns = Integer.parseInt(columnsInput.getText());
        if (columns < 2) {
            displayError("Invalid matrix: At least 2 columns are required");
            return;
        }
        if(rows < columns-1){
            displayError("Invalid matrix: Not enough equations");
            return;
        }

        // Prepare the matrix data
        double[][] matrixData = new double[rows][columns];
        Matrix matrix = new Matrix(matrixData);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                TextField field = matrixFields.get(row * columns + col);
                double value = Double.parseDouble(field.getText());
                matrix.set(row, col, value);
            }
        }

        // Solve using Gaussian elimination
        try {
            double[] solution = Gauss.solve(matrix);
            MatrixSteps matrixSteps = Gauss.getMatrixSteps(); // Get the steps from Gauss class
            displaySolution(solution, columns-1);
            displaySteps(matrixSteps.getSteps()); // Display steps
        } catch (IllegalArgumentException e) {
            displayError("Error: " + e.getMessage());
        }
    }

    @FXML
    public void solveGaussJordan() {
        int rows = Integer.parseInt(rowsInput.getText());
        int columns = Integer.parseInt(columnsInput.getText());
        if (columns < 2) {
            displayError("Invalid matrix: At least 2 columns are required");
            return;
        }
        if(rows < columns-1){
            displayError("Invalid matrix: Not enough equations");
            return;
        }

        // Prepare the matrix data
        double[][] matrixData = new double[rows][columns];
        Matrix matrix = new Matrix(matrixData);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                TextField field = matrixFields.get(row * columns + col);
                double value = Double.parseDouble(field.getText());
                matrix.set(row, col, value);
            }
        }

        // Solve using Gauss-Jordan elimination
        try {
            GaussJordan.reduce(matrix); // Perform reduction
            double[] solution = GaussJordan.solve(matrix); // Get solution
            displaySolution(solution, columns - 1);
            
            // Retrieve the matrix steps after reduction
            MatrixSteps matrixSteps = GaussJordan.getMatrixSteps(); 
            displaySteps(matrixSteps.getSteps()); // Display steps
        } catch (IllegalArgumentException e) {
            displayError("Error: " + e.getMessage());
        }
    }

    @FXML
    public void solveCramer(){
        int rows = Integer.parseInt(rowsInput.getText());
        int columns = Integer.parseInt(columnsInput.getText());
        if (columns < 2) {
            displayError("Invalid matrix: At least 2 columns are required");
            return;
        }

        double[][] matrixData1 = new double[rows][columns-1];
        double[][] matrixData2 = new double[rows][1];
        Matrix matrix1 = new Matrix(matrixData1);
        Matrix matrix2 = new Matrix(matrixData2);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                TextField field = matrixFields.get(row * columns + col);
                double value = Double.parseDouble(field.getText());
                if (col == columns-1) {
                    matrix2.set(row, 0, value);
                } else {
                    matrix1.set(row, col, value);
                }
            }
        }

        try {
            double[] solution = Cramer.solve(matrix1, matrix2); // Get solution
            displaySolution(solution, columns - 1);
            
            // Retrieve the matrix steps after reduction
            MatrixSteps matrixSteps = Cramer.getMatrixSteps(); 
            displaySteps(matrixSteps.getSteps());
        } catch (IllegalArgumentException e) {
            displayError("Error: " + e.getMessage());
        }
    }

    @FXML
    public void inverse(){
        int rows = Integer.parseInt(rowsInput.getText());
        int columns = Integer.parseInt(columnsInput.getText());
        if (columns < 2) {
            displayError("Invalid matrix: At least 2 columns are required");
            return;
        }

        double[][] matrixData1 = new double[rows][columns-1];
        double[][] matrixData2 = new double[rows][1];
        Matrix matrix1 = new Matrix(matrixData1);
        Matrix matrix2 = new Matrix(matrixData2);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                TextField field = matrixFields.get(row * columns + col);
                double value = Double.parseDouble(field.getText());
                if (col == columns-1) {
                    matrix2.set(row, 0, value);
                } else {
                    matrix1.set(row, col, value);
                }
            }
        }

        try {
            double[] solution = SplInverse.solve(matrix1, matrix2); // Get solution
            displaySolution(solution, columns - 1);
            
            // Retrieve the matrix steps after reduction
            MatrixSteps matrixSteps = SplInverse.getMatrixSteps(); 
            displaySteps(matrixSteps.getSteps());
        } catch (IllegalArgumentException e) {
            displayError("Error: " + e.getMessage());
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
            Label resultLabel = new Label(String.format("x%d = %.2f", i + 1, solution[i]));
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
