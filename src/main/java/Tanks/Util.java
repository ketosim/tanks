package Tanks;


import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

/**
 * Some helper functions.
 */

public class Util {
    /**
     * Parses an RGB string in the format "r,g,b" into an array of integers.
     * @param rgb The RGB string.
     * @return An array containing the red, green, and blue components.
     */
    public static int[] parseRGB(String rgb) {
        String[] values = rgb.split(",");
        int[] colors = new int[3]; 
        for (int i = 0; i < 3; i++) {
            colors[i] = Integer.parseInt(values[i].trim());
        }
        return colors;
    }

    /**
     * Creates a PVector from an angle in radians and scales it by a given factor.
     * @param angle The angle in radians.
     * @param magnitude The scaling factor.
     * @return A new PVector pointing in the direction of the angle, scaled by the magnitude.
     */
    public static PVector fromAngleAndMagnitude(float angle, float magnitude) {
        float x = PApplet.cos(angle) * magnitude;
        float y = PApplet.sin(angle) * magnitude;
        return new PVector(x, y);
    }

    /**
     * Maps a value from one range to another.
     * @param value The value to be scaled.
     * @param start1 The start of the first range.
     * @param stop1 The end of the first range.
     * @param start2 The start of the second range.
     * @param stop2 The end of the second range.
     * @return The mapped value.
     */
    public static float map(float value, float start1, float stop1, float start2, float stop2) {
        return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
    }

    /**
     * Updates the color of an image of pixels.
     * @param image The image to be updated.
     * @param color The new color.
     */
    public static void updateImageColor(PImage image, int color) {
        image.loadPixels();
        for (int i = 0; i < image.width; i++) {
            for (int j = 0; j < image.height; j++) {
                image.pixels[i + j * image.width] = color;
            }
        }
        image.updatePixels();
    }

    /**
     * Calculates the distance between two points.
     * @param p1 The first point.
     * @param p2 The second point.
     * @return The distance between the two points.
     */
    public static float distance(PVector p1, PVector p2) {
        float dx = p1.x - p2.x;
        float dy = p1.y - p2.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }


    
}
