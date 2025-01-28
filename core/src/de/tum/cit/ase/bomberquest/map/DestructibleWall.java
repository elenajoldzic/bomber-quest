package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.texture.Textures;

/**
 * A destructible wall that can be destroyed during the game.
 */
public class DestructibleWall extends Wall {

    /**
     * Constructs a destructible wall at the specified position in the game world.
     *
     * @param world The physics world the wall belongs to.
     * @param x     The x-coordinate of the wall's position.
     * @param y     The y-coordinate of the wall's position.
     */
    public DestructibleWall(World world, float x, float y) {
        super(world, x, y);
    }

    /**
     * Updates the state of the destructible wall.
     * Currently, this method does nothing but can be extended in the future
     * to include behavior such as animating destruction or other effects.
     *
     * @param deltaTime The time elapsed since the last update, in seconds.
     */
    @Override
    public void update(float deltaTime) {
        // No behavior currently implemented
    }

    /**
     * Returns the current appearance of the destructible wall.
     * This is typically the texture associated with destructible walls.
     *
     * @return The texture region representing the destructible wall's appearance.
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        return Textures.DESTRUCTIBLEWALL;
    }
}
