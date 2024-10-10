package chisli.utils.spl;

import chisli.utils.floats.SmallFloat;
import chisli.utils.matrix.Matrix;
import chisli.utils.matrix.MatrixSteps;

public class Gauss {
    private static MatrixSteps matrixSteps;

    public static String solve(Matrix augmentedMatrix) {
        matrixSteps = new MatrixSteps();
        int rowCount = augmentedMatrix.getRowCount();
        int columnCount = augmentedMatrix.getColumnCount();
        int variableCount = columnCount - 1;

        // Handle underdetermined system
        if (rowCount < variableCount) {
            matrixSteps.addStep("Not enough equations; free variables exist.");
        }

        // Track free variables
        boolean[] isFreeVariable = new boolean[variableCount];

        // Perform forward elimination
        forwardElimination(augmentedMatrix, rowCount, columnCount, variableCount, isFreeVariable);

        // Check for no solution
        if (checkNoSolution(augmentedMatrix, rowCount, columnCount)) {
            return "No solution exists.";
        }

        // Perform back substitution
        return backSubstitution(augmentedMatrix, rowCount, columnCount, variableCount, isFreeVariable);
    }

    private static void forwardElimination(Matrix augmentedMatrix, int rowCount, int columnCount, int variableCount, boolean[] isFreeVariable) {
        for (int pivotRow = 0; pivotRow < Math.min(rowCount, variableCount); pivotRow++) {
            if (Math.abs(augmentedMatrix.get(pivotRow, pivotRow)) < 1e-10) {
                // Handle row swap if necessary
                swapRowsIfNeeded(augmentedMatrix, pivotRow, rowCount);
            }

            // Mark free variable and skip if the pivot is near zero
            if (Math.abs(augmentedMatrix.get(pivotRow, pivotRow)) < 1e-10) {
                isFreeVariable[pivotRow] = true;
                continue;
            }

            // Normalize the pivot row
            normalizePivotRow(augmentedMatrix, pivotRow, columnCount);

            // Eliminate rows below the pivot
            eliminateRowsBelow(augmentedMatrix, pivotRow, rowCount, columnCount);
        }
    }

    private static void swapRowsIfNeeded(Matrix augmentedMatrix, int pivotRow, int rowCount) {
        int maxPivotRow = pivotRow;
        for (int currentRow = pivotRow + 1; currentRow < rowCount; currentRow++) {
            if (Math.abs(augmentedMatrix.get(currentRow, pivotRow)) > Math.abs(augmentedMatrix.get(maxPivotRow, pivotRow))) {
                maxPivotRow = currentRow;
            }
        }
        if (pivotRow != maxPivotRow) {
            augmentedMatrix.swapRows(pivotRow, maxPivotRow);
            matrixSteps.addStep(String.format("Swap row %d with row %d", pivotRow + 1, maxPivotRow + 1));
            matrixSteps.addMatrixState(augmentedMatrix.getString());
        }
    }

    private static void normalizePivotRow(Matrix augmentedMatrix, int pivotRow, int columnCount) {
        double pivotValue = augmentedMatrix.get(pivotRow, pivotRow);
        for (int col = pivotRow; col < columnCount; col++) {
            augmentedMatrix.set(pivotRow, col, SmallFloat.handleMinus0(augmentedMatrix.get(pivotRow, col) / pivotValue));
        }
        matrixSteps.addStep(String.format("Normalize row %d by dividing by %.4f", pivotRow + 1, pivotValue));
        matrixSteps.addMatrixState(augmentedMatrix.getString());
    }

    private static void eliminateRowsBelow(Matrix augmentedMatrix, int pivotRow, int rowCount, int columnCount) {
        for (int rowBelow = pivotRow + 1; rowBelow < rowCount; rowBelow++) {
            double eliminationFactor = augmentedMatrix.get(rowBelow, pivotRow);
            matrixSteps.addStep(String.format("Eliminating row %d using row %d with factor %.4f", rowBelow + 1, pivotRow + 1, eliminationFactor));
            for (int col = pivotRow; col < columnCount; col++) {
                augmentedMatrix.set(rowBelow, col, SmallFloat.handleMinus0(augmentedMatrix.get(rowBelow, col) - eliminationFactor * augmentedMatrix.get(pivotRow, col)));
            }
            matrixSteps.addMatrixState(augmentedMatrix.getString());
        }
    }

    private static boolean checkNoSolution(Matrix augmentedMatrix, int rowCount, int columnCount) {
        for (int row = 0; row < rowCount; row++) {
            // Check if the row corresponds to a contradiction (e.g., 0 = k, where k ≠ 0)
            boolean isZeroRow = true;
            for (int col = 0; col < columnCount - 1; col++) {
                if (Math.abs(augmentedMatrix.get(row, col)) > 1e-10) {
                    isZeroRow = false;
                    break;
                }
            }
            // If the row is all zeros but the last entry is not zero, there's no solution
            if (isZeroRow && Math.abs(augmentedMatrix.get(row, columnCount - 1)) > 1e-10) {
                return true; // No solution
            }
        }
        return false; // No contradictions found
    }

    private static String backSubstitution(Matrix augmentedMatrix, int rowCount, int columnCount, int variableCount, boolean[] isFreeVariable) {
        double[] solution = new double[variableCount];
        String[] parametricSolution = new String[variableCount];

        // Initialize parametric solutions
        for (int i = 0; i < variableCount; i++) {
            parametricSolution[i] = "";
        }

        // Perform back substitution
        for (int row = variableCount - 1; row >= 0; row--) {
            // Handle cases where there are fewer rows than variables
            if (row >= rowCount || isFreeVariable[row]) {
                matrixSteps.addStep(String.format("x%d is a free variable", row + 1));
                parametricSolution[row] = String.format("a%d", row + 1); // Assign free variable
                continue;
            }

            // Start with the constant term (rightmost column in augmented matrix)
            solution[row] = augmentedMatrix.get(row, columnCount - 1);
            matrixSteps.addStep(String.format("Starting back substitution for x%d", row + 1));

            // Subtract known values of other variables, including free variables
            for (int col = row + 1; col < variableCount; col++) {
                if (isFreeVariable[col]) {
                    // Handle parametric terms from free variables
                    parametricSolution[row] += String.format(" - %.4f*a%d", augmentedMatrix.get(row, col), col + 1);
                } else {
                    // Subtract values of already solved variables
                    solution[row] -= augmentedMatrix.get(row, col) * solution[col];
                }
            }

            // Handle the -0 case
            solution[row] = SmallFloat.handleMinus0(solution[row]);

            // Combine numeric solution with parametric terms, if any
            if (parametricSolution[row].isEmpty()) {
                parametricSolution[row] = String.format("%.2f", solution[row]);
            } else {
                parametricSolution[row] = String.format("%.2f%s", solution[row], parametricSolution[row]);
            }

            matrixSteps.addStep(String.format("Final value for x%d = %s", row + 1, parametricSolution[row]));
        }

        // Format the final solution
        return formatFinalSolution(parametricSolution);
    }

    private static String formatFinalSolution(String[] parametricSolution) {
        matrixSteps.addStep("Final solution:");
        StringBuilder resultBuilder = new StringBuilder();
        for (int i = 0; i < parametricSolution.length; i++) {
            resultBuilder.append(String.format("x%d = %s\n", i + 1, parametricSolution[i]));
            matrixSteps.addStep(String.format("x%d = %s", i + 1, parametricSolution[i]));
        }
        return resultBuilder.toString();
    }

    public static MatrixSteps getMatrixSteps() {
        return matrixSteps;
    }
}
