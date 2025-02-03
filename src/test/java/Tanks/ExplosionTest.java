package Tanks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import processing.core.PApplet;
import processing.core.PVector;

class ExplosionTest {
    private Explosion explosion;
    private PApplet mockApplet;
    private Level mockLevel;

    @BeforeEach
    public void setup() {
        mockApplet = mock(PApplet.class);
        mockLevel = mock(Level.class);
        when(mockLevel.getApp()).thenReturn(mockApplet);

        PVector location = new PVector(100, 100);
        float radius = 50;
        explosion = new Explosion(mockApplet, location, radius, mockLevel);
    }

    @Test
    public void testInitialProgress() {
        assertFalse(explosion.isFinished());
    }

    @Test
    public void testUpdateProgress() {
        explosion.update();
        float expectedProgress = 1.0f / App.FPS / 0.2f; // one frame progress
        assertEquals(expectedProgress, explosion.getElapsedTime() / 0.2f, 0.001, "Elapsed time should be updated correctly.");
    }

    @Test
    public void testExplosionCompletion() {
        for (int i = 0; i < App.FPS * 0.2f; i++) { // run updates for the duration of the explosion
            explosion.update();
        }
        assertTrue(explosion.isFinished(), "Explosion should be finished after max time.");
    }

    
}