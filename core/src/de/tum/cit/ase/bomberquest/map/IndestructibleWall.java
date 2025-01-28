package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.texture.Textures;

/**
 * An indestructible wall that cannot be destroyed.
 */
public class IndestructibleWall extends Wall {

    /**
     * Constructs an indestructible wall at the specified position in the game world.
     *
     * @param world The physics world the wall belongs to.
     * @param x     The x-coordinate of the wall's position.
     * @param y     The y-coordinate of the wall's position.
     */
    public IndestructibleWall(World world, float x, float y) {
        super(world, x, y);
    }

    /**
     * Updates the state of the indestructible wall.
     * Since indestructible walls do not have dynamic behavior, this method does nothing.
     *
     * @param deltaTime The time elapsed since the last update, in seconds.
     */
    @Override
    public void update(float deltaTime) {
        // No behavior currently implemented
    }

    /**
     * Returns the current appearance of the indestructible wall.
     * This is typically the texture associated with indestructible walls.
     *
     * @return The texture region representing the indestructible wall's appearance.
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        return Textures.INDESTRUCTIBLEWALL;
    }
}
