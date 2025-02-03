package Tanks;

import processing.core.PVector;
import processing.core.PApplet;

/**
 * The ImpactListener interface defines a callback method for handling the
 * impact event of a projectile.
 */
interface ImpactListener {
    void onImpact(PVector location);
}

/**
 * The Projectile class represents a projectile fired by a tank. It handles
 * the physics of the projectile's motion, including gravity and wind effects,
 * and checks for collisions with the terrain.
 */
public class Projectile {
    private PVector position;
    private PVector velocity;
    private float gravity = 7.2f / App.FPS; 
    private float wind; 
    private Tank tankOwner;
    private boolean isActive = true;  
    private ImpactListener impactListener; 


    /**
     * Constructs a new Projectile object.
     * 
     * @param start The starting position of the projectile.
     * @param initialVelocity The initial velocity of the projectile.
     * @param wind The wind effect on the projectile.
     * @param owner The tank that fired the projectile.
     */
    public Projectile(PVector start, PVector initialVelocity, float wind, Tank owner) {
        this.position = start.copy();
        this.velocity = initialVelocity.copy();
        this.wind = wind;
        this.tankOwner = owner; 
 
    }

    /**
     * Sets the impact listener for the projectile.
     * 
     * @param listener The impact listener to be set.
     */
    public void setImpactListener(ImpactListener listener) {
        this.impactListener = listener;
    }

    /**
     * Checks if the projectile is active.
     * 
     * @return True if the projectile is active, otherwise false.
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Updates the projectile's position based on its velocity, gravity, and wind effects.
     * Checks for collisions with the terrain and triggers the impact event if necessary.
     * 
     * @param terrainHeights The heights of the terrain at different x-coordinates.
     */
    public void update(int[] terrainHeights) {
        if (!isActive) return;

        // Apply gravity and wind effects to the velocity
        velocity.y += gravity;
        velocity.x += wind / App.FPS;

        // Update projectile position
        position.add(velocity);

        // Check if the projectile's x-coordinate is within the bounds of the terrain array
        int terrainIndex = (int) position.x;
        if (terrainIndex >= 0 && terrainIndex < terrainHeights.length) {
            if (position.y >= terrainHeights[terrainIndex]) {
                if (impactListener != null) {
                    impactListener.onImpact(new PVector(position.x, position.y));
                }
                isActive = false;  // Deactivate the projectile since it hit the terrain
            }
        }
    }

    public PVector getPosition(){
        return position;
    }

    /**
     * Displays the projectile on the screen.
     * 
     * @param app The PApplet instance used for drawing.
     */
    public void display(PApplet app) {
        if (!isActive) return;
        int tankColor = app.color(tankOwner.getColor()[0], tankOwner.getColor()[1], tankOwner.getColor()[2]);
        app.fill(tankColor);
        app.stroke(0);
        app.strokeWeight(1);
        app.ellipse(position.x, position.y, 5, 5);  // Draw projectile as a simple circle with players color
    }
}
