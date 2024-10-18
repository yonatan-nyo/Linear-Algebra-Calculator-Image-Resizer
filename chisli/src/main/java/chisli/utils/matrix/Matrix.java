package chisli.utils.matrix;

public class Matrix {
    double[][] data;
    int rows, cols;

    // Constructor
    public Matrix(double[][] data) {
        this.data = data;
        this.rows = data.length;
        this.cols = data[0].length;
    }

    // Get element at position (row, col)
    public double get(int row, int col) {
        return data[row][col];
    }

    // Set element at position (row, col)
    public void set(int row, int col, double value) {
        data[row][col] = value;
    }

    // Get row count
    public int getRowCount() {
        return rows;
    }

    // Get column count
    public int getColumnCount() {
        return cols;
    }

    // Print matrix
    public void print() {
        for (double[] row : data) {
            for (double val : row) {
                System.out.printf("%10.4f", val);
            }
            System.out.println();
        }
    }

    // Swap rows (for use in Gaussian elimination, etc.)
    public void swapRows(int row1, int row2) {
        double[] temp = data[row1];
        data[row1] = data[row2];
        data[row2] = temp;
    }

    // Method to calculate the inverse of the matrix
    public Matrix inverse() {
        if (rows != cols) {
            throw new IllegalArgumentException("Matrix must be square to find its inverse.");
        }

        // Create an identity matrix of the same size
        double[][] identity = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            identity[i][i] = 1;
        }

        // Augment the original matrix with the identity matrix
        double[][] augmented = new double[rows][2 * cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                augmented[i][j] = data[i][j];       // Original matrix
                augmented[i][j + cols] = identity[i][j]; // Identity matrix
            }
        }

        // Perform Gauss-Jordan elimination to reduce to reduced row echelon form (RREF)
        for (int i = 0; i < rows; i++) {
            // Step 1: Make the diagonal element 1 (pivot)
            double pivot = augmented[i][i];
            if (Math.abs(pivot) < 1e-10) {
                throw new IllegalArgumentException("Matrix is singular and cannot be inverted.");
            }

            // Normalize the pivot row
            for (int j = 0; j < 2 * cols; j++) {
                augmented[i][j] /= pivot;
            }

            // Eliminate other entries in this column
            for (int k = 0; k < rows; k++) {
                if (k != i) { // Don't eliminate the pivot row itself
                    double factor = augmented[k][i];
                    for (int j = 0; j < 2 * cols; j++) {
                        augmented[k][j] -= factor * augmented[i][j];
                    }
                }
            }
        }

        // Extract the inverse matrix from the augmented matrix
        double[][] inverseData = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                inverseData[i][j] = augmented[i][j + cols];
            }
        }

        return new Matrix(inverseData);
    }

    // Matrix multiplication method
    public static Matrix multiply(Matrix A, Matrix B) {
        if (A.getColumnCount() != B.getRowCount()) {
            throw new IllegalArgumentException("Matrix dimensions do not match for multiplication.");
        }

        double[][] result = new double[A.getRowCount()][B.getColumnCount()];

        for (int i = 0; i < A.getRowCount(); i++) {
            for (int j = 0; j < B.getColumnCount(); j++) {
                for (int k = 0; k < A.getColumnCount(); k++) {
                    result[i][j] += A.get(i, k) * B.get(k, j);
                }
            }
        }

        return new Matrix(result);
    }

    // Helper method to calculate determinant using recursion
    public double determinant() {
        if (rows != cols) {
            throw new IllegalArgumentException("Matrix must be square to calculate determinant.");
        }
        return determinant(data);
    }

    public String getString() {
        Matrix matrix = new Matrix(data);
        StringBuilder sb = new StringBuilder();
        int rows = matrix.getRowCount();
        int cols = matrix.getColumnCount();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sb.append(String.format("%10.4f", matrix.get(i, j)));
                if (j < cols - 1) {
                    sb.append(" ");
                }
            }
            if(i < rows - 1){
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    // Recursive method to calculate determinant
    private double determinant(double[][] matrix) {
        int n = matrix.length;
        if (n == 1) {
            return matrix[0][0];
        }
        if (n == 2) {
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        }

        double det = 0;
        for (int col = 0; col < n; col++) {
            double[][] subMatrix = new double[n - 1][n - 1];
            for (int i = 1; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (j < col) {
                        subMatrix[i - 1][j] = matrix[i][j];
                    } else if (j > col) {
                        subMatrix[i - 1][j - 1] = matrix[i][j];
                    }
                }
            }
            det += (col % 2 == 0 ? 1 : -1) * matrix[0][col] * determinant(subMatrix);
        }
        return det;
    }

    
    // Helper method to check if two rows are proportional (identical up to a scalar multiple)
    private boolean areRowsIdentical(int row1, int row2) {
        double ratio = 0;
        boolean firstNonZeroFound = false;

        for (int col = 0; col < cols; col++) {
            double val1 = data[row1][col];
            double val2 = data[row2][col];

            // Skip if both values are zero (don't affect proportionality)
            if (val1 == 0 && val2 == 0) {
                continue;
            }

            // If one is zero and the other is not, they are not proportional
            if (val1 == 0 || val2 == 0) {
                return false;
            }

            // Calculate the ratio for the first non-zero pair of elements
            if (!firstNonZeroFound) {
                ratio = val1 / val2;
                firstNonZeroFound = true;
            } else {
                // For subsequent non-zero elements, check if the ratio is the same
                if (val1 / val2 != ratio) {
                    return false;
                }
            }
        }

        return firstNonZeroFound; // Ensure there's at least one non-zero element for proportionality
    }
    
    // Helper method to clear (set to zero) a row
    private void clearRow(int row) {
        for (int col = 0; col < cols; col++) {
            data[row][col] = 0;
        }
    }
    
    // Helper method to check if a row contains only zeros
    private boolean isZeroRow(int row) {
        for (int col = 0; col < cols; col++) {
            if (data[row][col] != 0) {
                return false;
            }
        }
        return true;
    }

    public Matrix getCleanedMatrix() {
        int lowestNonZeroRow = rows - 1;  // Start tracking the lowest non-zero row from the last row
    
        // Loop over all pairs of rows to check for identical rows
        for (int i1 = 0; i1 < rows; i1++) {
            for (int i2 = i1 + 1; i2 < rows; i2++) {
                if (areRowsIdentical(i1, i2)) {
                    // If two rows are identical, set one to zero
                    clearRow(i2);
                }
            }
        }
    
        // Move zero rows to the bottom
        for (int i = 0; i < rows-1; i++) {
            if (isZeroRow(i)) {
                swapRows(i, lowestNonZeroRow);
                lowestNonZeroRow--;
            }
        }
    
        return new Matrix(data);
    }
    
}
