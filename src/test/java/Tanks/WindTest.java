package Tanks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import processing.core.PApplet;
import processing.core.PImage;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class WindTest {
    private Wind wind;
    private PApplet mockApplet;

    @BeforeEach
    public void setup() {
        mockApplet = Mockito.mock(PApplet.class);

        PImage dummyImage = new PImage(32, 32);

        when(mockApplet.loadImage(anyString())).thenReturn(dummyImage);

        wind = new Wind(mockApplet);
    }

    @Test
    public void testInitialWindValue() {
        float currentWind = wind.getCurrentWind();
        assertTrue(currentWind >= -1.05f && currentWind <= 1.05f, "Initial wind should be within limits");
    }

    @Test
    public void testUpdateWindWithinLimits() {
        for (int i = 0; i < 100; i++) {
            wind.updateWind();
            float updatedWind = wind.getCurrentWind();
            assertTrue(updatedWind >= -1.05f && updatedWind <= 1.05f, "Updated wind should be within limits");
        }
    }

    @Test
    public void testWindChange() {
        float initialWind = wind.getCurrentWind();
        wind.updateWind();
        float updatedWind = wind.getCurrentWind();
        assertTrue(updatedWind != initialWind, "Wind should change after updateWind()");
    }
}
