package de.tum.cit.ase.bomberquest.texture;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Contains all texture constants used in the game.
 * It is good practice to keep all textures and animations in constants to avoid loading them multiple times.
 * These can be referenced anywhere they are needed.
 */
public class Textures {
    
    public static final TextureRegion FLOWERS = SpriteSheet.BASIC_TILES.at(2, 5);

    public static final TextureRegion CHEST = SpriteSheet.BASIC_TILES.at(5, 5);

    public static final TextureRegion DESTRUCTIBLEWALL = SpriteSheet.BASIC_TILES.at(10, 1);

    //public static final TextureRegion DESTRUCTIBLEWALL = SpriteSheet.ORIGINAL_BOMBERMAN.at(4, 5);

    public static final TextureRegion INDESTRUCTIBLEWALL = SpriteSheet.BASIC_TILES.at(1, 1);

    public static final TextureRegion EXIT = SpriteSheet.BASIC_TILES.at(7, 3);

    public static final TextureRegion ENTRANCE = SpriteSheet.BASIC_TILES.at(7, 1);

    public static final TextureRegion CONCURRENTBOMB = SpriteSheet.ORIGINAL_BOMBERMAN.at(15, 1);

    public static final TextureRegion BLASTRADIUS = SpriteSheet.ORIGINAL_BOMBERMAN.at(15, 2);

    public static final TextureRegion ENEMY = SpriteSheet.ORIGINAL_BOMBERMAN.at(19, 7);

    // Elena
    public static final TextureRegion BOMB = SpriteSheet.ORIGINAL_BOMBERMAN.at(14, 7);
}
