package chisli.utils.matrix;

import java.util.ArrayList;
import java.util.List;

public class MatrixSteps {
    private List<String> steps;

    public MatrixSteps() {
        steps = new ArrayList<>();
    }

    // Add a step to the list
    public void addStep(String step) {
        steps.add(step);
    }

    // Add the matrix state to the steps
    public void addMatrixState(String matrixState) {
        steps.add(matrixState);
    }

    // Get all the steps
    public List<String> getSteps() {
        return steps;
    }

    // Clear the steps
    public void clearSteps() {
        steps.clear();
    }
}
