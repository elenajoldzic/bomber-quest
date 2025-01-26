package de.tum.cit.ase.bomberquest.texture;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Contains all animation constants used in the game.
 * It is good practice to keep all textures and animations in constants to avoid loading them multiple times.
 * These can be referenced anywhere they are needed.
 */
public class Animations {

    /**
     * The animation for the character walking down.
     * The character walks down using a sequence of frames from the sprite sheet.
     */
    public static final Animation<TextureRegion> CHARACTER_WALK_DOWN = new Animation<>(0.1f,
            SpriteSheet.CHARACTER.at(1, 1),
            SpriteSheet.CHARACTER.at(1, 2),
            SpriteSheet.CHARACTER.at(1, 3),
            SpriteSheet.CHARACTER.at(1, 4)
    );

    /**
     * The animation for the character walking to the right.
     * The character walks to the right using a sequence of frames from the sprite sheet.
     */
    public static final Animation<TextureRegion> CHARACTER_WALK_RIGHT = new Animation<>(0.1f,
            SpriteSheet.CHARACTER.at(2, 1),
            SpriteSheet.CHARACTER.at(2, 2),
            SpriteSheet.CHARACTER.at(2, 3),
            SpriteSheet.CHARACTER.at(2, 4)
    );

    /**
     * The animation for the character walking up.
     * The character walks up using a sequence of frames from the sprite sheet.
     */
    public static final Animation<TextureRegion> CHARACTER_WALK_UP = new Animation<>(0.1f,
            SpriteSheet.CHARACTER.at(3, 1),
            SpriteSheet.CHARACTER.at(3, 2),
            SpriteSheet.CHARACTER.at(3, 3),
            SpriteSheet.CHARACTER.at(3, 4)
    );

    /**
     * The animation for the character walking to the left.
     * The character walks to the left using a sequence of frames from the sprite sheet.
     */
    public static final Animation<TextureRegion> CHARACTER_WALK_LEFT = new Animation<>(0.1f,
            SpriteSheet.CHARACTER.at(4, 1),
            SpriteSheet.CHARACTER.at(4, 2),
            SpriteSheet.CHARACTER.at(4, 3),
            SpriteSheet.CHARACTER.at(4, 4)
    );

    /**
     * The animation for the enemy walking.
     * The enemy walks using a sequence of frames from the sprite sheet.
     */
    public static final Animation<TextureRegion> ENEMY_WALK = new Animation<>(0.1f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(19, 1),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(19, 2),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(19, 3),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(19, 4),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(19, 5),
            SpriteSheet.ORIGINAL_BOMBERMAN.at(19, 6)
    );

    /**
     * The animation for a bomb in its idle state.
     * The bomb uses a sequence of frames from the sprite sheet to represent its idle state before it explodes.
     */
    public static final Animation<TextureRegion> BOMB_IDLE = new Animation<>(0.1f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(4, 1), // Bomb frame 1
            SpriteSheet.ORIGINAL_BOMBERMAN.at(4, 2), // Bomb frame 2
            SpriteSheet.ORIGINAL_BOMBERMAN.at(4, 3)  // Bomb frame 3
    );

    /**
     * The animation for the bomb explosion.
     * The bomb explodes using a sequence of frames from the sprite sheet.
     */
    public static final Animation<TextureRegion> BOMB_EXPLOSION = new Animation<>(0.1f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(3, 2), // Bomb frame 1
            SpriteSheet.ORIGINAL_BOMBERMAN.at(3, 3), // Bomb frame 2
            SpriteSheet.ORIGINAL_BOMBERMAN.at(3, 4)  // Bomb frame 3
    );

    /**
     * The animation for the explosion center.
     * The center of the explosion is shown using a specific frame from the sprite sheet.
     */
    public static final Animation<TextureRegion> EXPLOSION_CENTER = new Animation<>(0.1f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(12, 3)
    );

    /**
     * The animation for the horizontal part of the explosion.
     * This is shown using a specific frame from the sprite sheet for horizontal explosions.
     */
    public static final Animation<TextureRegion> EXPLOSION_HORIZONTAL = new Animation<>(0.1f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(12, 2)
    );

    /**
     * The animation for the vertical part of the explosion.
     * This is shown using a specific frame from the sprite sheet for vertical explosions.
     */
    public static final Animation<TextureRegion> EXPLOSION_VERTICAL = new Animation<>(0.1f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(11, 3)
    );

    /**
     * The animation for the explosion end in the upward direction.
     * This is shown using a specific frame from the sprite sheet for the upward end of the explosion.
     */
    public static final Animation<TextureRegion> EXPLOSION_END_UP = new Animation<>(0.1f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(10, 3)
    );

    /**
     * The animation for the explosion end in the downward direction.
     * This is shown using a specific frame from the sprite sheet for the downward end of the explosion.
     */
    public static final Animation<TextureRegion> EXPLOSION_END_DOWN = new Animation<>(0.1f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(14, 3)
    );

    /**
     * The animation for the explosion end in the right direction.
     * This is shown using a specific frame from the sprite sheet for the right end of the explosion.
     */
    public static final Animation<TextureRegion> EXPLOSION_END_R = new Animation<>(0.1f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(12, 5)
    );

    /**
     * The animation for the explosion end in the left direction.
     * This is shown using a specific frame from the sprite sheet for the left end of the explosion.
     */
    public static final Animation<TextureRegion> EXPLOSION_END_L = new Animation<>(0.1f,
            SpriteSheet.ORIGINAL_BOMBERMAN.at(12, 1)
    );
}
