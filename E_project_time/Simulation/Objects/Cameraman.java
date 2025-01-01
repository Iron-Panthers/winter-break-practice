package E_project_time.Simulation.Objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import E_project_time.Display;
import E_project_time.Main;
import E_project_time.Simulation.Simulation;
import E_project_time.Simulation.Vector;

public class Cameraman extends Drawable implements Collider {
    //region Data
    private static final CameramanData[] CAMERAMEN_TYPES = new CameramanData[] {
        new CameramanData(40, 20, 0.5, 1, "E_project_time/Assets/tvtitan2.gif", 1, 0.5, 20),
        new CameramanData(20, 10, 1.4, 2, "E_project_time/Assets/scaredcamera.gif", 0.5, 0.5, 15),
        new CameramanData(150, 30, 0.3, 0.5, "E_project_time/Assets/dancingcamera.gif", 0.1, 0.45, 30),
        new CameramanData(300, 50, 0.1, 0.1, "E_project_time/Assets/floating.gif", 0.6, 0.4, 50),
        new CameramanData(10, 15, 1.5, 2, "E_project_time/Assets/popup.gif", 0.4, 0.5, 20),
        new CameramanData(1000, 100, 0.07, 0.07, "E_project_time/Assets/mega.gif", 0.4, 0.35, 100)
    };

    private static class CameramanData {
        public int Health;
        public int Attack;
        public double MinSpeed;
        public double MaxSpeed;
        public String FilePath;
        public double FrameSpeed;
        public double Chance;
        public int Points;
        public CameramanData(int health, int attack, double minSpeed, double maxSpeed, String filePath, double frameSpeed, double chance, int points) {
            Health = health;
            Attack = attack;
            MinSpeed = minSpeed;
            MaxSpeed = maxSpeed;
            FilePath = filePath;
            FrameSpeed = frameSpeed;
            Chance = chance;
            Points = points;
        }
    }
    // endregion

    private Vector EntrancePosition;
    private Vector Direction;
    private int Width, Height;

    // Cameraman
    private ImageReader CameramanGIFReader;
    private int TotalFrames;
    private double CurrentFrame = 0;
    private double FrameSpeed = 0;

    private AffineTransform transformation;
    private AffineTransformOp transformationOp;
    
    // Stats
    private int MaxHealth, Health;
    private double Speed;
    private int Attack;
    private int Points;

    public Cameraman() {
        super(10000, 10000);

        CameramanData type = GenerateRandomType();
        Speed = Math.random() * (type.MaxSpeed - type.MinSpeed) + type.MinSpeed;
        Attack = type.Attack;
        MaxHealth = Health = type.Health;      
        FrameSpeed = type.FrameSpeed;
        Points = type.Points;

        // Prepare GIF Frames for rendering
        try {
            ImageInputStream input = ImageIO.createImageInputStream(new File(type.FilePath));

            // Get the GIF reader
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
            if (!readers.hasNext()) throw new IOException("No GIF readers found");

            CameramanGIFReader = readers.next();
            CameramanGIFReader.setInput(input);

            Width = CameramanGIFReader.getWidth(0);
            Height = CameramanGIFReader.getHeight(0);

            TotalFrames = CameramanGIFReader.getNumImages(true);

            // Prepare transformations
            transformation = AffineTransform.getScaleInstance(1, -1);
            transformation.translate(0, -CameramanGIFReader.getHeight(0));
            transformationOp = new AffineTransformOp(transformation, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        } catch (IOException e) { e.printStackTrace(); }

        // Generate random spawn location at sides of screen
        int side = (int)(Math.random() * 3);
        switch (side) {
            case 0: EntrancePosition = new Vector(Math.random(), 1 - Height / (double)Display.VIEWPORT_HEIGHT); break;
            case 1: EntrancePosition = new Vector(1 - Width / (double)Display.VIEWPORT_WIDTH, Math.random()); break;
            case 2: EntrancePosition = new Vector(0, Math.random()); break;
        }
        
        EntrancePosition.multiply(Display.VIEWPORT_WIDTH, Display.VIEWPORT_HEIGHT);
        Positionx = EntrancePosition.x - Width / 2; Positiony = EntrancePosition.y - Height / 2;

        // Get Direction of Movement
        double dst = Math.sqrt(Math.pow(Positionx + Width / 2 - Display.VIEWPORT_WIDTH / 2, 2) + 
            Math.pow(Positiony + Height / 2 - Display.VIEWPORT_HEIGHT / 2, 2));

        Direction = new Vector((Display.VIEWPORT_WIDTH / 2 - Positionx - Width / 2) / dst, 
            (Display.VIEWPORT_HEIGHT / 2 - Positiony - Height / 2) / dst);
    }

    @Override
    public void Draw(Graphics g) {
        try {
            g.drawImage(transformationOp.filter(CameramanGIFReader.read((int)(CurrentFrame += FrameSpeed) % TotalFrames), null), 
                (int)Positionx, (int)Positiony, null);
        } catch(IOException e) { e.printStackTrace(); }

        // Draw Healthbar
        g.setColor(Color.GREEN);
        g.fillRect((int)Positionx, (int)Positiony, (int)(Health / (double)MaxHealth * Width), 4);
        g.setColor(Color.RED);
        g.fillRect((int)(Health / (double)MaxHealth * Width + Positionx), (int)Positiony, 
            (int)((1 - Health / (double)MaxHealth) * Width), 4);
    }

    @Override
    public void PreUpdate() { 
        if (Math.abs(Positionx + Width / 2 - Display.VIEWPORT_WIDTH / 2) < 30 &&
            Math.abs(Positiony + Height / 2 - Display.VIEWPORT_HEIGHT / 2) < 30) {

            WaterGun.THESkibidiToilet.Health -= Attack;
            Destroy();

            WaterGun.THESkibidiToilet.VisualDamage();
        }

        if (Health <= 0) Destroy();

        Velocityx = Direction.x * Speed;
        Velocityy = Direction.y * Speed;
    }

    @Override
    public String toString() {
        return "The op of the skbidi toilet";
    }

    @Override
    public void HandleCollision(Drawable obj) {
        if (obj instanceof Particle) { // Kill Particles on hit with Cameraman
            double edgeDstX = (Width + 1) * 0.5 - Math.abs(obj.Positionx - Positionx - Width / 2);
            double edgeDstY = (Height) * 0.5 - Math.abs(obj.Positiony - Positiony - Height / 2);
            if (edgeDstX < 0 || edgeDstY < 0) return; 

            obj.Destroy();
            Health--;

            if (Health <= 0) { 
                if (!Destroyed) {
                    Simulation.AddObjectToQueue(new FallingText(Positionx + Width / 2, Positiony + Height, "SKIBIDI", Direction));
                    Main.Points += Points;
                }
            
                Destroy();
            }
        }
    }

    /**
     * Get a random data from CAMERAMEN_TYPES using the chance weights
     * @return A CameramanData
     */
    private CameramanData GenerateRandomType() {
        int greatestIndex = 0;
        double greatestValue = Math.random() * CAMERAMEN_TYPES[0].Chance, value = 0;
        for (int i = 0; i < CAMERAMEN_TYPES.length; i++) {
            if ((value = Math.random() * CAMERAMEN_TYPES[i].Chance) > greatestValue) {
                greatestValue = value;
                greatestIndex = i;
            }
        }
        
        return CAMERAMEN_TYPES[greatestIndex];
    }
}
