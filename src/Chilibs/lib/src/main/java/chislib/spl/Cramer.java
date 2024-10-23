package chislib.spl;

import chislib.floats.SmallFloat;
import chislib.matrix.Matrix;
import chislib.matrix.MatrixSteps;

public class Cramer {
    private static MatrixSteps matrixSteps;

    public static double[] solve(Matrix coefficientMatrix, Matrix constantMatrix, boolean isByCofactorExpansion) {
        matrixSteps = new MatrixSteps();

        matrixSteps.addStep("======================= Matrix given ======================");
        matrixSteps.addStep("Coefficient Matrix");
        matrixSteps.addMatrixState(coefficientMatrix.getString());
        matrixSteps.addStep("Constant Matrix");
        matrixSteps.addMatrixState(constantMatrix.getString());

        int numberOfEquations = coefficientMatrix.getRowCount();
        int numberOfVariables = coefficientMatrix.getColumnCount();

        // Check for underdetermined system (fewer equations than variables)
        if (numberOfEquations < numberOfVariables) {
            throw new IllegalArgumentException("The system is underdetermined: fewer equations than variables.");
        }

        matrixSteps.addStep("======================= Calculate determinant ======================");
        // If rows > cols, cut the rows to calculate the determinant
        Matrix cutMatrix;
        if (numberOfEquations > numberOfVariables) {
            matrixSteps.addStep("The system has extra equations. The determinant will be calculated for the first " + numberOfVariables + " rows.");
            double[][] cutData = new double[numberOfVariables][numberOfVariables];
            for (int i = 0; i < numberOfVariables; i++) {
                for (int j = 0; j < numberOfVariables; j++) {
                    cutData[i][j] = coefficientMatrix.get(i, j);
                }
            }
            cutMatrix = new Matrix(cutData); // Reduced matrix
        } else {
            cutMatrix = coefficientMatrix; // No need to cut the matrix if rows == cols
        }

        matrixSteps.addMatrixState(cutMatrix.getString());
        double determinantOfA = cutMatrix.determinant(isByCofactorExpansion);
        matrixSteps.addStep("Determinant of A: " + determinantOfA + "\n");

        if (Math.abs(determinantOfA) < 1e-10) {
            matrixSteps.addStep("The determinant of A is 0, the system does not have a unique solution.");
            throw new IllegalArgumentException("The determinant of A is 0, the system does not have a unique solution.");
        }

        matrixSteps.addStep("======================= Calculate Solution ======================");
        double[] solutions = new double[numberOfVariables];

        // For each variable, calculate the determinant of modified matrix
        for (int variableIndex = 0; variableIndex < numberOfVariables; variableIndex++) {
            double[][] modifiedMatrixData = new double[numberOfVariables][numberOfVariables];

            // Copy the original coefficient matrix, replacing the variableIndex-th column with constant matrix values
            for (int rowIndex = 0; rowIndex < numberOfVariables; rowIndex++) {
                for (int columnIndex = 0; columnIndex < numberOfVariables; columnIndex++) {
                    if (columnIndex == variableIndex) {
                        modifiedMatrixData[rowIndex][columnIndex] = constantMatrix.get(rowIndex, 0);
                    } else {
                        modifiedMatrixData[rowIndex][columnIndex] = coefficientMatrix.get(rowIndex, columnIndex);
                    }
                }
            }

            Matrix modifiedMatrix = new Matrix(modifiedMatrixData);
            double determinantOfModifiedMatrix = modifiedMatrix.determinant(isByCofactorExpansion);
            matrixSteps.addStep("Determinant of A_" + (variableIndex + 1) + ": " + determinantOfModifiedMatrix);

            solutions[variableIndex] = determinantOfModifiedMatrix / determinantOfA;
            solutions[variableIndex] = SmallFloat.handleMinus0(solutions[variableIndex]);
            // show calculation step
            matrixSteps.addStep(String.format("x%d = %.4f/%.4f", variableIndex + 1, determinantOfModifiedMatrix, determinantOfA));
            matrixSteps.addStep(String.format("x%d = %.4f\n", variableIndex + 1, solutions[variableIndex]));
        }

        // Step 3: Check extra equations for consistency
        if (numberOfEquations > numberOfVariables) {
            matrixSteps.addStep("======================= Verifying Extra Equations ======================");
            for (int extraEqIndex = numberOfVariables; extraEqIndex < numberOfEquations; extraEqIndex++) {
                double leftHandSide = 0;
                for (int varIndex = 0; varIndex < numberOfVariables; varIndex++) {
                    leftHandSide += coefficientMatrix.get(extraEqIndex, varIndex) * solutions[varIndex];
                }
                double rightHandSide = constantMatrix.get(extraEqIndex, 0);

                if (SmallFloat.handleMinus0(leftHandSide - rightHandSide) != 0.0) {
                    matrixSteps.addStep(String.format("Equation %d: %.4f ≠ %.4f", extraEqIndex + 1, leftHandSide, rightHandSide));
                    matrixSteps.addStep("The system is inconsistent: the extra equation does not match the solution.");
                    throw new IllegalArgumentException("Inconsistent system: extra equation does not match the solution.");
                } else {
                    matrixSteps.addStep(String.format("Equation %d: %.4f = %.4f", extraEqIndex + 1, leftHandSide, rightHandSide));
                }
            }
            matrixSteps.addStep("All extra equations are consistent.");
        }

        return solutions;
    }

    public static MatrixSteps getMatrixSteps() {
        return matrixSteps;
    }
}
