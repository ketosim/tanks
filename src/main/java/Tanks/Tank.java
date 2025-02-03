package Tanks;

import java.awt.Point;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

/**
 * The Tank class represents a tank in the game.
 * It handles the tank's properties, movements, and actions.
 */
public class Tank {
    private PImage image, fuelIcon, parachuteImage;
    private Point position;
    private Parachutes parachutes;
    private double turretAngle; 
    private int health, power, fuel, tankColor;
    private String name; 
    private boolean isFalling = false;
    private boolean isDestroyed = false; 
    private boolean isNextShotLarge = false;
    private float fallVelocity;
    private boolean isMoving = false;

    private static final int INITIAL_HEALTH = 100;
    private static final int INITIAL_POWER = 50;
    private static final int INITIAL_FUEL = 250;
    private static final double TURRET_SPEED = 3;
    private static final int DEFAULT_COLOR_R = 136; // Default grey in RGB
    private static final int DEFAULT_COLOR_G = 136;
    private static final int DEFAULT_COLOR_B = 136;
    private static final float TURRET_LENGTH = 15; 

    /**
     * Constructs a Tank object with a default color.
     * 
     * @param app The PApplet instance.
     * @param startPos The starting position of the tank.
     * @param name The name of the tank.
     */
    public Tank(PApplet app, Point startPos, String name) {
        this(app, DEFAULT_COLOR_R, DEFAULT_COLOR_G, DEFAULT_COLOR_B, startPos, name);
    }

    /**
     * Constructs a Tank object with a specified color.
     * 
     * @param app The PApplet instance.
     * @param r The red component of the tank color.
     * @param g The green component of the tank color.
     * @param b The blue component of the tank color.
     * @param startPos The starting position of the tank.
     * @param name The name of the tank.
     */
    public Tank(PApplet app, int r, int g, int b, Point startPos, String name) {
        this.position = startPos;
        this.turretAngle = -Math.PI / 2; // Initially facing upwards
        this.health = INITIAL_HEALTH;
        this.power = INITIAL_POWER;
        this.fuel = INITIAL_FUEL;
        this.name = name;
        this.parachutes = new Parachutes(App.INITIAL_PARACHUTES);
        this.tankColor = app.color(r, g, b);
        this.image = app.createImage(20, 10, PConstants.RGB);
        this.fuelIcon = app.loadImage( "Tanks/fuel.png");
        this.parachuteImage = app.loadImage("Tanks/parachute.png");
        parachuteImage.resize(32, 32);
        updateTankColor(tankColor);
    }


    /**
     * Updates the tank's state and position based on gravity and parachute status.
     * 
     * @param app The PApplet instance.
     * @param deltaTime The time difference between frames.
     * @param terrainHeights The heights of the terrain at different x-coordinates.
     */
    public void update(PApplet app, float deltaTime, int[] terrainHeights) {
        // System.out.println(this.getName() + "moving or not " + "isMoving" + isFalling+" falling or not "+isFalling);
        if (isFalling && !isMoving) {
            // System.out.println(name + " is falling. Current position: " + position.y);
            float descentRate = parachutes.isDeployed() ? Parachutes.SLOW_DESCENT_RATE : Parachutes.FAST_DESCENT_RATE;
            // System.out.println("Descent rate: " + descentRate + " pixels per second");
    
            int healthLost = parachutes.isDeployed() ? 0 : 1;
            // System.out.println("Health lost per p due to fall: " + healthLost);
    
            // Adjust fall velocity based on parachute status
            fallVelocity = descentRate * deltaTime;
            position.y += fallVelocity;
            // System.out.println("Updated position: " + position.y);
    
            health -= Parachutes.FAST_DESCENT_RATE / Parachutes.DAMAGE_PER_PIXEL / App.FPS * healthLost;
            // System.out.println("Updated health: " + health);
    
            // Check for ground contact
            if (position.y >= Math.min(terrainHeights[position.x], App.HEIGHT - 16)) {
                position.y = Math.min(terrainHeights[position.x], App.HEIGHT - 16);
                stopFalling();
            }
        }
        display(app);
        setMoving(false);
    }

    /**
     * Starts the tank falling, checking and deploying parachutes if available.
     */
    public void startFalling() {
        if (!isFalling) {
            isFalling = true;
            isMoving =false;
            parachutes.checkAndDeploy();
            fallVelocity = 0;
        }
    }

    /**
     * Stops the tank from falling and resets the fall velocity and parachute deployment status.
     */
    public void stopFalling() {
        isFalling = false;
        fallVelocity = 0;
        parachutes.resetDeployment();
    }

    /**
     * Updates the image color of the tank.
     * 
     * @param newColor The new color to be applied to the tank.
     */
    private void updateTankColor(int newColor) {
        Util.updateImageColor(this.image, newColor);
    }

    /**
     * Displays the tank on the screen.
     * 
     * @param app The PApplet instance used for drawing.
     */
    public void display(PApplet app) {
        app.pushMatrix();
        app.translate(position.x, position.y);
        // all subsequent parachutes and turret are relative to this new tank origin

        if (isFalling && parachutes.isDeployed()) {
            app.image(parachuteImage, -16, -32);
        }

        app.image(image, -image.width / 2, -image.height / 2);
        // left top corner

        app.rotate((float) turretAngle);
        app.stroke(0);
        app.strokeWeight(2);
        app.line(0, 0, TURRET_LENGTH, 0);
        app.popMatrix();
    }

    /**
     * Displays the UI elements health, fuel, and power on the screen.
     * 
     * @param app The PApplet instance used for drawing.
     * @param x The x-coor for the UI display.
     * @param y The y-coor for the UI display.
     */
    public void displayUI(PApplet app, float x, float y) {
        app.pushMatrix();
        app.translate(x, y);

        app.textAlign(PConstants.LEFT, PConstants.TOP);
        app.fill(0);
        app.textSize(16);
        app.text("Player " + this.name + "'s turn", 0, 0);

        app.image(this.fuelIcon, 160, 0, 20, 20);
        app.fill(0);
        app.text(this.fuel, 185, 0);

        app.image(this.parachuteImage, 160, 30, 20, 20);
        app.fill(0);
        app.text(this.parachutes.getCount(), 185, 30);

        app.fill(0, 0, 0);
        app.rect(App.WIDTH / 2 - 80, 2, App.CELLAVG * 4, App.CELLAVG / 2);

        app.fill(getColor()[0], getColor()[1], getColor()[2]);
        app.rect(App.WIDTH / 2 - 80, 2, App.CELLAVG * 4 * (this.health / 100.0f), App.CELLAVG / 2);

        app.fill(0);
        app.text("Health: ", App.WIDTH / 3 - 4, 0);
        app.text(this.health, App.WIDTH / 2 + 56, 0);
        app.text("Power:   " + this.power, App.WIDTH / 3 - 4, 28);

        app.popMatrix();
    }

    /**
     * Moves the tank by the specified distance.
     * 
     * @param dx The distance to move the tank.
     */
    public void move(int dx) {
        // System.out.println(this.getName()+ " MOVE CALLED");
        if (fuel > 0) {
            isMoving = true;
            isFalling = false;
            position.x += dx;
            fuel -= Math.abs(dx);
            // System.out.println("-----MOVING-----" +name + position.x + " " + position.y);
        }
    }

    /**
     * Adjusts the turret angle of the tank.
     * 
     * @param rotateRight True to rotate the turret to the right, false to rotate to the left.
     */
    public void adjustTurret(boolean rotateRight) {
        double angleAdjustment = TURRET_SPEED * (rotateRight ? -1 : 1) / App.FPS; // 0.1 radians per frame
        turretAngle += angleAdjustment; 

        if (turretAngle < 0) {
            turretAngle += 2 * Math.PI;
        } else if (turretAngle >= 2 * Math.PI) {
            turretAngle -= 2 * Math.PI;
        }
    }

    /**
     * Adjusts the firing power of the tank.
     * 
     * @param delta The amount to adjust the power by.
     */
    public void adjustPower(int delta) {
        power += delta;
        power = Math.max(0, Math.min(health, power));
    }

    /**
     * Changes the color of the tank according to congif after initiation.
     * 
     * @param app The PApplet instance.
     * @param r The red component of the new color.
     * @param g The green component of the new color.
     * @param b The blue component of the new color.
     */
    public void changeColor(PApplet app, int r, int g, int b) {
        int newColor = app.color(r, g, b);
        tankColor = newColor;
        updateTankColor(newColor);
    }


    /**
     * Checks if the tank is active (i.e., has health greater than 0).
     * 
     * @return True if the tank is active, false otherwise.
     */
    public boolean isActive() {
        return health > 0;
    }

    /**
     * Checks if the tank is falling.
     * 
     * @return True if the tank is falling, false otherwise.
     */
    public boolean isFalling() {
        return isFalling;
    }

    /**
     * Checks if the tank is destroyed.
     * 
     * @return True if the tank is destroyed, false otherwise.
     */
    public boolean isDestroyed() {
        return isDestroyed;
    }

    /**
     * Sets the destroyed status of the tank.
     * 
     * @param destroyed True if the tank is destroyed, false otherwise.
     */
    public void setDestroyed(boolean destroyed) {
        isDestroyed = destroyed;
    }

    /**
     * Sets whether the next shot will be a large shot.
     * 
     * @param isLarge True if the next shot will be large, false otherwise.
     */
    public void setNextShotLarge(boolean isLarge) {
        isNextShotLarge = isLarge;
    }

    /**
     * Checks if the next shot will be a large shot.
     * 
     * @return True if the next shot will be large, false otherwise.
     */
    public boolean isNextShotLarge() {
        return isNextShotLarge;
    }

    /**
     * Sets the moving status of the tank.
     * 
     * @param isMoving True if the tank is moving, false otherwise.
     */
    public void setMoving(boolean isMoving){
        this.isMoving = isMoving;
    }

    /**
     * Checks if the tank is moving.
     * 
     * @return True if the tank is moving, false otherwise.
     */
    public boolean isMoving() {
        return isMoving;
    }

    // Getter Setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    public int getHealth() {
        return health;
    }

    public Point getPosition() {
        return position;
    }

    public double getTurretAngle() {
        return turretAngle;
    }

    public void setTurretAngle(double angle){
        this.turretAngle = angle;
    }

    public void setPosition(Point newPosition) {
        this.position = newPosition;
    }

    public int getPower() {
        return power;
    }

    public int getFuel() {
        return fuel;
    }

    public int[] getColor() {
        int r = (tankColor >> 16) & 0xFF;
        int g = (tankColor >> 8) & 0xFF;
        int b = tankColor & 0xFF;
        return new int[]{r, g, b};
    }

    public Parachutes getParachutes() {
        return parachutes;
    }

}
