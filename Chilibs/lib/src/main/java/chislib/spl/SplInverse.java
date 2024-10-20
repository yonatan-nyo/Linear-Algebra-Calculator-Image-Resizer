package chislib.spl;

import chislib.floats.SmallFloat;
import chislib.matrix.Matrix;
import chislib.matrix.MatrixSteps;

public class SplInverse {
    private static MatrixSteps matrixSteps;

    public static double[] solve(Matrix coefficientMatrix, Matrix constantMatrix, boolean isDeterminantModeAdjoint) {
        matrixSteps = new MatrixSteps();

        matrixSteps.addStep("======================= Matrix given ======================");
        matrixSteps.addStep("Coefficient Matrix");
        matrixSteps.addMatrixState(coefficientMatrix.getString());
        matrixSteps.addStep("Constant Matrix");
        matrixSteps.addMatrixState(constantMatrix.getString());
        matrixSteps.addStep("");

        // Calculate the inverse of the coefficient matrix
        matrixSteps.addStep("======================= Calculate Inverse ======================");
        matrixSteps.addStep("Coefficient Matrix A");
        matrixSteps.addMatrixState(coefficientMatrix.getString());
        
        double determinantOfA = coefficientMatrix.determinant(isDeterminantModeAdjoint);
        matrixSteps.addStep("Determinant of A: " + determinantOfA + "\n");
        if (Math.abs(determinantOfA) < 1e-10) {
            matrixSteps.addStep("The determinant of A is 0, the system does not have a unique solution.");
            throw new IllegalArgumentException("The determinant of A is 0, the system does not have a unique solution.");
        }
        
        Matrix inverseMatrix = coefficientMatrix.inverse();
        matrixSteps.addStep("Inverse of Matrix A");
        matrixSteps.addMatrixState(inverseMatrix.getString());

        matrixSteps.addStep("Constant Matrix B");
        matrixSteps.addMatrixState(constantMatrix.getString());
        matrixSteps.addStep("");

        matrixSteps.addStep("======================= Calculate Solution ======================");
        // Multiply the inverse of the coefficient matrix by the constant matrix to find the solution
        Matrix solutionMatrix = Matrix.multiply(inverseMatrix, constantMatrix);
        matrixSteps.addStep("Multiply Inverse of Matrix A with Matrix B");
        matrixSteps.addMatrixState(solutionMatrix.getString());
        matrixSteps.addStep("");

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
