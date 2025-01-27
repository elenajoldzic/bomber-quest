package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.gamemechanism.BomberQuestGame;
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

    static {
        // Initialize the Box2D physics engine.
        com.badlogic.gdx.physics.box2d.Box2D.init();
    }

    private static final float TIME_STEP = 1f / Gdx.graphics.getDisplayMode().refreshRate;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private float physicsTime = 0;
    private int width = 0;
    private int height = 0;
    private final BomberQuestGame game;
    private final World world;
    private Player player;
    private Flowers[][] flowers;
    private List<DestructibleWall> destructibleWalls = new ArrayList<>();
    private List<IndestructibleWall> indestructibleWalls = new ArrayList<>();
    private List<ExplosionTile> explosionTiles = new ArrayList<>();
    private Exit exit;
    private List<PowerUp> powerUps = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();
    private List<Bomb> bombs = new ArrayList<>();
    private List<Body> bodiesToDestroy = new ArrayList<>();
    private List<DestructibleWall> wallsToRemove = new ArrayList<>();
    private List<Enemy> enemiesToRemove = new ArrayList<>();
    public boolean enemiesCleared = false;
    // Variables for screen shake
    private float shakeDuration = 0f;   // Remaining time of the shake
    private float shakeIntensity = 0f; // Intensity of the shake

    public GameMap(BomberQuestGame game) {
        this.game = game;
        this.world = new World(Vector2.Zero, true);
    }

    /**
     * Loads the map from a file.
     * This method parses the map data and creates the necessary game entities (walls, player, enemies, etc.).
     *
     * @param path The file path to the map data.
     */
    public void loadTheMap(String path) {
        MapLoader mapLoader = new MapLoader();
        mapLoader.loadMap(path);
        for (Map.Entry<String, Set<Integer>> entry : mapLoader.getMapData().entrySet()) {
            String[] coordinates = entry.getKey().split(",");
            int x = Integer.parseInt(coordinates[0].trim());
            int y = Integer.parseInt(coordinates[1].trim());
            if(x > this.width){
                this.width = x;
            }
            if(y > this.height ){
                this.height = y;
            }
            boolean hasDestructibleWall = entry.getValue().contains(MapLoader.DESTRUCTIBLE_WALL);

            for (int objectType : entry.getValue()) {
                if (objectType == MapLoader.DESTRUCTIBLE_WALL) continue;

                switch (objectType) {
                    case MapLoader.INDESTRUCTIBLE_WALL:
                        indestructibleWalls.add(new IndestructibleWall(world, x, y));
                        break;

                    case MapLoader.ENTRANCE:
                        this.player = new Player(world, x, y);
                        break;

                    case MapLoader.EXIT:
                        this.exit = new Exit(world, x, y);
                        break;

                    case MapLoader.ENEMY:
                        enemies.add(new Enemy(world, x, y));
                        break;

                    case MapLoader.BOMB_POWER_UP:
                        powerUps.add(new ConcurrentBomb(world, x, y));
                        break;

                    case MapLoader.BLAST_RADIUS_POWER_UP:
                        powerUps.add(new BlastRadius(world, x, y));
                        break;

                    default:
                        throw new RuntimeException("Unknown object type: " + objectType);
                }
            }

            if (hasDestructibleWall) {
                destructibleWalls.add(new DestructibleWall(world, x, y));
            }
        }

        this.flowers = new Flowers[this.width][this.height];
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

    public void triggerScreenShake(float duration, float intensity) {
        this.shakeDuration = duration;
        this.shakeIntensity = intensity;
    }


    /**
     * Updates all the bombs in the game.
     * This checks if the bomb is exploded and removes it from the list if so.
     *
     * @param deltaTime the time that has passed since the last update
     */
    public void updateBombs(float deltaTime) {
        Iterator<Bomb> iterator = bombs.iterator();
        while (iterator.hasNext()) {
            Bomb bomb = iterator.next();
            bomb.update(deltaTime);
            if (bomb.isExploded()) {
                iterator.remove();
                bomb.removeBomb(bomb);
            }
        }
    }

    /**
     * Updates all the explosion tiles in the game.
     * If the animation for an explosion tile has finished, it is removed.
     *
     * @param deltaTime the time that has passed since the last update
     */
    public void updateExplosionTiles(float deltaTime) {
        Iterator<ExplosionTile> iterator = explosionTiles.iterator();
        while (iterator.hasNext()) {
            ExplosionTile tile = iterator.next();
            tile.update(deltaTime);
            if (tile.isAnimationFinished()) {
                iterator.remove();
            }
        }
    }

    /**
     * Updates all the enemies in the game.
     * If all enemies are cleared, the exit is activated.
     *
     * @param deltaTime the time that has passed since the last update
     */
    public void updateEnemies(float deltaTime) {
        for (Enemy enemy : enemies) {
            enemy.update(deltaTime);
        }
        if (enemies.isEmpty() && !exit.isActive()) {
            exit.getBody().setActive(true);
            enemiesCleared = true;
            MusicTrack.EXITREVEAL.play();
        }
    }

    /**
     * Returns the Box2D world used for physics simulation.
     *
     * @return the world
     */
    public World getWorld() {
        return world;
    }

    /**
     * Returns the player in the game.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the exit in the game.
     *
     * @return the exit
     */
    public Exit getExit() {
        return exit;
    }

    /**
     * Returns the list of enemies in the game.
     *
     * @return the list of enemies
     */
    public List<Enemy> getEnemies() {
        return enemies;
    }

    /**
     * Returns the list of destructible walls in the game.
     *
     * @return the list of destructible walls
     */
    public List<DestructibleWall> getDestructibleWalls() {
        return destructibleWalls;
    }

    /**
     * Returns the list of indestructible walls in the game.
     *
     * @return the list of indestructible walls
     */
    public List<IndestructibleWall> getIndestructibleWalls() {
        return indestructibleWalls;
    }

    /**
     * Returns the list of power-ups in the game.
     *
     * @return the list of power-ups
     */
    public List<PowerUp> getPowerUps() {
        return powerUps;
    }

    /**
     * Returns the list of explosion tiles in the game.
     *
     * @return the list of explosion tiles
     */
    public List<ExplosionTile> getExplosionTiles() {
        return explosionTiles;
    }

    /**
     * Returns the flowers on the map.
     *
     * @return the list of flowers
     */
    public List<Flowers> getFlowers() {
        return Arrays.stream(flowers).flatMap(Arrays::stream).toList();
    }

    /**
     * Returns the list of bombs on the map.
     *
     * @return the list of bombs
     */
    public List<Bomb> getBombs() {
        return bombs;
    }

    /**
     * Returns the game instance.
     *
     * @return the game
     */
    public BomberQuestGame getGame() {
        return game;
    }

    public float getShakeDuration() {
        return shakeDuration;
    }

    public float getShakeIntensity() {
        return shakeIntensity;
    }

    public void setShakeDuration(float shakeDuration) {
        this.shakeDuration = shakeDuration;
    }

    /**
     * Removes a destructible wall from the game and destroys its associated physics body.
     *
     * @param wall the wall to remove
     */
    public void removeDestructibleWalls(DestructibleWall wall) {
        if (wall == null) return;
        if (wall.getBody() != null) {
            bodiesToDestroy.add(wall.getBody());
        }
        getDestructibleWalls().remove(wall);
    }

    /**
     * Removes a power-up from the game and destroys its associated physics body.
     *
     * @param powerUp the power-up to remove
     */
    public void removePowerUp(PowerUp powerUp) {
        if (powerUp == null) return;

        if (powerUp.getBody() != null) {
            bodiesToDestroy.add(powerUp.getBody());
            powerUp.setBody(null);
        }
        powerUps.remove(powerUp);
    }

    /**
     * Removes an enemy from the game and destroys its associated physics body.
     *
     * @param enemy the enemy to remove
     */
    public void removeEnemies(Enemy enemy) {
        if (enemy == null) return;

        if (enemy.getBody() != null) {
            bodiesToDestroy.add(enemy.getBody());
        }
        getEnemies().remove(enemy);
    }

    /**
     * Places a bomb on the map, if the player is allowed to place more bombs.
     */
    public void placeBomb() {
        if (getBombs().size() < player.getConcurrentBombCount()) {
            float bombX = MathUtils.round(player.getX());
            float bombY = MathUtils.round(player.getY());
            Bomb bomb = new Bomb(bombX, bombY, player.getBlastRadius(), this);
            bombs.add(bomb);
            MusicTrack.BOMBPLACE.play();
        }
    }

    /**
     * Queues a destructible wall for removal.
     *
     * @param wall the wall to queue for removal
     */
    public void queueWallForRemoval(DestructibleWall wall) {
        if (!wallsToRemove.contains(wall)) {
            wallsToRemove.add(wall);
        }
    }

    /**
     * Queues an enemy for removal.
     *
     * @param enemy the enemy to queue for removal
     */
    public void queueEnemyForRemoval(Enemy enemy) {
        if (!enemiesToRemove.contains(enemy)) {
            enemiesToRemove.add(enemy);
        }
    }

    /**
     * Processes the queued wall removals.
     * This removes all walls that have been queued for removal.
     */
    public void processPendingWallRemovals() {
        for (DestructibleWall wall : wallsToRemove) {
            removeDestructibleWalls(wall);
        }
        wallsToRemove.clear();
    }

    /**
     * Processes the queued enemy removals.
     * This removes all enemies that have been queued for removal.
     */
    public void processPendingEnemyRemovals() {
        for (Enemy enemy : enemiesToRemove) {
            removeEnemies(enemy);
        }
        enemiesToRemove.clear();
    }

    /**
     * Processes the queued body destructions.
     * This destroys all bodies that have been queued for destruction.
     */
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
        bodiesToDestroy.clear();
    }
}
