package Tanks;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import processing.core.PVector;
import processing.core.PImage;
import processing.core.PApplet;

public class UtilTest {

    // Test the parseRGB method
    @Test
    public void testParseRGB() {
        String rgb = "255, 128, 64";
        int[] colors = Util.parseRGB(rgb);
        assertArrayEquals(new int[]{255, 128, 64}, colors, "RGB parsing failed");
    }

    // Test the fromAngleAndMagnitude method
    @Test
    public void testFromAngleAndMagnitude() {
        float angle = (float) Math.PI / 4; // 45 degrees
        float magnitude = 10;
        PVector vector = Util.fromAngleAndMagnitude(angle, magnitude);
        assertEquals((float) Math.sqrt(50), vector.x, 0.01, "X component calculation failed");
        assertEquals((float) Math.sqrt(50), vector.y, 0.01, "Y component calculation failed");
    }

    // Test the map method
    @Test
    public void testMap() {
        float value = 5;
        float start1 = 0;
        float stop1 = 10;
        float start2 = 0;
        float stop2 = 100;
        float mappedValue = Util.map(value, start1, stop1, start2, stop2);
        assertEquals(50, mappedValue, "Value mapping failed");
    }

    // Test the updateImageColor method
    @Test
    public void testUpdateImageColor() {
        PApplet app = new PApplet();
        PImage image = app.createImage(10, 10, PImage.RGB);
        int color = app.color(255, 0, 0); // Red color

        Util.updateImageColor(image, color);

        image.loadPixels();
        for (int i = 0; i < image.pixels.length; i++) {
            assertEquals(color, image.pixels[i], "Image color update failed");
        }
    }

    // Test the distance method
    @Test
    public void testDistance() {
        PVector p1 = new PVector(0, 0);
        PVector p2 = new PVector(3, 4);
        float distance = Util.distance(p1, p2);
        assertEquals(5.0, distance, "Distance calculation failed");
    }
}
