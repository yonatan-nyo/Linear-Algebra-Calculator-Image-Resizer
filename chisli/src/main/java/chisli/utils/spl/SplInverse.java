package chisli.utils.spl;

import chisli.utils.matrix.Matrix;

public class SplInverse {
    public static double[] solve(Matrix A, Matrix B) {
        Matrix A_inv = A.inverse();

        Matrix X = Matrix.multiply(A_inv, B);

        double[] solution = new double[X.getRowCount()];
        for (int i = 0; i < X.getRowCount(); i++) {
            solution[i] = X.get(i, 0); 
        }

        return solution;
    }
}
