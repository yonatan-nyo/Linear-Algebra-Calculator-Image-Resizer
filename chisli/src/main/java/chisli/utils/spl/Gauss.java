package chisli.utils.spl;

import chisli.utils.matrix.Matrix;
import java.util.ArrayList;
import java.util.List;

public class Gauss {
    private static List<String> steps;

    public static double[] solve(Matrix matrix) {
        steps = new ArrayList<>(); // Initialize steps
        int rows = matrix.getRowCount();
        int cols = matrix.getColumnCount();
    
        for (int i = 0; i < rows; i++) {
            int maxRow = i;
            for (int k = i + 1; k < rows; k++) {
                if (Math.abs(matrix.get(k, i)) > Math.abs(matrix.get(maxRow, i))) {
                    maxRow = k;
                }
            }
    
            // Swap rows if needed
            matrix.swapRows(i, maxRow);
            if (i != maxRow) {
                steps.add(String.format("Swap row %d with row %d", i + 1, maxRow + 1));
                steps.add(matrix.getString());
            }
    
            // Check if matrix is singular
            if (Math.abs(matrix.get(i, i)) < 1e-10) {
                throw new IllegalArgumentException("Matrix is singular or system has no unique solution.");
            }
    
            // Make the leading entry 1 by dividing the row by the pivot
            double pivot = matrix.get(i, i);
            for (int j = i; j < cols; j++) {
                matrix.set(i, j, matrix.get(i, j) / pivot);
            }
            steps.add(String.format("Normalize row %d by dividing by %.4f", i + 1, pivot));
            steps.add(matrix.getString());
    
            // Elimination process
            for (int k = i + 1; k < rows; k++) {
                double factor = matrix.get(k, i); // No need to divide by pivot since it's 1 now
                steps.add(String.format("Eliminating row %d using row %d with factor %.4f", k + 1, i + 1, factor));
                for (int j = i; j < cols; j++) {
                    matrix.set(k, j, matrix.get(k, j) - factor * matrix.get(i, j));
                }
                steps.add(matrix.getString());
            }
    
            // Log matrix state after each row operation
            steps.add("Matrix after row operation " + (i + 1) + ":");
            steps.add(matrix.getString());
        }
    
        // Back substitution
        double[] solution = new double[rows];
        for (int i = rows - 1; i >= 0; i--) {
            solution[i] = matrix.get(i, cols - 1); // Right-hand side value
            for (int j = i + 1; j < rows; j++) {
                solution[i] -= matrix.get(i, j) * solution[j];
            }
            // No need to divide by the pivot, it's already normalized to 1
        }
    
        // Log final solution
        steps.add("Final solution:");
        for (int i = 0; i < solution.length; i++) {
            steps.add(String.format("x%d = %.4f", i + 1, solution[i]));
        }
    
        return solution;
    }
    
    public static List<String> getSteps() {
        return steps;
    }

    public static void clearSteps() {
        steps.clear();
    }
}