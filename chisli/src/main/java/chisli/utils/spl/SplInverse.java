package chisli.utils.spl;

import chisli.utils.floats.SmallFloat;
import chisli.utils.matrix.Matrix;
import chisli.utils.matrix.MatrixSteps;

public class SplInverse {
    private static MatrixSteps matrixSteps;
    
    public static double[] solve(Matrix A, Matrix B) {
        matrixSteps = new MatrixSteps();
        Matrix A_inv = A.inverse();
        matrixSteps.addStep("Matrix A");
        matrixSteps.addMatrixState(A.getString());
        matrixSteps.addStep("Inverse of matrix A");
        matrixSteps.addMatrixState(A_inv.getString());
        matrixSteps.addStep("Matrix B");
        matrixSteps.addMatrixState(B.getString());

        Matrix X = Matrix.multiply(A_inv, B);
        matrixSteps.addStep("Multiply Inverse of matrix A with matrix B");
        matrixSteps.addMatrixState(X.getString());

        double[] solution = new double[X.getRowCount()];
        for (int i = 0; i < X.getRowCount(); i++) {
            solution[i] = X.get(i, 0); 
            solution[i]=SmallFloat.handleMinus0(solution[i]);
        }

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
