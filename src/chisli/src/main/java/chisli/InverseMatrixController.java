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

import chislib.matrix.InverseMatrix;
import chislib.matrix.Matrix;
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

public class InverseMatrixController {
    @FXML
    private Button primaryButton, fileUploadButton;

    @FXML
    private ToggleButton standardModeToggle, adjointModeToggle;

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
    private boolean isInverseByAdjoin;

    @FXML
    public void initialize() {
        standardModeToggle.setSelected(true);
        updateModeLabel();

        standardModeToggle.setOnAction(event -> {
            if (standardModeToggle.isSelected()) {
                adjointModeToggle.setSelected(false);
                isInverseByAdjoin = false;
                updateModeLabel();
            }
        });

        adjointModeToggle.setOnAction(event -> {
            if (adjointModeToggle.isSelected()) {
                standardModeToggle.setSelected(false);
                isInverseByAdjoin = true;
                updateModeLabel();
            }
        });
    }

    private void updateModeLabel() {
        String mode = isInverseByAdjoin ? "Adjoint" : "OBE";
        selectedModeLabel.setText("Selected Mode for Inverse: " + mode);
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

        int rows = Integer.parseInt(rowsInput.getText());
        int columns = Integer.parseInt(columnsInput.getText());

        if (rows != columns) {
            displayError("Matrix must be square to calculate inverse.");
            return;
        }


        if (rowsInputString.isEmpty() || columnsInputString.isEmpty()) {
            displayError("Invalid matrix: Row size and column size must be filled");
            return;
        }

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
    public void solveInverse() {
        if (!isValidInput()) return;

        int rows = Integer.parseInt(rowsInput.getText());
        int columns = Integer.parseInt(columnsInput.getText());

        Matrix matrix = prepareMatrix(rows, columns);
        if (matrix == null) return;

        try {
            Matrix inverseMatrix;
            if (isInverseByAdjoin) {
                inverseMatrix = InverseMatrix.inverseByAdjoin(matrix, true);
            } else {
                inverseMatrix = InverseMatrix.inverseElementaryRowOperation(matrix, true);
            }
            String inverseMatrixString = inverseMatrix.getString();
        
            // Display the inverse matrix string in the TextArea
            stepsTextArea.setText("Inverse Matrix:\n" + inverseMatrixString);
            outputGrid.getChildren().clear();

            // Display the inverse matrix in the outputGrid
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    // Get the inverse matrix value and format it
                    String matrixValue = String.format("%.4f", inverseMatrix.get(row, col));

                    // Create a new Label for this value
                    Label label = new Label(matrixValue);

                    // Add the Label to the grid at the correct position
                    outputGrid.add(label, col, row);
                }
            }
        } catch (IllegalArgumentException e) {
            displayError("Error: " + e.getMessage());
        } finally {
            MatrixSteps matrixSteps = InverseMatrix.getMatrixSteps();
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

    private void displayError(String message) {
        stepsTextArea.clear();
        outputGrid.getChildren().clear();
        Label errorLabel = new Label(message);
        outputGrid.add(errorLabel, 0, 0);
    }

    @FXML
    public void downloadSolution() {
        StringBuilder solutionText = new StringBuilder();
    
        // Retrieve matrix dimensions
        int rows = Integer.parseInt(rowsInput.getText());
        int columns = Integer.parseInt(columnsInput.getText());
    
        // Retrieve text from outputGrid and format it as a square matrix
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                Label label = (Label) outputGrid.getChildren().get(row * columns + col);
                solutionText.append(label.getText()).append("\t");
            }
            solutionText.append("\n");
        }
    
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