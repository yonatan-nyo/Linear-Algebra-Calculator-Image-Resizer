package chisli;

import java.io.IOException;



import chisli.utils.matrix.Matrix;
import chisli.utils.spl.Gauss;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
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
    private void switchToRegresiBerganda() {
        try {
            Router.navigateToRegresiBerganda(); // Change to the desired navigation method
        } catch (IOException e) {
            e.printStackTrace(); // Handle exception
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
    private TextField rowsInput;
    
    @FXML
    private TextField columnsInput;

    @FXML
    private GridPane matrixGrid;

    @FXML
    private GridPane outputGrid;

    private List<TextField> matrixFields = new ArrayList<>();

    @FXML
    public void generateMatrix() {
        matrixGrid.getChildren().clear(); // Clear previous fields
        matrixFields.clear(); // Clear list of previous fields
        
        int rows = Integer.parseInt(rowsInput.getText());
        int columns = Integer.parseInt(columnsInput.getText());

        // Create TextFields for the matrix input
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                TextField field = new TextField();
                field.setPrefWidth(50);
                matrixFields.add(field);
                matrixGrid.add(field, col, row); // Add TextField to the GridPane
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
            displaySolution(solution);
        } catch (IllegalArgumentException e) {
            displayError("Error: " + e.getMessage());
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
