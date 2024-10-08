package chisli.utils.floats;

public class SmallFloat {
    public static double  handleMinus0(double  val) {
        if (val > -1e-4 && val <=0) {
            return -1*val;
        }
        return val;
    }
}
