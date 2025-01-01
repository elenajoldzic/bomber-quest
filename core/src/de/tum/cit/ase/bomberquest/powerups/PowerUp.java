package de.tum.cit.ase.bomberquest.powerups;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.texture.Drawable;

/**
 * Represents a generic power-up in the game.
 */
public abstract class PowerUp implements Drawable {

    protected final float x;
    protected final float y;
    protected Body body;

    public PowerUp(World world, float x, float y) {
        this.x = x;
        this.y = y;
        createHitbox(world);
    }

    /**
     * Create a Box2D body for the power-up.
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

    /**
     * Get the texture region to render for this power-up.
     * This method is abstract and should be implemented by subclasses.
     */
    public abstract TextureRegion getCurrentAppearance();

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
