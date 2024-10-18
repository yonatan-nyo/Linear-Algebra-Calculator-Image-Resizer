package chisli.utils.bicubicSplineInterpolation;

public class bicubic {
    private double[][] matrix;

    public bicubic(double[][] matrix) {
        if (matrix.length != 4 || matrix[0].length != 4) {
            throw new IllegalArgumentException("Input matrix must be 4x4.");
        }
        this.matrix = matrix;
    }

    // Interpolate at point (x, y) using bicubic interpolation
    public double interpolate(double x, double y) {
        // Assuming x and y are in the range [0, 3] (4x4 matrix)

        // Get integer parts of x and y (the surrounding grid points)
        int x0 = (int) Math.floor(x);
        int y0 = (int) Math.floor(y);
        
        if(x < 0 || x > 1 || y < 0 || y > 1) {
            throw new IllegalArgumentException("inputted x and y must be in range 0..1");
        }
        
        // Ensure we don't go out of bounds
        x0 = Math.max(0, Math.min(x0, 1)); // Keep x0 between 0 and 1
        y0 = Math.max(0, Math.min(y0, 1)); // Keep y0 between 0 and 1

        // Get the four surrounding points for bicubic interpolation
        double[][] grid = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                // Make sure we stay within bounds of the 4x4 matrix
                grid[i][j] = matrix[Math.min(y0 + i, 3)][Math.min(x0 + j, 3)];
            }
        }

        // Perform cubic interpolation in the x direction
        double[] temp = new double[4];
        for (int i = 0; i < 4; i++) {
            temp[i] = cubicInterpolate(grid[i][0], grid[i][1], grid[i][2], grid[i][3], x - x0);
        }

        // Perform cubic interpolation in the y direction using the results from x
        return cubicInterpolate(temp[0], temp[1], temp[2], temp[3], y - y0);
    }

    // Cubic interpolation helper function
    private double cubicInterpolate(double v0, double v1, double v2, double v3, double fraction) {
        return v1 + 0.5 * fraction * (v2 - v0 + fraction * (2.0 * v0 - 5.0 * v1 + 4.0 * v2 - v3 + fraction * (3.0 * (v1 - v2) + v3 - v0)));
    }
}
