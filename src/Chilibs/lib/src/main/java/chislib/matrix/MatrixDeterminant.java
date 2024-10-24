package chislib.matrix;

import chislib.floats.SmallFloat;

public class MatrixDeterminant {
    private static MatrixSteps matrixSteps;

    // Default captureSteps is set to false
    public static double determinantByElementaryRowOperation(Matrix matrix) {
        return determinantByElementaryRowOperation(matrix, false);
    }

    public static double determinantByElementaryRowOperation(Matrix matrix, boolean captureSteps) {
        matrixSteps = new MatrixSteps();
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

        if (captureSteps) {
            matrixSteps.addStep("======================= Elementary Row Operation ======================");
            matrixSteps.addMatrixState(reducedMatrix.getString());
        }

        for (int i = 0; i < rows; i++) {
            // Step 1: Find a pivot element and swap rows if necessary
            if (Math.abs(reducedMatrix.get(i, i)) < 1e-10) {  // Consider a small threshold for numerical stability
                boolean swapped = false;
                for (int j = i + 1; j < rows; j++) {
                    if (Math.abs(reducedMatrix.get(j, i)) > 1e-10) {
                        reducedMatrix.swapRows(i, j);
                        swaps++;
                        swapped = true;
                        if (captureSteps) {
                            matrixSteps.addStep(String.format("Swapped row %d with row %d to use %s as pivot.", 
                                                i + 1, j + 1, reducedMatrix.get(i, i)));
                            matrixSteps.addMatrixState(reducedMatrix.getString());
                        }
                        break;
                    }
                }
                // If no pivot is found (matrix is singular), determinant is zero
                if (!swapped) {
                    if (captureSteps) {
                        matrixSteps.addStep("No pivot found. The matrix is singular. Determinant = 0.");
                    }
                    return 0;
                }
            }

            // Step 2: Eliminate the entries below the pivot element
            for (int j = i + 1; j < rows; j++) {
                double factor = reducedMatrix.get(j, i) / reducedMatrix.get(i, i);
                for (int k = i; k < cols; k++) {
                    reducedMatrix.set(j, k, reducedMatrix.get(j, k) - factor * reducedMatrix.get(i, k));
                }
                if (captureSteps) {
                    matrixSteps.addStep(String.format("Eliminated entry at row %d, column %d using factor %.4f", 
                                                j + 1, i + 1, factor));
                    matrixSteps.addMatrixState(reducedMatrix.getString());
                }
            }
        }

        // Step 3: Multiply diagonal elements to get the determinant
        StringBuilder determinantSteps = new StringBuilder("Determinant Calculation:\n");
        determinantSteps.append("Initial determinant = 1\n");
        
        for (int i = 0; i < rows; i++) {
            determinant *= reducedMatrix.get(i, i);
            // Capture 
            determinantSteps.append(String.format("Multiply by diagonal element at row %d: %s\n", 
                                    i + 1, reducedMatrix.get(i, i)));
        }

        // Adjust for row swaps
        if (swaps % 2 != 0) {
            determinant = -determinant;
            determinantSteps.append(String.format("Adjusted for row swaps (odd number of swaps): determinant = -%s\n", determinant));
        }

        if (captureSteps) {
            determinantSteps.append("Final determinant calculated: " + SmallFloat.handleMinus0(determinant));
            matrixSteps.addStep(determinantSteps.toString());
        }

        return SmallFloat.handleMinus0(determinant);
    }

    // Default captureSteps is set to false
    public static double determinantByCofactorExpansion(Matrix matrix) {
        return determinantByCofactorExpansion(matrix, false);
    }

    public static double determinantByCofactorExpansion(Matrix matrix, boolean captureSteps) {
        // Initialize matrixSteps for capturing the process
        matrixSteps = new MatrixSteps();
        double[][] data = matrix.data;
        int rowCount = matrix.getRowCount();
        int columnCount = matrix.getColumnCount();

        if (rowCount != columnCount) {
            throw new IllegalArgumentException("Matrix must be square to calculate determinant.");
        }

        if (captureSteps) {
            matrixSteps.addStep("======================= Cofactor Expansion ======================");
            matrixSteps.addMatrixState(matrix.getString());
        }

        // Call the fixed cofactor expansion determinant method
        return determinantByCofactorExpansion(data, captureSteps);
    }

    private static double determinantByCofactorExpansion(double[][] data, boolean captureSteps) {
        int size = data.length;

        if (size == 1) {
            if(captureSteps) {
                matrixSteps.addStep("Matrix is 1x1. Determinant is the element itself.");
                matrixSteps.addStep("Determinant = " + data[0][0]);
            }
            return data[0][0];
        } else if (size == 2) {
            if (captureSteps) {
                matrixSteps.addStep("Matrix is 2x2. Determinant calculated directly.");
                matrixSteps.addStep(String.format("Determinant = (%s * %s) - (%s * %s)", 
                        data[0][0], data[1][1], data[0][1], data[1][0]));
                matrixSteps.addStep("Determinant = " + (data[0][0] * data[1][1] - data[0][1] * data[1][0]));
            }
            return data[0][0] * data[1][1] - data[0][1] * data[1][0];
        }

        double determinant = 0.0;

        // Use cofactor expansion on the first row
        for (int j = 0; j < size; j++) {
            double cofactorValue = cofactor(data, 0, j, captureSteps);
            determinant += data[0][j] * cofactorValue;

            if (captureSteps) {
                matrixSteps.addStep(String.format("Cofactor for element at (0, %d): %s", j, cofactorValue));
                matrixSteps.addStep(String.format("Contribution to determinant from element %s: %s * %s", 
                        data[0][j], data[0][j], cofactorValue));
            }
        }

        if (captureSteps) {
            matrixSteps.addStep("");
            matrixSteps.addStep(String.format("Final determinant calculated matrix (%dx%d): %s", 
                data.length, data[0].length, SmallFloat.handleMinus0(determinant)));
        }

        return SmallFloat.handleMinus0(determinant);
    }

    private static double cofactor(double[][] matrix, int row, int col, boolean captureSteps) {
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

        // Log the creation of the submatrix if capturing steps
        if (captureSteps) {
            matrixSteps.addStep("");
            matrixSteps.addStep(String.format("Created submatrix by removing row %d and column %d", row + 1, col + 1));
            matrixSteps.addMatrixState(new Matrix(subMatrix).getString());
        }

        // Calculate the determinant of the submatrix
        double cofactorDeterminant = determinantByCofactorExpansion(subMatrix, captureSteps);
        return (row + col) % 2 == 0 ? cofactorDeterminant : -cofactorDeterminant;
    }

    public static MatrixSteps getMatrixSteps() {
        return matrixSteps;
    }
}
