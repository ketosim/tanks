package Tanks;

import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Manages the levels in the game, including loading levels, switching between levels, and handling game state.
 */
class LevelManager {
    private ArrayList<Level> levels;
    private int currentLevelIndex;
    private PApplet app;
    private JSONObject colorsConfig;
    private Scoreboard scoreboard;  // Shared scoreboard
    private boolean gameOver=false;
    private Map<String, Integer> playerColors = new HashMap<>();
    public String configPath;
    
    /**
     * Constructs a LevelManager with the given PApplet and configuration path.
     * @param app The PApplet instance.
     * @param configPath The path to the configuration file.
     */
    LevelManager(PApplet app, String configPath) {
        this.app = app;
        this.levels = new ArrayList<>();
        this.currentLevelIndex = 0;
        this.scoreboard = new Scoreboard();  // Initialize the shared scoreboard
        this.configPath = configPath;

        JSONObject jsonConfig = app.loadJSONObject(configPath);
        JSONArray levelsConfig = jsonConfig.getJSONArray("levels");
        this.colorsConfig = jsonConfig.getJSONObject("player_colours");
        for (int i = 0; i < levelsConfig.size(); i++) {
            // Pass the shared scoreboard to each level
            levels.add(new Level(app, levelsConfig.getJSONObject(i), colorsConfig, scoreboard));
        }
        initializePlayerColors(colorsConfig);
    }


    /**
     * Initializes player colors from the configuration.
     * @param colorsConfig The JSON object containing player color configurations.
     */
    private void initializePlayerColors(JSONObject colorsConfig) {
    // Retrieve keys as a Set<String> by casting, which is valid if all keys are indeed strings
    @SuppressWarnings("unchecked")
    Set<String> keySet = (Set<String>) colorsConfig.keys();
    Iterator<String> keys = keySet.iterator();

    while (keys.hasNext()) {
        String key = keys.next(); // Get the next key
        String value = colorsConfig.getString(key);
        
        if (value.equalsIgnoreCase("random")) {
            // Generate random color
            int r = (int) (Math.random() * 256);
            int g = (int) (Math.random() * 256);
            int b = (int) (Math.random() * 256);
            playerColors.put(key, app.color(r, g, b));
        } else {
            // Parse the RGB values for non-random entries
            int[] rgb = Util.parseRGB(value);
            playerColors.put(key, app.color(rgb[0], rgb[1], rgb[2])); // Store the color in the map
        }
    }
}

    /**
     * Returns the map of player colors.
     * @return The map of player colors.
     */
    public Map<String, Integer> getPlayerColors(){
        return playerColors;
    }


    /**
     * Restarts the game, resetting levels and the scoreboard.
     */
    public void restart() {
        currentLevelIndex = 0;  // Reset to the first level
        gameOver = false;       // Reset game over flag
        scoreboard.reset();     // Assuming you have a reset method in the Scoreboard class
        reloadLevels();         // Re-load or re-initialize all levels
    }

    /**
     * Reloads the levels from the configuration file.
     */
    private void reloadLevels() {
        levels.clear();  // Clear existing levels
        JSONObject jsonConfig = app.loadJSONObject(app.sketchPath(configPath));  // Reload configuration
        JSONArray levelsConfig = jsonConfig.getJSONArray("levels");
        for (int i = 0; i < levelsConfig.size(); i++) {
            levels.add(new Level(app, levelsConfig.getJSONObject(i), colorsConfig, scoreboard));
        }
    }


    /**
     * Advances to the next level if available; otherwise, sets the game over flag.
     */
    public void nextLevel() {
        if (currentLevelIndex + 1 < levels.size()) {
            currentLevelIndex++;
        } else {
            gameOver = true;  // Set game over flag
        }
    }

    /**
     * Returns the current level.
     * @return The current level, or null if there are no levels.
     */
    public Level getCurrentLevel() {
        return levels.isEmpty() ? null : levels.get(currentLevelIndex);
    }


    /**
     * Displays the current level.
     */
    public void displayCurrentLevel() {
        if (!gameOver) {
            Level currentLevel = getCurrentLevel();
            if (currentLevel != null) {
                // System.out.println("--DISPLAYING---");
                currentLevel.display();
            }
        }
    }

    /**
     * Checks if the game is over.
     * @return True if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    public int getCurrentLevelIndex(){
        return currentLevelIndex;
    }

    public Scoreboard getScoreboard(){
        return scoreboard;
    }

    public ArrayList<Level> getLevels() {
        return levels;
    }

    


}