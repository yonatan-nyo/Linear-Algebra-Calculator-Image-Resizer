package chisli.utils.spl;

import chisli.utils.floats.SmallFloat;
import chisli.utils.matrix.Matrix;
import chisli.utils.matrix.MatrixSteps;

public class Gauss {
    private static MatrixSteps matrixSteps;

    public static double[] solve(Matrix augmentedMatrix) {
        matrixSteps = new MatrixSteps(); 
        int rowCount = augmentedMatrix.getRowCount();
        int columnCount = augmentedMatrix.getColumnCount();
        int variableCount = columnCount - 1;

        // Forward elimination
        for (int pivotRow = 0; pivotRow < Math.min(rowCount, variableCount); pivotRow++) {
            // Swap rows only if the front element is zero
            if (Math.abs(augmentedMatrix.get(pivotRow, pivotRow)) < 1e-10) {
                int maxPivotRow = pivotRow;
                for (int currentRow = pivotRow + 1; currentRow < rowCount; currentRow++) {
                    if (Math.abs(augmentedMatrix.get(currentRow, pivotRow)) > Math.abs(augmentedMatrix.get(maxPivotRow, pivotRow))) {
                        maxPivotRow = currentRow;
                    }
                }

                // Swap rows if needed
                if (pivotRow != maxPivotRow) {
                    augmentedMatrix.swapRows(pivotRow, maxPivotRow);
                    matrixSteps.addStep(String.format("Swap row %d with row %d", pivotRow + 1, maxPivotRow + 1));
                    matrixSteps.addMatrixState(augmentedMatrix.getString());
                }
            }

            // Check if matrix is singular or system has no unique solution
            if (pivotRow < variableCount && Math.abs(augmentedMatrix.get(pivotRow, pivotRow)) < 1e-10) {
                throw new IllegalArgumentException("Matrix is singular or system has no unique solution.");
            }

            // Normalize pivot row (make the front into 1)
            double pivotValue = augmentedMatrix.get(pivotRow, pivotRow);
            if (pivotValue != 0) {
                for (int col = pivotRow; col < columnCount; col++) {
                    augmentedMatrix.set(pivotRow, col, SmallFloat.handleMinus0(augmentedMatrix.get(pivotRow, col) / pivotValue));
                }
                matrixSteps.addStep(String.format("Normalize row %d by dividing by %.4f", pivotRow + 1, pivotValue));
                matrixSteps.addMatrixState(augmentedMatrix.getString());
            }

            // Elimination process for rows below
            for (int rowBelow = pivotRow + 1; rowBelow < rowCount; rowBelow++) {
                double eliminationFactor = augmentedMatrix.get(rowBelow, pivotRow);
                matrixSteps.addStep(String.format("Eliminating row %d using row %d with factor %.4f", rowBelow + 1, pivotRow + 1, eliminationFactor));
                for (int col = pivotRow; col < columnCount; col++) {
                    augmentedMatrix.set(rowBelow, col, SmallFloat.handleMinus0(augmentedMatrix.get(rowBelow, col) - eliminationFactor * augmentedMatrix.get(pivotRow, col)));
                }
                matrixSteps.addMatrixState(augmentedMatrix.getString());
            }
        }

        // Back substitution
        double[] solution = new double[variableCount];
        for (int row = variableCount - 1; row >= 0; row--) {
            solution[row] = augmentedMatrix.get(row, columnCount - 1);  // Right-hand side of the equation
            matrixSteps.addStep(String.format("Starting back substitution for x%d", row + 1));

            // Check for inconsistency (all coefficients are 0 but the constant is non-zero)
            boolean allCoefficientsZero = true;
            for (int col = 0; col < variableCount; col++) {
                if (Math.abs(augmentedMatrix.get(row, col)) > 1e-10) {
                    allCoefficientsZero = false;
                    break;
                }
            }
            if (allCoefficientsZero && Math.abs(solution[row]) > 1e-10) {
                throw new IllegalArgumentException("The system has no solution due to inconsistency.");
            }

            String stepDescription = String.format("x%d = %.4f", row + 1, solution[row]);
            for (int col = row + 1; col < variableCount; col++) {
                // Prepare a string to show the subtraction process
                double currentCoefficient = augmentedMatrix.get(row, col);
                double currentSolution = solution[col];
                stepDescription += String.format(" - (%.4f * %.4f)", currentCoefficient, currentSolution);
                solution[row] -= currentCoefficient * currentSolution;

            }
            matrixSteps.addStep(stepDescription);
            solution[row] = SmallFloat.handleMinus0(solution[row]);  // Handle -0 case
            matrixSteps.addStep(String.format("Final value for x%d = %.4f\n", row + 1, solution[row]));
        }

        // Final check for inconsistent rows
        for (int row = 0; row < rowCount; row++) {
            boolean coefficientsZero = true;
            for (int col = 0; col < columnCount; col++) {
                if (col < columnCount - 1 && augmentedMatrix.get(row, col) != 0) {
                    coefficientsZero = false;
                }
                if (coefficientsZero && col == columnCount - 1 && augmentedMatrix.get(row, col) != 0) {
                    throw new IllegalArgumentException("The system has no solution due to inconsistency.");
                }
            }
        }

        // Log final solution
        matrixSteps.addStep("Final solution:");
        for (int i = 0; i < solution.length; i++) {
            matrixSteps.addStep(String.format("x%d = %.4f", i + 1, solution[i]));
        }

        return solution;
    }

    public static MatrixSteps getMatrixSteps() {
        return matrixSteps;
    }
}
