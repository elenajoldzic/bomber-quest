package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.texture.Drawable;

/**
 * Base class for walls (indestructible and destructible) with common properties and behavior.
 * This class now extends GameObject to inherit position-related methods and properties.
 * The Wall class represents an abstract wall object with the ability to have a Box2D hitbox
 * and its appearance in the game.
 */
public abstract class Wall extends GameObject implements Drawable {

    protected Body body;

    /**
     * Constructor for Wall.
     * This constructor initializes the wall's position using the GameObject constructor
     * and creates its Box2D hitbox in the specified world.
     * @param world The Box2D world to add the wall's hitbox to.
     * @param x The X position of the wall.
     * @param y The Y position of the wall.
     */
    public Wall(World world, float x, float y) {
        super(x, y);  // Calls the GameObject constructor to initialize position
        createHitbox(world);  // Creates the Box2D hitbox for the wall in the game world
    }

    /**
     * Create a Box2D body for the wall.
     * This method creates a static Box2D body and attaches it to the wall,
     * representing the collision properties of the wall in the physics world.
     * @param world The Box2D world to add the body to.
     */
    private void createHitbox(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;  // The wall is static and will not move
        bodyDef.position.set(this.x, this.y);  // Set the position of the body in the world
        this.body = world.createBody(bodyDef);  // Create the body in the Box2D world

        PolygonShape box = new PolygonShape();
        box.setAsBox(0.5f, 0.5f);  // Define the shape of the wall as a box (0.5f width and height)
        body.createFixture(box, 1.0f);  // Attach the shape to the body with a density of 1
        box.dispose();  // Dispose the shape after use to free resources

        body.setUserData(this);  // Attach this wall instance to the Box2D body for later reference
    }

    /**
     * Retrieves the X-coordinate of the wall.
     * This method returns the X position of the wall in the game world.
     * @return The X position of the wall.
     */
    @Override
    public float getX() {
        return x;  // Return the X position of the wall
    }

    /**
     * Retrieves the Y-coordinate of the wall.
     * This method returns the Y position of the wall in the game world.
     * @return The Y position of the wall.
     */
    @Override
    public float getY() {
        return y;  // Return the Y position of the wall
    }

    /**
     * Abstract method to get the texture of the wall.
     * This method must be implemented by subclasses to provide the specific appearance for the wall.
     * @return The texture region representing the current appearance of the wall.
     */
    @Override
    public abstract TextureRegion getCurrentAppearance();

    /**
     * Retrieves the Box2D body associated with the wall.
     * The body defines the physical properties of the wall, such as collision detection and physics interactions.
     * @return The Box2D body representing the wall's hitbox.
     */
    public Body getBody() {
        return body;  // Return the Box2D body for collision and physics calculations
    }
}
