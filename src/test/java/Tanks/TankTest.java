package Tanks;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import processing.core.PApplet;

public class TankTest {

    private PApplet app;
    private Tank tank;
    private static final int INITIAL_HEALTH = 100;
    private static final int INITIAL_POWER = 50;
    private static final int INITIAL_FUEL = 250;
    private static final Point START_POSITION = new Point(100, 50);
    private static final String TANK_NAME = "Tank1";
    private static final int INITIAL_PARACHUTES = 3;
    private static final double TURRET_SPEED = 3.0;

    @BeforeEach
    public void setUp() {
        app = new PApplet();
        PApplet.runSketch(new String[]{"App"}, app);
        app.setup();

        // Simulate images loading
        app.loadImage("Tanks/fuel.png");
        app.loadImage("Tanks/parachute.png");

        tank = new Tank(app, START_POSITION, TANK_NAME);
    }

    @Test
    public void testInitialValues() {
        assertEquals(INITIAL_HEALTH, tank.getHealth());
        assertEquals(INITIAL_POWER, tank.getPower());
        assertEquals(INITIAL_FUEL, tank.getFuel());
        assertEquals(TANK_NAME, tank.getName());
        assertEquals(START_POSITION, tank.getPosition());
    }

    @Test
    public void testMove() {
        int initialX = tank.getPosition().x;
        int moveDistance = 10;

        tank.move(moveDistance);
        assertEquals(initialX + moveDistance, tank.getPosition().x);
        assertEquals(INITIAL_FUEL - moveDistance, tank.getFuel());
    }



    @Test
    public void testAdjustPower() {
        int initialPower = tank.getPower();

        tank.adjustPower(10);
        assertEquals(initialPower + 10, tank.getPower());

        tank.adjustPower(-20);
        assertEquals(initialPower - 10, tank.getPower());
    }

    @Test
    public void testStartFalling() {
        tank.startFalling();
        assertTrue(tank.isFalling());
    }

    @Test
    public void testStopFalling() {
        tank.startFalling();
        tank.stopFalling();
        assertFalse(tank.isFalling());
    }

    @Test
    public void testUpdateFalling() {
        int[] terrainHeights = new int[200];
        terrainHeights[100] = 150; 

        tank.startFalling();
        tank.getParachutes().useParachute();//with parachut
        tank.update(app, 1/App.FPS, terrainHeights);
        assertEquals(100,tank.getHealth());
        assertEquals(INITIAL_PARACHUTES-1, tank.getParachutes().getCount());
        tank.startFalling();
        tank.getParachutes().resetDeployment();;//no parachut
        tank.update(app, 1/App.FPS, terrainHeights);
        assertEquals(96,tank.getHealth());
        assertEquals(INITIAL_PARACHUTES-2, tank.getParachutes().getCount());
        //initial health 100, 120p/second falling, 120/30fps = 4health/frame lost 
    }

    
    @Test
    public void testChangeColor() {
        tank.changeColor(app, 255, 0, 0);
        int[] color = tank.getColor();
        assertEquals(255, color[0]);
        assertEquals(0, color[1]);
        assertEquals(0, color[2]);
    }

    @Test
    public void testSetNextShotLarge() {
        tank.setNextShotLarge(true);
        assertTrue(tank.isNextShotLarge());

        tank.setNextShotLarge(false);
        assertFalse(tank.isNextShotLarge());
    }

    @Test
    public void testSetDestroyed() {
        tank.setDestroyed(true);
        assertTrue(tank.isDestroyed());

        tank.setDestroyed(false);
        assertFalse(tank.isDestroyed());
    }

    @Test
    public void testAdjustTurretRotateLeft() {
        double initialAngle = tank.getTurretAngle();
        tank.adjustTurret(false);  // Rotate left
        double newAngle =tank.getTurretAngle();

        double expectedAngle = initialAngle + (TURRET_SPEED / App.FPS);
        newAngle -= Math.PI * 2;

        assertEquals(expectedAngle, newAngle, 1e-9, "Turret angle should be adjusted to the left correctly");
    }

    @Test
    public void testDisplayUI() {
        app = new PApplet();
        PApplet.runSketch(new String[]{this.getClass().getSimpleName()}, app);
        // Simulate images loading
        app.loadImage("Tanks/fuel.png");
        app.loadImage("Tanks/parachute.png");

        tank = new Tank(app, START_POSITION, TANK_NAME);
        app.noLoop(); 
        app.setup();   

        try {
            tank.displayUI(app, 50, 50);  // Call the displayUI method
            assertTrue(true);
        } catch (Exception e) {
            fail( e.getMessage());
        }
    }
}
