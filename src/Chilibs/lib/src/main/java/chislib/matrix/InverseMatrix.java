package chislib.matrix;

import chislib.floats.SmallFloat;

public class InverseMatrix {
  private static MatrixSteps matrixSteps;
  
  public static Matrix inverseElementaryRowOperation(Matrix matrix) {
    return inverseElementaryRowOperation(matrix, false);
  }

  public static Matrix inverseElementaryRowOperation(Matrix matrix, boolean captureSteps) {
    matrixSteps = new MatrixSteps();
    int rows = matrix.getRowCount();
    int cols = matrix.getColumnCount();
    if (rows != cols) {
      throw new IllegalArgumentException("Matrix must be square to find its inverse.");
    }

    // Create an identity matrix of the same size
    double[][] identity = new double[rows][cols];
    for (int i = 0; i < rows; i++) {
        identity[i][i] = 1;
    }

    if (captureSteps) {
        matrixSteps.addStep("Initial Matrix:");
        matrixSteps.addMatrixState(matrix.getString());
        matrixSteps.addStep("Identity Matrix:");
        matrixSteps.addMatrixState(new Matrix(identity).getString());
    }

    // Augment the original matrix with the identity matrix
    double[][] augmented = new double[rows][2 * cols];
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            augmented[i][j] = matrix.get(i, j);       // Original matrix
            augmented[i][j + cols] = identity[i][j]; // Identity matrix
        }
    }

    if (captureSteps) {
        matrixSteps.addStep("Augmented Matrix:");
        matrixSteps.addMatrixState(new Matrix(augmented).getString());
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
            augmented[i][j] = SmallFloat.handleMinus0(augmented[i][j]);
        }

        if (captureSteps) {
            matrixSteps.addStep(String.format("Normalized row %d to make pivot 1:", i + 1));
            matrixSteps.addMatrixState(new Matrix(augmented).getString());
        }

        // Eliminate other entries in this column
        for (int k = 0; k < rows; k++) {
            if (k != i) { // Don't eliminate the pivot row itself
                double factor = augmented[k][i];
                for (int j = 0; j < 2 * cols; j++) {
                    augmented[k][j] -= factor * augmented[i][j];
                    augmented[k][j] = SmallFloat.handleMinus0(augmented[k][j]);
                }
                if (captureSteps) {
                    matrixSteps.addStep(String.format("Eliminated entry in row %d using row %d:", k + 1, i + 1));
                    matrixSteps.addMatrixState(new Matrix(augmented).getString());
                }
            }
        }
    }

    // Extract the inverse matrix from the augmented matrix
    double[][] inverseData = new double[rows][cols];
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            inverseData[i][j] = augmented[i][j + cols];
            inverseData[i][j] = SmallFloat.handleMinus0(inverseData[i][j]);
        }
    }

    if (captureSteps) {
        matrixSteps.addStep("Extracted Inverse Matrix:");
        matrixSteps.addMatrixState(new Matrix(inverseData).getString());
    }

    return new Matrix(inverseData);
  }

  public static Matrix inverseByAdjoin(Matrix matrix) {
    return inverseByAdjoin(matrix, false);
  }

  public static Matrix inverseByAdjoin(Matrix matrix, boolean captureSteps) {
    matrixSteps = new MatrixSteps();
    int size = matrix.getRowCount();

    if (size != matrix.getColumnCount()) {
        throw new IllegalArgumentException("Matrix must be square to find its inverse.");
    }

    double[][] data = matrix.data; // Assuming matrix has a getData method
    double[][] cofactors = new double[size][size];
    Matrix matrixData = new Matrix(data);
    
    if (captureSteps) {
        matrixSteps.addStep("Initial Matrix:");
        matrixSteps.addMatrixState(matrix.getString());
    }
    // Compute the matrix of cofactors
    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            double[][] minor = getMinor(data, i, j);
            Matrix matrixMinor = new Matrix(minor);
            cofactors[i][j] = Math.pow(-1, i + j) * MatrixDeterminant.determinantByElementaryRowOperation(matrixMinor); // Cofactor calculation
            if (captureSteps) {
            matrixSteps.addStep(String.format("Cofactor for element at (%d, %d):", i + 1, j + 1));
            matrixSteps.addMatrixState(matrixMinor.getString());
            }
        }
    }

    // Transpose the cofactor matrix (adjoint)
    double[][] adjoint = new double[size][size];
    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            adjoint[j][i] = cofactors[i][j]; // Transpose
            adjoint[j][i] = SmallFloat.handleMinus0(adjoint[j][i]);
        }
    }

    if (captureSteps) {
        matrixSteps.addStep("Adjoint Matrix:");
        matrixSteps.addMatrixState(new Matrix(adjoint).getString());
    }

    // Calculate determinant of the original matrix
    double determinant = MatrixDeterminant.determinantByElementaryRowOperation(matrixData);
    if (Math.abs(determinant) < 1e-10) {
        throw new IllegalArgumentException("Matrix is singular and cannot be inverted.");
    }

    if (captureSteps) {
        matrixSteps.addStep("Determinant of the original matrix:");
        matrixSteps.addStep(String.valueOf(determinant));
    }

    // Divide adjoint matrix by determinant to get the inverse
    double[][] inverseData = new double[size][size];
    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            inverseData[i][j] = adjoint[i][j] / determinant;
            inverseData[i][j] = SmallFloat.handleMinus0(inverseData[i][j]);
        }
    }

    if (captureSteps) {
        matrixSteps.addStep("Inverse Matrix:");
        matrixSteps.addMatrixState(new Matrix(inverseData).getString());
    }

    return new Matrix(inverseData);
}


private static double[][] getMinor(double[][] matrix, int row, int col) {
  int size = matrix.length;
  double[][] minor = new double[size - 1][size - 1];

  int minorRow = 0;
  for (int i = 0; i < size; i++) {
      if (i == row) continue; // Skip the current row
      int minorCol = 0;
      for (int j = 0; j < size; j++) {
          if (j == col) continue; // Skip the current column
          minor[minorRow][minorCol] = matrix[i][j];
          minorCol++;
      }
      minorRow++;
  }
  return minor;
}



  public static MatrixSteps getMatrixSteps() {
    return matrixSteps;
  }
}
