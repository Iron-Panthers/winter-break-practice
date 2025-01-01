package E_project_time.Simulation.Objects;

import java.awt.Color;
import java.awt.Graphics;

import E_project_time.Simulation.Simulation;

public class Box extends Drawable implements Collider {
    public double Width;
    public double Height;

    private double CenterX;
    private double CenterY;

    public Box(double x, double y, double width, double height) {
        super(x, y);

        this.Width = width;
        this.Height = height;

        CenterX = (x - 1) + (width + 1) * 0.5;
        CenterY = (y - 1) + (height) * 0.5;
    }

    @Override
    public void Draw(Graphics g) {
        g.setColor(Color.GRAY);
        g.fillRect((int)Positionx + 1, (int)Positiony + 1, 
            (int)Width - 2, (int)Height - 2);
    }

    @Override
    public void PreUpdate() { }

    @Override
    public void HandleCollision(Drawable obj) {
        double edgeDstX = (Width + 1) * 0.5 - Math.abs(obj.Positionx - CenterX);
        double edgeDstY = (Height) * 0.5 - Math.abs(obj.Positiony - CenterY);

        if (edgeDstX >= 0 && edgeDstY >= 0) {
            if (edgeDstX < edgeDstY) {
                obj.Positionx = (Width + 1) * 0.5 * Math.signum(obj.Positionx - CenterX) + CenterX;
                obj.Velocityx *= -Simulation.COLLISION_DAMPING;
            }
            else {
                obj.Positiony = (Height) * 0.5 * Math.signum(obj.Positiony - CenterY) + CenterY;
                obj.Velocityy *= -Simulation.COLLISION_DAMPING;
            }
        }
    }

    @Override
    public String toString() {
        return "ur mom";
    }
}
