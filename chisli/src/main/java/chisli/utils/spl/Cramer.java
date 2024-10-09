package chisli.utils.spl;

import chisli.utils.floats.SmallFloat;
import chisli.utils.matrix.Matrix;
import chisli.utils.matrix.MatrixSteps;

public class Cramer {
    private static MatrixSteps matrixSteps;

    public static double[] solve(Matrix A, Matrix B) {
        matrixSteps = new MatrixSteps();
        double detA = A.determinant();

        matrixSteps.addStep("Matrix A");
        matrixSteps.addMatrixState(A.getString());
        matrixSteps.addStep("Determinant of A: " + detA + "\n");

        matrixSteps.addStep("Matrix B");
        matrixSteps.addMatrixState(B.getString());
        
        if (Math.abs(detA) < 1e-10) {
            matrixSteps.addStep("The determinant of A is 0, the system does not have a unique solution.");
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

            matrixSteps.addStep("Matrix A_" + (i + 1) + " (replacing column " + (i + 1) + " of A with B):");
            matrixSteps.addMatrixState(Ai.getString());  // Assuming Matrix has a toString() method to display the matrix
            matrixSteps.addStep("Determinant of A_" + (i + 1) + ": " + detAi + "\n");

            // Step 4: Solve for x_i
            solution[i] = detAi / detA;
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
