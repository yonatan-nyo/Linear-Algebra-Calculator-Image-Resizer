package chisli.utils.interpolasiPolinomial;

import chisli.utils.matrix.Matrix;
import chisli.utils.spl.Gauss;

public class InterpolasiPolinomial {

    // Method to perform polynomial interpolation using Gauss elimination
    public static String solve(double[] xValues, double[] yValues, double xToEvaluate) {
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
        Matrix matrix = new Matrix(augmentedMatrix);
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

        // Use the coefficients to evaluate the polynomial at xToEvaluate
        double result = evaluatePolynomial(coefficients, xToEvaluate);
        return polynomial.toString() + ", f(" + xToEvaluate + ") = " + result;
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

    // Helper method to convert the solution to a double array
    private static double[] convertSolution(String[] solution) {
        int numVars = solution.length;
        double[] result = new double[numVars];

        for (int i = 0; i < numVars; i++) {
            String[] parts = solution[i].split("=");
            if (parts.length > 1) {
                result[i] = Double.parseDouble(parts[1].trim());
            } else {
                result[i] = 0.0;
            }
        }
        return result;
    }
}
