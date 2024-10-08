package chisli;

import java.io.IOException;
import chisli.utils.matrix.Matrix;
import chisli.utils.spl.Gauss;
import chisli.utils.spl.GaussJordan; // Import GaussJordan class
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
            displaySolution(solution);
            displaySteps(matrixSteps.getSteps()); // Display steps
        } catch (IllegalArgumentException e) {
            displayError("Error: " + e.getMessage());
        }
    }

    @FXML
    public void solveGaussJordan() {
        int rows = Integer.parseInt(rowsInput.getText());
        int columns = Integer.parseInt(columnsInput.getText());

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
            displaySolution(solution);
            
            // Retrieve the matrix steps after reduction
            MatrixSteps matrixSteps = GaussJordan.getMatrixSteps(); 
            displaySteps(matrixSteps.getSteps()); // Display steps
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

    private void displaySolution(double[] solution) {
        outputGrid.getChildren().clear();
        for (int i = 0; i < solution.length; i++) {
            Label resultLabel = new Label(String.format("x%d = %.2f", i + 1, solution[i]));
            outputGrid.add(resultLabel, 0, i);
        }
    }

    private void displayError(String message) {
        outputGrid.getChildren().clear();
        Label errorLabel = new Label(message);
        outputGrid.add(errorLabel, 0, 0);
    }
}
