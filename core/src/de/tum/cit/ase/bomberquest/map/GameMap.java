package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;
import de.tum.cit.ase.bomberquest.gamemechanism.MapLoader;
import de.tum.cit.ase.bomberquest.powerups.BlastRadius;
import de.tum.cit.ase.bomberquest.powerups.ConcurrentBomb;
import de.tum.cit.ase.bomberquest.powerups.PowerUp;

import java.util.*;

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
    private Player player;
    private Flowers[][] flowers;
    private List<DestructibleWall> destructibleWalls= new ArrayList<>();;
    private List<IndestructibleWall> indestructibleWalls= new ArrayList<>();
    private List<ExplosionTile> explosionTiles = new ArrayList<>();
    private Exit exit;
    private List<PowerUp> powerUps = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();
    private List<Bomb> bombs = new ArrayList<>();
    private List<Body> bodiesToDestroy = new ArrayList<>();  // Queue to hold bodies to remove
    private List<DestructibleWall> wallsToRemove = new ArrayList<>();
    private List<Enemy> enemiesToRemove = new ArrayList<>();
    public boolean enemiesCleared=false;

    public GameMap(BomberQuestGame game) {
        this.game = game;
        this.world = new World(Vector2.Zero, true);
    }

    public void loadTheMap(String path) {
        MapLoader mapLoader = new MapLoader();
        mapLoader.loadMap(path);

        // Iterate through the map properties file and split the digits into x and y coordinates
        for (Map.Entry<String, Set<Integer>> entry : mapLoader.getMapData().entrySet()) {
            String[] coordinates = entry.getKey().split(","); // "x,y"
            int x = Integer.parseInt(coordinates[0].trim());
            int y = Integer.parseInt(coordinates[1].trim());

            boolean hasDestructibleWall = entry.getValue().contains(MapLoader.DESTRUCTIBLE_WALL);

            // Iterate through all object types except destructible walls (we will create them latest to ensure they spawn on top of everything
            for (int objectType : entry.getValue()) {
                if (objectType == MapLoader.DESTRUCTIBLE_WALL) continue;

                // Create objects based on the type
                switch (objectType) {
                    case MapLoader.INDESTRUCTIBLE_WALL:
                        indestructibleWalls.add(new IndestructibleWall(world, x, y));
                        break;

                    case MapLoader.ENTRANCE:
                        this.player = new Player(world, x, y); // Create the player object
                        break;

                    case MapLoader.EXIT:
                        this.exit = new Exit(world, x, y); // Create the exit object
                        break;

                    case MapLoader.ENEMY:
                        enemies.add(new Enemy(world, x, y)); // Create an enemy
                        break;

                    case MapLoader.BOMB_POWER_UP:
                        powerUps.add(new ConcurrentBomb(world, x, y));// Power-up (e.g., bomb power-up)
                        break;

                    case MapLoader.BLAST_RADIUS_POWER_UP:
                        powerUps.add(new BlastRadius(world, x, y)); // Power-up (e.g., blast radius)
                        break;

                    default:
                        throw new RuntimeException("Unknown object type: " + objectType);
                }
            }

            // Always add the destructible wall last if it exists
            if (hasDestructibleWall) {
                destructibleWalls.add(new DestructibleWall(world, x, y));
            }
        }

        // Initialize flowers array
        this.flowers = new Flowers[50][50];
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
        updateEnemies(frameTime);
        this.player.update(frameTime);
        updateBombs(frameTime);
        updateExplosionTiles(frameTime);
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

    //Update methods
    //CHECKS THE BOMB AND IF IT IS EXPLODED, REMOVES FROM THE LIST AND ALSO REMOVES THE BODY
    public void updateBombs(float deltaTime) {
        Iterator<Bomb> iterator = bombs.iterator();
        while (iterator.hasNext()) {
            Bomb bomb = iterator.next();
            bomb.update(deltaTime);
            if (bomb.isExploded()) {
                iterator.remove();
                bomb.removeBomb(bomb);// Remove exploded bombs
            }
        }
    }
    public void updateExplosionTiles(float deltaTime){
        Iterator<ExplosionTile> iterator = explosionTiles.iterator();
        while (iterator.hasNext()) {
            ExplosionTile tile = iterator.next();
            tile.update(deltaTime);
            if (tile.isAnimationFinished()) {
                iterator.remove();
            }
        }
    }
    public void updateEnemies(float deltaTime) {
        for (Enemy enemy : enemies) {
            enemy.update(deltaTime);
        }
        if(enemies.isEmpty()&& !exit.isActive()){
            exit.getBody().setActive(true);
            enemiesCleared=true;
            MusicTrack.EXITREVEAL.play();
        }
    }

//Getters
    public World getWorld() {
        return world;
    }
    /**
     * Returns the player on the map.
     */
    public Player getPlayer() {
        return player;
    }
    public Exit getExit() {
        return exit;
    }
    public List<Enemy> getEnemies() {
        return enemies;
    }
    public List<DestructibleWall> getDestructibleWalls() {
        return destructibleWalls;
    }
    public List<IndestructibleWall> getIndestructibleWalls() {
        return indestructibleWalls;
    }
    public List<PowerUp> getPowerUps() {
        return powerUps;
    }
    public List<ExplosionTile> getExplosionTiles() {
        return explosionTiles;
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
    public BomberQuestGame getGame() {
        return game;
    }


//Removers
    public void removeDestructibleWalls(DestructibleWall wall) {
        if (wall == null) return;
        // Safely destroy the physics body associated with the wall
        if (wall.getBody() != null) {
            bodiesToDestroy.add(wall.getBody());
        }
        // Remove the wall from the list of destructible walls
        getDestructibleWalls().remove(wall);
    }
    public void removePowerUp(PowerUp powerUp) {
        if (powerUp == null) return;

        if (powerUp.getBody() != null) {
            bodiesToDestroy.add(powerUp.getBody()); // Queue the body for destruction
            powerUp.setBody(null); // Nullify to avoid reuse of the destroyed body
        }
        powerUps.remove(powerUp); // Remove the power-up from the game list
    }
    public void removeEnemies(Enemy enemy) {
        if (enemy == null) return;

        // Safely destroy the physics body associated with the wall
        if (enemy.getBody() != null) {
            bodiesToDestroy.add(enemy.getBody());
        }
        // Remove the wall from the list of destructible walls
        getEnemies().remove(enemy);
    }

//Bomb placing on map
    public void placeBomb() {
        if (getBombs().size() < player.getConcurrentBombCount()) {
            float bombX = MathUtils.round(player.getX());
            float bombY = MathUtils.round(player.getY());
            Bomb bomb = new Bomb(bombX, bombY, player.getBlastRadius(),this); // 2 seconds timer
            bombs.add(bomb);
            MusicTrack.BOMBPLACE.play();
        }
    }


    //Queuers for removals
    public void queueWallForRemoval(DestructibleWall wall) {
        if (!wallsToRemove.contains(wall)) {
            wallsToRemove.add(wall);
        }
    }
    public void queueEnemyForRemoval(Enemy enemy) {
        if (!enemiesToRemove.contains(enemy)) {
            enemiesToRemove.add(enemy);
        }
    }

    //Processing the removal queues
    public void processPendingWallRemovals() {
        for (DestructibleWall wall : wallsToRemove) {
            removeDestructibleWalls(wall);
        }
        wallsToRemove.clear();
    }
    public void processPendingEnemyRemovals() {
        for (Enemy enemy: enemiesToRemove) {
            removeEnemies(enemy);
        }
        enemiesToRemove.clear();
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

}
