package chisli.utils.spl;

import chisli.utils.floats.SmallFloat;
import chisli.utils.matrix.Matrix;
import chisli.utils.matrix.MatrixSteps;

public class GaussJordan {
    private static MatrixSteps matrixSteps;

    public static void reduce(Matrix matrix) {
        matrixSteps = new MatrixSteps(); // Initialize MatrixSteps
        int rows = matrix.getRowCount();
        int cols = matrix.getColumnCount();
        int variables = cols - 1;  // Number of variables (cols - 1 for augmented matrix)

        // Gauss-Jordan Elimination Process
        for (int i = 0; i < Math.min(rows, variables); i++) {
            double pivot = matrix.get(i, i);
            if (Math.abs(pivot) < 1e-10) {
                throw new IllegalArgumentException("Matrix is singular or system has no unique solution.");
            }

            // Normalize the pivot row
            for (int j = i; j < cols; j++) {
                matrix.set(i, j, matrix.get(i, j) / pivot);
            }
            matrixSteps.addStep(String.format("Normalize row %d by dividing by %.4f", i + 1, pivot));
            matrixSteps.addMatrixState(matrix.getString()); // Log the matrix state

            // Eliminate the other rows
            for (int k = 0; k < rows; k++) {
                if (k != i) {  // Don't eliminate the pivot row itself
                    double factor = matrix.get(k, i);
                    for (int j = i; j < cols; j++) {
                        matrix.set(k, j, matrix.get(k, j) - factor * matrix.get(i, j));
                    }
                    matrixSteps.addStep(String.format("Eliminating row %d using row %d with factor %.4f", k + 1, i + 1, factor));
                    matrixSteps.addMatrixState(matrix.getString()); // Log the matrix state after elimination
                }
            }
            matrixSteps.addStep("Matrix after row operation " + (i + 1) + ":");
            matrixSteps.addMatrixState(matrix.getString()); // Log the matrix state after row operation
        }
    }

    public static double[] solve(Matrix matrix) {
        reduce(matrix); // Perform Gauss-Jordan elimination
        int cols = matrix.getColumnCount();
        int variables = cols - 1;  // Only focus on variables, ignore extra equations

        double[] solution = new double[variables];
        for (int i = 0; i < variables; i++) {
            solution[i] = matrix.get(i, cols - 1);  // Extract the solution from the last column
            // Set to 0 if the result is below 1e-4
            solution[i] = SmallFloat.handleMinus0(solution[i]);
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
