package de.tum.cit.ase.bomberquest.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import de.tum.cit.ase.bomberquest.gamemechanism.GameTimer;
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
        font.getData().setScale(0.75f,0.75f);

        // Draw the HUD elements
        //Draw "Press Esc to Pause!" text
        font.draw(spriteBatch, "Press Esc to Pause!", 10, Gdx.graphics.getHeight() - 10);

        // Calculate and display the remaining time in MM:SS format
        long elapsedTime = TimeUtils.timeSinceMillis(gameTimer.getStartTime());
        long remainingTimeMillis = Math.max(0, GameTimer.TOTAL_TIME_MILLIS - elapsedTime);
        long minutes = remainingTimeMillis / 60000;
        long seconds = (remainingTimeMillis % 60000) / 1000;
        String timeText = String.format("Time Remaining: %02d:%02d", minutes, seconds);
        font.draw(spriteBatch, timeText, 10, Gdx.graphics.getHeight() - 40);

        //Draw concurrent bomb count in HUD (yellow)
        font.setColor(Color.GOLD);
        font.draw(spriteBatch, "Bombs placeable: " + player.getConcurrentBombCount(), 10, Gdx.graphics.getHeight() - 80);
        font.setColor(Color.GOLD);

        //Draw blast radius value in HUD (yellow)
        font.setColor(Color.GOLD);
        font.draw(spriteBatch, "Blast radius:  " + player.getBlastRadius(), 10, Gdx.graphics.getHeight() - 110);
        font.setColor(Color.GOLD);

        //Draw the "Clear enemies to unlock the exit!" warning, if enemies are cleared, draw the "exit unlocked" text
        if(!map.enemiesCleared){
            font.setColor(Color.FIREBRICK);
            font.draw(spriteBatch, "Clear enemies to unlock the exit!", 10, Gdx.graphics.getHeight() - 150);
            font.setColor(Color.WHITE);
        }else{
            font.setColor(Color.GREEN);
            font.draw(spriteBatch, "EXIT UNLOCKED!", 10, Gdx.graphics.getHeight() - 180);
            font.setColor(Color.WHITE);
        }

        //Draw the count for remaining enemies
        if(!map.enemiesCleared) {
            font.draw(spriteBatch, "Enemies left: " + map.getEnemies().size(), 10, Gdx.graphics.getHeight() - 180);
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
