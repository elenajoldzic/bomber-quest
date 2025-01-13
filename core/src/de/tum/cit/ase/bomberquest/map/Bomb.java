package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;
import de.tum.cit.ase.bomberquest.screen.YouLoseScreen;
import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Drawable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a bomb that can be placed by the player in the game.
 */

// (Elena)
public class Bomb implements Drawable {
    private float x, y;
    private float timer=3; // Time until explosion
    private int blastRadius;
    private float elapsedTime = 0; // For animation purposes
    private boolean isExploded = false;
    private final GameMap gameMap;
    //private final Body body;


    public Bomb(World world, float x, float y, int blastRadius, GameMap gameMap) {
        this.x = x;
        this.y = y;
        //this.timer = timer;
        this.blastRadius = blastRadius;
        //this.body=createBody(world);

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
                MusicTrack.BOMBEXPLODE.play();
                explode();
            }
        }
        if (isExploded) {
            Iterator<ExplosionTile> iterator = gameMap.getExplosionTiles().iterator();
            while (iterator.hasNext()) {
                ExplosionTile tile = iterator.next();
                tile.update(deltaTime);
                if (tile.isAnimationFinished()) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Triggers the bomb's explosion.
     * @paramworld The Box2D world.
     */
    private void explode() {
        isExploded = true;

        createExplosionTiles();
        Vector2 bombPosition = new Vector2(x, y);
        gameMap.handleExplosion(bombPosition, blastRadius,this);

        for (ExplosionTile tile : gameMap.getExplosionTiles()) {
            checkTile(tile.getX(), tile.getY());
        }
    }

    /**
     * Creates the physical body of the bomb in the game world.
     * @param world The Box2D world.
     * @return The created body.
     */
    private Body createBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(0.5f); // Bomb size

        body.createFixture(shape, 1.0f).setUserData(this);
        shape.dispose();

        return body;
    }

    public boolean isTileAvailable(float x, float y) {
        for (IndestructibleWall wall : gameMap.getIndestructibleWalls()) {
            if (Math.round(wall.getX()) == x && Math.round(wall.getY()) == y) {
                return false; // Blocked by a wall
            }
        }
        return true; // The tile is available
    }

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
                    RightStop=true;
                }
            }

            // Left
            if (!LeftStop) {
                if (isTileAvailable(x - i, y)) {
                    gameMap.getExplosionTiles().add(new ExplosionTile(x - i, y,
                            i == blastRadius ? Animations.EXPLOSION_END_L : Animations.EXPLOSION_HORIZONTAL));
                } else {
                    LeftStop=true;
                }
            }

            // Up
            if(!UpStop) {
                if (isTileAvailable(x, y + i)) {
                    gameMap.getExplosionTiles().add(new ExplosionTile(x, y + i,
                            i == blastRadius ? Animations.EXPLOSION_END_UP : Animations.EXPLOSION_VERTICAL));
                } else {
                    UpStop=true;
                }
            }

            // Down
            if(!DownStop) {
                if (isTileAvailable(x, y - i)) {
                    gameMap.getExplosionTiles().add(new ExplosionTile(x, y - i,
                            i == blastRadius ? Animations.EXPLOSION_END_DOWN : Animations.EXPLOSION_VERTICAL));
                } else {
                    DownStop=true;
                }
            }
        }
    }


    public boolean isExploded() {
        return isExploded;
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        /*if (isExploded) {
            return Animations.BOMB_EXPLOSION.getKeyFrame(elapsedTime, false);
        }*/
        return Animations.BOMB_IDLE.getKeyFrame(elapsedTime, true);
    }

    /**
     * Gets the blast radius of the bomb.
     * @return The blast radius.
     */
    public int getBlastRadius() {
        return blastRadius;
    }

    public void renderExplosion(SpriteBatch batch) {
        if (isExploded) {
            for (ExplosionTile tile : gameMap.getExplosionTiles()) {
                batch.draw(tile.getCurrentAppearance(), tile.getX(), tile.getY(), 1, 1); // Tile size = 1x1
                if (tile.isAnimationFinished()) {
                    gameMap.getExplosionTiles().remove(tile);
                }
            }

        }

    }
    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    public float getTimer() {
        return timer;
    }

    public void setTimer(float timer) {
        this.timer = timer;
    }

    /*public Body getBody() {
        return body;
    }*/
    /**
     * Destroys the bomb's physical body in the game world.
     * @paramworld The Box2D world.
     */

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

}
