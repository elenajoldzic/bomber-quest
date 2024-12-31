package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.GameTimer;
import de.tum.cit.ase.bomberquest.map.*;
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
    public GameScreen(BomberQuestGame game) {
        this.game = game;
        this.spriteBatch = game.getSpriteBatch();
        this.map = game.getMap();

        // Initialize the timer
        this.gameTimer = new GameTimer(game);

        this.player = map.getPlayer();

        this.hud = new Hud(spriteBatch, game.getSkin().getFont("font"), gameTimer, player);
        // Create and configure the camera for the game view
        this.mapCamera = new OrthographicCamera();
        this.mapCamera.setToOrtho(false);

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
                    // Transition to the YouLoseScreen
                    game.setScreen(new YouLoseScreen(game));
                }

                // Check if the player reaches the exit
                if ((userDataA instanceof Player && userDataB instanceof Exit) ||
                        (userDataA instanceof Exit && userDataB instanceof Player)) {
                    // Transition to the WinScreen
                    game.setScreen(new WinScreen(game));
                }
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
            game.goToMenu();
        }

        // Elena
        // Check for space key press to plant a bomb
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            placeBomb();
        }

        // Clear the previous frame from the screen
        ScreenUtils.clear(Color.BLACK);

        // Cap frame time to 250ms to prevent spiral of death
        float frameTime = Math.min(deltaTime, 0.250f);

        // Update the map state
        map.tick(frameTime);

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
     * Currently, this just centers the camera at the origin.
     */
    private void updateCamera() {
        mapCamera.setToOrtho(false);

        // THIS CODE MAKES THE CAMERA MOVE WITH PLAYER
        mapCamera.position.x = map.getPlayer().getX() * TILE_SIZE_PX * SCALE;
        mapCamera.position.y = map.getPlayer().getY() * TILE_SIZE_PX * SCALE;

        mapCamera.update(); // This is necessary to apply the changes
    }

    private void renderMap() {
        // This configures the spriteBatch to use the camera's perspective when rendering
        spriteBatch.setProjectionMatrix(mapCamera.combined);

        // Start drawing
        spriteBatch.begin();

        // Render everything in the map here, in order from lowest to highest (later things appear on top)
        for (Flowers flowers : map.getFlowers()) {
            draw(spriteBatch, flowers);
        }
        draw(spriteBatch, map.getChest());

        draw(spriteBatch, map.getDestructibleWalls()); // DRAWS THE DESTRUCTIBLE WALL
        draw(spriteBatch, map.getIndestructibleWalls()); // DRAWS THE INDESTRUCTIBLE WALL

        draw(spriteBatch, map.getExit()); // DRAWS THE EXIT
        draw(spriteBatch, map.getEntrance()); // DRAWS THE ENTRANCE

        draw(spriteBatch, map.getConcurrentBomb()); // DRAWS THE CONCURRENT BOMB
        draw(spriteBatch, map.getBlastRadius()); // DRAWS THE BLAST RADIUS

        draw(spriteBatch, map.getEnemy()); // DRAWS THE ENEMY
        draw(spriteBatch, map.getPlayer());

        // Elena
        // Draw bombs
        for (Bomb bomb : map.getBombs()) {
            draw(spriteBatch, bomb);
        }

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

    private void placeBomb() {
        if (map.getBombs().size() < player.getConcurrentBombCount()) {
            float bombX = MathUtils.round(player.getX());
            float bombY = MathUtils.round(player.getY());
            Bomb bomb = new Bomb(bombX, bombY, player.getBlastRadius()); // 2 seconds timer
            map.addBomb(bomb);
        }
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
