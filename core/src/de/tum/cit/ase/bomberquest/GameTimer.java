package de.tum.cit.ase.bomberquest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import de.tum.cit.ase.bomberquest.screen.YouLoseScreen;

/**
 * Represents a game timer that tracks remaining time and handles the timeout event.
 */
public class GameTimer {

    public static final long TOTAL_TIME_MILLIS = 100*10 * 1000; // 2:50 in milliseconds

    private final BomberQuestGame game; // Reference to the game instance
    public final long startTime; // Time when the timer started
    private final BitmapFont font; // Font for displaying the timer

    /**
     * Constructor for the GameTimer.
     * @param game the main game instance
     */
    public GameTimer(BomberQuestGame game) {
        this.game = game;
        this.startTime = TimeUtils.millis();
        this.font = new BitmapFont(); // Create a basic font for displaying the timer
    }

    /**
     * Updates the game timer and checks for timeout.
     */
    public void update() {
        long elapsedTime = TimeUtils.timeSinceMillis(startTime);
        if (elapsedTime >= TOTAL_TIME_MILLIS) {
            // Timer has run out; handle the player losing
            handleTimeout();
        }
    }

    /**
     * Renders the remaining time on the screen.
     * @param spriteBatch the SpriteBatch used for rendering
     */
    public void render(SpriteBatch spriteBatch) {

        long elapsedTime = TimeUtils.timeSinceMillis(startTime);
        long remainingTimeMillis = Math.max(0, TOTAL_TIME_MILLIS - elapsedTime);

        // Calculate minutes and seconds remaining
        long minutes = remainingTimeMillis / 60000;
        long seconds = (remainingTimeMillis % 60000) / 1000;

        // Display the time in "MM:SS" format
        String timeText = String.format("%02d:%02d", minutes, seconds);
        spriteBatch.begin();
        font.draw(spriteBatch, "Time Remaining: " + timeText, 10, Gdx.graphics.getHeight() - 10);
        spriteBatch.end();

    }



    /**
     * Handles the timeout event by killing the player and switching to the YouLoseScreen.
     */
    public void handleTimeout() {
        game.setScreen(new YouLoseScreen(game)); // Switch to the YouLoseScreen
    }

    /**
     * Disposes of resources used by the timer.
     */
    public void dispose() {
        font.dispose();
    }
}
