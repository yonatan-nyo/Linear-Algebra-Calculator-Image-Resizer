package chislib.spl;

import chislib.floats.SmallFloat;
import chislib.matrix.Matrix;
import chislib.matrix.MatrixSteps;

public class GaussJordan {
    private static MatrixSteps matrixSteps;

    public static void reduce(Matrix augmentedMatrix, boolean isCaptureSteps) {
        int rowCount = augmentedMatrix.getRowCount();
        int columnCount = augmentedMatrix.getColumnCount();
        int variableCount = columnCount - 1; 

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
                    if (isCaptureSteps) {
                        matrixSteps.addStep(String.format("Swap row %d with row %d", pivotRow + 1, maxPivotRow + 1));
                        matrixSteps.addMatrixState(augmentedMatrix.getString());
                    }
                }
            }

            // Normalize the pivot row
            pivotValue = augmentedMatrix.get(pivotRow, pivotRow);
            if (pivotValue != 0) {
                for (int col = pivotRow; col < columnCount; col++) {
                    augmentedMatrix.set(pivotRow, col, SmallFloat.handleMinus0(augmentedMatrix.get(pivotRow, col) / pivotValue));
                }
                if (isCaptureSteps) {
                    matrixSteps.addStep(String.format("Normalize row %d by dividing by %.4f", pivotRow + 1, pivotValue));
                    matrixSteps.addMatrixState(augmentedMatrix.getString());
                }
            }

            // Eliminate the other rows
            for (int targetRow = 0; targetRow < rowCount; targetRow++) {
                if (targetRow != pivotRow) {  // Don't eliminate the pivot row itself
                    double eliminationFactor = augmentedMatrix.get(targetRow, pivotRow);
                    for (int col = pivotRow; col < columnCount; col++) {
                        augmentedMatrix.set(targetRow, col, SmallFloat.handleMinus0(augmentedMatrix.get(targetRow, col) - eliminationFactor * augmentedMatrix.get(pivotRow, col)));
                    }
                    if (isCaptureSteps) {
                        matrixSteps.addStep(String.format("Eliminating row %d using row %d with factor %.4f", targetRow + 1, pivotRow + 1, eliminationFactor));
                        matrixSteps.addMatrixState(augmentedMatrix.getString());

                    }
                }
            }

            // Sorting is not mandatory to get the correct solution, do it if steps are captured
            if(isCaptureSteps){
                // Sort the rows to ensure that rows with smaller leading elements are at the top
                for (int i = 0; i < rowCount - 1; i++) {
                    for (int j = 0; j < rowCount - i - 1; j++) {
                        int firstLeadingCol = -1;
                        int secondLeadingCol = -1;
    
                        // Find the first leading non-zero column in row j
                        for (int col = 0; col < columnCount; col++) {
                            if (Math.abs(augmentedMatrix.get(j, col)) > 1e-4) {
                                firstLeadingCol = col;
                                break;
                            }
                        }
    
                        // Find the first leading non-zero column in row j+1
                        for (int col = 0; col < columnCount; col++) {
                            if (Math.abs(augmentedMatrix.get(j + 1, col)) > 1e-4) {
                                secondLeadingCol = col;
                                break;
                            }
                        }
    
                        // If the leading column index of row j+1 is smaller, swap the rows
                        if (secondLeadingCol != -1 && (firstLeadingCol == -1 || secondLeadingCol < firstLeadingCol)) {
                            augmentedMatrix.swapRows(j, j + 1);
                            if (isCaptureSteps) {
                                matrixSteps.addStep(String.format("Swap row %d with row %d", j + 1, j + 2));
                                matrixSteps.addMatrixState(augmentedMatrix.getString());
                            }
                        }
                    }
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
        boolean[] isDependantVarCalculated = new boolean[variableCount]; //Track calculation

        // Initialize solution placeholders (x1, x2, etc.)
        for (int i = 0; i < variableCount; i++) {
            strSolution[i] = String.format("x%d", i + 1);
            isFreeVariable[i] = false;  // Initially assume all are not free variables
            isVariablesCalculated[i]=false;
            isDependantVarCalculated[i]=true;
        }
        
        // Back substitution
        for (int row = rowCount - 1; row >= 0; row--) {
            double rhs = rrefMatrix.get(row, columnCount - 1);  // Right-hand side value
            int leadingColIdx = -1;
            boolean allZeroCoefficients = true;

            // Check if row doesnt contain all zero coefficients
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

            // If all coefficients are zero but the RHS is not zero, it's inconsistent
            if (allZeroCoefficients && Math.abs(rhs) > 1e-4) {
                matrixSteps.addStep(String.format("All coefficient(s) on row %d are 0, but RHS is not 0", row + 1));
                throw new IllegalArgumentException("The system has no solution due to inconsistency.");
            }


            // If the first non-zero element is not on the diagonal, mark it as a free variable
            if (leadingColIdx != row && leadingColIdx != -1) {
                isFreeVariable[row] = true;
                for(int i=leadingColIdx+1;i<variableCount;i++){
                    if(!isVariablesCalculated[i]){
                        strSolution[i] = String.format("x%d (free variable)", i + 1);
                        matrixSteps.addStep(String.format("x%d is a free variable", i + 1));
                        isVariablesCalculated[i]=true;
                        isFreeVariable[i] = true; 
                    }
                }
            }
            
        
            if(leadingColIdx != -1){
                isVariablesCalculated[leadingColIdx]=true;
            }

            // If the row has non-zero diagonal (pivot), perform back substitution
            StringBuilder equationBuilder = new StringBuilder(String.format("x%d = %.4f", leadingColIdx + 1, rhs));
            double backSubstitutionValue = rhs;

            // Back substitute and check dependencies on other variables
            for (int col = leadingColIdx + 1; col < variableCount; col++) {
                double coefficient = rrefMatrix.get(row, col);

                if (SmallFloat.handleMinus0(coefficient) != 0 && !strSolution[col].equals(String.format("x%d = 0.0000", col+1))) {
                    equationBuilder.append(String.format(" - (%.4f * (%s))", coefficient, strSolution[col].replace(" (free variable)", "")));
                    if(!isVariablesCalculated[col]){
                        isDependantVarCalculated[leadingColIdx]=false;
                        isVariablesCalculated[col]=true;
                        isFreeVariable[col] = true;
                        strSolution[col] = String.format("x%d (free variable)", col + 1);
                        matrixSteps.addStep(String.format("x%d is a free variable", col + 1));
                    }
                }

                if(!isFreeVariable[col] && coefficient != 0){
                    backSubstitutionValue -= coefficient * doubleSolution[col]; 
                }else if(leadingColIdx!=-1 && coefficient != 0){
                    isFreeVariable[leadingColIdx] = true;
                }
            }
            isVariablesCalculated[leadingColIdx] = true;

            // Update both symbolic and numeric solutions
            if(leadingColIdx != -1){
                strSolution[leadingColIdx] = equationBuilder.toString();
                doubleSolution[leadingColIdx] = backSubstitutionValue;
                
                matrixSteps.addStep(String.format("Final equation for x%d: %s", leadingColIdx + 1, strSolution[leadingColIdx]));
                
                if(!isFreeVariable[leadingColIdx] && isDependantVarCalculated[leadingColIdx]){ 
                    strSolution[leadingColIdx] = String.format("x%d = %.4f",leadingColIdx+1, backSubstitutionValue);
                }
            }
            

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
        reduce(augmentedMatrix, true); // Capture steps by default

        // Step 4: Extract the solution using the new helper function
        matrixSteps.addStep("\n=================== Back Substitution ===================");
        return getResultFromReducedRowEchelon(augmentedMatrix);
    }

    // New method to solve without recording steps
    public static double[] solveWithoutSteps(Matrix augmentedMatrix) {
        // Perform Gauss-Jordan elimination without capturing steps
        reduce(augmentedMatrix, false);
        
        // Get the results directly without logging steps
        int variableCount = augmentedMatrix.getColumnCount() - 1;
        double[] doubleSolution = new double[variableCount];

        // Retrieve the numeric solutions from the last column
        for (int i = 0; i < variableCount; i++) {
            doubleSolution[i] = augmentedMatrix.get(i, augmentedMatrix.getColumnCount() - 1);
        }
        
        return doubleSolution;
    }

    public static MatrixSteps getMatrixSteps() {
        return matrixSteps;
    }
}
