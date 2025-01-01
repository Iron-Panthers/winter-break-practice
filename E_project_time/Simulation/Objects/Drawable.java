package E_project_time.Simulation.Objects;

import java.awt.Graphics;

import E_project_time.Display;
import E_project_time.Simulation.Simulation;

/**
 * A class that represents an object drawable to the screen
 */
public abstract class Drawable {
    public double Positionx;
    public double Positiony;

    public boolean Visible = true;
    public boolean AllowsCollision = true;

    public double Velocityx = 0;
    public double Velocityy = 0;

    public int id;
    protected boolean Destroyed = false;

    public Drawable(double x, double y) {
        this.Positionx = x;
        this.Positiony = y;

        this.id = (int)Math.floor(Math.random() * Integer.MAX_VALUE);
    }

    /**
     * Draw the object
     * @param g - Graphics input
     */
    public abstract void Draw(Graphics g);

    /**
     * Update the object before positions are updated (i.e. set velocities)
     */
    public abstract void PreUpdate();

    /**
     * Update the object (run custom logic) (REMEMBER TO CALL SUPER FIRST)
     */
    public void Update() {
        // Apply Velocity
        Positionx += Velocityx;
        Positiony += Velocityy;

        if (AllowsCollision) {
            for (Collider collider : Simulation.SimulationColliders)
                collider.HandleCollision(this);
        }
    }

    /**
     * Destroy the drawable in the next frame
     */
    public void Destroy() {
        if (Destroyed) return;

        Destroyed = true;
        Simulation.AddObjectToRemovalQueue(this);
    }

    /**
     * You know what this does
     */
    public abstract String toString();


    /**
     * Checks if drawable is visible (good for basic culling)
     * @return Returns true if visible
     */
    public boolean IsVisible() {
        return Visible && Positionx >= 0 && Positionx < Display.VIEWPORT_WIDTH &&
            Positiony >= 0 && Positiony < Display.VIEWPORT_HEIGHT;
    }

    /**
     * Checks if drawable would be visible as a position (like IsVisible() but without Visible)
     * @return Returns true if within range
     */
    public boolean WithinVisibleRange() {
        return Positionx >= 0 && Positionx < Display.VIEWPORT_WIDTH &&
            Positiony >= 0 && Positiony < Display.VIEWPORT_HEIGHT;
    }
}
