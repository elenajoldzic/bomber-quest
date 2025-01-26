package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.bomberquest.texture.Drawable;

/**
 * Represents an explosion tile in the game, which shows an animated explosion effect.
 * The explosion animation plays for a brief period, after which the explosion is considered finished.
 */
public class ExplosionTile extends GameObject implements Drawable {

    // The X and Y position of the explosion tile in the game world.
    private float x, y;

    // The time elapsed since the explosion started. Used to control the animation.
    private float elapsedTime;

    // The animation representing the explosion.
    private final Animation<TextureRegion> animation;

    // A flag to indicate whether the explosion animation has finished.
    private boolean finished;

    /**
     * Creates a new explosion tile at the specified position with the given animation.
     *
     * @param x The X position of the explosion tile.
     * @param y The Y position of the explosion tile.
     * @param animation The animation to be played for the explosion effect.
     */
    public ExplosionTile(float x, float y, Animation<TextureRegion> animation) {
        super(x, y);  // Calls the parent constructor to set the position.
        this.x = x;
        this.y = y;
        this.elapsedTime = 0f;  // Initialize elapsed time to zero.
        this.animation = animation;  // Set the animation.
        finished = false;  // Initially, the animation is not finished.
    }

    /**
     * Updates the state of the explosion tile, including the elapsed time and checking if the animation is finished.
     *
     * @param deltaTime The time elapsed since the last frame. This is used to update the animation.
     */
    public void update(float deltaTime) {
        elapsedTime += deltaTime;  // Increment the elapsed time by the time difference since the last frame.

        // If the elapsed time is greater than or equal to 0.5 seconds, the animation is considered finished.
        if (elapsedTime >= 0.5f) {
            finished = true;  // Mark the animation as finished.
        }
    }

    /**
     * Retrieves the current frame of the explosion animation.
     * The frame is selected based on the elapsed time of the animation.
     *
     * @return The current texture region (frame) of the animation.
     */
    public TextureRegion getCurrentAppearance() {
        return animation.getKeyFrame(elapsedTime, false);  // Get the current frame of the animation based on elapsed time.
    }

    /**
     * Checks if the animation has finished playing.
     * This can be used to determine if the explosion has completed.
     *
     * @return True if the animation is finished, false otherwise.
     */
    public boolean isAnimationFinished() {
        return finished;  // Return the finished status of the animation.
    }

    /**
     * Retrieves the X position of the explosion tile in the game world.
     *
     * @return The X position of the explosion tile.
     */
    public float getX() {
        return x;  // Return the stored X position.
    }

    /**
     * Retrieves the Y position of the explosion tile in the game world.
     *
     * @return The Y position of the explosion tile.
     */
    public float getY() {
        return y;  // Return the stored Y position.
    }
}

