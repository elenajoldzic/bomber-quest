package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;
import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Drawable;

/**
 * Represents the player character in the game.
 * The player has a hitbox for collision detection and movement in the game world.
 * The class now extends GameObject to leverage shared properties and methods such as position.
 */
public class Player extends GameObject implements Drawable {

    /** Total time elapsed since the game started. Used for calculating player movement and animation. */
    private float elapsedTime;

    /** The Box2D hitbox of the player, used for collision detection and movement in the game world. */
    private final Body hitbox;

    /** The current direction the player is moving in. Initially, there is no movement. */
    private Direction currentDirection = Direction.NONE;

    /** Maximum number of bombs the player can place and the blast radius of those bombs. */
    private int concurrentBombCount;
    private int blastRadius;

    /** Flag indicating whether the player is currently walking. */
    private boolean isWalking = false;

    /**
     * Constructor for Player.
     * This constructor initializes the player's position using the GameObject constructor
     * and creates the Box2D hitbox for collision and physics interactions.
     *
     * @param world The Box2D world to add the player's hitbox to.
     * @param x The initial X position of the player.
     * @param y The initial Y position of the player.
     */
    public Player(World world, float x, float y) {
        super(x, y);  // Calls the GameObject constructor to set the player's position
        this.hitbox = createHitbox(world, x, y);  // Creates the Box2D hitbox for the player
        this.concurrentBombCount = 1;  // Default maximum bombs the player can place
        this.blastRadius = 1;  // Default blast radius of the player's bombs
    }

    /**
     * Creates the Box2D body for the player.
     * The body represents the player's collision area in the game world, and it will be used
     * by the physics engine to move the player and detect collisions.
     *
     * @param world The Box2D world to create the body in.
     * @param startX The initial X position of the player.
     * @param startY The initial Y position of the player.
     * @return The created Box2D body for the player.
     */
    private Body createHitbox(World world, float startX, float startY) {
        // BodyDef serves as the blueprint for the Box2D body.
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;  // The player is dynamic, so it can be moved and collide.
        bodyDef.position.set(startX, startY);  // Set the player's initial position.

        // Create the body in the Box2D world using the defined blueprint.
        Body body = world.createBody(bodyDef);

        // Create the player's shape using a circle (representing the player's hitbox).
        CircleShape circle = new CircleShape();
        circle.setRadius(0.3f);  // Set the radius of the circle to match the player's size.

        // Attach the shape to the body as a fixture (defining its physical properties).
        body.createFixture(circle, 1.0f);  // The density of the fixture is 1.0 (standard for players).

        // Dispose of the shape as it is no longer needed after the fixture is created.
        circle.dispose();

        // Set the player as the user data for the body, so we can refer to the player from the body later.
        body.setUserData(this);

        return body;  // Return the created body.
    }

    // ENUM for defining the player's movement directions.
    public enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE
    }

    /**
     * Updates the player's state, including movement and animation.
     * This method checks for player input (arrow keys) and updates the player's velocity accordingly.
     *
     * @param frameTime The time elapsed since the last frame, used to update animation and movement.
     */
    public void update(float frameTime) {
        this.elapsedTime += frameTime;  // Increase the elapsed time for animation.
        float speed = 3.5f;  // Define the player's movement speed.
        float xVelocity = 0;
        float yVelocity = 0;
        boolean isArrowKeyPressed = false;

        // Handle player movement based on key presses.
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.UP)) {
            currentDirection = Direction.UP;
            yVelocity = speed;
            isArrowKeyPressed = true;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.DOWN)) {
            currentDirection = Direction.DOWN;
            yVelocity = -speed;  // Moving down decreases the Y position.
            isArrowKeyPressed = true;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
            currentDirection = Direction.LEFT;
            xVelocity = -speed;  // Moving left decreases the X position.
            isArrowKeyPressed = true;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
            currentDirection = Direction.RIGHT;
            xVelocity = speed;  // Moving right increases the X position.
            isArrowKeyPressed = true;
        }

        // Play walking sound if the player is moving, stop sound if not.
        if (isArrowKeyPressed && !isWalking) {
            MusicTrack.WALKING.play();  // Play walking sound if the player starts moving.
        } else if (!isArrowKeyPressed && isWalking) {
            MusicTrack.WALKING.dispose();  // Stop walking sound if the player stops moving.
        }

        isWalking = isArrowKeyPressed;  // Update the walking status.

        // Set the player's velocity in the Box2D world based on input.
        this.hitbox.setLinearVelocity(xVelocity, yVelocity);
    }

    /**
     * Retrieves the current graphical appearance of the player.
     * Based on the current direction, the appropriate animation frame is returned.
     *
     * @return The texture region representing the player's current appearance.
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        switch (currentDirection) {
            case UP:
                return Animations.CHARACTER_WALK_UP.getKeyFrame(this.elapsedTime, true);
            case DOWN:
                return Animations.CHARACTER_WALK_DOWN.getKeyFrame(this.elapsedTime, true);
            case LEFT:
                return Animations.CHARACTER_WALK_LEFT.getKeyFrame(this.elapsedTime, true);
            case RIGHT:
                return Animations.CHARACTER_WALK_RIGHT.getKeyFrame(this.elapsedTime, true);
            case NONE:
            default:
                return Animations.CHARACTER_WALK_DOWN.getKeyFrame(this.elapsedTime, true);
        }
    }

    /**
     * Retrieves the X-coordinate of the player.
     * The player's X-coordinate is based on the position of the hitbox in the Box2D world.
     *
     * @return The current X-coordinate of the player.
     */
    @Override
    public float getX() {
        return hitbox.getPosition().x;  // Get the X position from the Box2D hitbox.
    }

    /**
     * Retrieves the Y-coordinate of the player.
     * The player's Y-coordinate is based on the position of the hitbox in the Box2D world.
     *
     * @return The current Y-coordinate of the player.
     */
    @Override
    public float getY() {
        return hitbox.getPosition().y;  // Get the Y position from the Box2D hitbox.
    }

    // Getter and setter methods for concurrentBombCount and blastRadius.

    public int getConcurrentBombCount() {
        return concurrentBombCount;
    }

    public void setConcurrentBombCount(int concurrentBombCount) {
        this.concurrentBombCount = concurrentBombCount;
    }

    public int getBlastRadius() {
        return blastRadius;
    }

    public void setBlastRadius(int blastRadius) {
        this.blastRadius = blastRadius;
    }
}
