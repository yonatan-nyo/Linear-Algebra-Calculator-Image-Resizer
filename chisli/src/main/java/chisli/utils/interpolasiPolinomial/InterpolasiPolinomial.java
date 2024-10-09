package chisli.utils.interpolasiPolinomial;

public class InterpolasiPolinomial {

    // Method to perform Lagrange interpolation
    public static double solve(double[] xValues, double[] yValues, double x) {
        if (xValues.length != yValues.length) {
            throw new IllegalArgumentException("xValues and yValues must have the same length.");
        }

        double result = 0.0;
        int n = xValues.length;

        // Lagrange interpolation formula
        for (int i = 0; i < n; i++) {
            double term = yValues[i];
            for (int j = 0; j < n; j++) {
                if (j != i) {
                    term *= (x - xValues[j]) / (xValues[i] - xValues[j]);
                }
            }
            result += term;
        }

        return result;
    }
}
