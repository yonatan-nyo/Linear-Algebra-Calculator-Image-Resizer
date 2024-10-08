package chisli.utils.spl;

import chisli.utils.floats.SmallFloat;
import chisli.utils.matrix.Matrix;
import chisli.utils.matrix.MatrixSteps;

public class Gauss {
    private static MatrixSteps matrixSteps;

    public static double[] solve(Matrix matrix) {
        matrixSteps = new MatrixSteps(); // Initialize MatrixSteps
        int rows = matrix.getRowCount();
        int cols = matrix.getColumnCount();

        for (int i = 0; i < rows; i++) {
            int maxRow = i;
            for (int k = i + 1; k < rows; k++) {
                if (Math.abs(matrix.get(k, i)) > Math.abs(matrix.get(maxRow, i))) {
                    maxRow = k;
                }
            }

            // Swap rows if needed
            matrix.swapRows(i, maxRow);
            if (i != maxRow) {
                matrixSteps.addStep(String.format("Swap row %d with row %d", i + 1, maxRow + 1));
                matrixSteps.addMatrixState(matrix.getString());
            }

            // Check if matrix is singular
            if (Math.abs(matrix.get(i, i)) < 1e-10) {
                throw new IllegalArgumentException("Matrix is singular or system has no unique solution.");
            }

            // Normalize row
            double pivot = matrix.get(i, i);
            for (int j = i; j < cols; j++) {
                matrix.set(i, j, matrix.get(i, j) / pivot);
            }
            matrixSteps.addStep(String.format("Normalize row %d by dividing by %.4f", i + 1, pivot));
            matrixSteps.addMatrixState(matrix.getString());

            // Elimination process
            for (int k = i + 1; k < rows; k++) {
                double factor = matrix.get(k, i);
                matrixSteps.addStep(String.format("Eliminating row %d using row %d with factor %.4f", k + 1, i + 1, factor));
                for (int j = i; j < cols; j++) {
                    matrix.set(k, j, matrix.get(k, j) - factor * matrix.get(i, j));
                }
                matrixSteps.addMatrixState(matrix.getString());
            }

            // Log matrix state after row operation
            matrixSteps.addStep("Matrix after row operation " + (i + 1) + ":");
            matrixSteps.addMatrixState(matrix.getString());
        }

        // Back substitution
        double[] solution = new double[rows];
        for (int i = rows - 1; i >= 0; i--) {
            solution[i] = matrix.get(i, cols - 1);
            for (int j = i + 1; j < rows; j++) {
                solution[i] -= matrix.get(i, j) * solution[j];
            }
            solution[i]=SmallFloat.handleMinus0(solution[i]);
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
