package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Drawable;

/**
 * Represents the player character in the game.
 * The player has a hitbox, so it can collide with other objects in the game.
 */
public class Player implements Drawable {

    /** Total time elapsed since the game started. We use this for calculating the player movement and animating it. */
    private float elapsedTime;

    /** The Box2D hitbox of the player, used for position and collision detection. */
    private final Body hitbox;

    /** Player's current direction. We start with none*/
    private Direction currentDirection=Direction.NONE;

    /** Player's concurrentBombCount and blastRadius attributes  */
    private int concurrentBombCount; // Maximum bombs the player can place
    private int blastRadius;         // Blast radius of bombs

    // (Elena)
    private Bomb carriedBomb;        // player carried bomb
    private boolean isCarryingBomb = false;


    public Player(World world, float x, float y) {
        this.hitbox = createHitbox(world, x, y);
        this.concurrentBombCount = 1; // Default bomb count
        this.blastRadius = 1;
    }

    /**
     * Creates a Box2D body for the player.
     * This is what the physics engine uses to move the player around and detect collisions with other bodies.
     * @param world The Box2D world to add the body to.
     * @param startX The initial X position.
     * @param startY The initial Y position.
     * @return The created body.
     */
    private Body createHitbox(World world, float startX, float startY) {
        // BodyDef is like a blueprint for the movement properties of the body.
        BodyDef bodyDef = new BodyDef();
        // Dynamic bodies are affected by forces and collisions.
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set the initial position of the body.
        bodyDef.position.set(startX, startY);
        // Create the body in the world using the body definition.
        Body body = world.createBody(bodyDef);
        // Now we need to give the body a shape so the physics engine knows how to collide with it.
        // We'll use a circle shape for the player.
        CircleShape circle = new CircleShape();
        // Give the circle a radius of 0.3 tiles (the player is 0.6 tiles wide).
        circle.setRadius(0.3f);
        // Attach the shape to the body as a fixture.
        // Bodies can have multiple fixtures, but we only need one for the player.
        body.createFixture(circle, 1.0f);
        // We're done with the shape, so we should dispose of it to free up memory.
        circle.dispose();
        // Set the player as the user data of the body so we can look up the player from the body later.
        body.setUserData(this);
        return body;
    }

    /**
     * Move the player around in a circle by updating the linear velocity of its hitbox every frame.
     * This doesn't actually move the player, but it tells the physics engine how the player should move next frame.
     *
     */
    /*
    public void tick(float frameTime) {
        this.elapsedTime += frameTime;
        // Make the player move in a circle with radius 2 tiles
        // You can change this to make the player move differently, e.g. in response to user input.
        // See Gdx.input.isKeyPressed() for keyboard input
        float xVelocity = (float) Math.sin(this.elapsedTime) * 2;
        float yVelocity = (float) Math.cos(this.elapsedTime) * 2;
        this.hitbox.setLinearVelocity(xVelocity, yVelocity);
    }
*/


    //ENUM FOR DEFINING PLAYERS MOVING DIRECTION
    public enum Direction{
        UP,
        DOWN,
        LEFT,
        RIGHT,
        NONE
    }

    //THIS METHOD MAKES OUR PLAYER MOVE
    public void update(float frameTime) {
        this.elapsedTime += frameTime;
        float speed = 3.5f; //create a constant speed value for player to move
        float xVelocity = 0;
        float yVelocity = 0;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.UP)) {
            currentDirection=Direction.UP;
            yVelocity = (float) speed;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.DOWN)) {
            currentDirection=Direction.DOWN;
            yVelocity = (float) -speed; //on the negative side of y axis, different direction
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
            currentDirection=Direction.LEFT;
            xVelocity = (float) -speed; // Move left
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
            currentDirection=Direction.RIGHT;
            xVelocity = (float) speed;// Move right
        }
        this.hitbox.setLinearVelocity(xVelocity, yVelocity); //method for making our player move

    }


    @Override
    public TextureRegion getCurrentAppearance() {
        // Get the frame of the walk down animation that corresponds to the current time.
        // return Animations.CHARACTER_WALK_DOWN.getKeyFrame(this.elapsedTime, true);
        switch (currentDirection){
            case UP:
                return Animations.CHARACTER_WALK_UP.getKeyFrame(this.elapsedTime,true);
            case DOWN:
                return Animations.CHARACTER_WALK_DOWN.getKeyFrame(this.elapsedTime,true);
            case LEFT:
                return Animations.CHARACTER_WALK_LEFT.getKeyFrame(this.elapsedTime,true);
            case RIGHT:
                return Animations.CHARACTER_WALK_RIGHT.getKeyFrame(this.elapsedTime,true);
            case NONE:
            default:
                return Animations.CHARACTER_WALK_DOWN.getKeyFrame(this.elapsedTime,true);
        }
    }

    @Override
    public float getX() {
        // The x-coordinate of the player is the x-coordinate of the hitbox (this can change every frame).
        return hitbox.getPosition().x;
    }

    @Override
    public float getY() {
        // The y-coordinate of the player is the y-coordinate of the hitbox (this can change every frame).
        return hitbox.getPosition().y;
    }

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