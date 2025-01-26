package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;

/**
 * Represents the exit object in the game.
 * The exit is a static object that has a hitbox, so the player cannot walk through it.
 * It marks the end of a level or area in the game.
 */
public class Exit extends GameObject implements Drawable {

    // The position of the exit. Since the exit does not move, we store the position directly.
    private final float x;
    private final float y;

    // The Box2D body (hitbox) for the exit, used for collision detection.
    private Body body;

    /**
     * Creates an exit at the specified position.
     * The exit's Box2D body is also created, although it is static (it does not move).
     *
     * @param world The Box2D world to add the exit's hitbox to.
     * @param x The X position of the exit in the game world.
     * @param y The Y position of the exit in the game world.
     */
    public Exit(World world, float x, float y) {
        super(x, y);  // Calls the GameObject constructor to set the initial position of the exit.
        this.x = x;
        this.y = y;
        // Create the hitbox (Box2D body) for the exit.
        body = createHitbox(world);
    }

    /**
     * Creates a Box2D body for the exit.
     * The body is static since the exit does not move. The exit is a simple square shape that the player can collide with.
     *
     * @param world The Box2D world to add the body to.
     * @return The created Box2D body for the exit.
     */
    private Body createHitbox(World world) {
        // BodyDef defines the properties and behaviors of the Box2D body.
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;  // The exit is static (does not move).
        bodyDef.position.set(this.x, this.y);  // Set the position of the exit.

        // Create the body in the Box2D world using the body definition.
        Body body = world.createBody(bodyDef);

        // Define the shape of the body (a square for the exit).
        PolygonShape box = new PolygonShape();
        box.setAsBox(0.4f, 0.4f);  // Make the polygon a square with a side length of 0.8 tiles.

        // Attach the shape as a fixture to the body. Fixtures define the physical properties of bodies.
        body.createFixture(box, 1.0f);  // Density of the fixture is set to 1.0 (standard for static objects).

        // Dispose of the shape after attaching it to the body to free up memory.
        box.dispose();

        // Deactivate the body since it's static (not needed for collisions after setup).
        body.setActive(false);

        // Set the exit instance as the user data of the body so it can be accessed later.
        body.setUserData(this);

        return body;  // Return the created Box2D body.
    }

    /**
     * Updates the state of the exit.
     * Since the exit is static and does not move or change during the game, this method does nothing.
     *
     * @param deltaTime The time elapsed since the last frame. Not used in this case.
     */
    @Override
    public void update(float deltaTime) {
        // The exit does not update, so this method is empty.
    }

    /**
     * Retrieves the current graphical appearance of the exit.
     * The exit is represented by a fixed texture.
     *
     * @return The texture region representing the exit.
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        return Textures.EXIT;  // Return the static texture for the exit.
    }

    /**
     * Retrieves the X position of the exit in the game world.
     *
     * @return The X position of the exit.
     */
    @Override
    public float getX() {
        return x;  // Return the stored X position.
    }

    /**
     * Retrieves the Y position of the exit in the game world.
     *
     * @return The Y position of the exit.
     */
    @Override
    public float getY() {
        return y;  // Return the stored Y position.
    }

    /**
     * Checks if the exit's Box2D body is active.
     * Since the exit is static, its body is inactive by default.
     *
     * @return True if the exit's body is active; false otherwise.
     */
    public boolean isActive() {
        return this.body.isActive();  // Return the active status of the Box2D body.
    }

    /**
     * Retrieves the Box2D body of the exit.
     * This body is used for collision detection, though it is static and doesn't move.
     *
     * @return The Box2D body for the exit.
     */
    public Body getBody() {
        return body;  // Return the Box2D body (hitbox) for the exit.
    }
}
