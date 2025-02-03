package Tanks;

/**
 * The TerrainPixel class represents a single pixel of the terrain in the game.
 * It contains information about the height and color of the pixel, which can 
 * represent different types of terrain or damage states.
 */
public class TerrainPixel {
    private int height; // Height of this terrain pixel
    private int color;  // Color can represent different types of terrain or damage states

    /**
     * Constructs a TerrainPixel with the specified height and color.
     * 
     * @param height The height of the terrain pixel.
     * @param color The color of the terrain pixel.
     */
    public TerrainPixel(int height, int color) {
        this.height = height;
        this.color = color;
    }

    /**
     * Gets the height of this terrain pixel.
     * 
     * @return The height of the terrain pixel.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the height of this terrain pixel.
     * 
     * @param height The new height of the terrain pixel.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Gets the color of this terrain pixel.
     * 
     * @return The color of the terrain pixel.
     */
    public int getColor() {
        return color;
    }

    /**
     * Sets the color of this terrain pixel.
     * 
     * @param color The new color of the terrain pixel.
     */
    public void setColor(int color) {
        this.color = color;
    }
}
