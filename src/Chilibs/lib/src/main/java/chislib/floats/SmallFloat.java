package chislib.floats;

public class SmallFloat {

    private static final double EPSILON = 1e-4; // Define a small threshold for comparison

    public static double handleMinus0(double val) {
        // Check if val is approximately -0.0, or between -EPSILON and EPSILON
        if (Math.abs(val) < EPSILON) {
            return 0.0;  
        }
    
        return val;  
    }
}
