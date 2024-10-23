package chislib.spl;

import chislib.floats.SmallFloat;
import chislib.matrix.Matrix;
import chislib.matrix.MatrixSteps;

public class SplInverse {
    private static MatrixSteps matrixSteps;

    public static double[] solve(Matrix coefficientMatrix, Matrix constantMatrix, boolean isByCofactorExpansion) {
        matrixSteps = new MatrixSteps();

        matrixSteps.addStep("======================= Matrix given ======================");
        matrixSteps.addStep("Coefficient Matrix");
        matrixSteps.addMatrixState(coefficientMatrix.getString());
        matrixSteps.addStep("Constant Matrix");
        matrixSteps.addMatrixState(constantMatrix.getString());
        matrixSteps.addStep("");

        int numberOfEquations = coefficientMatrix.getRowCount();
        int numberOfVariables = coefficientMatrix.getColumnCount();

        // Cut the matrix if there are extra equations
        Matrix cutCoefficientMatrix;
        Matrix cutConstantMatrix;

        if (numberOfEquations > numberOfVariables) {
            matrixSteps.addStep("The system has extra equations. The matrices will be cut to match the number of variables.");
            
            // Cut coefficient matrix
            double[][] cutCoeffData = new double[numberOfVariables][numberOfVariables];
            for (int i = 0; i < numberOfVariables; i++) {
                for (int j = 0; j < numberOfVariables; j++) {
                    cutCoeffData[i][j] = coefficientMatrix.get(i, j);
                }
            }
            cutCoefficientMatrix = new Matrix(cutCoeffData);

            // Cut constant matrix
            double[][] cutConstData = new double[numberOfVariables][1];
            for (int i = 0; i < numberOfVariables; i++) {
                cutConstData[i][0] = constantMatrix.get(i, 0);
            }
            cutConstantMatrix = new Matrix(cutConstData);
        } else {
            cutCoefficientMatrix = coefficientMatrix;
            cutConstantMatrix = constantMatrix;
        }

        // Calculate the inverse of the coefficient matrix
        matrixSteps.addStep("======================= Calculate Inverse ======================");
        matrixSteps.addStep("Coefficient Matrix A (possibly cut)");
        matrixSteps.addMatrixState(cutCoefficientMatrix.getString());
        
        double determinantOfA = cutCoefficientMatrix.determinant(isByCofactorExpansion);
        matrixSteps.addStep("Determinant of A: " + determinantOfA + "\n");
        if (Math.abs(determinantOfA) < 1e-10) {
            matrixSteps.addStep("The determinant of A is 0, the system does not have a unique solution.");
            throw new IllegalArgumentException("The determinant of A is 0, the system does not have a unique solution.");
        }
        
        Matrix inverseMatrix = cutCoefficientMatrix.inverse();
        matrixSteps.addStep("Inverse of Matrix A");
        matrixSteps.addMatrixState(inverseMatrix.getString());

        matrixSteps.addStep("Constant Matrix B (possibly cut)");
        matrixSteps.addMatrixState(cutConstantMatrix.getString());
        matrixSteps.addStep("");

        matrixSteps.addStep("======================= Calculate Solution ======================");
        // Multiply the inverse of the coefficient matrix by the constant matrix to find the solution
        Matrix solutionMatrix = Matrix.multiply(inverseMatrix, cutConstantMatrix);
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

        // Check extra equations for consistency
        if (numberOfEquations > numberOfVariables) {
            matrixSteps.addStep("======================= Verifying Extra Equations ======================");
            for (int extraEqIndex = numberOfVariables; extraEqIndex < numberOfEquations; extraEqIndex++) {
                double leftHandSide = 0;
                for (int varIndex = 0; varIndex < numberOfVariables; varIndex++) {
                    leftHandSide += coefficientMatrix.get(extraEqIndex, varIndex) * solutions[varIndex];
                }
                double rightHandSide = constantMatrix.get(extraEqIndex, 0);

                if (SmallFloat.handleMinus0(leftHandSide - rightHandSide) != 0.0) {
                    matrixSteps.addStep(String.format("Equation %d: %.4f ≠ %.4f", extraEqIndex + 1, leftHandSide, rightHandSide));
                    matrixSteps.addStep("The system is inconsistent: the extra equation does not match the solution.");
                    throw new IllegalArgumentException("Inconsistent system: extra equation does not match the solution.");
                } else {
                    matrixSteps.addStep(String.format("Equation %d: %.4f = %.4f", extraEqIndex + 1, leftHandSide, rightHandSide));
                }
            }
            matrixSteps.addStep("All extra equations are consistent.");
        }

        return solutions;
    }

    public static MatrixSteps getMatrixSteps() {
        return matrixSteps;
    }
}
