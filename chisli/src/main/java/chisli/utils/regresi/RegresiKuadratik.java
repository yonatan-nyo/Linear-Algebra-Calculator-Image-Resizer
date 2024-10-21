package chisli.utils.regresi;
import java.util.Arrays;

import chislib.matrix.Matrix;
import chislib.spl.Gauss;

public class RegresiKuadratik {
    private double[] coef; // Coefficients for the regression

    // Solve using Normal Estimation Equation for Multiple Linear Regression
    public double[] solve(double[][] xValues, double[] yValues) {
        int n = xValues.length; // Number of data points
        int m = xValues[0].length; // Number of features (x1, x2, ..., xm)
    
        // Add a column of 1's for the intercept (b0)
        double[][] augMtxArray = new double[n][m + 5]; // Augmented matrix with quadratic and interaction terms
        for (int i = 0; i < n; i++) {
            augMtxArray[i][0] = 1.0; // Intercept term
            augMtxArray[i][1] = xValues[i][0]; // Linear term x1
            augMtxArray[i][2] = xValues[i][1]; // Linear term x2
            augMtxArray[i][3] = xValues[i][0] * xValues[i][0]; // Quadratic term x1^2
            augMtxArray[i][4] = xValues[i][1] * xValues[i][1]; // Quadratic term x2^2
            augMtxArray[i][5] = xValues[i][0] * xValues[i][1]; // Interaction term x1 * x2
            augMtxArray[i][6] = yValues[i]; // Target y
        }

    
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
        if(count ==1){
            throw new IllegalArgumentException("Function cannot be calculated because only 1 unique point is provided.");
        }
        String[] solution = Gauss.solve(augMtx);
    
        // Store the solution in the instance variable `coef` for later use in predict
        this.coef = convertSolution(solution);
        
        return this.coef;
    }
    
    // Predict value for new input xk
    public double predict(double[] xk) {
        if (coef == null || xk.length != 2) {
            throw new IllegalArgumentException("Model has not been trained or invalid input size.");
        }
    
        // Prediction formula for quadratic regression
        double result = coef[0]; // Intercept term
        result += coef[1] * xk[0]; // Linear term x1
        result += coef[2] * xk[1]; // Linear term x2
        result += coef[3] * xk[0] * xk[0]; // Quadratic term x1^2
        result += coef[4] * xk[1] * xk[1]; // Quadratic term x2^2
        result += coef[5] * xk[0] * xk[1]; // Interaction term x1 * x2
    
        return result;
    }
    

    private double[] convertSolution(String[] solution) {
        int numVars = solution.length; // Number of variables
        double[] result = new double[numVars]; // Array to hold double solutions
    
        for (int i = 0; i < numVars; i++) {
            String sol = solution[i];
    
            // Check if the solution contains "free variable"
            if (sol.contains("free variable")) {
                // If there is a free variable, return an array of zeros
                Arrays.fill(result, 0.0);
                return result;
            }
    
            // Extract the numerical value from the solution string
            String[] parts = sol.split("="); // Split at '='
            if (parts.length > 1) {
                try {
                    // Parse the entire right-hand side of the equation (after '='), including negative numbers
                    String valueStr = parts[1].trim();
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
    
        return result;
    }    
}
