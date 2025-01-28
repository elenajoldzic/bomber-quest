package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Drawable;

/**
 * Represents an enemy in the game.
 * The enemy is a static object with a hitbox, preventing the player from walking through it.
 * The enemy also has a movement pattern, which is defined to move in a circular path.
 */
public class Enemy extends GameObject implements Drawable {

    /** Total time elapsed since the game started. Used to calculate the enemy's movement and animation. */
    private float elapsedTime;

    /** The Box2D hitbox of the enemy, used for position and collision detection. */
    private final Body hitbox;

    /**
     * Creates an enemy at the specified position in the game world.
     * The enemy is initialized with a hitbox for collision detection.
     *
     * @param world The Box2D world where the enemy's hitbox will be added.
     * @param x The X position of the enemy in the game world.
     * @param y The Y position of the enemy in the game world.
     */
    public Enemy(World world, float x, float y) {
        super(x, y);  // Calls the GameObject constructor to set the initial position of the enemy.
        this.hitbox = createHitbox(world, x, y);  // Creates the Box2D hitbox for the enemy.
    }

    /**
     * Creates a Box2D body for the enemy.
     * The body is used for the enemy's position and collision detection in the physics world.
     *
     * @param world The Box2D world to add the body to.
     * @param startX The X position of the enemy.
     * @param startY The Y position of the enemy.
     * @return The Box2D body for the enemy, used in physics interactions.
     */
    private Body createHitbox(World world, float startX, float startY) {
        // BodyDef defines the properties and behaviors of the Box2D body.
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;  // The enemy is dynamic, meaning it is affected by physics.
        bodyDef.position.set(startX, startY);  // Set the initial position of the enemy.

        // Create the body in the Box2D world using the defined properties.
        Body body = world.createBody(bodyDef);

        // Define the shape of the body (a circle for the enemy).
        CircleShape circle = new CircleShape();
        circle.setRadius(0.3f);  // Set the radius for the circle hitbox (about 0.6 tiles wide for the enemy).

        // Attach the shape as a fixture to the body. Fixtures define the physical properties of bodies.
        body.createFixture(circle, 1.0f);  // Density of the fixture is set to 1.0 (standard for enemies).

        // Dispose of the shape after attaching it to the body, to free up memory.
        circle.dispose();

        // Set the enemy instance as the user data of the body so it can be accessed later.
        body.setUserData(this);

        return body;  // Return the created Box2D body.
    }

    /**
     * Retrieves the current graphical appearance of the enemy.
     * The enemy's appearance is determined based on elapsed time and animations.
     * @return The texture region representing the current appearance of the enemy.
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        // Use the enemy's walking animation, based on the elapsed time.
        return Animations.ENEMY_WALK.getKeyFrame(this.elapsedTime, true);
    }

    /**
     * Retrieves the X position of the enemy in the game world.
     * The position is obtained from the Box2D hitbox.
     *
     * @return The current X position of the enemy.
     */
    @Override
    public float getX() {
        return hitbox.getPosition().x;  // Get the X coordinate from the Box2D hitbox.
    }

    /**
     * Retrieves the Y position of the enemy in the game world.
     * The position is obtained from the Box2D hitbox.
     *
     * @return The current Y position of the enemy.
     */
    @Override
    public float getY() {
        return hitbox.getPosition().y;  // Get the Y coordinate from the Box2D hitbox.
    }

    /**
     * Retrieves the Box2D body of the enemy.
     * This is useful for accessing the hitbox directly if needed.
     *
     * @return The Box2D body of the enemy, used for collision detection and physics.
     */
    public Body getBody() {
        return hitbox;  // Return the Box2D body (hitbox) for this enemy.
    }

    /**
     * Updates the enemy's state.
     * This includes updating its animation time and moving the enemy in a circular path based on elapsed time.
     *
     * @param deltaTime The time elapsed since the last frame. This is used to calculate the movement.
     */
    public void update(float deltaTime) {
        this.elapsedTime += deltaTime;  // Increase the elapsed time, which is used for animation.

        // Calculate the enemy's velocity to move in a circular pattern.
        // The velocities are determined using sine and cosine functions.
        float xVelocity = (float) Math.sin(this.elapsedTime) * 2;  // Move horizontally in a sine wave pattern.
        float yVelocity = (float) Math.cos(this.elapsedTime) * 2;  // Move vertically in a cosine wave pattern.

        // Set the calculated velocity for the enemy's Box2D body.
        this.hitbox.setLinearVelocity(xVelocity, yVelocity);
    }
}

