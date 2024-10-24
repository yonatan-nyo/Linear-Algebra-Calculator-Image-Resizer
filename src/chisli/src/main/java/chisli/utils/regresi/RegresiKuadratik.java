package chisli.utils.regresi;
import chislib.matrix.Matrix;
import chislib.spl.Gauss;

public class RegresiKuadratik {
    private double[] coef; // Coefficients for the regression

    // Solve using Normal Estimation Equation for Multiple Linear Regression
    public double[] solve(double[][] xValues, double[] yValues) {
        int n = xValues.length; // Number of data points
        int m = xValues[0].length; // Number of features (x1, x2, ..., xm)
    
        // Calculate the size of the augmented matrix
        int XCols = 1 + m + m + (m * (m - 1)) / 2; // Intercept + linear terms + quadratic terms + interaction terms
        double[][] X = new double[n][XCols];

        double[][] Y = new double[n][1];
        for (int i = 0; i < n; i++) {
            Y[i][0] = yValues[i];
        }

        for (int i = 0; i < n; i++) {
            int colIndex = 0;
            X[i][colIndex++] = 1.0; // Intercept term

            // Linear terms
            for (int j = 0; j < m; j++) {
                X[i][colIndex++] = xValues[i][j];
            }

            // Quadratic terms
            for (int j = 0; j < m; j++) {
                X[i][colIndex++] = xValues[i][j] * xValues[i][j];
            }

            // Interaction terms
            for (int j = 0; j < m; j++) {
                for (int k = j + 1; k < m; k++) {
                    X[i][colIndex++] = xValues[i][j] * xValues[i][k];
                }
            }
        }
        
        // Transpose of matrix X (X^T)
        Matrix xMatrix = new Matrix(X);
        Matrix xTransposed = xMatrix.getTranspose();
    
        // X^T * X
        Matrix xTx = Matrix.multiply(xTransposed, xMatrix);
    
        // X^T * Y
        Matrix xTy = Matrix.multiply(xTransposed, new Matrix(Y));
    
        // Form the augmented matrix by combining (X^T * X) and (X^T * Y)
        double[][] augMtxArray = new double[XCols][XCols + 1]; // XCols for b coefficients, XCols + 1 for augmented column
        for (int i = 0; i < XCols; i++) {
            // Fill in (X^T * X)
            for (int j = 0; j < XCols; j++) {
                augMtxArray[i][j] = xTx.get(i, j);
            }
            // Append (X^T * Y) as the last column
            augMtxArray[i][XCols] = xTy.get(i, 0);
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

        // Store the solution in the instance variable `coef` for later use in predict
        this.coef = convertSolution(solution);
        
        return this.coef;
    }
    
    // Predict value for new input xk
    public double predict(double[] xk) {
        if (coef == null) {
            throw new IllegalArgumentException("Model has not been trained or invalid input size.");
        }
    
        // Prediction formula for quadratic regression
        double result = coef[0]; // Intercept term
        int coefIndex = 1;

        // Linear terms
        for (int i = 0; i < xk.length; i++) {
            result += coef[coefIndex++] * xk[i];
        }

        // Quadratic terms
        for (int i = 0; i < xk.length; i++) {
            result += coef[coefIndex++] * xk[i] * xk[i];
        }

        // Interaction terms
        for (int i = 0; i < xk.length; i++) {
            for (int j = i + 1; j < xk.length; j++) {
                result += coef[coefIndex++] * xk[i] * xk[j];
            }
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