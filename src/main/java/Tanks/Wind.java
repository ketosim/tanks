package Tanks;

import java.util.Random;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

/**
 * The Wind class simulates wind effects in the game, including random wind changes
 * and displaying the current wind direction and strength.
 */
public class Wind {
    private float currentWind;
    private static final float MAX_WIND = 35.0f;
    private static final float MIN_WIND = -35.0f;
    private static final float WIND_CHANGE_LIMIT = 5.0f; 
    private static final float WIND_FORCE_SCALING = 0.03f; 

    private PImage windLeft;
    private PImage windRight;
    private Random random;

    /**
     * Constructs a Wind object and initializes the wind direction and images.
     * 
     * @param app The PApplet instance used to load images.
     */
    public Wind(PApplet app) {
        random = new Random();
        currentWind = randomBlow(MIN_WIND, MAX_WIND);

        windLeft = app.loadImage("Tanks/wind-1.png");
        windRight = app.loadImage("Tanks/wind.png");
        windLeft.resize(App.CELLAVG, App.CELLAVG);
        windRight.resize(App.CELLAVG, App.CELLAVG);
    }

    /**
     * Generates a random float between the specified minimum and maximum values.
     * 
     * @param min The minimum value.
     * @param max The maximum value.
     * @return A random float between min and max.
     */
    private float randomBlow(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    /**
     * Updates the wind direction and strength with a random change.
     */
    public void updateWind() {
        float change = randomBlow(-WIND_CHANGE_LIMIT, WIND_CHANGE_LIMIT);
        currentWind += change;
        currentWind = Math.max(MIN_WIND, Math.min(MAX_WIND, currentWind));
    }

    /**
     * Gets the current wind value scaled by the wind force scaling factor.
     * 
     * @return The current wind value.
     */
    public float getCurrentWind() {
        return currentWind * WIND_FORCE_SCALING;
    }

    /**
     * Gets the appropriate wind image based on the current wind direction.
     * 
     * @return The PImage representing the wind direction.
     */
    public PImage getWindImage() {
        return currentWind < 0 ? windLeft : windRight;
    }

    /**
     * Displays the wind direction and strength on the screen.
     * 
     * @param app The PApplet instance used for drawing.
     * @param x The x-coordinate where the wind information is displayed.
     * @param y The y-coordinate where the wind information is displayed.
     */
    public void display(PApplet app, float x, float y) {
        PImage img = getWindImage();

        app.imageMode(PConstants.CORNER);
        app.image(img, x, y);

        app.textAlign(PConstants.LEFT, PConstants.TOP);
        app.fill(0);
        app.textSize(18);
        app.text(Math.round(currentWind), x + img.width + 6, y + 2); 
    }
}
