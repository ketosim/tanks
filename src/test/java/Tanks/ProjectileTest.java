package Tanks;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import processing.core.PApplet;
import processing.core.PVector;
import java.awt.Point;

class ProjectileTest {

    private PApplet app;
    private Projectile projectile;
    private int[] terrainHeights;

    @BeforeEach
    public void setup() {
        app = new PApplet() {
            public void settings() {
                size(800, 600);
            }
        };
        PApplet.runSketch(new String[]{"App"}, app);
        app.setup();

        PVector start = new PVector(50, 50);
        PVector initialVelocity = new PVector(5, -5);
        float wind = 0.5f;
        Tank tankOwner = new Tank(app, new Point(0, 0), "A");

        projectile = new Projectile(start, initialVelocity, wind, tankOwner);
        terrainHeights = new int[800];
        for (int i = 0; i < terrainHeights.length; i++) {
            terrainHeights[i] = app.height; // Flat ground
        }

        projectile.setImpactListener(location -> {
            System.out.println("Impact at: " + location);
        });
    }

    @Test
    public void testProjectileInitialPosition() {
        assertEquals(new PVector(50, 50), projectile.getPosition(), "Initial position should be (50, 50)");
    }

    @Test
    public void testProjectileMotion() {
        
        // Update the projectile for a few frames
        for (int i = 0; i < 5; i++) {
            projectile.update(terrainHeights);
        }

        // Calculate expected position
        PVector expectedPosition = new PVector(50, 50);
        PVector velocity = new PVector(5, -5);
        for (int i = 0; i < 5; i++) {
            velocity.y += 7.2f / App.FPS;
            velocity.x += 0.5f / App.FPS;
            expectedPosition.add(velocity.copy());
        }
        assertEquals(expectedPosition.y, projectile.getPosition().y, 0.1);

        assertEquals(expectedPosition.x, projectile.getPosition().x, 0.1);
    }

    @Test
    public void testProjectileImpact() {
        // Set terrain height to cause an impact
        terrainHeights[55] = 55;

        while (projectile.isActive()) {
            projectile.update(terrainHeights);
        }

        assertFalse(projectile.isActive(), "Projectile should be inactive after impact");
    }

    @Test
    public void testProjectileDisplay() {
        app.background(255);

        // Display the projectile
        projectile.display(app);

    }
}
