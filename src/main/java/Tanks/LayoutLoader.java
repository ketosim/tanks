package Tanks;

import java.util.ArrayList;
import java.util.Arrays;
import java.awt.Point;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * LayoutLoader is responsible for loading game layouts including terrain, trees, and tanks.
 */
public class LayoutLoader {
    private PApplet app;
    private int[] smoothedHeights; 
    private ArrayList<Point> treeAnchorPoints = new ArrayList<>();

    /**
     * Constructs a LayoutLoader with the specified PApplet instance.
     * @param app the PApplet instance used for loading resources and drawing
     */
    public LayoutLoader(PApplet app) {
        this.app = app;
    }

    /**
     * Loads the tree layout from a text file.
     * @param layoutTxtPath the path to the layout text file
     * @param treeImage the image of the tree
     * @param heights the array of terrain heights
     * @return a list of trees
     */
    public ArrayList<Tree> loadTreeLayout(String layoutTxtPath, PImage treeImage, int[] heights) {
        ArrayList<Tree> trees = new ArrayList<>();
        String[] rows = app.loadStrings(layoutTxtPath);
        this.treeAnchorPoints.clear(); 

        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows[i].length(); j++) {
                if (rows[i].charAt(j) == 'T') {
                    int anchorX = j * App.CELLSIZE; 
                    int anchorY = heights[anchorX];

                    Point anchorPoint = new Point(anchorX, anchorY);
                    trees.add(new Tree(treeImage, anchorPoint));
                    treeAnchorPoints.add(anchorPoint);
                }
            }
        }

        return trees;
    }

    /**
     * Loads the terrain layout from a text file.
     * @param layoutTxtPath the path to the layout text file
     * @param r the red component of the terrain color
     * @param g the green component of the terrain color
     * @param b the blue component of the terrain color
     * @return a list of terrain pixels
     */
    public ArrayList<TerrainPixel> loadTerrainLayout(String layoutTxtPath, int r, int g, int b) {
        ArrayList<TerrainPixel> terrain = new ArrayList<>();
        String[] rows = app.loadStrings(layoutTxtPath);
        int[] heights = new int[App.WIDTH / App.CELLSIZE + 1];
        Arrays.fill(heights, App.HEIGHT); // Ground up

        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows[i].length(); j++) {
                if (rows[i].charAt(j) == 'X' && i * App.CELLHEIGHT < heights[j]) {
                    heights[j] = i * App.CELLHEIGHT;  
                }
            }
        }

        // Twice smooth the heights
        int[] expandedHeights = expandHeights(heights, 32);
        int[] firstSmooth = smoothHeights(expandedHeights, 32);
        this.smoothedHeights = smoothHeights(firstSmooth, 32); 

        // Create terrain pixels using the smoothed heights
        for (int height : smoothedHeights) {
            terrain.add(new TerrainPixel(App.HEIGHT - height, app.color(r, g, b)));
        }
        return terrain;
    }

    /**
     * Loads the tank layout from a text file.
     * @param layoutTxtPath the path to the layout text file
     * @param heights the array of terrain heights
     * @return a list of tanks
     */
    public ArrayList<Tank> loadTanksLayout(String layoutTxtPath,int[] heights) {
        ArrayList<Tank> tanks = new ArrayList<>();
        String[] rows = app.loadStrings(layoutTxtPath);

        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows[i].length(); j++) {
                if (rows[i].charAt(j) =='A' || rows[i].charAt(j) =='B'||
                rows[i].charAt(j) =='C'||rows[i].charAt(j) =='D'||rows[i].charAt(j) =='E'
                ) {
                    String name = String.valueOf(rows[i].charAt(j));
                    int anchorX = j * App.CELLSIZE ; 
                    int anchorY = heights[anchorX] ;

                    Point anchorPoint = new Point(anchorX, anchorY);
                    tanks.add(new Tank(this.app,anchorPoint,name ));
                }
            }
        }
        return tanks;
    }

    /**
     * Expands the heights array by repeating each height value a specified number of times.
     * @param heights the original array of heights
     * @param repeatCount the number of times to repeat each height value
     * @return the expanded array of heights
     */
    private int[] expandHeights(int[] heights, int repeatCount) {
        int[] expanded = new int[heights.length * repeatCount];
        for (int i = 0; i < heights.length; i++) {
            Arrays.fill(expanded, i * repeatCount, (i + 1) * repeatCount, heights[i]);
        }
        return expanded;
    }

    /**
     * Smooths the heights array using a moving average with a specified window size.
     * @param heights the original array of heights
     * @param windowSize the size of the moving average window
     * @return the smoothed array of heights
     */
    private int[] smoothHeights(int[] heights, int windowSize) {
        int[] smoothed = new int[heights.length];
        for (int i = 0; i < heights.length; i++) {
            int avg = 0;
            int count = 0;
            for (int j = Math.max(0, i - windowSize / 2); j < Math.min(heights.length, i + windowSize / 2); j++) {
                avg += heights[j];
                count++;
            }
            smoothed[i] = avg / count;
        }
        return smoothed;
    }

    /**
     * Gets the smoothed heights of the terrain.
     * @return the array of smoothed heights
     */
    public int[] getSmoothedHeights() {
        return this.smoothedHeights;
    }
    

}

