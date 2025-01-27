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
    
    public Flowers(int x, int y) {
        super(x, y);
        this.x = x;
        this.y = y;
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public TextureRegion getCurrentAppearance() {
        return Textures.TILES;
    }
    
    @Override
    public float getX() {
        return x;
    }
    
    @Override
    public float getY() {
        return y;
    }
}
