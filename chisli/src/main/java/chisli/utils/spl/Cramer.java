package chisli.utils.spl;

import chisli.utils.floats.SmallFloat;
import chisli.utils.matrix.Matrix;
import chisli.utils.matrix.MatrixSteps;

public class Cramer {
    private static MatrixSteps matrixSteps;

    public static double[] solve(Matrix coefficientMatrix, Matrix constantMatrix) {
        matrixSteps = new MatrixSteps();
        int numberOfEquations = coefficientMatrix.getRowCount();
        int numberOfVariables = coefficientMatrix.getColumnCount();

        // Check for extra equations
        if (numberOfEquations > numberOfVariables) {
            matrixSteps.addStep("Warning: The number of equations is greater than the number of variables. There may be extra equations.");
            throw new IllegalArgumentException("The number of equations is greater than the number of variables. Please provide a valid system.");
        }

        double determinantOfA = coefficientMatrix.determinant();

        matrixSteps.addStep("Coefficient Matrix A");
        matrixSteps.addMatrixState(coefficientMatrix.getString());
        matrixSteps.addStep("Determinant of A: " + determinantOfA + "\n");

        matrixSteps.addStep("Constant Matrix B");
        matrixSteps.addMatrixState(constantMatrix.getString());

        if (Math.abs(determinantOfA) < 1e-10) {
            matrixSteps.addStep("The determinant of A is 0, the system does not have a unique solution.");
            throw new IllegalArgumentException("The determinant of A is 0, the system does not have a unique solution.");
        }

        // Prepare an array to store solutions
        double[] solutions = new double[numberOfEquations];

        // Step 2: For each variable, calculate the determinant of modified matrix
        for (int variableIndex = 0; variableIndex < numberOfEquations; variableIndex++) {
            // Create a new matrix for Ai
            double[][] modifiedMatrixData = new double[numberOfEquations][numberOfVariables];

            // Copy A, replacing the variableIndex-th column with B
            for (int equationIndex = 0; equationIndex < numberOfEquations; equationIndex++) {
                for (int columnIndex = 0; columnIndex < numberOfVariables; columnIndex++) {
                    if (columnIndex == variableIndex) {
                        modifiedMatrixData[equationIndex][columnIndex] = constantMatrix.get(equationIndex, 0); // Replace with B's values
                    } else {
                        modifiedMatrixData[equationIndex][columnIndex] = coefficientMatrix.get(equationIndex, columnIndex);
                    }
                }
            }

            // Create the modified matrix Ai
            Matrix modifiedMatrix = new Matrix(modifiedMatrixData);

            // Step 3: Calculate the determinant of Ai
            double determinantOfModifiedMatrix = modifiedMatrix.determinant();

            matrixSteps.addStep("Modified Matrix A_" + (variableIndex + 1) + " (replacing column " + (variableIndex + 1) + " of A with B):");
            matrixSteps.addMatrixState(modifiedMatrix.getString());  // Assuming Matrix has a toString() method to display the matrix
            matrixSteps.addStep("Determinant of A_" + (variableIndex + 1) + ": " + determinantOfModifiedMatrix + "\n");

            // Step 4: Solve for the variable
            solutions[variableIndex] = determinantOfModifiedMatrix / determinantOfA;
            solutions[variableIndex] = SmallFloat.handleMinus0(solutions[variableIndex]);
        }

        matrixSteps.addStep("Final solution:");
        for (int i = 0; i < solutions.length; i++) {
            matrixSteps.addStep(String.format("x%d = %.4f", i + 1, solutions[i]));
        }

        return solutions;
    }

    public static MatrixSteps getMatrixSteps() {
        return matrixSteps;
    }
}
