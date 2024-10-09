package chisli.utils.spl;

import chisli.utils.floats.SmallFloat;
import chisli.utils.matrix.Matrix;
import chisli.utils.matrix.MatrixSteps;

public class GaussJordan {
    private static MatrixSteps matrixSteps;

    public static void reduce(Matrix augmentedMatrix) {
        matrixSteps = new MatrixSteps(); 
        int rowCount = augmentedMatrix.getRowCount();
        int columnCount = augmentedMatrix.getColumnCount();
        int variableCount = columnCount - 1;  // Number of variables (column - 1 for augmented matrix)

        // Gauss-Jordan Elimination Process
        for (int pivotRow = 0; pivotRow < Math.min(rowCount, variableCount); pivotRow++) {
            double pivotValue = augmentedMatrix.get(pivotRow, pivotRow);
            if (Math.abs(pivotValue) < 1e-10) {
                throw new IllegalArgumentException("Matrix is singular or system has no unique solution.");
            }

            // Normalize the pivot row
            for (int col = pivotRow; col < columnCount; col++) {
                augmentedMatrix.set(pivotRow, col, augmentedMatrix.get(pivotRow, col) / pivotValue);
            }
            matrixSteps.addStep(String.format("Normalize row %d by dividing by %.4f", pivotRow + 1, pivotValue));
            matrixSteps.addMatrixState(augmentedMatrix.getString()); 

            // Eliminate the other rows
            for (int targetRow = 0; targetRow < rowCount; targetRow++) {
                if (targetRow != pivotRow) {  // Don't eliminate the pivot row itself
                    double eliminationFactor = augmentedMatrix.get(targetRow, pivotRow);
                    for (int col = pivotRow; col < columnCount; col++) {
                        augmentedMatrix.set(targetRow, col, augmentedMatrix.get(targetRow, col) - eliminationFactor * augmentedMatrix.get(pivotRow, col));
                    }
                    matrixSteps.addStep(String.format("Eliminating row %d using row %d with factor %.4f", targetRow + 1, pivotRow + 1, eliminationFactor));
                    matrixSteps.addMatrixState(augmentedMatrix.getString()); 
                }
            }
        }
    }

    public static double[] solve(Matrix augmentedMatrix) {
        // Perform Gauss-Jordan elimination
        reduce(augmentedMatrix); 

        int columnCount = augmentedMatrix.getColumnCount();

        // Only focus on variables, ignore extra equations
        int variableCount = columnCount - 1;  

        double[] solutionVector = new double[variableCount];
        for (int i = 0; i < variableCount; i++) {
            solutionVector[i] = augmentedMatrix.get(i, columnCount - 1);  // Extract the solution from the last column
            // Set to 0 if the result is below 1e-4
            solutionVector[i] = SmallFloat.handleMinus0(solutionVector[i]);
        }

        // Log final solution
        matrixSteps.addStep("Final solution:");
        for (int i = 0; i < solutionVector.length; i++) {
            matrixSteps.addStep(String.format("x%d = %.4f", i + 1, solutionVector[i]));
        }

        return solutionVector;
    }

    public static MatrixSteps getMatrixSteps() {
        return matrixSteps;
    }
}
