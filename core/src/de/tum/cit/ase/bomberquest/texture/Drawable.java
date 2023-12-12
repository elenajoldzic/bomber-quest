package de.tum.cit.ase.bomberquest.texture;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Represents something that can be drawn on the screen.
 * NOTE: The position returned by {@link #getX()} and {@link #getY()} is the
 * position of the BOTTOM LEFT CORNER of the texture in the game.
 */
public interface Drawable {
    
    /**
     * Gets the current appearance of the Drawable.
     * This can change over time.
     * @return The current appearance as a {@link TextureRegion}.
     */
    TextureRegion getCurrentAppearance();
    
    /**
     * Gets the X coordinate of the drawable in the game world grid.
     * Note that this is a TILE coordinate, not a pixel coordinate.
     * It must be multiplied by {@link de.tum.cit.ase.bomberquest.screen.GameScreen#TILE_SIZE_PX}
     * and {@link de.tum.cit.ase.bomberquest.screen.GameScreen#SCALE} to get the pixel coordinate.
     * @return The X coordinate of the drawable.
     */
    float getX();
    
    /**
     * Gets the Y coordinate of the drawable in the game world grid.
     * Note that this is a TILE coordinate, not a pixel coordinate.
     * It must be multiplied by {@link de.tum.cit.ase.bomberquest.screen.GameScreen#TILE_SIZE_PX}
     * and {@link de.tum.cit.ase.bomberquest.screen.GameScreen#SCALE} to get the pixel coordinate.
     * @return The Y coordinate of the drawable.
     */
    float getY();
    
}
