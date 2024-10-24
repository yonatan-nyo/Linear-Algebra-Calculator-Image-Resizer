package chisli.utils.bicubicSplineInterpolation;

import chislib.matrix.Matrix;
import chislib.matrix.MatrixSteps;
import chislib.spl.GaussJordan;

public class BicubicSplineInterpolation {
    private double[][] matrix;
    private MatrixSteps matrixSteps;
    private boolean trackSteps;

    // Constructor with optional step tracking (default is true)
    public BicubicSplineInterpolation(double[][] matrix) {
        this(matrix, true);
    }

    // Constructor with step tracking parameter
    public BicubicSplineInterpolation(double[][] matrix, boolean trackSteps) {
        if (matrix.length != 4 || matrix[0].length != 4) {
            throw new IllegalArgumentException("Input matrix must be 4x4.");
        }
        this.matrix = matrix;
        this.trackSteps = trackSteps;
        if (trackSteps) {
            this.matrixSteps = new MatrixSteps();
        }
    }

    private void populatePowers(double[][][] xPowers, double[][][] yPowers) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                double xPower = 1;
                double yPower = 1;
                xPowers[i][j][0] = 1;
                yPowers[i][j][0] = 1;
                for (int k = 1; k < 4; k++) {
                    xPower *= i;
                    yPower *= j;
                    xPowers[i][j][k] = xPower;
                    yPowers[i][j][k] = yPower;
                }
            }
        }
    }

    // Interpolate at point (x, y) using bicubic interpolation
    public double interpolate(double x, double y) {
        if (x < 0 || x > 1 || y < 0 || y > 1) {
            throw new IllegalArgumentException("inputted x and y must be in range 0..1");
        }

        int row = 0;
        double[][] mtx = new double[16][17];

        double[][][] xPowers = new double[2][2][4];
        double[][][] yPowers = new double[2][2][4];
        populatePowers(xPowers, yPowers);
        
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                eq(j, i, row, mtx, xPowers[i][j], yPowers[i][j]);
                row++;
            }
        }
        getY(matrix, mtx);

        // If step tracking is enabled, add steps
        if (trackSteps) {
            matrixSteps.addStep("X matrix:");
            double[][] subMatrix = getSubMatrix(mtx, 16);
            matrixSteps.addMatrixState(new Matrix(subMatrix).getString());

            matrixSteps.addStep("y matrix:");
            double[][] column17 = getColumn(mtx, 16);
            matrixSteps.addMatrixState(new Matrix(column17).getString());
        }
        
        Matrix mtxMatrix = new Matrix(mtx);
        double[] doubleSolution = GaussJordan.solveWithoutSteps(mtxMatrix);

        double[][] mtxa = new double[4][4];
        int counter = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                mtxa[j][i] = doubleSolution[counter++];
            }
        }

        if (trackSteps) {
            matrixSteps.addStep("Matrix a solution (solved with gauss):");
            matrixSteps.addMatrixState(new Matrix(mtxa).getString());
        }

        double answer = 0;
        double[] xPowersa = new double[4];
        double[] yPowersa = new double[4];
        xPowersa[0] = 1;
        yPowersa[0] = 1;
        for (int i = 1; i < 4; i++) {
            xPowersa[i] = xPowersa[i - 1] * x;
            yPowersa[i] = yPowersa[i - 1] * y;
        }
        
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                answer += mtxa[i][j] * (xPowersa[j] * yPowersa[i]);
            }
        }

        if (trackSteps) {
            matrixSteps.addStep("Final interpolated answer: " + answer);
        }

        return answer;
    }

    private void eq(int x, int y, int row, double[][] matrix, double[] xPowers, double[] yPowers) {
        int k = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrix[row][k] = xPowers[j] * yPowers[i];
                if (j != 0) {
                    matrix[row+4][k] = j * xPowers[j - 1] * yPowers[i];
                }
                if (i != 0) {
                    matrix[row+8][k] = i * xPowers[j] * yPowers[i - 1];
                }
                if (i != 0 && j != 0) {
                    matrix[row+12][k] = i * j * xPowers[j - 1] * yPowers[i - 1];
                }
                k += 1;
            }
        }
    }

    private void getY(double[][] matrix1, double[][] matrix2) {
        int i = 0;
        for(int j=1;j<3;j++){
            for(int k=1;k<3;k++){
                matrix2[i][16] = matrix1[k][j];
                i += 1;
            }
        }
        for(int j=1;j<3;j++){
            for(int k=1;k<3;k++){
                matrix2[i][16] = (matrix1[k+1][j]-matrix1[k-1][j])/2;
                i += 1;
            }
        }
        for(int j=1;j<3;j++){
            for(int k=1;k<3;k++){
                matrix2[i][16] = (matrix1[k][j+1]-matrix1[k][j-1])/2;
                i += 1;
            }
        }
        for(int j=1;j<3;j++){
            for(int k=1;k<3;k++){
                matrix2[i][16] = (matrix1[k+1][j+1]-matrix1[k-1][j]-matrix1[k][j-1]-matrix1[k][j])/4;
                i += 1;
            }
        }
    }

    private double[][] getSubMatrix(double[][] matrix, int endColumn) {
        int rows = matrix.length;
        double[][] subMatrix = new double[rows][endColumn];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(matrix[i], 0, subMatrix[i], 0, endColumn);
        }
        return subMatrix;
    }

    private double[][] getColumn(double[][] matrix, int column) {
        int rows = matrix.length;
        double[][] columnMatrix = new double[rows][1];
        for (int i = 0; i < rows; i++) {
            columnMatrix[i][0] = matrix[i][column];
        }
        return columnMatrix;
    }

    public MatrixSteps getMatrixSteps() {
        return matrixSteps;
    }
}
