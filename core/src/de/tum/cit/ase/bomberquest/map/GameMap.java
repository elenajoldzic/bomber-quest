package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.BomberQuestGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Represents the game map.
 * Holds all the objects and entities in the game.
 */
public class GameMap {

    // A static block is executed once when the class is referenced for the first time.
    static {
        // Initialize the Box2D physics engine.
        com.badlogic.gdx.physics.box2d.Box2D.init();
    }

    // Box2D physics simulation parameters
    private static final float TIME_STEP = 1f / Gdx.graphics.getDisplayMode().refreshRate;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;

    private float physicsTime = 0;

    private final BomberQuestGame game;
    private final World world;

    private final Player player;
    private final Chest chest;
    private final Flowers[][] flowers;
    private final DestructibleWall destructibleWalls;
    private final IndestructibleWall indestructibleWalls;
    private final Exit exit;
    private final Entrance entrance;
    private final ConcurrentBomb concurrentBomb;
    private final BlastRadius blastRadius;
    private final Enemy enemy;

    // (Elena)
    private final List<Bomb> bombs; // List to store active bombs

    public GameMap(BomberQuestGame game) {
        this.game = game;
        this.world = new World(Vector2.Zero, true);

        this.player = new Player(this.world, 1, 3);
        this.chest = new Chest(world, 3, 3);

        this.destructibleWalls = new DestructibleWall(world, 4, 5);
        this.indestructibleWalls = new IndestructibleWall(world, 5, 5);
        this.exit = new Exit(world, 6, 5);
        this.entrance = new Entrance(world, 6, 6);
        this.concurrentBomb = new ConcurrentBomb(world, 5, 3);
        this.blastRadius = new BlastRadius(world, 4, 2);
        this.enemy = new Enemy(world, 5, 2);

        this.flowers = new Flowers[7][7];
        for (int i = 0; i < flowers.length; i++) {
            for (int j = 0; j < flowers[i].length; j++) {
                this.flowers[i][j] = new Flowers(i, j);
            }
        }

        // Elena
        this.bombs = new ArrayList<>(); // Initialize the bomb list
    }

    /**
     * Updates the game state. This is called once per frame.
     * Every dynamic object in the game should update its state here.
     * @param frameTime the time that has passed since the last update
     */
    public void tick(float frameTime) {
        this.enemy.tick(frameTime);
        this.player.update(frameTime);

        // Update bombs
        Iterator<Bomb> iterator = bombs.iterator();
        while (iterator.hasNext()) {
            Bomb bomb = iterator.next();
            if (bomb.update(frameTime)) {
                bomb.explode(world);
                iterator.remove();
                // TODO: Handle explosion effects and damage logic
            }
        }

        doPhysicsStep(frameTime);
    }

    /**
     * Places a bomb at the specified position.
     * @param x The x-coordinate of the bomb.
     * @param y The y-coordinate of the bomb.
     * @param blastRadius The blast radius of the bomb.
     */
    public void placeBomb(float x, float y, int blastRadius) {
        Bomb bomb = new Bomb(world, x, y, blastRadius);
        bombs.add(bomb);
    }

    /**
     * Performs as many physics steps as necessary to catch up to the given frame time.
     * This will update the Box2D world by the given time step.
     * @param frameTime Time since last frame in seconds
     */
    private void doPhysicsStep(float frameTime) {
        this.physicsTime += frameTime;
        while (this.physicsTime >= TIME_STEP) {
            this.world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            this.physicsTime -= TIME_STEP;
        }
    }

    public World getWorld() {
        return world;
    }

    public Player getPlayer() {
        return player;
    }

    public Chest getChest() {
        return chest;
    }

    public DestructibleWall getDestructibleWalls() {
        return destructibleWalls;
    }

    public IndestructibleWall getIndestructibleWalls() {
        return indestructibleWalls;
    }

    public Exit getExit() {
        return exit;
    }

    public Entrance getEntrance() {
        return entrance;
    }

    public ConcurrentBomb getConcurrentBomb() {
        return concurrentBomb;
    }

    public BlastRadius getBlastRadius() {
        return blastRadius;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public List<Flowers> getFlowers() {
        return Arrays.stream(flowers).flatMap(Arrays::stream).toList();
    }

    // Elena
    /**
     * Returns the list of active bombs.
     * @return List of bombs.
     */
    public List<Bomb> getBombs() {
        return bombs;
    }
}
