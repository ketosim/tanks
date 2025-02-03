package Tanks;

import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import java.util.*;

/**
 * The main application class for the Tanks game.
 * This class extends PApplet and handles the game loop, user input, and displays levels.
 */
public class App extends PApplet {
    public static final int CELLSIZE = 32;
    public static final int CELLHEIGHT = 32;
    public static final int CELLAVG = 32;
    public static final int TOPBAR = 0;
    public static int WIDTH = 864;
    public static int HEIGHT = 640;
    public static final int BOARD_WIDTH = WIDTH / CELLSIZE;
    public static final int BOARD_HEIGHT = 20;
    public static final int INITIAL_PARACHUTES = 3;
    public static final int FPS = 30;
    public static final int BOX_WIDTH = 250;
    public static final int BOX_PADDING = 20;
    public static final int SCORE_UPDATE_INTERVAL = 700;
    public static final int SCOREBOARD_WIDTH = 150;
    public static final int SCOREBOARD_TITLE_HEIGHT = 30;
    public static final int SCOREBOARD_ROW_HEIGHT = 24;
    public static final int SCOREBOARD_X_OFFSET = 20;
    public static final int SCOREBOARD_Y_OFFSET = 40;
    public String configPath;

    private LevelManager levelManager;
    private boolean displayFinalScores = false;
    private List<Map.Entry<String, Integer>> sortedScores;
    private long lastScoreUpdateTime = 0;
    private int currentScoreIndex = 0;

    /**
     * Constructor to initialize the application with a default config path.
     */
    public App() {
        this.configPath = "config.json";
    }

    /**
     * Constructor to initialize the application with a specified config path.
     * @param configPath The path to the configuration file.
     */
    public App(String configPath) {
        this.configPath = configPath;
    }

    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    @Override
    public void setup() {
        frameRate(FPS);
        levelManager = new LevelManager(this, configPath);
    }

    @Override
    public void keyPressed(KeyEvent event) {
        super.keyPressed(event);
        if (event.getKey() == 'r' || event.getKey() == 'R') {
            if (levelManager.isGameOver()) {
                restartGame();
            }
        } else if (event.getKey() == 'n' || event.getKey() == 'N') {
            levelManager.nextLevel();
        }

        Level currentLevel = levelManager.getCurrentLevel();
        if (currentLevel != null) {
            currentLevel.keyPressed();
        }
    }

    @Override
    public void draw() {
        if (!levelManager.isGameOver()) {
            levelManager.displayCurrentLevel();
            if (levelManager.getCurrentLevel() != null && levelManager.getCurrentLevel().shallAdvanceLevel()) {
                levelManager.nextLevel();
            }
            displayScoreboard();
        } else {
            if (!displayFinalScores) {
                displayFinalScoresBox();
            }
            displayFinalScores();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO: Implement power-ups, like repair, extra fuel, and teleport.
    }

    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }

    /**
     * Restarts the game by resetting the level manager and score display variables.
     */
    public void restartGame() {
        levelManager.restart();
        displayFinalScores = false;
        sortedScores = null;
        lastScoreUpdateTime = 0;
        currentScoreIndex = 0;
    }

    /**
     * Prepares and sorts the scores from the scoreboard in descending order.
     */
    private void prepareAndSortScores() {
        if (sortedScores == null) {
            Scoreboard scoreboard = levelManager.getCurrentLevel().getScoreboard();
            sortedScores = new ArrayList<>(scoreboard.getScores().entrySet());
            sortedScores.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        }
    }

    /**
     * Displays the final scores box at the end of the game.
     */
    private void displayFinalScoresBox() {
        prepareAndSortScores();
        Map.Entry<String, Integer> winner = sortedScores.get(0);
        int winnerColor = levelManager.getPlayerColors().get(winner.getKey());

        int boxHeight = sortedScores.size() * 35 + BOX_PADDING;
        int boxX = WIDTH / 2 - BOX_WIDTH / 2;
        int boxY = HEIGHT / 2 - boxHeight / 2;

        fill(winnerColor, 100);
        stroke(0);
        rect(boxX, boxY, BOX_WIDTH, boxHeight);

        textAlign(CENTER, CENTER);
        textSize(30);
        fill(winnerColor, 100);
        text("Winner is player " + winner.getKey(), WIDTH / 2, boxY - 40);

        displayFinalScores = true;
    }

    /**
     * Displays the final scores with an animation effect.
     */
    private void displayFinalScores() {
        prepareAndSortScores();

        if (millis() - lastScoreUpdateTime > SCORE_UPDATE_INTERVAL && currentScoreIndex < sortedScores.size()) {
            lastScoreUpdateTime = millis();
            currentScoreIndex++;
        }

        textAlign(CENTER, CENTER);
        textSize(22);
        float y = HEIGHT / 2 - (sortedScores.size() * 15);
        for (int i = 0; i < currentScoreIndex; i++) {
            Map.Entry<String, Integer> score = sortedScores.get(i);
            fill(levelManager.getPlayerColors().get(score.getKey()));
            text("PLAYER " + score.getKey() + "   " + score.getValue(), WIDTH / 2, y);
            y += 35;
        }
    }

    /**
     * Displays the current scoreboard.
     */
    private void displayScoreboard() {
        Level currentLevel = levelManager.getCurrentLevel();
        if (currentLevel != null) {
            Scoreboard scoreboard = currentLevel.getScoreboard();
            Map<String, Integer> scores = new TreeMap<>(scoreboard.getScores());

            int scoreCount = scores.size();
            int scoreboardHeight = SCOREBOARD_TITLE_HEIGHT + SCOREBOARD_ROW_HEIGHT * scoreCount;
            int scoreboardX = width - SCOREBOARD_WIDTH - SCOREBOARD_X_OFFSET;
            int scoreboardY = SCOREBOARD_Y_OFFSET;

            textSize(16);
            textAlign(LEFT, TOP);

            fill(255, 255, 255, 100);
            stroke(0);
            rect(scoreboardX, scoreboardY, SCOREBOARD_WIDTH, scoreboardHeight);

            fill(0);
            text("SCORES", scoreboardX + 40, scoreboardY + 5);

            int nameColumnX = scoreboardX + 10;
            int scoreColumnX = scoreboardX + 110;
            int textY = scoreboardY + SCOREBOARD_TITLE_HEIGHT;

            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                String player = entry.getKey();
                Integer score = entry.getValue();

                fill(levelManager.getPlayerColors().get(player));
                text("PLAYER " + player, nameColumnX, textY);
                text(score.toString(), scoreColumnX, textY);
                textY += SCOREBOARD_ROW_HEIGHT;
            }
        }
    }

    public LevelManager getLevelManager(){
        return levelManager;
    }

    
}
