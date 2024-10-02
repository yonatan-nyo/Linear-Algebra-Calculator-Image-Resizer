package chisli.utils.spl;

import chisli.utils.matrix.Matrix;

public class Cramer {

    public static double[] solve(Matrix A, Matrix B) {
        double detA = A.determinant();

        if (Math.abs(detA) < 1e-10) {
            throw new IllegalArgumentException("The determinant of A is 0, the system does not have a unique solution.");
        }

        // Prepare an array to store solutions
        double[] solution = new double[A.getRowCount()];

        // Step 2: For each variable, calculate the determinant of modified matrix
        for (int i = 0; i < A.getRowCount(); i++) {
            // Create a new matrix for Ai
            double[][] Ai_data = new double[A.getRowCount()][A.getColumnCount()];

            // Copy A, replacing the i-th column with B
            for (int row = 0; row < A.getRowCount(); row++) {
                for (int col = 0; col < A.getColumnCount(); col++) {
                    if (col == i) {
                        Ai_data[row][col] = B.get(row, 0); // Replace with B's values
                    } else {
                        Ai_data[row][col] = A.get(row, col);
                    }
                }
            }

            // Create the modified matrix Ai
            Matrix Ai = new Matrix(Ai_data);

            // Step 3: Calculate the determinant of Ai
            double detAi = Ai.determinant();

            // Step 4: Solve for x_i
            solution[i] = detAi / detA;
        }

        return solution;
    }
}
