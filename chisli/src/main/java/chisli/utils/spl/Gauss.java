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
        int variables = cols - 1;  // Number of variables (cols - 1 for augmented matrix)

        // Forward elimination
        for (int i = 0; i < Math.min(rows, variables); i++) {
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

            // Check if matrix is singular or system has no unique solution
            if (i < variables && Math.abs(matrix.get(i, i)) < 1e-10) {
                throw new IllegalArgumentException("Matrix is singular or system has no unique solution.");
            }

            // Normalize row
            double pivot = matrix.get(i, i);
            if (pivot != 0) {
                for (int j = i; j < cols; j++) {
                    matrix.set(i, j, matrix.get(i, j) / pivot);
                }
                matrixSteps.addStep(String.format("Normalize row %d by dividing by %.4f", i + 1, pivot));
                matrixSteps.addMatrixState(matrix.getString());
            }

            // Elimination process
            for (int k = i + 1; k < rows; k++) {
                double factor = matrix.get(k, i);
                matrixSteps.addStep(String.format("Eliminating row %d using row %d with factor %.4f", k + 1, i + 1, factor));
                for (int j = i; j < cols; j++) {
                    matrix.set(k, j, matrix.get(k, j) - factor * matrix.get(i, j));
                }
                matrixSteps.addMatrixState(matrix.getString());
            }
        }

        // Back substitution
        double[] solution = new double[variables];
        for (int i = variables - 1; i >= 0; i--) {
            solution[i] = matrix.get(i, cols - 1);  // Right-hand side of the equation

            // Check if the current row is inconsistent (all coefficients are 0 but the constant is non-zero)
            boolean allZeroCoefficients = true;
            for (int j = 0; j < variables; j++) {
                if (Math.abs(matrix.get(i, j)) > 1e-10) {
                    allZeroCoefficients = false;
                    break;
                }
            }
            if (allZeroCoefficients && Math.abs(solution[i]) > 1e-10) {
                throw new IllegalArgumentException("The system has no solution due to inconsistency.");
            }

            for (int j = i + 1; j < variables; j++) {
                solution[i] -= matrix.get(i, j) * solution[j];
            }
            solution[i] = SmallFloat.handleMinus0(solution[i]);  // Handle -0 case
        }

        // Check when coefficients 0 and value is not 0
        for (int i=0;i<rows;i++){
            boolean areCoefficientsZero = true;
            for(int j=0;j<cols;j++){
                if(j<cols-1 && matrix.get(i, j)!=0){
                    areCoefficientsZero=false;
                }
                if(areCoefficientsZero && j==cols-1 && matrix.get(i, j)!=0){
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
