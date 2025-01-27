package de.tum.cit.ase.bomberquest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.ase.bomberquest.gamemechanism.BomberQuestGame;
import de.tum.cit.ase.bomberquest.gamemechanism.GameTimer;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;
import de.tum.cit.ase.bomberquest.hud.Hud;
import de.tum.cit.ase.bomberquest.map.*;
import de.tum.cit.ase.bomberquest.powerups.BlastRadius;
import de.tum.cit.ase.bomberquest.powerups.ConcurrentBomb;
import de.tum.cit.ase.bomberquest.powerups.PowerUp;
import de.tum.cit.ase.bomberquest.texture.Drawable;

/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */

public class GameScreen implements Screen {

    /**
     * The size of a grid cell in pixels.
     * This allows us to think of coordinates in terms of square grid tiles
     * (e.g. x=1, y=1 is the bottom left corner of the map)
     * rather than absolute pixel coordinates.
     */
    public static final int TILE_SIZE_PX = 16;

    /**
     * The scale of the game.
     * This is used to make everything in the game look bigger or smaller.
     */
    public static final int SCALE = 4;

    private final BomberQuestGame game;
    private final SpriteBatch spriteBatch;
    private final GameMap map;
    private final Hud hud;
    private final OrthographicCamera mapCamera;
    private GameTimer gameTimer;
    private Player player;


    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(BomberQuestGame game, GameTimer gameTimer) {
        this.game = game;
        this.spriteBatch = game.getSpriteBatch();
        this.map = game.getMap();
        this.gameTimer=gameTimer;
        this.player = map.getPlayer();
        this.hud = new Hud(spriteBatch, game.getSkin().getFont("font"), gameTimer, player,map);
        this.mapCamera = new OrthographicCamera();
        this.mapCamera.setToOrtho(false);
        this.mapCamera.zoom=1.3f;

        // Set up the collision listener
        setupCollisionListener();
    }

    // THIS METHOD LOOKS FOR A COLLISION BETWEEN PLAYER AND OTHER OBJECTS
    private void setupCollisionListener() {
        map.getWorld().setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Object userDataA = contact.getFixtureA().getBody().getUserData();
                Object userDataB = contact.getFixtureB().getBody().getUserData();

                // Check if the player and enemy collide
                if ((userDataA instanceof Player && userDataB instanceof Enemy) ||
                        (userDataA instanceof Enemy && userDataB instanceof Player)) {
                    MusicTrack.PLAYERDIE.play();
                    // Transition to the YouLoseScreen
                    game.setScreen(new YouLoseScreen(game));
                }

                // Check if the player reaches the exit
                if ((userDataA instanceof Player && userDataB instanceof Exit) ||
                        (userDataA instanceof Exit && userDataB instanceof Player)) {
                    // Transition to the WinScreen
                    game.setScreen(new WinScreen(game));
                }

                //Check if the player touches a powerup
                if ((userDataA instanceof Player && userDataB instanceof PowerUp) ||
                        (userDataB instanceof Player && userDataA instanceof PowerUp)) {
                    Player player = (userDataA instanceof Player) ? (Player) userDataA : (Player) userDataB;
                    PowerUp powerUp = (userDataA instanceof PowerUp) ? (PowerUp) userDataA : (PowerUp) userDataB;
                    //call handling method
                    handlePowerUpPickup(player, powerUp);
                }
            }



        private void handlePowerUpPickup(Player player, PowerUp powerUp) {
            //Check if the powerup is BlastRadius
            if (powerUp instanceof BlastRadius) {
                MusicTrack.POWERUPSOUND.play();
                //Increase blast radius of bombs by 1
                player.setBlastRadius(Math.min(player.getBlastRadius() + 1, 9));
            }
            //Check if the powerup is ConcurrentBomb
            else if (powerUp instanceof ConcurrentBomb) {
                MusicTrack.POWERUPSOUND.play();
                //Increase concurrent bomb count by 1
                player.setConcurrentBombCount(Math.min(player.getConcurrentBombCount() + 1, 9));
            }
            // Remove the power-up from the map
            map.removePowerUp(powerUp);
        }
        @Override
            public void endContact(Contact contact) {

            }
            @Override
            public void preSolve(Contact contact, Manifold manifold) {
            }
            @Override
            public void postSolve(Contact contact, ContactImpulse contactImpulse) {
            }
        });
    }

    /**
     * The render method is called every frame to render the game.
     * @param deltaTime The time in seconds since the last render.
     */
    @Override
    public void render(float deltaTime) {
        // Check for escape key press to go back to the menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            MusicTrack.BUTTONSOUND.play();
            game.goToPause();
        }

        // Adjust camera zoom with '*' and '-'
        if (Gdx.input.isKeyPressed(Input.Keys.PLUS) || Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
            mapCamera.zoom = Math.max(mapCamera.zoom - 0.02f, 0.5f); // Zoom in (min zoom level: 0.5)
        }
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            mapCamera.zoom = Math.min(mapCamera.zoom + 0.02f, 2f); // Zoom out (max zoom level: 2)
        }

        // Check for space key press to plant a bomb
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            map.placeBomb();
        }

        // Clear the previous frame from the screen
        ScreenUtils.clear(Color.BLACK);

        // Cap frame time to 250ms to prevent spiral of death
        float frameTime = Math.min(deltaTime, 0.250f);

        // Update the map state
        map.tick(frameTime);

        // Process deferred body destructions
        map.processPendingBodyDestruction();
        map.processPendingWallRemovals();
        map.processPendingEnemyRemovals();

        // Update and render the timer
        gameTimer.update();

        // Update the camera
        updateCamera();

        // Render the map on the screen
        renderMap();

        // Render the HUD on the screen
        hud.render();
    }

    /**
     * Updates the camera to match the current state of the game.
     * Camera moves when player goes out of defined safe zone in the middle of screen.
     */
    private void updateCamera() {
        // Define the safe zone size (in pixels)
        float safeZoneWidth = 100 * SCALE;  // Width of the safe zone
        float safeZoneHeight = 100 * SCALE; // Height of the safe zone

        // Get the player's position
        float playerX = map.getPlayer().getX() * TILE_SIZE_PX * SCALE;
        float playerY = map.getPlayer().getY() * TILE_SIZE_PX * SCALE;

        // Calculate the safe zone bounds relative to the camera's center
        float safeZoneLeft = mapCamera.position.x - safeZoneWidth / 2f;
        float safeZoneRight = mapCamera.position.x + safeZoneWidth / 2f;
        float safeZoneBottom = mapCamera.position.y - safeZoneHeight / 2f;
        float safeZoneTop = mapCamera.position.y + safeZoneHeight / 2f;

        // Check if the player is outside the safe zone
        if (playerX < safeZoneLeft) {
            mapCamera.position.x -= (safeZoneLeft - playerX);
        } else if (playerX > safeZoneRight) {
            mapCamera.position.x += (playerX - safeZoneRight);
        }

        if (playerY < safeZoneBottom) {
            mapCamera.position.y -= (safeZoneBottom - playerY);
        } else if (playerY > safeZoneTop) {
            mapCamera.position.y += (playerY - safeZoneTop);
        }

        // Update the camera
        mapCamera.update();
    }

    private void renderMap() {
        // This configures the spriteBatch to use the camera's perspective when rendering
        spriteBatch.setProjectionMatrix(mapCamera.combined);

        // Start drawing
        spriteBatch.begin();

        // Render everything in the map here, in order from lowest to highest (later things appear on top)
        //Draw flowers
        for (Flowers flowers : map.getFlowers()) {
            draw(spriteBatch, flowers);
        }
       //Draws exit if it is active
        if (map.getExit().isActive()) {
            draw(spriteBatch, map.getExit());
        }
        //Draws Powerups
        for (PowerUp powerUp : map.getPowerUps()) {
            draw(spriteBatch, powerUp);
        }
        //Draw destructible walls
        for(DestructibleWall destructibleWall: map.getDestructibleWalls()){
            draw(spriteBatch, destructibleWall);
        }
       //Draw indestructible walls
        for(IndestructibleWall indestructibleWall: map.getIndestructibleWalls()){
            draw(spriteBatch, indestructibleWall);
        }
        // Draw bombs
        for (Bomb bomb : map.getBombs()) {
            draw(spriteBatch, bomb);
        }
        //Draw enemies
        for (Enemy enemy : map.getEnemies()) {
            draw(spriteBatch, enemy);
        }
        //Draw explosion tiles
        for (ExplosionTile tile : map.getExplosionTiles()) {
            draw(spriteBatch, tile);
        }
        //Draw player
        draw(spriteBatch, map.getPlayer());

        // Finish drawing, i.e., send the drawn items to the graphics card
        spriteBatch.end();
    }

    /**
     * Draws this object on the screen.
     * The texture will be scaled by the game scale and the tile size.
     * This should only be called between spriteBatch.begin() and spriteBatch.end(), e.g., in the renderMap() method.
     * @param spriteBatch The SpriteBatch to draw with.
     */
    private static void draw(SpriteBatch spriteBatch, Drawable drawable) {
        TextureRegion texture = drawable.getCurrentAppearance();
        // Drawable coordinates are in tiles, so we need to scale them to pixels
        float x = drawable.getX() * TILE_SIZE_PX * SCALE;
        float y = drawable.getY() * TILE_SIZE_PX * SCALE;
        // Additionally scale everything by the game scale
        float width = texture.getRegionWidth() * SCALE;
        float height = texture.getRegionHeight() * SCALE;
        spriteBatch.draw(texture, x, y, width, height);
    }


    /**
     * Called when the window is resized.
     * This is where the camera is updated to match the new window size.
     * @param width The new window width.
     * @param height The new window height.
     */
    @Override
    public void resize(int width, int height) {
        mapCamera.setToOrtho(false);
        hud.resize(width, height);
    }

    // Unused methods from the Screen interface
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
