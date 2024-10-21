package chislib.matrix;

import chislib.floats.SmallFloat;

public class MatrixDeterminant {

    public static double determinantByElementaryRowOperation(Matrix matrix) {
        int rows = matrix.getRowCount();
        int cols = matrix.getColumnCount();
    
        // The matrix must be square to calculate the determinant
        if (rows != cols) {
            throw new IllegalArgumentException("Matrix must be square to calculate determinant.");
        }
    
        // Create a deep copy of the matrix data to avoid modifying the original matrix
        double[][] originalData = matrix.data;
        double[][] copiedData = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            System.arraycopy(originalData[i], 0, copiedData[i], 0, cols);
        }
        
        Matrix reducedMatrix = new Matrix(copiedData);  // Create a new matrix with the copied data
    
        double determinant = 1;
        int swaps = 0;  // To track the number of row swaps
    
        for (int i = 0; i < rows; i++) {
            // Step 1: Find a pivot element and swap rows if necessary
            if (Math.abs(reducedMatrix.get(i, i)) < 1e-10) {  // Consider a small threshold for numerical stability
                boolean swapped = false;
                for (int j = i + 1; j < rows; j++) {
                    if (Math.abs(reducedMatrix.get(j, i)) > 1e-10) {
                        reducedMatrix.swapRows(i, j);
                        swaps++;
                        swapped = true;
                        break;
                    }
                }
                // If no pivot is found (matrix is singular), determinant is zero
                if (!swapped) {
                    return 0;
                }
            }
    
            // Step 2: Eliminate the entries below the pivot element
            for (int j = i + 1; j < rows; j++) {
                double factor = reducedMatrix.get(j, i) / reducedMatrix.get(i, i);
                for (int k = i; k < cols; k++) {
                    reducedMatrix.set(j, k, reducedMatrix.get(j, k) - factor * reducedMatrix.get(i, k));
                }
            }
        }
    
        // Step 3: Multiply diagonal elements to get the determinant
        for (int i = 0; i < rows; i++) {
            determinant *= reducedMatrix.get(i, i);
        }
    
        // Adjust for row swaps
        if (swaps % 2 != 0) {
            determinant = -determinant;
        }
    
        return SmallFloat.handleMinus0(determinant);
    }
    
    // Method to calculate determinant using adjoint method
    public static double determinantByAdjoint(Matrix matrix) {
        double[][] data = matrix.data;
        int rows = matrix.getRowCount();
        int cols = matrix.getColumnCount();
        
        if (rows != cols) {
            throw new IllegalArgumentException("Matrix must be square to calculate determinant.");
        }
        return determinantByAdjoint(data);
    }

    // Method to calculate determinant using adjoint method
    private static double determinantByAdjoint(double[][] matrix) {
        int n = matrix.length;
        if (n == 1) {
            return matrix[0][0];
        }
        if (n == 2) {
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        }

        double[][] cofactorMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                cofactorMatrix[i][j] = cofactor(matrix, i, j);
            }
        }

        double[][] adjointMatrix = transpose(cofactorMatrix);
        double det = 0;
        for (int i = 0; i < n; i++) {
            det += matrix[0][i] * adjointMatrix[0][i];
        }
        return SmallFloat.handleMinus0(det);
    }

    // Method to calculate cofactor of a matrix element
    private static double cofactor(double[][] matrix, int row, int col) {
        int n = matrix.length;
        double[][] subMatrix = new double[n - 1][n - 1];
        for (int i = 0, subI = 0; i < n; i++) {
            if (i == row) continue;
            for (int j = 0, subJ = 0; j < n; j++) {
                if (j == col) continue;
                subMatrix[subI][subJ++] = matrix[i][j];
            }
            subI++;
        }
        return ((row + col) % 2 == 0 ? 1 : -1) * determinantByElementaryRowOperation(new Matrix(subMatrix));
    }

    // Method to transpose a matrix
    private static double[][] transpose(double[][] matrix) {
        int n = matrix.length;
        double[][] transposed = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                transposed[i][j] = matrix[j][i];
            }
        }
        return transposed;
    }
}