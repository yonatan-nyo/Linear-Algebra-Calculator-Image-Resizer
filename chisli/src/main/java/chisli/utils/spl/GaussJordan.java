package chisli.utils.spl;

import chisli.utils.matrix.Matrix;

public class GaussJordan {

    public static void reduce(Matrix matrix) {
        int rows = matrix.getRowCount();
        int cols = matrix.getColumnCount();

        for (int i = 0; i < rows; i++) {
            double pivot = matrix.get(i, i);
            if (Math.abs(pivot) < 1e-10) {
                throw new IllegalArgumentException("Matrix is singular or system has no unique solution.");
            }

            for (int j = i; j < cols; j++) {
                matrix.set(i, j, matrix.get(i, j) / pivot);
            }

            for (int k = 0; k < rows; k++) {
                if (k != i) {  // Don't eliminate the pivot row itself
                    double factor = matrix.get(k, i);
                    for (int j = i; j < cols; j++) {
                        matrix.set(k, j, matrix.get(k, j) - factor * matrix.get(i, j));
                    }
                }
            }
        }
    }

    public static double[] solve(Matrix matrix) {
        int rows = matrix.getRowCount();
        int cols = matrix.getColumnCount();

        double[] solution = new double[rows];
        for (int i = 0; i < rows; i++) {
            solution[i] = matrix.get(i, cols - 1);  
        }

        return solution;
    }
}
