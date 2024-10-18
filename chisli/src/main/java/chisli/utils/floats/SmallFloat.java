package chisli.utils.floats;

public class SmallFloat {

    public static double handleMinus0(double val) {
        // Check if val is exactly -0.0 or less than -0.0001
        if (Double.doubleToRawLongBits(val) == Double.doubleToRawLongBits(-0.0) || (-0.0001<val) && (val<0)) {
            return 0.0;  
        }
    
        return val;  
    }
}