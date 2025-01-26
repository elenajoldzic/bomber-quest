package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Represents a generic game object in the BomberQuest game.
 * This class serves as a superclass for specific game objects like Bombs,
 * providing shared properties and methods such as position and appearance.
 */
public abstract class GameObject {

    /** X-coordinate of the game object on the game map. */
    protected float x;

    /** Y-coordinate of the game object on the game map. */
    protected float y;

    /**
     * Constructor for creating a game object with specified coordinates.
     *
     * @param x The X-coordinate of the object.
     * @param y The Y-coordinate of the object.
     */
    public GameObject(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Updates the state of the game object. Specific subclasses will override this method to provide behavior.
     *
     * @param deltaTime Time elapsed since the last frame.
     */
    public abstract void update(float deltaTime);

    /**
     * Retrieves the current graphical appearance of the game object.
     * Subclasses will provide specific appearances.
     *
     * @return The texture region representing the current appearance.
     */
    public abstract TextureRegion getCurrentAppearance();

    /**
     * Retrieves the X-coordinate of the object.
     *
     * @return The X-coordinate.
     */
    public float getX() {
        return x;
    }

    /**
     * Retrieves the Y-coordinate of the object.
     *
     * @return The Y-coordinate.
     */
    public float getY() {
        return y;
    }
}
