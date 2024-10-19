package chisli;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import chisli.utils.imageResizer.ImageResizer; // Use the ImageResizer class

public class ImageResizeController {
    @FXML
    private Canvas imageCanvas;

    @FXML
    private Slider widthResizeFactorSlider; // New slider for width resizing factor

    @FXML
    private Label widthResizeFactorLabel; // Label to display the current width slider value

    @FXML
    private Slider heightResizeFactorSlider; // New slider for height resizing factor

    @FXML
    private Label heightResizeFactorLabel; // Label to display the current height slider value

    @FXML
    private ProgressIndicator loadingIndicator; 

    private Image originalImage;
    private Image compressedImage; 

    private static final int MAX_CANVAS_SIZE = 700; // Maximum size for the canvas

    @FXML
    public void initialize() {
        // Initialize sliders and labels
        setupContextMenu();

        // Update label whenever the width slider value changes
        widthResizeFactorSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            widthResizeFactorLabel.setText(String.format("%.1f", newValue.doubleValue()));
        });

        // Update label whenever the height slider value changes
        heightResizeFactorSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            heightResizeFactorLabel.setText(String.format("%.1f", newValue.doubleValue()));
        });
    }

    @FXML
    private void switchToSistemPersamaanLinear() {
        try {
            Router.navigateToSistemPersamaanLinear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToInterpolasiPolinomial() {
        try {
            Router.navigateToInterpolasiPolinomial();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToRegresiLinierBerganda() {
        try {
            Router.navigateToRegresiLinierBerganda();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToRegresiKuadratikBerganda() {
        try {
            Router.navigateToRegresiKuadratikBerganda();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToBicubicSplineInterpolation() {
        try {
            Router.navigateToBicubicSplineInterpolation();
        } catch (IOException e) {
            e.printStackTrace();
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
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            originalImage = new Image(file.toURI().toString());

            double maxHeight = 500;
            double maxWidth = 500;
            double factor = Math.min(maxWidth / originalImage.getWidth(), maxHeight / originalImage.getHeight());
            double newWidth = originalImage.getWidth() * factor;
            double newHeight = originalImage.getHeight() * factor;

            imageCanvas.setWidth(newWidth);
            imageCanvas.setHeight(newHeight);

            // Draw the original image with scaling on the canvas
            drawImage(originalImage);
        }
    }

    @FXML
    private void resizeImage() {
        if (originalImage != null) {
            double widthResizeFactor = widthResizeFactorSlider.getValue(); // Get the current width slider value
            double heightResizeFactor = heightResizeFactorSlider.getValue(); // Get the current height slider value

            double newWidth = originalImage.getWidth() * widthResizeFactor;
            double newHeight = originalImage.getHeight() * heightResizeFactor;
            double maxHeight = 500;
            double maxWidth = 500;
            double factor = Math.min(maxWidth / newWidth, maxHeight / maxHeight);
            double newCanvasWidth = newWidth * factor;
            double newCanvasHeight = newHeight * factor;

            // Show loading indicator
            loadingIndicator.setVisible(true);
            new Thread(() -> {
                // Resize the image and store it in the compressedImage variable
                compressedImage = bicubicSplineInterpolationResize(originalImage, (int)newWidth, (int)newHeight);

                System.out.println((int)newWidth + " " + (int)newHeight);
                System.out.println(newCanvasWidth + " " + newCanvasHeight);
                
                // Update UI on the JavaFX Application Thread
                javafx.application.Platform.runLater(() -> {
                    // Draw the resized image with scaling on the canvas
                    imageCanvas.setWidth(newCanvasWidth);
                    imageCanvas.setHeight(newCanvasHeight);
                    
                    drawImage(compressedImage); // Use compressedImage here

                    // Hide loading indicator
                    loadingIndicator.setVisible(false);
                });
            }).start(); // Start a new thread to avoid blocking the UI
        }
    }

    private void drawImage(Image image) {
        GraphicsContext gc = imageCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, imageCanvas.getWidth(), imageCanvas.getHeight());

        // Calculate scaling factor to fit the canvas size
        double scaleX = Math.min(MAX_CANVAS_SIZE / image.getWidth(), 1);
        double scaleY = Math.min(MAX_CANVAS_SIZE / image.getHeight(), 1);
        double scale = Math.min(scaleX, scaleY); // Maintain aspect ratio

        // Calculate the new width and height after scaling
        double newWidth = image.getWidth() * scale;
        double newHeight = image.getHeight() * scale;

        // Draw the image centered on the canvas
        double x = (imageCanvas.getWidth() - newWidth) / 2;
        double y = (imageCanvas.getHeight() - newHeight) / 2;

        gc.drawImage(image, x, y, newWidth, newHeight);
    }

    private Image bicubicSplineInterpolationResize(Image image, int newWidth, int newHeight) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        BufferedImage resizedBufferedImage = ImageResizer.resizeBufferedImage(bufferedImage, newWidth, newHeight);
        return SwingFXUtils.toFXImage(resizedBufferedImage, null);
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem saveImageItem = new MenuItem("Save Image");

        saveImageItem.setOnAction(e -> saveImage());
        contextMenu.getItems().add(saveImageItem);

        imageCanvas.setOnContextMenuRequested(e -> {
            contextMenu.show(imageCanvas, e.getScreenX(), e.getScreenY());
        });
    }

    private void saveImage() {
        if (compressedImage != null) { // Check if there is a compressed image
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
            File file = fileChooser.showSaveDialog(new Stage());
    
            if (file != null) {
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(compressedImage, null); // Use compressedImage
    
                // Show loading indicator during save operation
                loadingIndicator.setVisible(true);
                new Thread(() -> {
                    try {
                        javax.imageio.ImageIO.write(bufferedImage, "png", file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        // Hide loading indicator after saving
                        javafx.application.Platform.runLater(() -> loadingIndicator.setVisible(false));
                    }
                }).start(); // Start a new thread to avoid blocking the UI
            }
        } else {
            // Optionally, notify the user that there is no image to save
            System.out.println("No compressed image available to save.");
        }
    }
}
