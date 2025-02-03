package Tanks;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import processing.core.PApplet;
import processing.core.PVector;

public class LevelTest {

    private App app;
    private LevelManager levelManager;

    @BeforeEach
    public void setUp() {
        app = new App();
        PApplet.runSketch(new String[]{"App"}, app);
        app.setup();
        levelManager = app.getLevelManager();
        assertNotNull(levelManager, "LevelManager should be initialized");
    }

    @Test
    public void testInitializeScores() {
        Level level = levelManager.getCurrentLevel();
        level.initializeScores();

        assertEquals(0, level.getScoreboard().getScore("A"));
        assertEquals(0, level.getScoreboard().getScore("B"));
        assertEquals(0, level.getScoreboard().getScore("C"));
        assertEquals(0, level.getScoreboard().getScore("D"));
    }

    @Test
    public void testModifyTerrain() {
        Level level = levelManager.getCurrentLevel();
        level.terrainHeights = new int[864];
        for (int i = 0; i < 864; i++) {
            level.terrainHeights[i] = 700;
        }

        PVector explosionLocation = new PVector(50, 700);
        float radius = 10.0f;

        level.modifyTerrain(explosionLocation, radius);

        for (int i = 40; i <= 60; i++) {
            assertNotEquals(50, level.terrainHeights[i]);
        }
    }

    @Test
    public void testShallAdvanceLevel() {
        Level level = levelManager.getCurrentLevel();
        Tank tankA = levelManager.getCurrentLevel().getTanks().get(0);
        Tank tankB = levelManager.getCurrentLevel().getTanks().get(2);
        Tank tankC = levelManager.getCurrentLevel().getTanks().get(1);
        tankA.setHealth(0);
        tankB.setHealth(0);
        tankC.setHealth(0);

        assertTrue(level.shallAdvanceLevel());
    }

    @Test
    public void testFireProjectile() {
        Level level = levelManager.getCurrentLevel();

        // Ensure initial conditions
        assertEquals(0, level.getProjectiles().size());

        // Fire a projectile
        level.fireProjectile();

        // Verify that a projectile was added
        assertEquals(1, level.getProjectiles().size());

        Projectile projectile = level.getProjectiles().get(0);
        assertNotNull(projectile);
        level.updateProjectiles();
        level.updateExplosions();

    }

    @Test
    public void testAttemptedAddOns() {
        // Initial conditions
        Level level = levelManager.getCurrentLevel();
        Tank tankA = levelManager.getCurrentLevel().getTanks().get(0);
        level.getScoreboard().addScore(tankA.getName(), 200);

        assertEquals(3, tankA.getParachutes().getCount());
        assertFalse(tankA.isNextShotLarge());
        assertEquals(250, tankA.getFuel());
        assertEquals(200, level.getScoreboard().getScore(tankA.getName()));

        level.attemptedFuelUp(tankA);
        level.nextShotMightBeLarge(tankA);
        level.mightBuyAParachute(tankA);

        assertTrue(tankA.isNextShotLarge());
        assertEquals(4, tankA.getParachutes().getCount());
        assertEquals(350, tankA.getFuel());
        assertEquals(155, level.getScoreboard().getScore(tankA.getName()));
        // reduced 10 for fuel 20 for bigshot 15 for parachute 

        Tank tankB = levelManager.getCurrentLevel().getTanks().get(1);
        level.getScoreboard().addScore(tankB.getName(), 0);
        // tankB has no score, no purchase

        assertEquals(3, tankB.getParachutes().getCount());
        assertFalse(tankB.isNextShotLarge());
        assertEquals(250, tankB.getFuel());
        assertEquals(0, level.getScoreboard().getScore(tankB.getName()));

        level.attemptedFuelUp(tankB);
        level.mightBuyAParachute(tankB);
        level.nextShotMightBeLarge(tankB);
        level.attemptRepair(tankB);

        assertFalse(tankB.isNextShotLarge());
        assertEquals(250, tankB.getFuel());
        assertEquals(0, level.getScoreboard().getScore(tankB.getName()));
        assertEquals(3, tankB.getParachutes().getCount());

        Tank tankC = levelManager.getCurrentLevel().getTanks().get(2);
        tankC.setHealth(50);
        level.getScoreboard().addScore(tankC.getName(), 200);

        assertEquals(50, tankC.getHealth());

        level.attemptRepair(tankC);
        assertEquals(70, tankC.getHealth());

    }

    @Test
    public void testCalculateDamage() {
        Level level = levelManager.getCurrentLevel();
        Tank tankA = levelManager.getCurrentLevel().getTanks().get(0);
        Tank tankOwner = levelManager.getCurrentLevel().getTanks().get(1);

        assertEquals(100, tankA.getHealth());
        PVector explosionLocation = new PVector(tankA.getPosition().x, tankA.getPosition().y);
        // explosion happen at A's spot
        int explosionRadius = 30;
        int damageDone = level.calculateDamage(explosionLocation, tankOwner, explosionRadius);
        
        assertEquals(60, damageDone);
        assertEquals(40, tankA.getHealth());// full blownup lose60 health


    }

    @Test
    public void testExplosion() {
        Level level = levelManager.getCurrentLevel();
        Tank tankA = levelManager.getCurrentLevel().getTanks().get(0);
        PVector explosionLocation = new PVector(tankA.getPosition().x, tankA.getPosition().y);
        // explosion happen at A's spot
        int explosionRadius = 30;
        level.createExplosion(explosionLocation, explosionRadius);
    }
}
