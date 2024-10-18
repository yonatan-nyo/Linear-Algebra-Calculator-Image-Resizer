package chisli;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import chisli.utils.bicubicSplineInterpolation.BicubicSplineInterpolation; // Add this import statement

public class ImageResizeController {
    @FXML
    public void initialize() {
        // Any initialization if needed
    }

    @FXML
    private void switchToSistemPersamaanLinear() {
        try {
            Router.navigateToSistemPersamaanLinear(); // Change to the desired navigation method
        } catch (IOException e) {
            e.printStackTrace(); // Handle exception
        }
    }    
    @FXML
    private void switchToInterpolasiPolinomial() {
        try {
            Router.navigateToInterpolasiPolinomial(); // Change to the desired navigation method
        } catch (IOException e) {
            e.printStackTrace(); // Handle exception
        }
    }
    @FXML
    private void switchToRegresiBerganda() {
        try {
            Router.navigateToRegresiBerganda(); // Change to the desired navigation method
        } catch (IOException e) {
            e.printStackTrace(); // Handle exception
        }
    }
    @FXML
    private void switchToBicubicSplineInterpolation() {
        try {
            Router.navigateToBicubicSplineInterpolation(); // Change to the desired navigation method
        } catch (IOException e) {
            e.printStackTrace(); // Handle exception
        }
    }
    @FXML
    private void switchToImageResize() {
        try {
            Router.navigateToImageResize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Canvas imageCanvas;

    @FXML
    private TextField widthField;

    @FXML
    private TextField heightField;

    private Image originalImage;

    @FXML
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            originalImage = new Image(file.toURI().toString());
            drawImage(originalImage);
        }
    }

    @FXML
    private void resizeImage() {
        if (originalImage != null) {
            int newWidth = Integer.parseInt(widthField.getText());
            int newHeight = Integer.parseInt(heightField.getText());
            Image resizedImage = bicubicSplineInterpolationResize(originalImage, newWidth, newHeight);
            drawImage(resizedImage);
        }
    }

    private void drawImage(Image image) {
        GraphicsContext gc = imageCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, imageCanvas.getWidth(), imageCanvas.getHeight());
        gc.drawImage(image, 0, 0, imageCanvas.getWidth(), imageCanvas.getHeight());
    }

    private Image bicubicSplineInterpolationResize(Image image, int newWidth, int newHeight) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        BufferedImage resizedBufferedImage = resizeBufferedImage(bufferedImage, newWidth, newHeight);
        return SwingFXUtils.toFXImage(resizedBufferedImage, null);
    }

    private BufferedImage resizeBufferedImage(BufferedImage originalImage, int newWidth, int newHeight) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
        
        double[][] redChannel = new double[4][4];
        double[][] greenChannel = new double[4][4];
        double[][] blueChannel = new double[4][4];
        
        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                double srcX = (double) x * (originalWidth - 1) / (newWidth - 1);
                double srcY = (double) y * (originalHeight - 1) / (newHeight - 1);
                
                int x0 = (int) Math.floor(srcX);
                int y0 = (int) Math.floor(srcY);
                
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        int xi = Math.min(Math.max(x0 - 1 + i, 0), originalWidth - 1);
                        int yj = Math.min(Math.max(y0 - 1 + j, 0), originalHeight - 1);
                        int rgb = originalImage.getRGB(xi, yj);
                        redChannel[i][j] = (rgb >> 16) & 0xFF;
                        greenChannel[i][j] = (rgb >> 8) & 0xFF;
                        blueChannel[i][j] = rgb & 0xFF;
                    }
                }
                
                BicubicSplineInterpolation redInterpolation = new BicubicSplineInterpolation(redChannel);
                BicubicSplineInterpolation greenInterpolation = new BicubicSplineInterpolation(greenChannel);
                BicubicSplineInterpolation blueInterpolation = new BicubicSplineInterpolation(blueChannel);
                
                int red = (int) Math.round(redInterpolation.interpolate(srcX - x0, srcY - y0));
                int green = (int) Math.round(greenInterpolation.interpolate(srcX - x0, srcY - y0));
                int blue = (int) Math.round(blueInterpolation.interpolate(srcX - x0, srcY - y0));
                
                int rgb = (red << 16) | (green << 8) | blue;
                resizedImage.setRGB(x, y, rgb);
            }
        }
        
        return resizedImage;
    }
}
