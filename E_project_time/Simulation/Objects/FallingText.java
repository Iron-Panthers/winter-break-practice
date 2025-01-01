package E_project_time.Simulation.Objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import E_project_time.Simulation.Simulation;
import E_project_time.Simulation.Vector;

public class FallingText extends Drawable {
    private String Text;

    private static final Font FONT = new Font("Comic Sans", Font.CENTER_BASELINE, 10);

    public FallingText(double x, double y, String text, Vector dir) {
        super(x, y);
        Text = text;

        Velocityx = -dir.x * 3;
        Velocityy = -dir.y * 3;
    }

    @Override
    public void Draw(Graphics g) {
        g.setColor(Color.ORANGE);
        g.setFont(FONT);
        g.drawString(Text, (int)Positionx, (int)Positiony);
    }

    @Override
    public void PreUpdate() {
        if (!IsVisible()) Destroy();

        // Apply drag and gravity
        Velocityx *= 0.99;
        Velocityy -= Simulation.GRAVITY;
    }

    @Override
    public String toString() {
        return Text;
    }
    
}
