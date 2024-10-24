package chisli.utils.regresi;

import chislib.matrix.Matrix;
import chislib.spl.Gauss;

public class RegresiLinier {

    private double[] b; // Coefficients for the regression

    // Solve using Normal Estimation Equation for Multiple Linear Regression
    public double[] solve(double[][] xValues, double[] yValues) {
        int n = xValues.length; // Number of data points
        int m = xValues[0].length; // Number of features (x1, x2, ..., xm)
    
        // Create matrix X with an additional column for the intercept
        double[][] X = new double[n][m + 1];
        for (int i = 0; i < n; i++) {
            X[i][0] = 1.0; // Intercept term (b0)
            System.arraycopy(xValues[i], 0, X[i], 1, m); // Copy the features
        }
    
        // Convert yValues to a column vector
        double[][] Y = new double[n][1];
        for (int i = 0; i < n; i++) {
            Y[i][0] = yValues[i];
        }
    
        // Transpose of matrix X (X^T)
        Matrix xMatrix = new Matrix(X);
        Matrix xTransposed = xMatrix.getTranspose();
    
        // X^T * X
        Matrix xTx = Matrix.multiply(xTransposed, xMatrix);
    
        // X^T * Y
        Matrix xTy = Matrix.multiply(xTransposed, new Matrix(Y));
    
        // Form the augmented matrix by combining (X^T * X) and (X^T * Y)
        double[][] augMtxArray = new double[m + 1][m + 2]; // m+1 for b coefficients, m+2 for augmented column
        for (int i = 0; i < m + 1; i++) {
            // Fill in (X^T * X)
            for (int j = 0; j < m + 1; j++) {
                augMtxArray[i][j] = xTx.get(i, j);
            }
            // Append (X^T * Y) as the last column
            augMtxArray[i][m + 1] = xTy.get(i, 0);
        }
    
        // Convert augmented matrix to Matrix object and clean it
        Matrix augMtx = new Matrix(augMtxArray).getCleanedMatrix();
    

        // check how many lines are non all zero
        int count = 0;
        for (int i = 0; i < augMtx.getRowCount(); i++) {
            boolean allZero = true;
            for (int j = 0; j < augMtx.getColumnCount(); j++) {
                if (augMtx.get(i, j) != 0) {
                    allZero = false;
                    break;
                }
            }
            if (!allZero) {
                count++;
                if(count == augMtx.getColumnCount() - 1) {
                    break;
                }
            }
        }
        if(count == 1){
            throw new IllegalArgumentException("Function cannot be calculated because only 1 unique point is provided.");
        }

        // Use Gauss.solve to find the solution
        String[] solution = Gauss.solve(augMtx);
    
        // Convert solution from Gauss solver into double array
        this.b = convertSolution(solution);
    
        return this.b;
    }
    
    // Predict value for new input xk
    public double predict(double[] xk) {
        if (b == null || xk.length + 1 != b.length) {
            throw new IllegalArgumentException("Model has not been trained or invalid input size.");
        }

        double result = b[0]; // Intercept term
        for (int i = 0; i < xk.length; i++) {
            result += b[i + 1] * xk[i]; // Multiply by corresponding coefficients
        }

        return result;
    }

    private double[] convertSolution(String[] solution) {
        int numVars = solution.length; // Number of variables
        double[] result = new double[numVars]; // Array to hold double solutions
    
        for (int i = 0; i < numVars; i++) {
            String sol = solution[i];
    
            // Check if the solution contains "free variable"
            if (sol.contains("free variable")) {
                // If there is a free variable, set the current index to 0
                result[i] = 0.0;
            } else {
                // Extract the numerical value from the solution string
                String[] parts = sol.split("="); // Split at '='
                if (parts.length > 1) {
                    try {
                        // Parse the entire right-hand side of the equation (after '='), including negative numbers
                        String valueStr = parts[1].trim().split(" ")[0]; // Take only the first part after '='
                        double value = Double.parseDouble(valueStr);
                        result[i] = value; // Store the double value
                    } catch (NumberFormatException e) {
                        // Handle parsing error if the value is not a number
                        result[i] = 0.0;
                    }
                } else {
                    // If the solution format is unexpected, set to zero
                    result[i] = 0.0;
                }
            }
        }
    
        return result;
    }  
}