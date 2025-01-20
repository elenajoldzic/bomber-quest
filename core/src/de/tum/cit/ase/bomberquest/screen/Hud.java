package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import de.tum.cit.ase.bomberquest.GameTimer;
import de.tum.cit.ase.bomberquest.map.GameMap;
import de.tum.cit.ase.bomberquest.map.Player;

/**
 * A Heads-Up Display (HUD) that displays information on the screen.
 * It uses a separate camera so that it is always fixed on the screen.
 */

public class Hud {
    
    /** The SpriteBatch used to draw the HUD. This is the same as the one used in the GameScreen. */
    private final SpriteBatch spriteBatch;
    /** The font used to draw text on the screen. */
    private final BitmapFont font;
    /** The camera used to render the HUD. */
    private final OrthographicCamera camera;
    private final GameTimer gameTimer;

    private final Player player;
    private final GameMap map;
    
    public Hud(SpriteBatch spriteBatch, BitmapFont font, GameTimer gameTimer, Player player,GameMap map) {
        this.spriteBatch = spriteBatch;
        this.font = font;
        this.camera = new OrthographicCamera();
        this.gameTimer=gameTimer;
        this.player=player;
        this.map=map;
    }
    
    /**
     * Renders the HUD on the screen.
     * This uses a different OrthographicCamera so that the HUD is always fixed on the screen.
     */
    public void render() {
        // Render from the camera's perspective
        spriteBatch.setProjectionMatrix(camera.combined);

        // Start drawing
        spriteBatch.begin();
        // Draw the HUD elements
        font.draw(spriteBatch, "Press Esc to Pause!", 10, Gdx.graphics.getHeight() - 10);


        // Calculate and display the remaining time in MM:SS format
        long elapsedTime = TimeUtils.timeSinceMillis(gameTimer.startTime);
        long remainingTimeMillis = Math.max(0, GameTimer.TOTAL_TIME_MILLIS - elapsedTime);
        long minutes = remainingTimeMillis / 60000;
        long seconds = (remainingTimeMillis % 60000) / 1000;
        String timeText = String.format("Time Remaining: %02d:%02d", minutes, seconds);
        font.draw(spriteBatch, timeText, 10, Gdx.graphics.getHeight() - 50);

        font.draw(spriteBatch, "Bombs placeable: "+player.getConcurrentBombCount(), 10, Gdx.graphics.getHeight() - 90);

        font.draw(spriteBatch, "Blast radius:  "+player.getBlastRadius(), 10, Gdx.graphics.getHeight() - 130);

        if(!map.enemiesCleared){
            font.draw(spriteBatch, "Clear enemies to unlock the exit!", 10, Gdx.graphics.getHeight() - 170);
        }else{
            font.draw(spriteBatch, "EXIT UNLOCKED!", 10, Gdx.graphics.getHeight() - 170);
        }

        if(!map.enemiesCleared) {
            font.draw(spriteBatch, "Enemies left: " + map.getEnemies().size(), 10, Gdx.graphics.getHeight() - 210);
        }

        // Finish drawing
        spriteBatch.end();
    }
    
    /**
     * Resizes the HUD when the screen size changes.
     * This is called when the window is resized.
     * @param width The new width of the screen.
     * @param height The new height of the screen.
     */
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }
    
}
