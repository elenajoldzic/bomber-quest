package de.tum.cit.ase.bomberquest.gamemechanism;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.TimeUtils;
import de.tum.cit.ase.bomberquest.screens.YouLoseScreen;

/**
 * Represents a game timer that tracks remaining time and handles the timeout event.
 */
public class GameTimer {

    public static final long TOTAL_TIME_MILLIS = 5 * 60 * 1000; // 2:50 in milliseconds

    private final BomberQuestGame game;
    private long startTime;
    private long pauseTime;
    private boolean isPaused;
    private final BitmapFont font;

    public GameTimer(BomberQuestGame game) {
        this.game = game;
        this.startTime = TimeUtils.millis(); // Set the initial start time
        this.font = new BitmapFont();
        this.isPaused = false;
        this.pauseTime = 0;
    }

    /**
     * Updates the game timer and checks for timeout.
     */
    public void update() {
        if (!isPaused) {
            long elapsedTime = TimeUtils.timeSinceMillis(startTime);
            if (elapsedTime >= TOTAL_TIME_MILLIS) {
                handleTimeout();
            }
        }
    }

    /**
     * Handles the timeout event by transitioning to the game over screen.
     */
    private void handleTimeout() {
        game.setScreen(new YouLoseScreen(game)); // Switch to the YouLoseScreen when time runs out
    }

    /**
     * Disposes of resources used by the timer.
     */
    public void dispose() {
        font.dispose();
    }

    /**
     * Pauses the game timer. Stores the time when the game is paused.
     */
    public void pause() {
        if (!isPaused) {
            isPaused = true;
            pauseTime = TimeUtils.millis(); // Record the time when the game is paused
        }
    }

    /**
     * Resumes the game timer. Adjusts the start time to account for the time paused.
     */
    public void resume() {
        if (isPaused) {
            isPaused = false;
            long pauseDuration = TimeUtils.millis() - pauseTime; // Calculate how long the game was paused
            startTime += pauseDuration; // Adjust the start time to continue from where we left off
        }
    }

    /**
     * Gets the start time of the game timer.
     */
    public long getStartTime() {
        return startTime;
    }


    /**
     * Resets the game timer. Set the start time to the current time
     */
    public void reset() {
        startTime = TimeUtils.millis();
    }
}
