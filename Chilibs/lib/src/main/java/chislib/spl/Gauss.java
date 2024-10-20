package chislib.spl;

import chislib.floats.SmallFloat;
import chislib.matrix.Matrix;
import chislib.matrix.MatrixSteps;

public class Gauss {
    private static MatrixSteps matrixSteps;

    // Step 1: Forward elimination (Row Echelon Form)
    public static Matrix getEchelon(Matrix augmentedMatrix) {
        int rowCount = augmentedMatrix.getRowCount();
        int columnCount = augmentedMatrix.getColumnCount();
        int variableCount = columnCount - 1;

        // Forward elimination
        for (int pivotRow = 0; pivotRow < Math.min(rowCount, variableCount); pivotRow++) {
            // Swap rows if needed
            if (Math.abs(augmentedMatrix.get(pivotRow, pivotRow)) < 1e-4) {
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

            int firstColIdxNot0 = -1;
            for (int col = pivotRow; col < variableCount; col++) {
                if (Math.abs(augmentedMatrix.get(pivotRow, col)) > 1e-4) {
                    firstColIdxNot0 = col;
                    break;
                }
            }

            if(firstColIdxNot0 != -1){
                // Normalize pivot row
                double pivotValue = augmentedMatrix.get(pivotRow, firstColIdxNot0);
                if (pivotValue != 0) {
                    for (int col = pivotRow; col < columnCount; col++) {
                        augmentedMatrix.set(pivotRow, col, SmallFloat.handleMinus0(augmentedMatrix.get(pivotRow, col) / pivotValue));
                    }
                    matrixSteps.addStep(String.format("Normalize row %d by dividing by %.4f", pivotRow + 1, pivotValue));
                    matrixSteps.addMatrixState(augmentedMatrix.getString());
                }
    
                // Elimination for rows below
                for (int rowBelow = pivotRow + 1; rowBelow < rowCount; rowBelow++) {
                    double eliminationFactor = augmentedMatrix.get(rowBelow, firstColIdxNot0);
                    matrixSteps.addStep(String.format("Eliminating row %d using row %d with factor %.4f", rowBelow + 1, pivotRow + 1, eliminationFactor));
                    for (int col = pivotRow; col < columnCount; col++) {
                        augmentedMatrix.set(rowBelow, col, SmallFloat.handleMinus0(augmentedMatrix.get(rowBelow, col) - eliminationFactor * augmentedMatrix.get(pivotRow, col)));
                    }
                    matrixSteps.addMatrixState(augmentedMatrix.getString());
                }
            }
            
        }
        return augmentedMatrix; // Return the matrix in row echelon form
    }

    // Step 2: Back substitution to get the result, handling free variables or unique solutions
    public static String[] getResultFromEchelon(Matrix echelonMatrix) {
        int rowCount = echelonMatrix.getRowCount();
        int columnCount = echelonMatrix.getColumnCount();
        int variableCount = columnCount - 1;

        String[] strSolution = new String[variableCount];  // For symbolic solution
        double[] doubleSolution = new double[variableCount];  // For numeric solution
        boolean[] isFreeVariable = new boolean[variableCount];  // Track free variables/ Track free variables
        boolean[] isVariablesCalculated = new boolean[variableCount]; //Track calculation
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
            double rhs = echelonMatrix.get(row, columnCount - 1);  // Right-hand side value
            boolean allZeroCoefficients = true;
            int firstColIdxNot0 = -1;
            
            // Check if row contains all zero coefficients
            for (int col = row; col < variableCount; col++) {
                if (Math.abs(echelonMatrix.get(row, col)) > 1e-4) {
                    allZeroCoefficients = false;
                    break;
                }
            }

            for(int col=0;col<variableCount;col++){
                if(Math.abs(echelonMatrix.get(row, col)) > 1e-4){
                    firstColIdxNot0 = col;
                    break;
                }
            }


            // If all coefficients are zero but the RHS is not zero, it's inconsistent
            if (allZeroCoefficients && Math.abs(rhs) > 1e-4) {
                matrixSteps.addStep(String.format("All coefficient(s) on row %d are 0, but RHS is not 0", row + 1));
                throw new IllegalArgumentException("The system has no solution due to inconsistency.");
            }


            // If the first non-zero element is not on the diagonal, mark it as a free variable
            if (firstColIdxNot0 != row && firstColIdxNot0 != -1) {
                isFreeVariable[row] = true;
                for(int i=firstColIdxNot0+1;i<variableCount;i++){
                    if(!isVariablesCalculated[i]){
                        strSolution[i] = String.format("x%d (free variable)", i + 1);
                        matrixSteps.addStep(String.format("x%d is a free variable", i + 1));
                        isVariablesCalculated[i]=true;
                        isFreeVariable[i] = true; 
                    }
                }
            }
            
        
            if(firstColIdxNot0 != -1){
                isVariablesCalculated[firstColIdxNot0]=true;
            }

            // If the row has non-zero diagonal, perform back substitution
            StringBuilder equationBuilder = new StringBuilder(String.format("x%d = %.4f", firstColIdxNot0 + 1, rhs));
            double backSubstitutionValue = rhs;

            // Back substitute, finding dependencies on other variables
            for (int col = firstColIdxNot0 + 1; col < variableCount; col++) {
                double coefficient = echelonMatrix.get(row, col);
                
                if(coefficient != 0 ){
                    equationBuilder.append(String.format(" - (%.4f * (%s))", coefficient, strSolution[col]));
                    if(!isVariablesCalculated[col]){
                        isDependantVarCalculated[firstColIdxNot0]=false;
                        isVariablesCalculated[col]=true;
                        isFreeVariable[col] = true;
                        strSolution[col] = String.format("x%d (free variable)", col + 1);
                        matrixSteps.addStep(String.format("x%d is a free variable", col + 1));
                    }
                }

                if(!isFreeVariable[col] && coefficient != 0){
                    backSubstitutionValue -= coefficient * doubleSolution[col]; 
                }else if(firstColIdxNot0!=-1 && coefficient != 0){
                    isFreeVariable[firstColIdxNot0] = true;
                }
            }

            // Update both symbolic and numeric solutions
            if(firstColIdxNot0 != -1){
                strSolution[firstColIdxNot0] = equationBuilder.toString();
                doubleSolution[firstColIdxNot0] = backSubstitutionValue;
                
                matrixSteps.addStep(String.format("Final equation for x%d: %s", firstColIdxNot0 + 1, strSolution[firstColIdxNot0]));
                
                if(!isFreeVariable[firstColIdxNot0] && isDependantVarCalculated[firstColIdxNot0]){ 
                    strSolution[firstColIdxNot0] = String.format("x%d = %.4f",firstColIdxNot0+1, backSubstitutionValue);
                }
            }
        }

        for(int i=0;i<variableCount;i++){
            if(!isVariablesCalculated[i]){
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

    // Complete solve method using both steps
    public static String[] solve(Matrix augmentedMatrix) {
        matrixSteps = new MatrixSteps();
        matrixSteps.addStep("======================= Matrix given ======================");
        matrixSteps.addMatrixState(augmentedMatrix.getString());
        matrixSteps.addStep("\n======== Gaussian Elimination (Row Echelon Form) ========");
        Matrix echelonMatrix = getEchelon(augmentedMatrix);
        matrixSteps.addStep("\n=================== Back Substitution ===================");
        return getResultFromEchelon(echelonMatrix);
    }

    public static MatrixSteps getMatrixSteps() {
        return matrixSteps;
    }
}
