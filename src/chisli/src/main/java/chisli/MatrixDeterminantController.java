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
import chislib.matrix.MatrixDeterminant;
import chislib.matrix.MatrixSteps;
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

public class MatrixDeterminantController {
    @FXML
    private Button primaryButton, fileUploadButton;

    @FXML
    private ToggleButton standardModeToggle, cofactorModeToggle;

    @FXML
    private Label selectedModeLabel;

    @FXML
    private TextField rowsInput, columnsInput;

    @FXML
    private TextArea stepsTextArea;

    @FXML
    private GridPane matrixGrid, outputGrid;

    @FXML
    private ComboBox<String> determinantModeComboBox;

    private List<TextField> matrixFields = new ArrayList<>();
    private boolean isDeterminantModeCofactorExpansion;

    @FXML
    public void initialize() {
        standardModeToggle.setSelected(true);
        updateModeLabel();

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

    private void updateModeLabel() {
        String mode = isDeterminantModeCofactorExpansion ? "Cofactor Expansion" : "OBE";
        selectedModeLabel.setText("Selected Mode for Determinant: " + mode);
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
    private void switchToInverseMatrix() {
        try {
            Router.navigateToInverseMatrix();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void generateMatrix() {
        matrixGrid.getChildren().clear();
        matrixFields.clear();

        String rowsInputString = rowsInput.getText().trim();
        String columnsInputString = columnsInput.getText().trim();

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

    @FXML
    private void handleFileUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File file = fileChooser.showOpenDialog(primaryButton.getScene().getWindow());

        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                List<String> lines = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }

                int rows = lines.size();
                int columns = lines.get(0).trim().split("\\s+").length;

                rowsInput.setText(String.valueOf(rows));
                columnsInput.setText(String.valueOf(columns));

                generateMatrix();

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
    public void solveDeterminant() {
        if (!isValidInput()) return;

        int rows = Integer.parseInt(rowsInput.getText());
        int columns = Integer.parseInt(columnsInput.getText());

        Matrix matrix = prepareMatrix(rows, columns);
        if (matrix == null) return;

        try {
            double determinant = matrix.determinant(isDeterminantModeCofactorExpansion, true);
            displaySolution(determinant);
        } catch (IllegalArgumentException e) {
            displayError("Error: " + e.getMessage());
        } finally {
            MatrixSteps matrixSteps = MatrixDeterminant.getMatrixSteps();
            displaySteps(matrixSteps.getSteps());
        }
    }

    private boolean isValidInput() {
        String rowsInputString = rowsInput.getText();
        String columnsInputString = columnsInput.getText();
        if (rowsInputString.isEmpty() || columnsInputString.isEmpty()) {
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

    private void displaySteps(List<String> steps) {
        stepsTextArea.clear();
        for (String step : steps) {
            stepsTextArea.appendText(step + "\n");
        }
    }

    private void displaySolution(double solution) {
        outputGrid.getChildren().clear();
        Label resultLabel = new Label(String.format("determinant = %.4f", solution));
        outputGrid.add(resultLabel, 0, 0);
    }

    private void displayError(String message) {
        stepsTextArea.clear();
        outputGrid.getChildren().clear();
        Label errorLabel = new Label(message);
        outputGrid.add(errorLabel, 0, 0);
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