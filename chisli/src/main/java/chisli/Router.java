package chisli;

import java.io.IOException;
import javafx.fxml.FXML;

public class Router {
    @FXML
    public static void navigate(String page) throws IOException {
        App.setRoot(page);
    }

    @FXML
    public static void navigateToSistemPersamaanLinear() throws IOException {
        navigate("sistem-persamaan-linear");
    }

    @FXML
    public static void navigateToInterpolasiPolinomial() throws IOException {
        navigate("interpolasi-polinomial");
    }

    @FXML
    public static void navigateToRegresiBerganda() throws IOException {
        navigate("regresi-berganda");
    }

    @FXML
    public static void navigateToBicubicSplineInterpolation() throws IOException {
        navigate("bicubic-spline-interpolation");
    }

    @FXML
    public static void navigateToImageResize() throws IOException {
        navigate("image-resize");
    }
}