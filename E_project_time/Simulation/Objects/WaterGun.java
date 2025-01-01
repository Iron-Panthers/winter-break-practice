package E_project_time.Simulation.Objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import E_project_time.PanelGrid;
import E_project_time.Simulation.Simulation;
import E_project_time.Simulation.Utilities;

public class WaterGun extends Drawable {
    public static WaterGun THESkibidiToilet;

    public double Angle = 0;
    public double Length = 15;
    public double VelocityMultiplier = 5;

    // Skibidi Toilet
    private BufferedImage SkibidiToiletImage;
    private static final String SkibidiPath = "E_project_time/Assets/skibidi.png";

    public int Width, Height;

    // Stats
    public int MaxHealth = 300, Health = MaxHealth;
    public int VisualDamaged = 0;

    public WaterGun(double x, double y) {
        super(x, y);

        if (THESkibidiToilet != null) Destroy();
        else THESkibidiToilet = this;

        try {
            SkibidiToiletImage = ImageIO.read(new File(SkibidiPath));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void Draw(Graphics g) {
        BufferedImage rotated = Utilities.RotateBufferedImage(SkibidiToiletImage, Angle, 1 + VisualDamaged * 0.2);
        Width = rotated.getWidth(); Height = rotated.getHeight();

        if (VisualDamaged > 0) g.setXORMode(Color.PINK);
        g.drawImage(rotated, (int)Positionx - Width / 2, (int)Positiony - Height / 2, null);

        // Draw Healthbar
        if (VisualDamaged > 0) g.setXORMode(Color.BLACK);
        g.setColor(Color.GREEN);
        g.fillRect((int)Positionx - 15, (int)Positiony - 40, (int)(Health / (double)MaxHealth * 30), 4);
        g.setColor(Color.RED);
        g.fillRect((int)(Health / (double)MaxHealth * 30 + Positionx - 15), (int)Positiony - 40, 
            (int)((1 - Health / (double)MaxHealth) * 30), 4);
    }

    @Override
    public void PreUpdate() {
        Angle = Math.atan2(PanelGrid.MousePosition.x - Positionx, PanelGrid.MousePosition.y - Positiony);
    }

    @Override
    public void Update() {
        super.Update();

        if (PanelGrid.IsMouseDown) {
            for (int i = 0; i < 4; i++) {
                Particle p = new Particle(Math.sin(Angle) * Length + Positionx + (Math.random() - 0.5) * 10, 
                    Math.cos(Angle) * Length + Positiony + (Math.random() - 0.5) * 10);
                p.Velocityx = Math.sin(Angle) * VelocityMultiplier;
                p.Velocityy = Math.cos(Angle) * VelocityMultiplier;

                Simulation.AddObjectToQueue(p);
            }
        }

        if (Health <= 0) Destroy();
        if (VisualDamaged > 0) VisualDamaged--;
    }

    @Override
    public String toString() {
        return "American public school";
    }

    public void VisualDamage() {
        VisualDamaged = 10;
    }
}
