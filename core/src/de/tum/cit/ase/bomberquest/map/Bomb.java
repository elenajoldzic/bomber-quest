package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;
import de.tum.cit.ase.bomberquest.screens.YouLoseScreen;
import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Drawable;

/**
 * Represents a bomb that can be placed by the player in the game.
 */
public class Bomb extends GameObject implements Drawable {

    private float timer = 3; // Time until explosion
    private int blastRadius;
    private float elapsedTime = 0; // For animation purposes
    private boolean isExploded = false;
    private final GameMap gameMap;

    public Bomb(float x, float y, int blastRadius, GameMap gameMap) {
        super(x, y);  // Call the parent class constructor
        this.blastRadius = blastRadius;
        this.gameMap = gameMap;
    }

    /**
     * Updates the bomb's state, including the timer and animation.
     * @param deltaTime Time elapsed since the last frame.
     */
    @Override
    public void update(float deltaTime) {
        if (!isExploded) {
            elapsedTime += deltaTime; // Update animation time
            timer -= deltaTime; // Count down the timer

            if (timer <= 0) {
                explode(); // Trigger explosion once the timer reaches zero
            }
        }
    }

    /**
     * Triggers the bomb's explosion, plays explosion sound, creates explosion tiles,
     * and handles interactions with the environment like destructible walls and enemies.
     */
    private void explode() {
        isExploded = true;
        MusicTrack.BOMBEXPLODE.dispose(); // Dispose previous explosion sound
        MusicTrack.BOMBEXPLODE.play(); // Play explosion sound
        createExplosionTiles(); // Create explosion effect tiles
        Vector2 bombPosition = new Vector2(getX(), getY());
        handleExplosion(bombPosition, blastRadius, this); // Handle explosion damage and effects
        // Check each explosion tile to interact with surrounding objects
        for (ExplosionTile tile : gameMap.getExplosionTiles()) {
            checkTile(tile.getX(), tile.getY()); // Interact with the objects at each explosion tile
        }
    }

    /**
     * Handles the explosion's effect on surrounding tiles based on the blast radius.
     * @param position The position of the bomb.
     * @param blastRadius The radius of the explosion.
     * @param bomb The bomb object that triggered the explosion.
     */
    public void handleExplosion(Vector2 position, int blastRadius, Bomb bomb) {
        for (int i = 1; i <= blastRadius; i++) {
            // Right
            float targetX = position.x + i;
            if (!bomb.isTileAvailable(targetX, position.y)) {
                if (bomb.isDestructibleWall(targetX, position.y)) {
                    bomb.checkTile(targetX, position.y); // Trigger wall destruction
                }
                break; // Stop if the explosion hits an impassable object
            }
            bomb.checkTile(targetX, position.y); // Check for interactions with objects

            // Left
            targetX = position.x - i;
            if (!bomb.isTileAvailable(targetX, position.y)) {
                if (bomb.isDestructibleWall(targetX, position.y)) {
                    bomb.checkTile(targetX, position.y); // Trigger wall destruction
                }
                break; // Stop if the explosion hits an impassable object
            }
            bomb.checkTile(targetX, position.y); // Check for interactions with objects
        }

        for (int i = 1; i <= blastRadius; i++) {
            // Up
            float targetY = position.y + i;
            if (!bomb.isTileAvailable(position.x, targetY)) {
                if (bomb.isDestructibleWall(position.x, targetY)) {
                    bomb.checkTile(position.x, targetY); // Trigger wall destruction
                }
                break; // Stop if the explosion hits an impassable object
            }
            bomb.checkTile(position.x, targetY); // Check for interactions with objects

            // Down
            targetY = position.y - i;
            if (!bomb.isTileAvailable(position.x, targetY)) {
                if (bomb.isDestructibleWall(position.x, targetY)) {
                    bomb.checkTile(position.x, targetY); // Trigger wall destruction
                }
                break; // Stop if the explosion hits an impassable object
            }
            bomb.checkTile(position.x, targetY); // Check for interactions with objects
        }
    }

    /**
     * Checks whether a tile is available for explosion (not blocked by walls).
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @return true if the tile is available for explosion, false if blocked.
     */
    public boolean isTileAvailable(float x, float y) {
        // Check if the tile is blocked by any indestructible wall
        for (IndestructibleWall wall : gameMap.getIndestructibleWalls()) {
            if (Math.round(wall.getX()) == x && Math.round(wall.getY()) == y) {
                return false; // Blocked by an indestructible wall
            }
        }
        // Check if the tile is blocked by any destructible wall
        for (DestructibleWall wall : gameMap.getDestructibleWalls()) {
            if (Math.round(wall.getX()) == x && Math.round(wall.getY()) == y) {
                return false; // Blocked by a destructible wall
            }
        }
        return true; // The tile is available
    }

    /**
     * Checks a specific tile to determine if it contains destructible walls or enemies,
     * and applies effects like wall destruction or player/enemy damage.
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     */
    public void checkTile(float x, float y) {
        if (!isTileAvailable(x, y)) {
            return; // Return early if the tile is not available
        }

        // Check for enemies at the specified tile and apply damage
        for (Enemy enemy : gameMap.getEnemies()) {
            if (Math.round(enemy.getX()) == x && Math.round(enemy.getY()) == y) {
                MusicTrack.ENEMYDIE.play(); // Play enemy death sound
                gameMap.queueEnemyForRemoval(enemy); // Remove the enemy from the game
            }
        }
        // Check for destructible walls at the specified tile and destroy them
        for (DestructibleWall wall : gameMap.getDestructibleWalls()) {
            if (Math.round(wall.getX()) == x && Math.round(wall.getY()) == y) {
                gameMap.queueWallForRemoval(wall); // Remove the destructible wall from the game
            }
        }
        // Check if the player is at the specified tile and handle player death
        if (Math.round(gameMap.getPlayer().getX()) == x && Math.round(gameMap.getPlayer().getY()) == y) {
            MusicTrack.PLAYERDIE.play(); // Play player death sound
            gameMap.getGame().setScreen(new YouLoseScreen(gameMap.getGame())); // Game over
        }
    }

    /**
     * Checks if the tile contains a destructible wall.
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @return true if the tile contains a destructible wall, false otherwise.
     */
    private boolean isDestructibleWall(float x, float y) {
        for (DestructibleWall wall : gameMap.getDestructibleWalls()) {
            if (Math.round(wall.getX()) == x && Math.round(wall.getY()) == y) {
                return true; // The tile contains a destructible wall
            }
        }
        return false; // No destructible wall at this tile
    }

    /**
     * Creates explosion tiles at the bomb's position and in the surrounding area based on the blast radius.
     * Handles interactions with walls (destructible and indestructible).
     */
    private void createExplosionTiles() {
        gameMap.getExplosionTiles().add(new ExplosionTile(getX(), getY(), Animations.EXPLOSION_CENTER)); // Add center explosion tile
        boolean RightStop = false, LeftStop = false, UpStop = false, DownStop = false;

        // Generate explosion tiles in all directions (right, left, up, down) based on the blast radius
        for (int i = 1; i <= blastRadius; i++) {
            // Right
            if (!RightStop) {
                if (isTileAvailable(getX() + i, getY())) {
                    gameMap.getExplosionTiles().add(new ExplosionTile(getX() + i, getY(),
                            i == blastRadius ? Animations.EXPLOSION_END_R : Animations.EXPLOSION_HORIZONTAL));
                } else {
                    if (isDestructibleWall(getX() + i, getY())) {
                        gameMap.getExplosionTiles().add(new ExplosionTile(getX() + i, getY(), Animations.EXPLOSION_END_R));
                        gameMap.queueWallForRemoval(findDestructibleWall(getX() + i, getY())); // Destroy the wall
                    }
                    RightStop = true; // Stop further explosion in this direction
                }
            }

            // Left
            if (!LeftStop) {
                if (isTileAvailable(getX() - i, getY())) {
                    gameMap.getExplosionTiles().add(new ExplosionTile(getX() - i, getY(),
                            i == blastRadius ? Animations.EXPLOSION_END_L : Animations.EXPLOSION_HORIZONTAL));
                } else {
                    if (isDestructibleWall(getX() - i, getY())) {
                        gameMap.getExplosionTiles().add(new ExplosionTile(getX() - i, getY(), Animations.EXPLOSION_END_L));
                        gameMap.queueWallForRemoval(findDestructibleWall(getX() - i, getY())); // Destroy the wall
                    }
                    LeftStop = true; // Stop further explosion in this direction
                }
            }

            // Up
            if (!UpStop) {
                if (isTileAvailable(getX(), getY() + i)) {
                    gameMap.getExplosionTiles().add(new ExplosionTile(getX(), getY() + i,
                            i == blastRadius ? Animations.EXPLOSION_END_UP : Animations.EXPLOSION_VERTICAL));
                } else {
                    if (isDestructibleWall(getX(), getY() + i)) {
                        gameMap.getExplosionTiles().add(new ExplosionTile(getX(), getY() + i, Animations.EXPLOSION_END_UP));
                        gameMap.queueWallForRemoval(findDestructibleWall(getX(), getY() + i)); // Destroy the wall
                    }
                    UpStop = true; // Stop further explosion in this direction
                }
            }

            // Down
            if (!DownStop) {
                if (isTileAvailable(getX(), getY() - i)) {
                    gameMap.getExplosionTiles().add(new ExplosionTile(getX(), getY() - i,
                            i == blastRadius ? Animations.EXPLOSION_END_DOWN : Animations.EXPLOSION_VERTICAL));
                } else {
                    if (isDestructibleWall(getX(), getY() - i)) {
                        gameMap.getExplosionTiles().add(new ExplosionTile(getX(), getY() - i, Animations.EXPLOSION_END_DOWN));
                        gameMap.queueWallForRemoval(findDestructibleWall(getX(), getY() - i)); // Destroy the wall
                    }
                    DownStop = true; // Stop further explosion in this direction
                }
            }
        }
    }

    /**
     * Finds and returns a destructible wall at the specified position.
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @return The destructible wall at the specified position, or null if no wall is found.
     */
    private DestructibleWall findDestructibleWall(float x, float y) {
        for (DestructibleWall wall : gameMap.getDestructibleWalls()) {
            if (Math.round(wall.getX()) == x && Math.round(wall.getY()) == y) {
                return wall; // Return the destructible wall at the specified position
            }
        }
        return null; // No destructible wall found at the specified position
    }

    /**
     * Returns the current appearance of the bomb for animation purposes.
     * @return The current texture region of the bomb.
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        return Animations.BOMB_IDLE.getKeyFrame(elapsedTime, true); // Return the idle animation frame
    }

    /**
     * Returns the x-coordinate of the bomb.
     * @return The x-coordinate of the bomb.
     */
    @Override
    public float getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the bomb.
     * @return The y-coordinate of the bomb.
     */
    @Override
    public float getY() {
        return y;
    }

    /**
     * Checks if the bomb has exploded.
     * @return true if the bomb has exploded, false otherwise.
     */
    public boolean isExploded() {
        return isExploded;
    }

    /**
     * Removes the bomb from the game map.
     * @param bomb The bomb to be removed.
     */
    public void removeBomb(Bomb bomb) {
        gameMap.getBombs().remove(bomb); // Remove the bomb from the list of bombs
    }
}
