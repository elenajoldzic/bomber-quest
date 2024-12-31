package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Drawable;

/**
 * Represents a bomb that can be placed by the player in the game.
 */

// (Elena)
public class Bomb implements Drawable {

    private final Body body;
    private final int blastRadius;
    private float timer;
    private static final float EXPLOSION_TIME = 3.0f; // Bomb explodes after 3 seconds

    public Bomb(World world, float x, float y, int blastRadius) {
        this.blastRadius = blastRadius;
        this.timer = 0;
        this.body = createBody(world, x, y);
    }

    /**
     * Creates the physical body of the bomb in the game world.
     * @param world The Box2D world.
     * @param x The x-coordinate where the bomb is placed.
     * @param y The y-coordinate where the bomb is placed.
     * @return The created body.
     */
    private Body createBody(World world, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(0.3f); // Bomb size

        body.createFixture(shape, 1.0f).setUserData(this);
        shape.dispose();

        return body;
    }

    /**
     * Updates the bomb's state and checks if it should explode.
     * @param deltaTime Time elapsed since the last frame.
     * @return True if the bomb explodes, false otherwise.
     */
    public boolean update(float deltaTime) {
        timer += deltaTime;
        return timer >= EXPLOSION_TIME;
    }

    /**
     * Gets the blast radius of the bomb.
     * @return The blast radius.
     */
    public int getBlastRadius() {
        return blastRadius;
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        return Animations.BOMB_IDLE.getKeyFrame(timer, true);
    }

    @Override
    public float getX() {
        return body.getPosition().x;
    }

    @Override
    public float getY() {
        return body.getPosition().y;
    }

    /**
     * Destroys the bomb's physical body in the game world.
     * @param world The Box2D world.
     */
    public void explode(World world) {
        world.destroyBody(body);
    }

    public void placeAt(World world, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody; // Bomb is static once placed
        bodyDef.position.set(x, y);
        Body hitbox = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(0.2f); // Adjust bomb size
        hitbox.createFixture(circle, 0.0f);
        circle.dispose();
    }
}
