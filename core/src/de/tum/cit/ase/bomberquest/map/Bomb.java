package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.screen.YouLoseScreen;
import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Drawable;

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
                explode();
            }
        }
    }

    /**
     * Triggers the bomb's explosion.
     * @paramworld The Box2D world.
     */
    private void explode() {
        isExploded = true;
        Vector2 bombPosition = new Vector2(x, y);
        gameMap.handleExplosion(bombPosition, blastRadius,this);

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

    /*
    /**
     * Updates the bomb's state and checks if it should explode.
     * @param deltaTime Time elapsed since the last frame.
     * @return True if the bomb explodes, false otherwise.

    public boolean update(float deltaTime) {
        timer += deltaTime;
        return timer >= EXPLOSION_TIE;
    }
     */

    public boolean isExploded() {
        return isExploded;
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        if (isExploded) {
            return Animations.BOMB_EXPLOSION.getKeyFrame(elapsedTime, false);
        }
        return Animations.BOMB_IDLE.getKeyFrame(elapsedTime, true);
    }

    /**
     * Gets the blast radius of the bomb.
     * @return The blast radius.
     */
    public int getBlastRadius() {
        return blastRadius;
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
        // Check for enemies, walls, or the player at (x, y)
        for (Enemy enemy : gameMap.getEnemies()) {
            if (Math.round(enemy.getX()) == x && Math.round(enemy.getY()) == y) {
                gameMap.queueEnemyForRemoval(enemy);
            }
        }

        for (DestructibleWall wall : gameMap.getDestructibleWalls()) {
            if (Math.round(wall.getX()) == x && Math.round(wall.getY()) == y) {
                gameMap.queueWallForRemoval(wall);
            }
        }

        if (Math.round(gameMap.getPlayer().getX()) == x && Math.round(gameMap.getPlayer().getY()) == y) {
            //.setScreen(new YouLoseScreen(game)); // Game over
        }
    }

}
