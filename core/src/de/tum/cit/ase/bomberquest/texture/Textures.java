package de.tum.cit.ase.bomberquest.texture;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Contains all texture constants used in the game.
 * It is good practice to keep all textures and animations in constants to avoid loading them multiple times.
 * These can be referenced anywhere they are needed.
 */
public class Textures {
    
    public static final TextureRegion FLOWERS = SpriteSheet.BASIC_TILES.at(2, 5);

    public static final TextureRegion DESTRUCTIBLEWALL = SpriteSheet.ORIGINAL_BOMBERMAN.at(4, 5);

    public static final TextureRegion INDESTRUCTIBLEWALL = SpriteSheet.ORIGINAL_BOMBERMAN.at(4,4);

    public static final TextureRegion EXIT = SpriteSheet.BASIC_TILES.at(8, 2);

    public static final TextureRegion CONCURRENTBOMB = SpriteSheet.ORIGINAL_BOMBERMAN.at(15, 1);

    public static final TextureRegion BLASTRADIUS = SpriteSheet.ORIGINAL_BOMBERMAN.at(15, 2);
}
