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

            // Set canvas size based on original image dimensions
            updateCanvasSize(originalImage.getWidth(), originalImage.getHeight());

            drawImage(originalImage); // This must be called after canvas is initialized
        } else {
            System.out.println("No file selected");
        }
    }
    

    @FXML
    private void resizeImage() {
        if (originalImage != null) {
            double widthResizeFactor = widthResizeFactorSlider.getValue(); // Get the current width slider value
            double heightResizeFactor = heightResizeFactorSlider.getValue(); // Get the current height slider value

            double newWidth = originalImage.getWidth() * widthResizeFactor;
            double newHeight = originalImage.getHeight() * heightResizeFactor;

            // Show loading indicator
            loadingIndicator.setVisible(true);
            new Thread(() -> {
                // Resize the image and store it in the compressedImage variable
                compressedImage = bicubicSplineInterpolationResize(originalImage, (int)newWidth, (int)newHeight);

                // Update UI on the JavaFX Application Thread
                javafx.application.Platform.runLater(() -> {
                    // Update canvas size based on new image dimensions
                    updateCanvasSize(newWidth, newHeight);
                    
                    // Draw the resized image with scaling on the canvas
                    drawImage(compressedImage); // Use compressedImage here

                    // Hide loading indicator
                    loadingIndicator.setVisible(false);
                });
            }).start(); // Start a new thread to avoid blocking the UI
        }
    }

    private void drawImage(Image image) {
        if (image == null) {
            System.err.println("Cannot draw image: image is null.");
            return; // Exit the method if the image is null
        }
    
        GraphicsContext gc = imageCanvas.getGraphicsContext2D();
        if (gc == null) {
            System.err.println("Cannot draw image: graphics context is null.");
            return; // Exit the method if the graphics context is null
        }
    
        // Clear the canvas
        gc.clearRect(0, 0, imageCanvas.getWidth(), imageCanvas.getHeight());
    
        // Validate canvas dimensions
        double canvasWidth = imageCanvas.getWidth();
        double canvasHeight = imageCanvas.getHeight();
        if (canvasWidth <= 0 || canvasHeight <= 0) {
            System.err.println("Cannot draw image: invalid canvas dimensions.");
            return; // Exit the method if the canvas dimensions are invalid
        }
    
        // Calculate scaling factor to fit the canvas size
        double scaleX = Math.min(canvasWidth / image.getWidth(), 1);
        double scaleY = Math.min(canvasHeight / image.getHeight(), 1);
        double scale = Math.min(scaleX, scaleY); // Maintain aspect ratio
    
        // Calculate the new width and height after scaling
        double newWidth = image.getWidth() * scale;
        double newHeight = image.getHeight() * scale;
    
        // Draw the image centered on the canvas
        double x = (canvasWidth - newWidth) / 2;
        double y = (canvasHeight - newHeight) / 2;
    
        gc.drawImage(image, x, y, newWidth, newHeight);
    }
    
    private void updateCanvasSize(double width, double height) {
        // Set the maximum width to 700
        double maxWidth = 700;
        
        // If the width exceeds the maximum width, scale down proportionally
        if (width > maxWidth) {
            double scaleFactor = maxWidth / width;
            width = maxWidth;
            height = height * scaleFactor; // Scale height to maintain aspect ratio
        }
        
        // Validate the dimensions before setting
        if (width <= 0 || height <= 0) {
            System.err.println("Invalid canvas dimensions: width = " + width + ", height = " + height);
            return; // Avoid setting invalid dimensions
        }
        
        // Set the canvas size with the adjusted width and height
        imageCanvas.setWidth(width);
        imageCanvas.setHeight(height);
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
