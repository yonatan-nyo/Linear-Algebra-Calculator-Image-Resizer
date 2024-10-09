package chisli.utils.floats;

public class SmallFloat {

    public static double handleMinus0(double val) {
        // Check if val is -0.0 or a small negative value close to -0
        if (val < 0 && val > -1e-2) {
            return -1 * val;  // Convert it to a positive value
        }

        return val;  // Return the original value if it's not a small negative value
    }
}
