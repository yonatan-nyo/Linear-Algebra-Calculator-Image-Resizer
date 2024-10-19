package chisli.utils.regresi;
import java.util.Arrays;

import chisli.utils.matrix.Matrix;
import chisli.utils.spl.Gauss;

public class RegresiLinier {

    private double[] b; // Coefficients for the regression

    // Solve using Normal Estimation Equation for Multiple Linear Regression
    public double[] solve(double[][] xValues, double[] yValues) {
        System.out.println("xValues: " + Arrays.deepToString(xValues));
        System.out.println("yValues: " + Arrays.toString(yValues));
        int n = xValues.length; // Number of data points
        int m = xValues[0].length; // Number of features (x1, x2, ..., xm)
    
        // Add a column of 1's for the intercept (b0)
        double[][] augMtxArray = new double[n][m + 2];
        for (int i = 0; i < n; i++) {
            augMtxArray[i][0] = 1.0; // Intercept term
            augMtxArray[i][m+1] = yValues[i];
            System.arraycopy(xValues[i], 0, augMtxArray[i], 1, m); // Copy the rest of the features
        }
        System.out.println("Augmented Matrix: " + Arrays.deepToString(augMtxArray));
    
        Matrix augMtx = new Matrix(augMtxArray);
        augMtx.print();
        String[] solution = Gauss.solve(augMtx);
        System.out.println("solution: " + Arrays.toString(solution));
    
        // Store the solution in the instance variable `b` for later use in predict
        this.b = convertSolution(solution);
        System.out.println("b values: " + Arrays.toString(this.b));
        
        return this.b;
    }
    
    // Predict value for new input xk
    public double predict(double[] xk) {
        System.out.println("b values(for prediction): " + Arrays.toString(b));
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
