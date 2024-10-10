package chisli.utils.regresi;

import java.util.Arrays;

public class RegresiLinier {

    // Method to perform multiple linear regression using the Normal Estimation Equation
    public static double[][] solve(double[][] x, double[] y) {
        System.out.println("xValues: " + Arrays.deepToString(x));
        System.out.println("yValues: " + Arrays.toString(y));
        int n = x.length; // Number of samples
        int m = x[0].length; // Number of features
        
        // Add an extra column of ones for the intercept term
        double[][] xWithIntercept = new double[n][m + 1];
        for (int i = 0; i < n; i++) {
            xWithIntercept[i][0] = 1.0; // Intercept term
            for (int j = 0; j < m; j++) {
                xWithIntercept[i][j + 1] = x[i][j]; // Features
            }
        }
        System.out.println("xWithIntercept: " + Arrays.deepToString(xWithIntercept));
        
        // Calculate coefficients using Normal Equation: (X^T * X)^-1 * X^T * Y
        double[][] xTransposed = transpose(xWithIntercept);
        System.out.println("xTransposed: " + Arrays.deepToString(xTransposed));
        double[][] xTx = multiply(xTransposed, xWithIntercept);
        System.out.println("xTx: " + Arrays.deepToString(xTx));
        double[][] xTxInverse = invert(xTx);
        System.out.println("xTxInverse: " + Arrays.deepToString(xTxInverse));
        double[][] yMatrix = transformToColumnMatrix(y);
        System.out.println("yMatrix: " + Arrays.deepToString(yMatrix));
        double[][] xTy = multiply(xTransposed, yMatrix);
        System.out.println("xTy: " + Arrays.deepToString(xTy));
    
        double[][] coefficients = multiply(xTxInverse, xTy);
        System.out.println("coefficients: " + Arrays.deepToString(coefficients));
        return coefficients;
    }    

    private static double[][] transpose(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] transposed = new double[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }
        return transposed;
    }

    private static double[][] multiply(double[][] a, double[][] b) {
        int rowsA = a.length;
        int colsA = a[0].length;
        int colsB = b[0].length;
        double[][] result = new double[rowsA][colsB];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < colsA; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }

    private static double[][] invert(double[][] matrix) {
        int n = matrix.length;
        double[][] augmentedMatrix = new double[n][2 * n];
    
        // Create the augmented matrix [matrix | identity]
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                augmentedMatrix[i][j] = matrix[i][j];
            }
            augmentedMatrix[i][i + n] = 1; // Set the identity part
        }
    
        // Perform Gaussian elimination
        for (int i = 0; i < n; i++) {
            // Find the pivot
            double pivot = augmentedMatrix[i][i];
            if (Math.abs(pivot) < 1e-10) { // Check for zero pivot
                throw new IllegalArgumentException("Matrix is singular and cannot be inverted.");
            }
    
            // Normalize the pivot row
            for (int j = 0; j < 2 * n; j++) {
                augmentedMatrix[i][j] /= pivot;
            }
    
            // Eliminate the current column in other rows
            for (int k = 0; k < n; k++) {
                if (k != i) {
                    double factor = augmentedMatrix[k][i];
                    for (int j = 0; j < 2 * n; j++) {
                        augmentedMatrix[k][j] -= factor * augmentedMatrix[i][j];
                    }
                }
            }
        }
    
        // Extract the right part of the augmented matrix as the inverse
        double[][] inverseMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inverseMatrix[i][j] = augmentedMatrix[i][j + n];
            }
        }
    
        return inverseMatrix;
    }
    

    private static double[][] transformToColumnMatrix(double[] array) {
        double[][] columnMatrix = new double[array.length][1];
        for (int i = 0; i < array.length; i++) {
            columnMatrix[i][0] = array[i];
        }
        return columnMatrix;
    }
}
