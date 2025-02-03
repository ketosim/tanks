package Tanks;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONObject;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

/**
 * The Level class manages all aspects of a game level, including terrain,
 * trees, tanks, projectiles, and explosions. It also handles the display
 * and update logic for each frame.
 */
public class Level {
    private String layoutPath;
    private PImage background;
    private PApplet app;
    private Wind wind;

    private ArrayList<TerrainPixel> terrainPixels;
    private ArrayList<Tree> trees;
    private ArrayList<Tank> tanks;
    private ArrayList<Projectile> projectiles;
    private ArrayList<Explosion> explosions;

    public int[] terrainHeights;
    private int currentTankIndex = 0; // the assumption is there will be same number of players thru all levels

    private long arrowStartTime;
    private static final int ARROW_DURATION = 2000; 
    private boolean isArrowVisible = false;
    private PImage arrowImage;

    private Scoreboard scoreboard;

    /**
     * Constructs a new Level.
     * @param app The PApplet instance.
     * @param levelConfig The JSON configuration for the level.
     * @param colorsConfig The JSON configuration for the colors.
     * @param scoreboard The scoreboard for tracking scores.
     */
    public Level(PApplet app, JSONObject levelConfig, JSONObject colorsConfig, Scoreboard scoreboard){
        this.app = app;
        this.layoutPath = levelConfig.getString("layout");
        this.background = app.loadImage("Tanks/" + levelConfig.getString("background"));
        this.projectiles = new ArrayList<>();
        this.wind = new Wind(app);
        this.explosions = new ArrayList<>();
        this.scoreboard = scoreboard;

        // 2 seconds long arrow
        this.arrowImage = app.loadImage("Tanks/arrow2.png");
        arrowImage.resize(App.CELLSIZE, App.CELLSIZE * 2);
        resetArrowTimer();

        loadLevelComponents(levelConfig, colorsConfig);
        initializeScores();
        tanks.sort(Comparator.comparing(Tank::getName));
        tanks.forEach(this::updateTankPosition);

    }

    /**
     * Loads the components of the level such as terrain, trees, and tanks.
     * @param levelConfig The JSON configuration for the level.
     * @param colorsConfig The JSON configuration for the colors.
     */
    private void loadLevelComponents(JSONObject levelConfig, JSONObject colorsConfig) {
        LayoutLoader loader = new LayoutLoader(app);
        String foregroundColour = levelConfig.getString("foreground-colour");
        int[] rgb = Util.parseRGB(foregroundColour);
        this.terrainPixels = loader.loadTerrainLayout(layoutPath, rgb[0], rgb[1], rgb[2]);
        this.terrainHeights = loader.getSmoothedHeights();

        if (levelConfig.hasKey("trees")) {
            PImage treeImage = app.loadImage("Tanks/" + levelConfig.getString("trees"));
            treeImage.resize(App.CELLSIZE, App.CELLSIZE);
            this.trees = loader.loadTreeLayout(layoutPath, treeImage, terrainHeights);
        }

        this.tanks = loader.loadTanksLayout(layoutPath, terrainHeights);
        for (Tank tank : tanks) {
            if (colorsConfig.hasKey(tank.getName())) {
                String tankColor = colorsConfig.getString(tank.getName());
                int[] rgbTank = Util.parseRGB(tankColor);
                tank.changeColor(app, rgbTank[0], rgbTank[1], rgbTank[2]);
            }
        }
    }

    /**
     * Initializes the scores for each tank.
     */
    public void initializeScores() {
        for (Tank tank : tanks) {
            scoreboard.addScore(tank.getName(), 0); 
        }
    }

    /**
     * Displays the current state of the level.
     */
    public void display() {
        app.background(255);
        app.image(this.background, 0, 0);
        update();
        displayTerrain();
        displayTrees();
        displayProjectiles();
        displayWind();
        displayTankInfo();
        displayExplosions();
        displayTanks();
        
    }

    /**
     * Updates the state of the level.
     */
    public void update() {
        updateArrowVisibility();
        updateTanks();       
        updateExplosions();
        updateProjectiles();

    }

    /**
     * Updates the positions and states of all tanks in the level.
     */
    public void updateTanks() {
        float deltaTime = 1.0f / App.FPS; // Calculate delta time based on frame rate
        ArrayList<Tank> activeTanks = new ArrayList<>();
        for (Tank tank : tanks) {
            // Only update the tank position if it is not falling
            if (!tank.isFalling()) {
                updateTankPosition(tank);
            }
            tank.update(app, deltaTime, terrainHeights); // Update the tank with delta time and terrain heights
            
            if (tank.isActive()) {
                activeTanks.add(tank); // Add the tank to the active tanks list
        } else if (!tank.isDestroyed()) {
            // If the tank is destroyed but the explosion has not been created yet
            createExplosion(new PVector(tank.getPosition().x, tank.getPosition().y), 30); // Create explosion
            tank.setDestroyed(true); // Mark the tank as destroyed
        }
    }
    tanks = activeTanks; // Update the tanks list with active tanks
    if (tanks.size() > 0 && currentTankIndex >= tanks.size()) {
        currentTankIndex = 0; // Reset to the first tank if the current index is out of bounds
    }
}

    /**
     * Updates the visibility of the arrow indicating the current tank.
     */
    private void updateArrowVisibility() {
        long currentTime = System.currentTimeMillis();
        if (isArrowVisible && (currentTime - arrowStartTime > ARROW_DURATION)) {
            isArrowVisible = false;
            // System.out.println("Arrow visibility turned off");
        }
    }

    /**
     * Updates the explosions in the level.
     */
    public void updateExplosions() {
        Iterator<Explosion> it = explosions.iterator();
        while (it.hasNext()) {
            Explosion explosion = it.next();
            explosion.update();
            if (explosion.isFinished()) {
                it.remove(); // Remove the explosion if it's finished
            }
        }
    }

    /**
     * Updates the projectiles in the level.
     */
    public void updateProjectiles() {
        Iterator<Projectile> it = projectiles.iterator();
        while (it.hasNext()) {
            Projectile projectile = it.next();
            projectile.update(terrainHeights);
            if (!projectile.isActive()) {
                it.remove();
            }
        }
    }

    /**
     * Displays the terrain of the level.
     */
    private void displayTerrain() {
        int x = 0;
        app.noStroke();
        for (int height : terrainHeights) {
            app.fill(terrainPixels.get(0).getColor());
            app.rect(x, height, 1, App.HEIGHT - height);
            x++;
        }
    }

    /**
     * Displays the trees in the level.
     */
    private void displayTrees() {
        if (trees != null) {
            for (Tree tree : trees) {
                if (tree != null) tree.display(app);
            }
        }
    }

    /**
     * Displays the tanks in the level.
     */
    private void displayTanks() {
        for (int i = 0; i < tanks.size(); i++) {
            Tank tank = tanks.get(i);

            // Only update the tank position if it is not falling
            if (!tank.isFalling()) {
                updateTankPosition(tank);
            }

            tank.display(app);

            if (i == currentTankIndex && isArrowVisible) {
                float arrowX = tank.getPosition().x - App.CELLSIZE / 2.0f;
                float arrowY = tank.getPosition().y - App.CELLSIZE * 3;
                app.image(arrowImage, arrowX, arrowY);
            }
        }
    }


    /**
     * Displays the projectiles in the level.
     */
    private void displayProjectiles() {
        for (Projectile projectile : projectiles) {
            projectile.update(terrainHeights);
            projectile.display(app);
        }
    }

    /**
     * Displays the explosions in the level.
     */
    private void displayExplosions() {
        for (Explosion explosion : explosions) {
            explosion.drawExplosion(app);
        }
    }

    /**
     * Displays the wind in the level.
     */
    private void displayWind() {
        wind.display(app, App.WIDTH - 80, App.CELLAVG / 4);
    }

    /**
     * Displays the information of the current tank.
     */
    private void displayTankInfo() {
        if (currentTankIndex < tanks.size()) {
            Tank currentTank = tanks.get(currentTankIndex);
            currentTank.displayUI(app, App.CELLAVG / 4, App.CELLAVG / 4);
            if (currentTank.isNextShotLarge()) {
                app.fill(0);  // Red text for emphasis
                app.text("Super-Sized EXP", 8, 35);
            }
        }
    }

    /**
     * Updates the position of the tank based on the terrain height.
     * @param tank The tank to be updated.
     */
    private void updateTankPosition(Tank tank) {
        int tankX = Math.max(0, Math.min((int) tank.getPosition().x, App.WIDTH - 1));
        int terrainY = terrainHeights[tankX];
        Point pos = tank.getPosition();
    
        if (pos.y < terrainY && !tank.isMoving()) {
            // System.out.println("Tank " +pos.y+  "versus  terrain " +terrainY + tank.getName() + " is above terrain, starting to fall");
            tank.startFalling(); // Start falling if the tank is above the terrain
        } else {
            pos.y = terrainY; // Set the tank directly on the terrain if it's not falling
            tank.stopFalling(); // Ensure the tank stops falling when it hits the terrain
        }
    
        tank.setPosition(pos);
    }
    

    /**
     * Resets the arrow timer for indicating the current tank.
     */
    private void resetArrowTimer() {
        arrowStartTime = System.currentTimeMillis();
        isArrowVisible = true;
    }

    /**
     * Modifies the terrain based on the explosion location and radius.
     * @param location The location of the explosion.
     * @param radius The radius of the explosion.
     */
    public void modifyTerrain(PVector location, float radius) {
        if (terrainHeights == null) {
            // System.out.println("Error: terrainHeights is null");
            return;
        }
        // for (Tank tank : tanks) {
        //     Point pos = tank.getPosition();
        //     int tankX = Math.max(0, Math.min((int) pos.x, App.WIDTH - 1));
        //     int terrainY = terrainHeights[tankX];
        //     // System.out.println("PRE MODIFY " + tank.getPosition().x + " "+ tank.getPosition().y+ "   "+ terrainY);
        // }
    
        int start = Math.max(0, (int) (location.x - radius));
        int end = Math.min(terrainHeights.length - 1, (int) (location.x + radius));
    
        for (int i = start; i <= end; i++) {
            float dx = i - location.x;
            float dy = (float) Math.sqrt(radius * radius - dx * dx);
            float upperY = location.y - dy;
            float lowerY = location.y + dy;
    
            if (terrainHeights[i] < upperY) {
                float explosionCoverage = 2 * dy;
                terrainHeights[i] = (int) (terrainHeights[i] + explosionCoverage);
            }
    
            if (terrainHeights[i] >= upperY && terrainHeights[i] <= lowerY) {
                terrainHeights[i] = (int) (lowerY);
            }
        }
   
        updateTreePositions();
    
        // Check and update tank positions after terrain modification
        for (Tank tank : tanks) {
            updateTankPosition(tank);
        }
    }
    

    /**
     * Updates the positions of the trees based on the terrain heights.
     */
    public void updateTreePositions() {
        if (trees != null) {
            for (Tree tree : trees) {
                if (tree != null && tree.getPosition() != null) {
                    int terrainIndex = tree.getPosition().x;
                    if (terrainIndex >= 0 && terrainIndex < terrainHeights.length) {
                        tree.getPosition().y = terrainHeights[terrainIndex];
                    }
                }
            }
        }
    }

    /**
     * Handles key press events for tank controls and power-ups.
     */
    public void keyPressed() {
        switch (app.keyCode) {
            case PConstants.LEFT:
                tanks.get(currentTankIndex).move(-60 / App.FPS);
                break;
            case PConstants.RIGHT:
                tanks.get(currentTankIndex).move(60 / App.FPS);
                break;
            case PConstants.DOWN:
                tanks.get(currentTankIndex).adjustTurret(false);
                break;
            case PConstants.UP:
                tanks.get(currentTankIndex).adjustTurret(true);
                break;
            case 'W':
            case 'w':
                tanks.get(currentTankIndex).adjustPower(36 / App.FPS);
                System.out.println(tanks.get(currentTankIndex).getName() + " has power of " + tanks.get(currentTankIndex).getPower());
                break;
            case 'S':
            case 's':
                tanks.get(currentTankIndex).adjustPower(-36 / App.FPS);
                System.out.println(tanks.get(currentTankIndex).getName() + " has power of " + tanks.get(currentTankIndex).getPower());
                break;
            case 32:
                fireProjectile();
                currentTankIndex = (currentTankIndex + 1) % tanks.size();
                System.out.println("Now controlling: " + tanks.get(currentTankIndex).getName());
                resetArrowTimer();
                wind.updateWind();
                break;
            case 'R': 
            case 'r':
                attemptRepair(tanks.get(currentTankIndex));
                break;
            case 'F':
            case 'f':
                attemptedFuelUp(tanks.get(currentTankIndex));
                break;
            case 'X':
            case 'x':
                nextShotMightBeLarge(tanks.get(currentTankIndex));
                break;
            case 'P':
            case 'p':
                mightBuyAParachute(tanks.get(currentTankIndex));
                break;

        }
    }

    /**
     * Fires a projectile from the current tank.
     */
    public void fireProjectile() {
        Tank currentTank = tanks.get(currentTankIndex);
        PVector start = new PVector(currentTank.getPosition().x, currentTank.getPosition().y);
        float powerToPixel = Util.map(currentTank.getPower(), 0, 100, 2, 18);
        PVector velocity = Util.fromAngleAndMagnitude((float) currentTank.getTurretAngle(), powerToPixel);

        Projectile projectile = new Projectile(start, velocity, wind.getCurrentWind(), currentTank);

        int radius = currentTank.isNextShotLarge() ? 60 : 30;  // Adjust radius based on power-up
    
        projectile.setImpactListener(location -> {
            int damageDone = calculateDamage(location, currentTank, radius);
            if (damageDone > 0) {
                getScoreboard().addScore(currentTank.getName(), damageDone);
            }

            createExplosion(location, radius); // Ensure the explosion is triggered here

            modifyTerrain(location, radius); // Modify the terrain on impact
    
            // Check and update tank positions after terrain modification
            for (Tank tank : tanks) {
                Point pos = tank.getPosition();
                int tankX = Math.max(0, Math.min((int) pos.x, App.WIDTH - 1));
                int terrainY = terrainHeights[tankX];
    
                if (pos.y < terrainY) {
                    System.out.println("Tank " + tank.getName() + " is now above terrain after modification, starting to fall");
                    tank.startFalling();
                }
            }
        });
    
        projectiles.add(projectile);
        currentTank.setNextShotLarge(false);
    }
    

    /**
     * Calculates the damage dealt by a projectile.
     * Apply health damage to tank victims.
     * @param location The location of the explosion.
     * @param tankOwner The tank that fired the projectile.
     * @param explosionRadius The radius of the explosion.
     * @return The total damage done.
     */
    public int calculateDamage(PVector location, Tank tankOwner, int explosionRadius) {
        int maxDamage = 60; // Maximum possible damage, same for 30 and 60 rad explosions
        int damageDone = 0;

        for (Tank tank : tanks) {
float distance = Util.distance(location, new PVector(tank.getPosition().x, tank.getPosition().y));
            if (distance <= explosionRadius ) {
                int damage = (int) (maxDamage - (distance / explosionRadius) * maxDamage);
                if (damage > 0) {
                    System.out.println("damage done by "+ tankOwner.getName() + " to " + tank.getName());
                    System.out.println(tank.getName()+ " old health " + tank.getHealth());
                    int newHealth = tank.getHealth() - (int) damage;
                    newHealth = Math.max(0, newHealth); 
                    tank.setHealth(newHealth); 
                    tank.setPower(Math.min(newHealth, tank.getPower())); // Update new power cap value
                    System.out.println(tank.getName()+ " new health " + tank.getHealth());
                    if (tank != tankOwner){
                        damageDone += damage;
                    }
                }
            }
        }
        System.out.println(tankOwner.getName() + " gained total " + damageDone);
        return damageDone;
    }

    /**
     * Creates an explosion at the given location with the specified radius.
     * @param location The location of the explosion.
     * @param radius The radius of the explosion.
     */
    public void createExplosion(PVector location, float radius) {
        explosions.add(new Explosion(app, location, radius, this));
    }

    /**
     * Attempts to repair the current tank.
     * @param tank The current tank.
     */
    public void attemptRepair(Tank tank) {
        // 'r' cost 20 score, health up 20 up to 100
        int repairCost = 20;
        int repairAmount = 20;
        int maxHealth = 100;
        Scoreboard scoreboard = getScoreboard();
        int currentScore = scoreboard.getScore(tank.getName());

        if (currentScore >= repairCost && tank.getHealth() < 100) {
            int newHealth = Math.min(tank.getHealth() + repairAmount, maxHealth);
            tank.setHealth(newHealth);
            scoreboard.addScore(tank.getName(), -repairCost);
            System.out.println(tank.getName() + " repaired to " + newHealth + ". CurrentScore: " + (currentScore - repairCost));
        } else {
            System.out.println(tank.getName() + " cannot afford a repair OR full health. CurrentScore " + currentScore);
        }
    }

    /**
     * Attempts to refuel the current tank.
     * @param tank The current tank.
     */
    public void attemptedFuelUp(Tank tank) {
        //'f' cost 10 score, fuel up 100
        int fuelCost = 10;
        int fuelAmount = 100;
        Scoreboard scoreboard = getScoreboard();
        int currentScore = scoreboard.getScore(tank.getName());

        if (currentScore >= fuelCost) {
            int newFuel = tank.getFuel() + fuelAmount;
            tank.setFuel(newFuel);
            scoreboard.addScore(tank.getName(), -fuelCost);
            System.out.println(tank.getName() + " fueled up to " + newFuel );
        } else {
            System.out.println(tank.getName() + " cannot afford a fuel up. ");
        }
    }

    /**
     * Attempts to set the next shot to be a larger projectile.
     * @param tank The current tank.
     */
    public void nextShotMightBeLarge(Tank tank) {
        int fuelCost = 20;
        Scoreboard scoreboard = getScoreboard();
        int currentScore = scoreboard.getScore(tank.getName());

        if (currentScore >= fuelCost) {
            tank.setNextShotLarge(true);
            scoreboard.addScore(tank.getName(), -fuelCost);
            System.out.println(tank.getName() + "'s next shot will be a larger projectile.");
        } else {
            System.out.println(tank.getName() + " unfortunately does not have enough points to activate larger projectile.");
        }
    }

    /**
     * Attempts to buy one parachute
     * @param tank The current tank.
     */
    public void mightBuyAParachute(Tank tank) {
        int fuelCost = 15;
        Scoreboard scoreboard = getScoreboard();
        int currentScore = scoreboard.getScore(tank.getName());

        if (currentScore >= fuelCost) {
            tank.getParachutes().buyOneParachute();
            scoreboard.addScore(tank.getName(), -fuelCost);

            System.out.println(tank.getName() + "! Congrats on your purchase of a parachute!");
        } else {
            System.out.println(tank.getName() + " Unfortunately not enough points to buy one parachute!");
        }
    }

    /**
     * Checks if the level should advance to the next level.
     * @return True if the level should advance, otherwise false.
     */
    public boolean shallAdvanceLevel() {
        int activeTanksCount = 0;
        for (Tank tank : tanks) {
            if (tank.getHealth() > 0) {
                activeTanksCount++;
            }
        }
        return activeTanksCount <= 1;  // True if one or zero tanks are active
    }

    // Getter methods
    public PApplet getApp() {
        return app;
    }

    public ArrayList<Tank> getTanks() {
        return tanks;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void setCurrentTankIndex(int index) {
        this.currentTankIndex = index;
    }

    public int getCurrentTankIndex() {
        return currentTankIndex;
    }

    public ArrayList<Projectile> getProjectiles(){
        return projectiles;
    }
   
}
