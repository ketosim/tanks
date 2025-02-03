package Tanks;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.event.KeyEvent;




public class AppTest {

    private App app;

    @BeforeEach
    public void setUp() {
        app = new App("config.json");
        PApplet.runSketch(new String[]{"App"}, app);
        app.setup();
        app.delay(1000);
        assertNotNull(app.getLevelManager(), "LevelManager should be initialized");
    }

    private void simulateKeyPress(char key) {
        KeyEvent keyEvent = new KeyEvent(app, System.currentTimeMillis(), KeyEvent.PRESS, 0, key, 0);
        app.keyPressed(keyEvent);
    }



    @Test
    public void testRestartGame() {
        simulateKeyPress('n');
        simulateKeyPress('n');
        simulateKeyPress('n');
        simulateKeyPress('r');
        assertEquals(0, app.getLevelManager().getCurrentLevelIndex());
    }

    @Test
    public void testNextLevel() {
        simulateKeyPress('n');
        assertEquals(1, app.getLevelManager().getCurrentLevelIndex());
    }

    @Test
    public void testGameOverDisplay() {
        while (!app.getLevelManager().isGameOver()) {
            app.getLevelManager().nextLevel();
        }
        
        assertTrue(app.getLevelManager().isGameOver(), "Game should be over after last level");
    }


}
