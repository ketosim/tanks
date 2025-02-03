package Tanks;

import java.util.HashMap;
import java.util.Map;

/**
 * The Scoreboard class manages the scores of players in the game.
 * It provides methods to add, retrieve, and reset scores.
 */
public class Scoreboard {
    private Map<String, Integer> scores;

    /**
     * Constructs a new Scoreboard with an empty score map.
     */
    public Scoreboard() {
        scores = new HashMap<>();
    }

    /**
     * Adds a score to the specified player's total score.
     * If the player does not exist in the scoreboard, they are added with the given score.
     *
     * @param playerName The name of the player.
     * @param score The score to be added to the player's total score.
     */
    public void addScore(String playerName, int score) {
        // System.out.println("Score updating " + playerName + " +++++++ " + score);
        scores.put(playerName, scores.getOrDefault(playerName, 0) + score);
    }

    /**
     * Returns a map of player names to their corresponding scores.
     *
     * @return A map containing the scores of all players.
     */
    public Map<String, Integer> getScores() {
        return new HashMap<>(scores); 
    }

    /**
     * Resets the scoreboard, clearing all player scores.
     */
    public void reset() {
        scores.clear();
    }

    /**
     * Returns the number of players with scores in the scoreboard.
     *
     * @return The number of players with scores.
     */
    public int getScoreCount() {
        return scores.size();
    }

    /**
     * Returns the score of the specified player.
     * If the player does not exist in the scoreboard, returns 0.
     *
     * @param playerName The name of the player.
     * @return The score of the player, or 0 if the player does not exist.
     */
    public int getScore(String playerName) {
        return scores.getOrDefault(playerName, 0);
    }
}
