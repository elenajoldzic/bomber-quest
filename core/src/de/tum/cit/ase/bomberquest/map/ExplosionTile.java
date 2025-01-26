package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.bomberquest.texture.Drawable;


public class ExplosionTile implements Drawable {
    private float x, y;
    private float elapsedTime;
    private final Animation<TextureRegion> animation;
    private boolean finished;
    public ExplosionTile(float x, float y, Animation<TextureRegion> animation) {
        this.x = x;
        this.y = y;
        this.elapsedTime = 0f;
        this.animation=animation;
        finished=false;
    }

    public void update(float deltaTime) {
        elapsedTime += deltaTime;
        // If the animation is finished (after 2 seconds), mark it as finished
        if (elapsedTime >= 0.5f) {
            finished = true;
        }
    }

    public TextureRegion getCurrentAppearance(){
        return animation.getKeyFrame(elapsedTime, false);
    }
    public boolean isAnimationFinished() {
        return finished;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

}

