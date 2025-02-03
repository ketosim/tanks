package Tanks;

import processing.core.PApplet;
import processing.core.PImage;
import java.awt.Point;

/**
 * The Tree class represents a tree in the game. It contains the image and position of the tree,
 * and provides methods to display the tree in the game.
 */
public class Tree {
    private PImage image;  // The image representing the tree
    private Point position; // The position of the tree in the game

    /**
     * Constructs a Tree object with the specified image and position.
     * 
     * @param image The image representing the tree.
     * @param position The position of the tree.
     */
    public Tree(PImage image, Point position) {
        this.image = image;
        this.position = position;
    }

    /**
     * Displays the tree on the screen using the specified PApplet instance.
     * 
     * @param app The PApplet instance used for drawing.
     */
    public void display(PApplet app) {
        // Offset the position so that the trunk of the tree is the anchor point
        app.image(image, position.x - App.CELLSIZE / 2, position.y - App.CELLSIZE + 2);
        // +2 moves the tree downward so the tree trunk is grounded into the soil
    }

    /**
     * Gets the position of the tree.
     * 
     * @return The position of the tree.
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Gets the image of the tree.
     * 
     * @return The image of the tree.
     */
    public PImage getImage() {
        return image;
    }
}
