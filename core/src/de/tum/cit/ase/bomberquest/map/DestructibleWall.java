package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;

/**
 * A chest is a static object with a hitbox, so the player cannot walk through it.
 */
public class DestructibleWall implements Drawable {

    // We would normally get the position from the hitbox, but since we don't need to move the chest, we can store the position directly.
    private final float x;
    private final float y;
    private Body body;

    /**
     * Create a chest at the given position.
     * @param world The Box2D world to add the chest's hitbox to.
     * @param x The X position.
     * @param y The Y position.
     */
    public DestructibleWall(World world, float x, float y) {
        this.x = x;
        this.y = y;
        // Since the hitbox never moves, and we never need to change it, we don't need to store a reference to it.
        createHitbox(world);
    }

    /**
     * Create a Box2D body for the chest.
     * @param world The Box2D world to add the body to.
     */
    private void createHitbox(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(this.x, this.y);
        this.body = world.createBody(bodyDef);
        PolygonShape box = new PolygonShape();
        box.setAsBox(0.5f, 0.5f);
        body.createFixture(box, 1.0f);
        box.dispose();
        body.setUserData(this);
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        return Textures.DESTRUCTIBLEWALL;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }
    public Body getBody(){
        return body;
    }
}
