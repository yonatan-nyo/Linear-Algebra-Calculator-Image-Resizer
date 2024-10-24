package chislib.matrix;

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
    public Matrix inverse(){
        return inverse(true);
    }

    public Matrix inverse(boolean isByAdjoint) {
        return inverse(isByAdjoint, false);
    }

    public Matrix inverse(boolean isByAdjoint, boolean captureSteps) {
        if (isByAdjoint) {
            return InverseMatrix.inverseByAdjoin(this, captureSteps);
        } else {
            return InverseMatrix.inverseElementaryRowOperation(this, captureSteps);
        }
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

    public double determinant() {
        return determinant(true); // Default to using Laplace expansion
    }

    // Helper method to calculate determinant using recursion
    public double determinant(boolean isByCofactorExpansion) {
        return determinant(isByCofactorExpansion, false);
    }

    public double determinant(boolean isByCofactorExpansion, boolean isCaptureSteps) {
        Matrix matrix = new Matrix(data);
        return isByCofactorExpansion? MatrixDeterminant.determinantByCofactorExpansion(matrix,isCaptureSteps): MatrixDeterminant.determinantByElementaryRowOperation(matrix,isCaptureSteps);
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
