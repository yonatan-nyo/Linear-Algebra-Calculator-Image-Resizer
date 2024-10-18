package chisli.utils.spl;

import chisli.utils.floats.SmallFloat;
import chisli.utils.matrix.Matrix;
import chisli.utils.matrix.MatrixSteps;

public class GaussJordan {
    private static MatrixSteps matrixSteps;

    public static void reduce(Matrix augmentedMatrix) {
        int rowCount = augmentedMatrix.getRowCount();
        int columnCount = augmentedMatrix.getColumnCount();
        int variableCount = columnCount - 1;  // Number of variables (column - 1 for augmented matrix)

        // Gauss-Jordan Elimination Process
        for (int pivotRow = 0; pivotRow < Math.min(rowCount, variableCount); pivotRow++) {
            double pivotValue = augmentedMatrix.get(pivotRow, pivotRow);

            // Swap rows if needed
            if (Math.abs(pivotValue) < 1e-4) {
                int maxPivotRow = pivotRow;
                for (int currentRow = pivotRow + 1; currentRow < rowCount; currentRow++) {
                    if (Math.abs(augmentedMatrix.get(currentRow, pivotRow)) > Math.abs(augmentedMatrix.get(maxPivotRow, pivotRow))) {
                        maxPivotRow = currentRow;
                    }
                }

                if (pivotRow != maxPivotRow) {
                    augmentedMatrix.swapRows(pivotRow, maxPivotRow);
                    matrixSteps.addStep(String.format("Swap row %d with row %d", pivotRow + 1, maxPivotRow + 1));
                    matrixSteps.addMatrixState(augmentedMatrix.getString());
                }
            }

            // Normalize the pivot row
            pivotValue = augmentedMatrix.get(pivotRow, pivotRow);
            if(pivotValue != 0){
                for (int col = pivotRow; col < columnCount; col++) {
                    
                    augmentedMatrix.set(pivotRow, col, SmallFloat.handleMinus0(augmentedMatrix.get(pivotRow, col) / pivotValue));
                }
                matrixSteps.addStep(String.format("Normalize row %d by dividing by %.4f", pivotRow + 1, pivotValue));
                matrixSteps.addMatrixState(augmentedMatrix.getString()); 
            }

            // Eliminate the other rows
            for (int targetRow = 0; targetRow < rowCount; targetRow++) {
                if (targetRow != pivotRow) {  // Don't eliminate the pivot row itself
                    double eliminationFactor = augmentedMatrix.get(targetRow, pivotRow);
                    for (int col = pivotRow; col < columnCount; col++) {
                        augmentedMatrix.set(targetRow, col, SmallFloat.handleMinus0(augmentedMatrix.get(targetRow, col) - eliminationFactor * augmentedMatrix.get(pivotRow, col)));
                    }
                    matrixSteps.addStep(String.format("Eliminating row %d using row %d with factor %.4f", targetRow + 1, pivotRow + 1, eliminationFactor));
                    matrixSteps.addMatrixState(augmentedMatrix.getString()); 
                }
            }
        }
    }

    public static String[] getResultFromReducedRowEchelon(Matrix rrefMatrix) {
        int rowCount = rrefMatrix.getRowCount();
        int columnCount = rrefMatrix.getColumnCount();
        int variableCount = columnCount - 1;  // Number of variables (ignore last column for constants)
    
        String[] strSolution = new String[variableCount];  // For symbolic solution
        double[] doubleSolution = new double[variableCount];  // For numeric solution
        boolean[] isFreeVariable = new boolean[variableCount];  // Track free variables
        boolean[] isVariablesCalculated = new boolean[variableCount]; // Track if a variable has been calculated
    
        // Initialize solution placeholders (x1, x2, etc.)
        for (int i = 0; i < variableCount; i++) {
            strSolution[i] = String.format("x%d", i + 1);
            isFreeVariable[i] = false;  // Initially assume all are not free variables
            isVariablesCalculated[i] = false;
        }
    
        // Back substitution
        for (int row = rowCount - 1; row >= 0; row--) {
            double rhs = rrefMatrix.get(row, columnCount - 1);  // Right-hand side value
            int leadingColIdx = -1;
            boolean allZeroCoefficients = true;
            
            // Check if row contains all zero coefficients
            for (int col = row; col < variableCount; col++) {
                if (Math.abs(rrefMatrix.get(row, col)) > 1e-4) {
                    allZeroCoefficients = false;
                    break;
                }
            }

    
            // Find the first non-zero coefficient in the row
            for (int col = 0; col < variableCount; col++) {
                if (Math.abs(rrefMatrix.get(row, col)) > 1e-4) {
                    leadingColIdx = col;
                    break;
                }
            }

            // If all coefficients are zero but the RHS is not zero, it's inconsistent
            if (allZeroCoefficients && Math.abs(rhs) > 1e-4) {
                matrixSteps.addStep(String.format("All coefficient(s) on row %d are 0, but RHS is not 0", row + 1));
                throw new IllegalArgumentException("The system has no solution due to inconsistency.");
            }

    
            // If no leading coefficient is found, move to the next row (free variable)
            if (leadingColIdx == -1) continue;
    
            // If the row has non-zero diagonal (pivot), perform back substitution
            StringBuilder equationBuilder = new StringBuilder(String.format("x%d = %.4f", leadingColIdx + 1, rhs));
            double backSubstitutionValue = rhs;
    
            // Back substitute and check dependencies on other variables
            for (int col = leadingColIdx + 1; col < variableCount; col++) {
                double coefficient = rrefMatrix.get(row, col);
                if (Math.abs(coefficient) > 1e-4) {
                    equationBuilder.append(String.format(" - (%.4f * (%s))", coefficient, strSolution[col]));
                    if (!isVariablesCalculated[col]) {
                        strSolution[col] = String.format("x%d (free variable)", col + 1);
                        isFreeVariable[col] = true;
                    }
                    backSubstitutionValue -= coefficient * doubleSolution[col];
                }
            }
    
            // Update both symbolic and numeric solutions
            strSolution[leadingColIdx] = equationBuilder.toString();
            doubleSolution[leadingColIdx] = backSubstitutionValue;
            isVariablesCalculated[leadingColIdx] = true;
            
            matrixSteps.addStep(String.format("Final equation for x%d: %s", leadingColIdx + 1, strSolution[leadingColIdx]));
        }
    
        // Mark any variables that haven't been calculated as free
        for (int i = 0; i < variableCount; i++) {
            if (!isVariablesCalculated[i]) {
                strSolution[i] = String.format("x%d (free variable)", i + 1);
                matrixSteps.addStep(String.format("x%d is a free variable", i + 1));
            }
        }
    
        // Final solution log
        matrixSteps.addStep("\nFinal solution:");
        for (int i = 0; i < strSolution.length; i++) {
            matrixSteps.addStep(strSolution[i]);
        }
    
        return strSolution;
    }
    

    public static String[] solve(Matrix augmentedMatrix) { 
        matrixSteps = new MatrixSteps();
        
        // Step 1: Log the initial matrix
        matrixSteps.addStep("======================= Matrix given ======================");
        matrixSteps.addMatrixState(augmentedMatrix.getString());
    
        // Step 2: Perform Gauss-Jordan elimination to get the reduced row echelon form
        matrixSteps.addStep("\n======== Elimination (Reduced Row Echelon Form) =========");
        reduce(augmentedMatrix); // The matrix is reduced in-place during this call
        
        // Step 4: Extract the solution using the new helper function
        matrixSteps.addStep("\n=================== Back Substitution ===================");
        return getResultFromReducedRowEchelon(augmentedMatrix);
    }
    

    public static MatrixSteps getMatrixSteps() {
        return matrixSteps;
    }
}
