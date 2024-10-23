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
        int rowCount = matrix.getRowCount();
        int columnCount = matrix.getColumnCount();
    
        if (rowCount != columnCount) {
            throw new IllegalArgumentException("Matrix must be square to calculate determinant.");
        }
    
        // Call the fixed adjoint determinant method
        return determinantByAdjoint(data);
    }
    
    private static double determinantByAdjoint(double[][] data) {
        int size = data.length;
    
        if (size == 1) {
            return data[0][0];
        } else if (size == 2) {
            return data[0][0] * data[1][1] - data[0][1] * data[1][0];
        }
    
        double determinant = 0.0;
    
        // Use cofactor expansion on the first row
        for (int j = 0; j < size; j++) {
            determinant += data[0][j] * cofactor(data, 0, j);
        }
    
        return SmallFloat.handleMinus0(determinant);
    }
    
    private static double cofactor(double[][] matrix, int row, int col) {
        int size = matrix.length;
        double[][] subMatrix = new double[size - 1][size - 1];
        int subRow = 0;
    
        for (int i = 0; i < size; i++) {
            if (i == row) {
                continue;
            }
    
            int subCol = 0;
            for (int j = 0; j < size; j++) {
                if (j == col) {
                    continue;
                }
    
                subMatrix[subRow][subCol++] = matrix[i][j];
            }
            subRow++;
        }
    
        return (row + col) % 2 == 0 ? determinantByAdjoint(subMatrix) : -determinantByAdjoint(subMatrix);
    }
}