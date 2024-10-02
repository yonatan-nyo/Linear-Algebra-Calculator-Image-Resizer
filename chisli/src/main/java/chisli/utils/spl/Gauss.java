package chisli.utils.spl;

import chisli.utils.matrix.Matrix;

public class Gauss {

    public static double[] solve(Matrix matrix) {
        int rows = matrix.getRowCount();
        int cols = matrix.getColumnCount();

        for (int i = 0; i < rows; i++) {
            int maxRow = i;
            for (int k = i + 1; k < rows; k++) {
                if (Math.abs(matrix.get(k, i)) > Math.abs(matrix.get(maxRow, i))) {
                    maxRow = k;
                }
            }
            matrix.swapRows(i, maxRow);

            if (Math.abs(matrix.get(i, i)) < 1e-10) {
                throw new IllegalArgumentException("Matrix is singular or system has no unique solution.");
            }

            for (int k = i + 1; k < rows; k++) {
                double factor = matrix.get(k, i) / matrix.get(i, i);
                for (int j = i; j < cols; j++) {
                    matrix.set(k, j, matrix.get(k, j) - factor * matrix.get(i, j));
                }
            }
        }

        double[] solution = new double[rows];
        for (int i = rows - 1; i >= 0; i--) {
            solution[i] = matrix.get(i, cols - 1);
            for (int j = i + 1; j < rows; j++) {
                solution[i] -= matrix.get(i, j) * solution[j];
            }
            solution[i] /= matrix.get(i, i);
        }

        return solution;
    }
}
