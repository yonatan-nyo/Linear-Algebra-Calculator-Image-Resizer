package chisli.utils.interpolasiPolinomial;

import java.util.Arrays;
import java.util.List;

import chislib.matrix.Matrix;
import chislib.spl.Gauss;

public class InterpolasiPolinomial {

    // Method to perform polynomial interpolation using Gauss elimination
    public static List<String> solve(double[] xValues, double[] yValues, double xToEvaluate) {
        if (xValues.length != yValues.length) {
            throw new IllegalArgumentException("xValues and yValues must have the same length.");
        }

        int n = xValues.length;
        double[][] augmentedMatrix = new double[n][n + 1];

        // Build the augmented matrix for polynomial interpolation
        for (int i = 0; i < n; i++) {
            double xi = 1;
            for (int j = 0; j < n; j++) {
                augmentedMatrix[i][j] = xi;
                xi *= xValues[i]; // This builds the polynomial terms (1, x, x^2, ...)
            }
            augmentedMatrix[i][n] = yValues[i]; // Augmented column (y-values)
        }


        // Convert the augmented matrix into a Matrix object and solve it using Gauss
        Matrix matrix = new Matrix(augmentedMatrix).getCleanedMatrix();
        // check how many lines are non all zero
        int count = 0;
        for (int i = 0; i < matrix.getRowCount(); i++) {
            boolean allZero = true;
            for (int j = 0; j < matrix.getColumnCount(); j++) {
                if (matrix.get(i, j) != 0) {
                    allZero = false;
                    break;
                }
            }
            if (!allZero) {
                count++;
                if(count == matrix.getColumnCount() - 1) {
                    break;
                }
            }
        }
        if(count ==1){
            throw new IllegalArgumentException("Function cannot be calculated because only 1 unique point is provided.");
        }


        String[] solution = Gauss.solve(matrix);

        // Convert the solution to double array
        double[] coefficients = convertSolution(solution);

        // Build the polynomial string for degree n
        StringBuilder polynomial = new StringBuilder("f(x) = ");
        for (int i = 0; i < coefficients.length; i++) {
            if (i > 0 && coefficients[i] >= 0) {
                polynomial.append("+");
            }
            polynomial.append(String.format("%.4f", coefficients[i]));
            if (i == 1) {
                polynomial.append("x ");
            } else if (i > 1) {
                polynomial.append("x^").append(i).append(" ");
            }
        }
        
        if (Arrays.stream(coefficients).allMatch(coef -> coef == 0)) {
            polynomial = new StringBuilder("");
            return Arrays.asList("The system has free variables.", "");
        }

        // Use the coefficients to evaluate the polynomial at xToEvaluate
        double result = evaluatePolynomial(coefficients, xToEvaluate);
        return Arrays.asList(polynomial.toString(), String.format("f(%.4f) = %.4f", xToEvaluate, result));
    }

    // Helper method to evaluate the polynomial at a given x
    private static double evaluatePolynomial(double[] coefficients, double x) {
        double result = 0;
        double power = 1;
        for (int i = 0; i < coefficients.length; i++) {
            result += coefficients[i] * power;
            power *= x;
        }
        return result;
    }

    private static double[] convertSolution(String[] solution) {
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
