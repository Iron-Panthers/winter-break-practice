package E_project_time;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.*;

import E_project_time.Simulation.Simulation;
import E_project_time.Simulation.Objects.Drawable;

public class PanelGrid extends JPanel implements MouseListener {
    private int _width;
    private int _height;

    public BufferedImage Output; 
    private Graphics _outputGraphics;

    private AffineTransform _transformation;
    private AffineTransformOp _transformationOp;

    public static boolean IsMouseDown = false;
    public static Point MousePosition = new Point(0, 0);

    public PanelGrid(int width, int height) {
        // Set board properties
        this._width = width;
        this._height = height;
        this.Output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this._outputGraphics = Output.getGraphics();

        // Activate MouseListener
        this.addMouseListener(this);

        // Initialize grid in JPanel
        this.setSize(width * Display.VIEWPORT_SCALE, height * Display.VIEWPORT_SCALE);
        this.setLocation(0, 0);
        this.setVisible(true);

        // Create Transformation to scale the JPanel
        this._transformation = new AffineTransform();
        this._transformation.scale(Display.VIEWPORT_SCALE, Display.VIEWPORT_SCALE);
        this._transformationOp = new AffineTransformOp(this._transformation, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    }

    // region Drawing Stuffs
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Update Mouse Position
        SetRelativeMousePosition();

        // Draw Sim Objects and apply transformations
        DrawSimulationObjects();
        g.drawImage(TransformOutput(), 0, _height * Display.VIEWPORT_SCALE, 
            _width * Display.VIEWPORT_SCALE, -_height * Display.VIEWPORT_SCALE, null);
        
        g.setColor(Color.WHITE);
        g.drawString("Particles: " + Simulation.SimulatedParticles.size(), 0, 10);
        g.setColor(Color.ORANGE);
        g.drawString("Points: " + Main.Points, 0, 25);
    }

    /**
     * Draw all the objects from Simulation.SimulationObjects to a BufferedImage
     */
    private void DrawSimulationObjects() {
        _outputGraphics.clearRect(0, 0, _width, _height);

        for (Drawable drawable : Simulation.SimulationObjects) {
            drawable.Draw(_outputGraphics);
        }
    }

    /**
     * Scale image output based on Display.VIEWPORT_SCALE
     * @return Returns the scaled image
     */
    private BufferedImage TransformOutput() {
        return _transformationOp.filter(Output, new BufferedImage(_width * Display.VIEWPORT_SCALE, 
            _height * Display.VIEWPORT_SCALE, BufferedImage.TYPE_INT_RGB));
    }

    // idk why i put this here but it's here
    /**
     * Sets the position of the mouse (relative to the window) (NOT TO BE CONFUSED WITH JPANEL.GETMOUSEPOSITION) ((0,0) is bottom left)
     */
    public void SetRelativeMousePosition() {
        Point pos = MouseInfo.getPointerInfo().getLocation(), m = this.getLocationOnScreen(); 
        int x = (pos.x - m.x) / Display.VIEWPORT_SCALE, y = _height - (pos.y - m.y) / Display.VIEWPORT_SCALE - 1;
        MousePosition = new Point(x, y);
    }
    // endregion
    // region Mouse Events
    // Sorry I have to override all of these and it doesn't work otherwise idk why
    @Override public void mouseClicked(MouseEvent e) { }
    @Override public void mousePressed(MouseEvent e) { IsMouseDown = true; }
    @Override public void mouseReleased(MouseEvent e) { IsMouseDown = false; }
    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { }
    // endregion
}
