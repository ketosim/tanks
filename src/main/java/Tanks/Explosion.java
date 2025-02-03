package Tanks;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * The Explosion class handles the visual representation and timing of an explosion
 * in the game. It also provides methods to update and draw the explosion.
 */
public class Explosion {
    private PVector location;
    private float radius;
    private Level level;
    private float elapsedTime = 0;
    private float maxTime = 0.2f; 

    /**
     * Constructs an Explosion object.
     * 
     * @param app      The PApplet instance.
     * @param location The location of the explosion.
     * @param radius   The radius of the explosion.
     * @param level    The level in which the explosion occurs.
     */
    public Explosion(PApplet app, PVector location, float radius, Level level) {
        this.location = location;
        this.radius = radius;
        this.level = level;
    }

    /**
     * Updates the state of the explosion. This method should be called once per frame
     * to update the explosion's animation.
     */
    public void update() {
        // 1/30 = 0.0333 seconds
        // 6 frames for explosion to finish
        float oneFrameTime = 1.0f / App.FPS;
        elapsedTime += oneFrameTime;
        if (elapsedTime < maxTime) {
            drawExplosion(level.getApp());
        }
    }

    /**
     * Draws the explosion animation based on the elapsed time.
     * 
     * @param app The PApplet instance.
     */
    public void drawExplosion(PApplet app) {
        float progress = elapsedTime / maxTime;
        drawCircle(app, progress, radius, 255, 0, 0);    // Red
        drawCircle(app, progress, radius * 0.5f, 255, 165, 0); // Orange
        drawCircle(app, progress, radius * 0.2f, 255, 255, 0);  // Yellow
    }

    /**
     * Draws a circle representing a part of the explosion.
     * 
     * @param app        The PApplet instance.
     * @param progress   The progress of the explosion animation.
     * @param baseRadius The base radius of the explosion.
     * @param r          The red color component.
     * @param g          The green color component.
     * @param b          The blue color component.
     */
    private void drawCircle(PApplet app, float progress, float baseRadius, int r, int g, int b) {
        float actualRadius = baseRadius * progress;
        app.noStroke();
        app.fill(r, g, b);
        app.ellipse(location.x, location.y, actualRadius * 2, actualRadius * 2);
    }

    /**
     * Checks if the explosion animation is finished.
     * 
     * @return True if the explosion animation is finished, otherwise false.
     */
    public boolean isFinished() {
        return elapsedTime >= maxTime;
    }

    public float getElapsedTime(){
        return elapsedTime;
    }


public float getMaxTime() {
    return maxTime;
}

public float getRadius() {
    return radius;
}
}
