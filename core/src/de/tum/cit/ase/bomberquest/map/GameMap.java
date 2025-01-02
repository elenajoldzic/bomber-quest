package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.powerups.BlastRadius;
import de.tum.cit.ase.bomberquest.powerups.ConcurrentBomb;
import de.tum.cit.ase.bomberquest.powerups.PowerUp;

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

    // Box2D physics simulation parameters (you can experiment with these if you want, but they work well as they are)
    /**
     * The time step for the physics simulation.
     * This is the amount of time that the physics simulation advances by in each frame.
     * It is set to 1/refreshRate, where refreshRate is the refresh rate of the monitor, e.g., 1/60 for 60 Hz.
     */
    private static final float TIME_STEP = 1f / Gdx.graphics.getDisplayMode().refreshRate;
    /**
     * The number of velocity iterations for the physics simulation.
     */
    private static final int VELOCITY_ITERATIONS = 6;
    /**
     * The number of position iterations for the physics simulation.
     */
    private static final int POSITION_ITERATIONS = 2;
    /**
     * The accumulated time since the last physics step.
     * We use this to keep the physics simulation at a constant rate even if the frame rate is variable.
     */
    private float physicsTime = 0;

    /**
     * The game, in case the map needs to access it.
     */
    private final BomberQuestGame game;
    /**
     * The Box2D world for physics simulation.
     */
    private final World world;

    // Game objects
    private final Player player;

    private final Chest chest;

    private final Flowers[][] flowers;

    //private final DestructibleWall destructibleWalls; //THESE ARE THE WALLS
    private List<DestructibleWall> destructibleWalls= new ArrayList<>();

    //private final IndestructibleWall indestructibleWalls; //THESE ARE THE WALLS
    private List<IndestructibleWall> indestructibleWalls= new ArrayList<>();


    private final Exit exit; // THIS IS THE EXIT

    private final Entrance entrance; // THIS IS THE ENTRANCE

    //private final ConcurrentBomb concurrentBomb; // THIS IS THE CONCURRENT BOMB

    //private final BlastRadius blastRadius; // THIS IS THE BLAST RADIUS

    private List<PowerUp> powerUps = new ArrayList<>();

    //private final Enemy enemy; // THIS IS THE ENEMY
    private List<Enemy> enemies = new ArrayList<>();

    private List<Bomb> bombs = new ArrayList<>();

    List<Body> bodiesToDestroy = new ArrayList<>();  // Queue to hold bodies to remove

    public GameMap(BomberQuestGame game) {
        this.game = game;
        this.world = new World(Vector2.Zero, true);
        // Create a player with initial position (1, 3)
        this.player = new Player(this.world, 1, 3);
        // Create a chest in the middle of the map
        this.chest = new Chest(world, 3, 3);




        addIndestructibleWalls (new IndestructibleWall(world, 5, 5)); // INITIALIZED WALLS

        this.exit = new Exit(world, 6, 5); // INITIALIZED EXIT

        this.entrance = new Entrance(world, 6, 6); // INITIALIZED ENTRANCE

        addIndestructibleWalls (new IndestructibleWall(world, 5, 5)); // INITIALIZED WALLS
        addDestructibleWalls(new DestructibleWall(world, 4, 5)); // INITIALIZED WALLS
        //this.concurrentBomb = new ConcurrentBomb(world, 5, 3); // INITIALIZED CONCURRENT BOMB
        addPowerUp(new ConcurrentBomb(world, 5, 3));
        //this.blastRadius = new BlastRadius(world, 4, 2); // INITIALIZED BLAST RADIUS
        addPowerUp(new BlastRadius(world, 4, 2));
        addEnemies(new Enemy(world, 5, 2)); // INITIALIZED ENEMY
        addIndestructibleWalls (new IndestructibleWall(world, 5, 5)); // INITIALIZED WALLS
        addDestructibleWalls(new DestructibleWall(world, 4, 5)); // INITIALIZED WALLS

        // Create flowers in a 7x7 grid
        this.flowers = new Flowers[7][7];
        for (int i = 0; i < flowers.length; i++) {
            for (int j = 0; j < flowers[i].length; j++) {
                this.flowers[i][j] = new Flowers(i, j);
            }
        }
    }



    /**
     * Updates the game state. This is called once per frame.
     * Every dynamic object in the game should update its state here.
     *
     * @param frameTime the time that has passed since the last update
     */

    //WE NEED TO PUT UPDATE METHODS IN HERE IN ORDER ANIMATIONS TO WORK
    public void tick(float frameTime) {
        //this.player.tick(frameTime);
        updateEnemies(frameTime);
        this.player.update(frameTime);
        updateBombs(frameTime);
        doPhysicsStep(frameTime);
    }

    /**
     * Performs as many physics steps as necessary to catch up to the given frame time.
     * This will update the Box2D world by the given time step.
     *
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

    /**
     * Returns the player on the map.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the chest on the map.
     */
    public Chest getChest() {
        return chest;
    }


    public Exit getExit() {
        return exit;
    }

    public Entrance getEntrance() {
        return entrance;
    }

    /*public ConcurrentBomb getConcurrentBomb() {
        return concurrentBomb;
    }*/

    /*public BlastRadius getBlastRadius() {
        return blastRadius;
    }*/

    public void addPowerUp(PowerUp powerUp) {
        powerUps.add(powerUp);
    }

    public void addEnemies(Enemy enemy){
        enemies.add(enemy);
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void updateEnemies(float deltaTime) {
        for (Enemy enemy : enemies) {
            enemy.update(deltaTime);
        }
    }

    public void addDestructibleWalls(DestructibleWall destructibleWall) {
        destructibleWalls.add(destructibleWall);
    }

    public List<DestructibleWall> getDestructibleWalls() {
        return destructibleWalls;
    }

    public void removeDestructibleWalls(DestructibleWall destructibleWall) {
        if (destructibleWall == null) {
            return;
        }else if (destructibleWalls.contains(destructibleWall)) {
            destructibleWalls.remove(destructibleWall);
        }
    }

    public void addIndestructibleWalls(IndestructibleWall indestructibleWall) {
        indestructibleWalls.add(indestructibleWall);
    }

    public List<IndestructibleWall> getIndestructibleWalls() {
        return indestructibleWalls;
    }


    public void removePowerUp(PowerUp powerUp) {
        if (powerUp == null) return;

        if (powerUp.getBody() != null) {
            bodiesToDestroy.add(powerUp.getBody()); // Queue the body for destruction
            powerUp.setBody(null); // Nullify to avoid reuse of the destroyed body
        }

        powerUps.remove(powerUp); // Remove the power-up from the game list

        /*if (powerUp == null) return;

            // Safely remove the power-up's physics body from the Box2D world
            if (powerUp.getBody() != null) {
                world.destroyBody(powerUp.getBody());
                powerUp.setBody(null); // Nullify to avoid accessing destroyed bodies
            }

            // Remove the power-up from the map's list of power-ups

            powerUps.remove(powerUp);*/
    }

    public void processPendingBodyDestruction() {
        for (Body body : bodiesToDestroy) {
            try {
                if (body != null) {
                    world.destroyBody(body);
                }
            } catch (Exception e) {
                System.err.println("Error destroying body: " + e.getMessage());
            }
        }
        bodiesToDestroy.clear(); // Clear the list after processing
    }

    public List<PowerUp> getPowerUps() {
        return powerUps;
    }



    /**
     * Returns the flowers on the map.
     */
    public List<Flowers> getFlowers() {
        return Arrays.stream(flowers).flatMap(Arrays::stream).toList();
    }

    public List<Bomb> getBombs() {
        return bombs;
    }

    public void addBomb(Bomb bomb) {
        bombs.add(bomb);
    }

    public void removeBomb(Bomb bomb) {
            // Trigger explosion effects (e.g., damaging nearby objects)

            // Queue the bomb's body for destruction
            bodiesToDestroy.add(bomb.getBody());

            // Remove the bomb from the list of active bombs
            bombs.remove(bomb);

    }

    public void placeBomb() {
        if (getBombs().size() < player.getConcurrentBombCount()) {
            float bombX = MathUtils.round(player.getX());
            float bombY = MathUtils.round(player.getY());
            Bomb bomb = new Bomb(world,bombX, bombY, player.getBlastRadius()); // 2 seconds timer
            addBomb(bomb);
        }
    }

    //CHECKS THE BOMB AND IF IT IS EXPLODED, REMOVES FROM THE LIST AND ALSO REMOVES THE BODY
    public void updateBombs(float deltaTime) {
        Iterator<Bomb> iterator = bombs.iterator();
        while (iterator.hasNext()) {
            Bomb bomb = iterator.next();
            bomb.update(deltaTime, world);
            if (bomb.isExploded()) {
                iterator.remove();
                removeBomb(bomb);// Remove exploded bombs
            }
        }
    }


}
