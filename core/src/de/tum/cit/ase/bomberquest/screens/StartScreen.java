package de.tum.cit.ase.bomberquest.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.ase.bomberquest.gamemechanism.BomberQuestGame;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;

import java.io.IOException;

/**
 * The MenuScreen class is responsible for displaying the main menu of the game.
 * It extends the LibGDX Screen class and sets up the UI components for the menu.
 */
public class StartScreen implements Screen {

    private final Stage stage;
    private final SpriteBatch spriteBatch; // SpriteBatch for rendering the background
    private final Texture backgroundTexture; // Texture for the background

    /**
     * Constructor for StartScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public StartScreen(BomberQuestGame game) {
        var camera = new OrthographicCamera();
        camera.zoom = 1.5f; // Set camera zoom for a closer view

        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements

        spriteBatch = new SpriteBatch(); // Initialize the SpriteBatch
        backgroundTexture = new Texture(Gdx.files.internal("assets/texture/bomberman2.jpg"));
        // Load the background texture

        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage

        // Add a label as a title
        table.add(new Label("Welcome to the game!", game.getSkin(), "title")).padBottom(80).row();
        table.add(new Label("How to play:", game.getSkin(), "default")).padBottom(20).row();
        table.add(new Label("1. Press Arrow keys to move", game.getSkin(), "default")).padBottom(20).row();
        table.add(new Label("2. Press SPACE to plant bomb", game.getSkin(), "default")).padBottom(20).row();
        table.add(new Label("3. Kill all enemies and reach the exit", game.getSkin(), "default")).padBottom(80).row();
        table.add(new Label("Select map file below: ", game.getSkin(), "default")).padBottom(20).row();

        // New Game button
        TextButton newGameButton = new TextButton("New Game", game.getSkin());
        table.add(newGameButton).width(300).row();
        newGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    MusicTrack.BUTTONSOUND.play();
                    game.loadNewGame(); // Load a new game map and start the game
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Exit button
        TextButton exitButton = new TextButton("Exit", game.getSkin());
        table.add(exitButton).width(300).row();
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MusicTrack.BUTTONSOUND.play();
                Gdx.app.exit(); // Exit the game
            }
        });
    }

    /**
     * The render method is called every frame to render the menu screen.
     * It clears the screen, draws the background, and draws the stage.
     *
     * @param deltaTime The time in seconds since the last render.
     */
    @Override
    public void render(float deltaTime) {
        float frameTime = Math.min(deltaTime, 0.250f); // Cap frame time to 250ms to prevent spiral of death
        ScreenUtils.clear(Color.BLACK);

        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();

        stage.act(frameTime); // Update the stage
        stage.draw(); // Draw the stage
    }

    /**
     * Called when the screen size changes. Updates the stage's viewport.
     *
     * @param width  The new width of the screen.
     * @param height The new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // Update the stage viewport on resize
    }

    /**
     * Disposes of assets and resources used by this screen.
     * This includes the stage, SpriteBatch, and background texture.
     */
    @Override
    public void dispose() {
        stage.dispose();
        spriteBatch.dispose();
        backgroundTexture.dispose();
    }

    /**
     * Called when this screen becomes the current screen for the game.
     * Sets the input processor to the stage so it can handle user input.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Called when the application is paused. Not used in this screen.
     */
    @Override
    public void pause() {
    }

    /**
     * Called when the application is resumed. Not used in this screen.
     */
    @Override
    public void resume() {
    }

    /**
     * Called when this screen is no longer the current screen.
     * Not used in this screen.
     */
    @Override
    public void hide() {
    }
}
