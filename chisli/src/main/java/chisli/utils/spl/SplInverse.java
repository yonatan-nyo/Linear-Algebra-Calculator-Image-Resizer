package chisli.utils.spl;

import chisli.utils.floats.SmallFloat;
import chisli.utils.matrix.Matrix;
import chisli.utils.matrix.MatrixSteps;

public class SplInverse {
    private static MatrixSteps matrixSteps;

    public static double[] solve(Matrix coefficientMatrix, Matrix constantMatrix) {
        matrixSteps = new MatrixSteps();

        // Calculate the inverse of the coefficient matrix
        Matrix inverseMatrix = coefficientMatrix.inverse();
        matrixSteps.addStep("Coefficient Matrix A");
        matrixSteps.addMatrixState(coefficientMatrix.getString());

        matrixSteps.addStep("Inverse of Matrix A");
        matrixSteps.addMatrixState(inverseMatrix.getString());

        matrixSteps.addStep("Constant Matrix B");
        matrixSteps.addMatrixState(constantMatrix.getString());

        // Multiply the inverse of the coefficient matrix by the constant matrix to find the solution
        Matrix solutionMatrix = Matrix.multiply(inverseMatrix, constantMatrix);
        matrixSteps.addStep("Multiply Inverse of Matrix A with Matrix B");
        matrixSteps.addMatrixState(solutionMatrix.getString());

        // Extract the solution from the resulting matrix
        double[] solutions = new double[solutionMatrix.getRowCount()];
        for (int rowIndex = 0; rowIndex < solutionMatrix.getRowCount(); rowIndex++) {
            solutions[rowIndex] = solutionMatrix.get(rowIndex, 0); 
            solutions[rowIndex] = SmallFloat.handleMinus0(solutions[rowIndex]);
        }

        matrixSteps.addStep("Final solution:");
        for (int i = 0; i < solutions.length; i++) {
            matrixSteps.addStep(String.format("x%d = %.4f", i + 1, solutions[i]));
        }

        return solutions;
    }

    public static MatrixSteps getMatrixSteps() {
        return matrixSteps;
    }
}
