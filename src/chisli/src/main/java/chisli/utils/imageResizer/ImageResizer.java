package chisli.utils.imageResizer;

import java.awt.image.BufferedImage;
import chisli.utils.bicubicSplineInterpolation.BicubicSplineInterpolation;

public class ImageResizer {
    public static BufferedImage resizeBufferedImage(BufferedImage originalImage, int newWidth, int newHeight) {
        long startTimeOrigin = System.currentTimeMillis();
    
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        
        // Choose a BufferedImage type that supports transparency (TYPE_INT_ARGB)
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        
        double[][] redChannel = new double[4][4];
        double[][] greenChannel = new double[4][4];
        double[][] blueChannel = new double[4][4];
        double[][] alphaChannel = new double[4][4];  // Add a channel for alpha (transparency)
    
        double widthRatio = (double) (originalWidth - 1) / (newWidth - 1);
        double heightRatio = (double) (originalHeight - 1) / (newHeight - 1);
        
        int lastX0 = -1;
        int lastY0 = -1;
        long startTime = System.currentTimeMillis();
    
        for (int y = 0; y < newHeight; y++) {
            double srcY = y * heightRatio;
            int y0 = (int) Math.floor(srcY);
            
            for (int x = 0; x < newWidth; x++) {
                double srcX = x * widthRatio;
                int x0 = (int) Math.floor(srcX);
    
                // Check if the pixel neighborhood has changed
                if (x0 != lastX0 || y0 != lastY0) {
                    // Populate the color channels for the 4x4 neighborhood, including alpha
                    populateColorChannels(originalImage, x0, y0, redChannel, greenChannel, blueChannel, alphaChannel, originalWidth, originalHeight);
                    lastX0 = x0;
                    lastY0 = y0;
                }
    
                // Interpolate for red, green, blue, and alpha channels
                BicubicSplineInterpolation redInterpolation = new BicubicSplineInterpolation(redChannel, false);
                BicubicSplineInterpolation greenInterpolation = new BicubicSplineInterpolation(greenChannel, false);
                BicubicSplineInterpolation blueInterpolation = new BicubicSplineInterpolation(blueChannel, false);
                BicubicSplineInterpolation alphaInterpolation = new BicubicSplineInterpolation(alphaChannel, false); // Alpha interpolation
    
                // Get interpolated values for each channel
                double redValue = redInterpolation.interpolate(srcX - x0, srcY - y0);
                double greenValue = greenInterpolation.interpolate(srcX - x0, srcY - y0);
                double blueValue = blueInterpolation.interpolate(srcX - x0, srcY - y0);
                double alphaValue = alphaInterpolation.interpolate(srcX - x0, srcY - y0);  // Alpha interpolation
    
                // Convert interpolated values to integers and clamp them
                int red = clamp((int) Math.round(redValue), 0, 255);
                int green = clamp((int) Math.round(greenValue), 0, 255);
                int blue = clamp((int) Math.round(blueValue), 0, 255);
                int alpha = clamp((int) Math.round(alphaValue), 0, 255);  // Clamp alpha
    
                // Combine the RGBA values and set the pixel in the resized image
                int rgba = (alpha << 24) | (red << 16) | (green << 8) | blue;
                resizedImage.setRGB(x, y, rgba);
            }
            
            if (y % 10 == 0 && y > 0) {
                long endTime = System.currentTimeMillis();
                System.out.println("Row " + y + "/" + newHeight + " time: " + (endTime - startTime) + " ms");
                startTime = System.currentTimeMillis();
            }
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("Total resizing time: " + (endTime - startTimeOrigin) + " ms");
        return resizedImage;
    }
    

// Populate color and alpha channels from the 4x4 neighborhood pixels
    private static void populateColorChannels(BufferedImage image, int x0, int y0, double[][] redChannel, double[][] greenChannel, double[][] blueChannel, double[][] alphaChannel, int width, int height) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int xi = clamp(x0 - 1 + i, 0, width - 1);
                int yj = clamp(y0 - 1 + j, 0, height - 1);
                int rgba = image.getRGB(xi, yj);
                
                redChannel[i][j] = (rgba >> 16) & 0xFF;
                greenChannel[i][j] = (rgba >> 8) & 0xFF;
                blueChannel[i][j] = rgba & 0xFF;
                alphaChannel[i][j] = (rgba >> 24) & 0xFF;  // Extract alpha channel
            }
        }
    }

    // Clamp function to ensure values stay within bounds
    private static int clamp(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }
}
