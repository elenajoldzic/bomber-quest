package de.tum.cit.ase.bomberquest.gamemechanism;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;
import de.tum.cit.ase.bomberquest.map.GameMap;
import de.tum.cit.ase.bomberquest.screen.*;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * The BomberQuestGame class represents the core of the Bomber Quest game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class BomberQuestGame extends Game {

    /**
     * Sprite Batch for rendering game elements.
     * This eats a lot of memory, so we only want one of these.
     */
    private SpriteBatch spriteBatch;

    /** The game's UI skin. This is used to style the game's UI elements. */
    private Skin skin;
    
    /**
     * The file chooser for loading map files from the user's computer.
     * This will give you access to a {@link com.badlogic.gdx.files.FileHandle} object,
     * which you can use to read the contents of the map file as a String, and then parse it into a {@link GameMap}.
     */
    private final NativeFileChooser fileChooser;
    
    /**
     * The map. This is where all the game objects are stored.
     * This is owned by {@link BomberQuestGame} and not by {@link GameScreen}
     * because the map should not be destroyed if we temporarily switch to another screen.
     */
    private GameMap map;
    private GameTimer gameTimer;  // Declare GameTimer here


    /**
     * Constructor for BomberQuestGame.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    public BomberQuestGame(NativeFileChooser fileChooser) {
        super();
        this.fileChooser = fileChooser;
    }

    /**
     * Called when the game is created. Initializes the SpriteBatch and Skin.
     * During the class constructor, libGDX is not fully initialized yet.
     * Therefore this method serves as a second constructor for the game,
     * and we can use libGDX resources here.
     */
    @Override
    public void create() {
        this.spriteBatch = new SpriteBatch(); // Create SpriteBatch for rendering
        this.skin = new Skin(Gdx.files.internal("skin/craftacular/craftacular-ui.json")); // Load UI skin
        this.map = new GameMap(this); // Create a new game map (you should change this to load the map from a file instead)
        this.gameTimer=new GameTimer(this);
        MusicTrack.MENUMUSIC.play(); // Play some background music
        goToStart();
    }

    public void goToStart() {
        this.setScreen(new StartScreen(this)); // Set the current screen to MenuScreen
    }
    /**
     * Switches to the menu screen.
     */
    public void goToPause() {
        gameTimer.pause();
        this.setScreen(new PauseScreen(this)); // Set the current screen to PauseScreen
    }

    /**
     * Switches to the game screen.
     */
    public void goToGame() {
        gameTimer.resume();
        this.setScreen(new GameScreen(this,gameTimer)); // Set the current screen to GameScreen
    }

    /** Returns the skin for UI elements. */
    public Skin getSkin() {
        return skin;
    }

    /** Returns the main SpriteBatch for rendering. */
    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }
    
    /** Returns the current map, if there is one. */
    public GameMap getMap() {
        return map;
    }
    
    /**
     * Switches to the given screen and disposes of the previous screen.
     * @param screen the new screen
     */
    @Override
    public void setScreen(Screen screen) {
        Screen previousScreen = super.screen;
        super.setScreen(screen);
        if (previousScreen != null) {
            previousScreen.dispose();
        }
        if(screen instanceof GameScreen){
            MusicTrack.WALKING.dispose();
            MusicTrack.MENUMUSIC.stop();
            MusicTrack.BACKGROUND.play();
        }
        if(screen instanceof StartScreen){
            MusicTrack.WALKING.dispose();
            MusicTrack.BACKGROUND.stop();
            MusicTrack.MENUMUSIC.dispose();
            MusicTrack.MENUMUSIC.play();
        }
        if (screen instanceof WinScreen){
            MusicTrack.WALKING.dispose();
            MusicTrack.BACKGROUND.stop();
            MusicTrack.WINSOUND.play();
        }
        if (screen instanceof PauseScreen){
            MusicTrack.WALKING.dispose();
            MusicTrack.BACKGROUND.stop();
            MusicTrack.MENUMUSIC.play();
        }
        if (screen instanceof YouLoseScreen){
            MusicTrack.WALKING.dispose();
            MusicTrack.BACKGROUND.stop();

        }
    }

    /**
     * Loads a new game map and starts the game.
     */
    public void loadNewGame() throws IOException {

            NativeFileChooserConfiguration config = new NativeFileChooserConfiguration();
            config.directory = new FileHandle(new File(System.getProperty("user.dir")));
            NativeFileChooserCallback callback = new NativeFileChooserCallback() {
                @Override
                public void onFileChosen(FileHandle file) {
                    if (file != null) {
                        String filePath = file.path();  // Get the path of the selected file

                        // Create a new GameMap instance and load the map data
                        GameMap map = new GameMap(BomberQuestGame.this);
                        map.loadTheMap(filePath);  // Load the map from the file

                        // Set the loaded map to the current game map
                        BomberQuestGame.this.map = map;
                        gameTimer.reset();
                        MusicTrack.BACKGROUND.dispose();
                        MusicTrack.BACKGROUND.play();
                        goToGame();
                    }

                }

                @Override
                public void onCancellation() {
                    System.out.println("File selection was cancelled.");
                }

                @Override
                public void onError(Exception exception) {
                    System.err.println("Error loading map file: " + exception.getMessage());
                    exception.printStackTrace();
                }
            };

            fileChooser.chooseFile(config, callback);

    }


    /** Cleans up resources when the game is disposed. */
    @Override
    public void dispose() {
        getScreen().hide(); // Hide the current screen
        getScreen().dispose(); // Dispose the current screen
        spriteBatch.dispose(); // Dispose the spriteBatch
        skin.dispose(); // Dispose the skin
    }
}
