package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;

/**
 * Flowers are a static object without any special properties.
 * They do not have a hitbox, so the player does not collide with them.
 * They are purely decorative and serve as a nice floor decoration.
 */
public class Flowers extends GameObject implements Drawable {

    private final int x;
    private final int y;

    /**
     * Constructs a new Flowers object at the specified coordinates.
     *
     * @param x the x-coordinate of the flower
     * @param y the y-coordinate of the flower
     */
    public Flowers(int x, int y) {
        super(x, y);
        this.x = x;
        this.y = y;
    }

    /**
     * Updates the state of the flower.
     * Since flowers are static and have no special behavior, this method does nothing.
     *
     * @param deltaTime the time elapsed since the last update, in seconds
     */
    @Override
    public void update(float deltaTime) {
        // No behavior to update for static flowers
    }

    /**
     * Returns the current appearance of the flower as a texture.
     *
     * @return the texture region representing the flower's appearance
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        return Textures.TILES;
    }

    /**
     * Returns the x-coordinate of the flower.
     *
     * @return the x-coordinate
     */
    @Override
    public float getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the flower.
     *
     * @return the y-coordinate
     */
    @Override
    public float getY() {
        return y;
    }
}
