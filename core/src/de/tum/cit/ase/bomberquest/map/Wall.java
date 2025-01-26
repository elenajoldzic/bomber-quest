package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.texture.Drawable;

/**
 * Base class for walls (indestructible and destructible) with common properties and behavior.
 */
public abstract class Wall implements Drawable {

    protected final float x;
    protected final float y;
    protected Body body;

    /**
     * Constructor for Wall.
     * @param world The Box2D world to add the wall's hitbox to.
     * @param x The X position.
     * @param y The Y position.
     */
    public Wall(World world, float x, float y) {
        this.x = x;
        this.y = y;
        createHitbox(world);
    }

    /**
     * Create a Box2D body for the wall.
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
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    /**
     * Abstract method to get the texture of the wall. Implemented in subclasses.
     */
    @Override
    public abstract TextureRegion getCurrentAppearance();

    public Body getBody() {
        return body;
    }
}

