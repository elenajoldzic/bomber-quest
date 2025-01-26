package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;
import de.tum.cit.ase.bomberquest.screen.YouLoseScreen;
import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import java.util.Iterator;

/**
 * Represents a bomb that can be placed by the player in the game.
 */


public class Bomb implements Drawable {
    private float x, y;
    private float timer=3; // Time until explosion
    private int blastRadius;
    private float elapsedTime = 0; // For animation purposes
    private boolean isExploded = false;
    private final GameMap gameMap;


    public Bomb(float x, float y, int blastRadius, GameMap gameMap) {
        this.x = x;
        this.y = y;
        this.blastRadius = blastRadius;
        this.gameMap = gameMap;
    }

    /**
     * Updates the bomb's state, including the timer and animation.
     * @param deltaTime Time elapsed since the last frame.
     */
    public void update(float deltaTime) {
        if (!isExploded) {
            elapsedTime += deltaTime; // Update animation time
            timer -= deltaTime; // Count down the timer

            if (timer <= 0) {
                explode();
            }
        }
        /*if (isExploded) {
            Iterator<ExplosionTile> iterator = gameMap.getExplosionTiles().iterator();
            while (iterator.hasNext()) {
                ExplosionTile tile = iterator.next();
                tile.update(deltaTime);
                if (tile.isAnimationFinished()) {
                    iterator.remove();
                }
            }
        }

         */
    }
    /**
     * Triggers the bomb's explosion.
     */
    private void explode() {
        isExploded = true;
        MusicTrack.BOMBEXPLODE.dispose();
        MusicTrack.BOMBEXPLODE.play();
        createExplosionTiles();
        Vector2 bombPosition = new Vector2(x, y);
        handleExplosion(bombPosition, blastRadius,this);
        for (ExplosionTile tile : gameMap.getExplosionTiles()) {
            checkTile(tile.getX(), tile.getY());
        }
    }

    //For the reachable radius of bombs, checks every tile in 4 direction.
    //First, checks if the tile is available, if not, it checks if it is a destructible wall.
    //If they dont hold, proceeds with checktile(removing the items)
    public void handleExplosion(Vector2 position, int blastRadius, Bomb bomb) {
        for (int i = 1; i <= blastRadius; i++) {
            // Right
            float targetX = position.x + i;
            if (!bomb.isTileAvailable(targetX, position.y)) {
                if (bomb.isDestructibleWall(targetX, position.y)) {
                    bomb.checkTile(targetX, position.y); // Trigger wall destruction
                }
                break;
            }
            bomb.checkTile(targetX, position.y);

            // Left
            targetX = position.x - i;
            if (!bomb.isTileAvailable(targetX, position.y)) {
                if (bomb.isDestructibleWall(targetX, position.y)) {
                    bomb.checkTile(targetX, position.y); // Trigger wall destruction
                }
                break;
            }
            bomb.checkTile(targetX, position.y);
        }

        for (int i = 1; i <= blastRadius; i++) {
            // Up
            float targetY = position.y + i;
            if (!bomb.isTileAvailable(position.x, targetY)) {
                if (bomb.isDestructibleWall(position.x, targetY)) {
                    bomb.checkTile(position.x, targetY); // Trigger wall destruction
                }
                break;
            }
            bomb.checkTile(position.x, targetY);

            // Down
            targetY = position.y - i;
            if (!bomb.isTileAvailable(position.x, targetY)) {
                if (bomb.isDestructibleWall(position.x, targetY)) {
                    bomb.checkTile(position.x, targetY); // Trigger wall destruction
                }
                break;
            }
            bomb.checkTile(position.x, targetY);
        }
    }

//This method checks the tile on x,y and returns false if there is a wall on that tile
    public boolean isTileAvailable(float x, float y) {
        for (IndestructibleWall wall : gameMap.getIndestructibleWalls()) {
            if (Math.round(wall.getX()) == x && Math.round(wall.getY()) == y) {
                return false; // Blocked by a wall
            }
        }
        // Check for destructible walls (this is for stopping explosion after hittin destructible wall)
        for (DestructibleWall wall : gameMap.getDestructibleWalls()) {
            if (Math.round(wall.getX()) == x && Math.round(wall.getY()) == y) {
                return false; // Blocked by a destructible wall (for continuation logic)
            }
        }
        return true; // The tile is available
    }

    //This method checks the tiles for enemies, destructible walls or player, and removes them if they encounter with explosion
    public void checkTile(float x, float y) {
        if(!isTileAvailable(x,y)){
            return;
        }

        // Check for enemies, walls, or the player at (x, y)
        for (Enemy enemy : gameMap.getEnemies()) {
            if (Math.round(enemy.getX()) == x && Math.round(enemy.getY()) == y) {
                MusicTrack.ENEMYDIE.play();
                gameMap.queueEnemyForRemoval(enemy);
            }
        }
        for (DestructibleWall wall : gameMap.getDestructibleWalls()) {
            if (Math.round(wall.getX()) == x && Math.round(wall.getY()) == y) {
                gameMap.queueWallForRemoval(wall);
            }
        }
        if (Math.round(gameMap.getPlayer().getX()) == x && Math.round(gameMap.getPlayer().getY()) == y) {
            MusicTrack.PLAYERDIE.play();
            gameMap.getGame().setScreen(new YouLoseScreen(gameMap.getGame())); // Game over
        }
    }





    private boolean isDestructibleWall(float x, float y) {
        for (DestructibleWall wall : gameMap.getDestructibleWalls()) {
            if (Math.round(wall.getX()) == x && Math.round(wall.getY()) == y) {
                return true; // The tile has a destructible wall
            }
        }
        return false; // No destructible wall on the tile
    }

    //Creates explosion animations in 4 directions
    private void createExplosionTiles() {
        // Center
        gameMap.getExplosionTiles().add(new ExplosionTile(x, y, Animations.EXPLOSION_CENTER));
        // Boolean values indicating whether the explosion stopped in each 4 directions.
        boolean RightStop=false;
        boolean LeftStop=false;
        boolean UpStop=false;
        boolean DownStop=false;

        // Horizontal and vertical explosions
        for (int i = 1; i <= blastRadius; i++) {
            //Right
            if (!RightStop) {
                if (isTileAvailable(x + i, y)) {
                    gameMap.getExplosionTiles().add(new ExplosionTile(x + i, y,
                            i == blastRadius ? Animations.EXPLOSION_END_R : Animations.EXPLOSION_HORIZONTAL));
                } else {
                    if (isDestructibleWall(x+i, y)) {
                        gameMap.getExplosionTiles().add(new ExplosionTile(x+i, y, Animations.EXPLOSION_END_R));
                        gameMap.queueWallForRemoval(findDestructibleWall(x+i, y));
                    }
                    RightStop = true;
                }
            }

            // Left
            if (!LeftStop) {
                if (isTileAvailable(x - i, y)) {
                    gameMap.getExplosionTiles().add(new ExplosionTile(x - i, y,
                            i == blastRadius ? Animations.EXPLOSION_END_L : Animations.EXPLOSION_HORIZONTAL));
                } else {
                    if (isDestructibleWall(x-i, y)) {
                        gameMap.getExplosionTiles().add(new ExplosionTile(x-i, y, Animations.EXPLOSION_END_L));
                        gameMap.queueWallForRemoval(findDestructibleWall(x-i, y));
                    }
                    LeftStop = true;
                }
            }

            // Up
            if(!UpStop) {
                if (isTileAvailable(x, y + i)) {
                    gameMap.getExplosionTiles().add(new ExplosionTile(x, y + i,
                            i == blastRadius ? Animations.EXPLOSION_END_UP : Animations.EXPLOSION_VERTICAL));
                } else {
                    if (isDestructibleWall(x, y+i)) {
                        gameMap.getExplosionTiles().add(new ExplosionTile(x, y+i, Animations.EXPLOSION_END_UP));
                        gameMap.queueWallForRemoval(findDestructibleWall(x, y+i));
                    }
                    UpStop = true;
                }
            }

            // Down
            if(!DownStop) {
                if (isTileAvailable(x, y - i)) {
                    gameMap.getExplosionTiles().add(new ExplosionTile(x, y - i,
                            i == blastRadius ? Animations.EXPLOSION_END_DOWN : Animations.EXPLOSION_VERTICAL));
                } else {
                    if (isDestructibleWall(x, y-i)) {
                        gameMap.getExplosionTiles().add(new ExplosionTile(x, y-i, Animations.EXPLOSION_END_DOWN));
                        gameMap.queueWallForRemoval(findDestructibleWall(x, y-i));
                    }
                    DownStop = true;
                }
            }
        }
    }
    private DestructibleWall findDestructibleWall(float x, float y) {
        for (DestructibleWall wall : gameMap.getDestructibleWalls()) {
            if (Math.round(wall.getX()) == x && Math.round(wall.getY()) == y) {
                return wall;
            }
        }
        return null; // No destructible wall found
    }

    public void removeBomb(Bomb bomb) {
        // Remove the bomb from the list of active bombs
        gameMap.getBombs().remove(bomb);
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        return Animations.BOMB_IDLE.getKeyFrame(elapsedTime, true);
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }
    public boolean isExploded() {
        return isExploded;
    }
}
