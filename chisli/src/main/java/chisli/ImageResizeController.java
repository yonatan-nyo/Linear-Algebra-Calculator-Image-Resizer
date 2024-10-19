package chisli;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import chisli.utils.imageResizer.ImageResizer; // Use the ImageResizer class

public class ImageResizeController {
    @FXML
    private Canvas imageCanvas;

    @FXML
    private TextField widthField;

    @FXML
    private TextField heightField;

    @FXML
    private ProgressIndicator loadingIndicator; 

    private Image originalImage;
    private Image compressedImage; 

    @FXML
    public void initialize() {
        // Any initialization if needed
        setupContextMenu();
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
            drawImage(originalImage);
        }
    }

    @FXML
    private void resizeImage() {
        if (originalImage != null) {
            int newWidth = Integer.parseInt(widthField.getText());
            int newHeight = Integer.parseInt(heightField.getText());
    
            // Show loading indicator
            loadingIndicator.setVisible(true);
            new Thread(() -> {
                // Resize the image and store it in the compressedImage variable
                compressedImage = bicubicSplineInterpolationResize(originalImage, newWidth, newHeight);
    
                // Calculate the scaling factor to maintain a height of 700 pixels
                int factor = 700 / newHeight;
                int newCanvasHeight = newHeight * factor;
                int newCanvasWidth = newWidth * factor;
    
                // Update UI on the JavaFX Application Thread
                javafx.application.Platform.runLater(() -> {
                    // Adjust canvas size
                    imageCanvas.setWidth(newCanvasWidth);
                    imageCanvas.setHeight(newCanvasHeight);
    
                    // Draw the resized image on the canvas
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
        gc.drawImage(image, 0, 0, imageCanvas.getWidth(), imageCanvas.getHeight());
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
