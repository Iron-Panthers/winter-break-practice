package E_project_time.Simulation.Objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Arrays;

import E_project_time.Display;
import E_project_time.PanelGrid;
import E_project_time.Simulation.Simulation;
import E_project_time.Simulation.Utilities;
import E_project_time.Simulation.Vector;

public class Particle extends Drawable {
    public double ParticleDensity;
    public double ParticleNearDensity;
    public double Predictedx;
    public double Predictedy;

    public double FoamPressure;

    public int Cell;

    public static boolean HasRenderedFluid = false;
    private static double[] RenderedImage = new double[Display.VIEWPORT_WIDTH * Display.VIEWPORT_HEIGHT];
    private static double[] RenderedFoam = new double[Display.VIEWPORT_WIDTH * Display.VIEWPORT_HEIGHT];

    private static int[][] GUASSIAN_3x3_BLUR = new int[][] {
        { 1, 2, 1 },
        { 2, 3, 2 },
        { 1, 2, 1 }
    };

    public Particle(double x, double y) {
        super(x, y);
    }

    @Override
    public void Draw(Graphics g) {
        if (!IsVisible() || HasRenderedFluid) return;

        HasRenderedFluid = true;
        MasterDraw(g);
    }

    /**
     * Draws the entire body of fluid to make it a singular whole instead of noisy pixels
     */
    public static void MasterDraw(Graphics g) {        
        Arrays.fill(RenderedImage, 0);
        Arrays.fill(RenderedFoam, 0);
        double foamMin = 0, foamMax = 0.2;

        // Get the values for every pixel
        Simulation.SimulatedParticles.parallelStream().forEach(p -> {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    if (p.Positionx + x < 0 || p.Positionx + x >= Display.VIEWPORT_WIDTH ||
                        p.Positiony + y < 0 || p.Positiony + y >= Display.VIEWPORT_HEIGHT) continue;

                    RenderedImage[(int)(p.Positiony + y) * Display.VIEWPORT_WIDTH + (int)p.Positionx + x] += GUASSIAN_3x3_BLUR[y + 1][x + 1] / 8.0;
                    RenderedFoam[(int)(p.Positiony + y) * Display.VIEWPORT_WIDTH + (int)p.Positionx + x] += GUASSIAN_3x3_BLUR[y + 1][x + 1] / 8.0 * p.FoamPressure;
                }
            }
        });

        // Draw
        for (int i = 0; i < RenderedImage.length; i++) {
            if (RenderedImage[i] == 0) continue;
            float foam = (float)Math.pow(Math.max(0, Math.min(1, (RenderedFoam[i] - foamMin) / foamMax + foamMin)), 2) * 0.9f;

            g.setColor(CalculateColor(foam, (float)Math.floor(i / Display.VIEWPORT_WIDTH) / Display.VIEWPORT_HEIGHT, 
                1f - Math.min((float)RenderedImage[i], 1)));
            g.fillRect(i % Display.VIEWPORT_WIDTH, i / Display.VIEWPORT_WIDTH, 1, 1);
        }
    }

    @Override
    public void PreUpdate() {
        // Apply Gravity
        Velocityy -= Simulation.GRAVITY;

        // Set predicted positions
        Predictedx = Positionx + Velocityx;
        Predictedy = Positiony + Velocityy;
    
        Cell = FindCell();
    }

    @Override
    public void Update() {
        super.Update();

        // Collision Check and Forces
        ApplyBoundingBoxCollision();

        // Update Foam
        Simulation.ForeachPointWithinRadius(this, p -> FoamPressure = FoamPressure * 0.9 + GetFoam(this, p) * 0.1);
    
        // Destroy particles randomly so that lag isn't too bad, but make sure that it is happening while the mouse is down so it isn't very obvious :)
        if (PanelGrid.IsMouseDown && Math.random() < 0.0001 && Simulation.SimulatedParticles.size() > Simulation.MAX_PARTICLES - 1000) Destroy();
    }

    /**
     * Check for collisions and apply COLLISION_DAMPING
     */
    private void ApplyBoundingBoxCollision() {
        // Equivalent to bounce
        if (Positionx < 0 || Positionx >= Display.VIEWPORT_WIDTH) {
            Velocityx *= -Simulation.COLLISION_DAMPING;
        }
        if (Positiony < 0 || Positiony >= Display.VIEWPORT_HEIGHT) {
            Velocityy *= -Simulation.COLLISION_DAMPING;
        }

        Positionx = Math.max(Math.min(Positionx, Display.VIEWPORT_WIDTH - 1), 0);
        Positiony = Math.max(Math.min(Positiony, Display.VIEWPORT_HEIGHT - 1), 0);
    }

    /**
     * Calculate the amount of foam based on the divergence of velocity
     * @param particle - The parent particle
     * @param neighbor - The neighbor particle
     * @return Foam value
     */
    private double GetFoam(Particle particle, Particle neighbor) {
        Vector relativeVelocity = new Vector(particle.Velocityx - neighbor.Velocityx, 
            particle.Velocityy - neighbor.Velocityy);
        double magnitude = Math.sqrt(Math.pow(relativeVelocity.x, 2) + Math.pow(relativeVelocity.y, 2));

        relativeVelocity.divide(Math.max(magnitude, 0.00001)); 
        
        double dstToNeighbor = Utilities.CalculateDistance(particle, neighbor);
        Vector dirToNeighbor = Utilities.CalculateNormalizedVector(particle, neighbor, dstToNeighbor);

        double convergeWeight = (1 - (relativeVelocity.x * dirToNeighbor.x + relativeVelocity.y * dirToNeighbor.y)) * 0.5;
        double influence = 1 - Math.min(1, dstToNeighbor / Simulation.SMOOTHING_RADIUS);

        return magnitude * convergeWeight * influence;
    }

    /**
     * Get the force when the mouse is down
     * @param radius - The radius of the force
     * @param strength - The strength multiplier of the force
     */
    private void InteractionForce(double radius, double strength) {
        double sqrDst = Math.pow(PanelGrid.MousePosition.x - Positionx, 2) + 
            Math.pow(PanelGrid.MousePosition.y - Positiony, 2);

        if (sqrDst < Math.pow(radius, 2)) {
            double dst = Math.sqrt(sqrDst);
            double centerT = 1 - dst / radius;

            Velocityx += ((dst <= 0.000001 ? 0 : (PanelGrid.MousePosition.x - Positionx) / dst * strength) - Velocityx) * centerT;
            Velocityy += ((dst <= 0.000001 ? 0 : (PanelGrid.MousePosition.y - Positiony) / dst * strength) - Velocityy) * centerT;
        }
    }

    /**
     * Calculate the color based on a variable of the particle
     * @return A color gradient between blue (0) and red (1) with greens in the middle
     */
    private Color CalculateColor() {
        double value = Math.sqrt(Math.pow(Velocityx, 2) + Math.pow(Velocityy, 2));
        float x = (float)Math.max(Math.min(value, 1), 0);
        return new Color(x, (float)Math.pow(1 - Math.pow(x - 0.5, 2), 3), 1 - x);
    }

    /**
     * Calculate the color based on a value
     * @param value - A value to plug in the get the color
     * @param blackness - Lerp to black where 0 is no black and 1 is black
     * @return A color gradient between blue (0) and red (1) with greens in the middle
     */
    public static Color CalculateColor(double value, float y, float blackness) {
        float x = (float)Math.max(Math.min(value, 1), 0);
        //return new Color(x * (1 - blackness), (float)Math.pow(1 - Math.pow(x - 0.5, 2), 3) * (1 - blackness), (1 - x) * (1 - blackness));
        return new Color((0.1f + 0.9f * x) * (1 - blackness), 
            (1 - (y * 0.7f + 0.3f) + (y * 0.7f + 0.3f) * x) * (1 - blackness), 1 - blackness);
    }

    /**
     * Gets the cell key based on the position of the particle 
     * @return The cell key
     */
    private int FindCell() {
        return (int)(Math.max(0, Math.min(Simulation.CELL_AMOUNT_X - 1, Math.floor(Predictedx / Simulation.SMOOTHING_RADIUS))) + 
            Math.max(0, Math.min(Simulation.CELL_AMOUNT_Y - 1, Math.floor(Predictedy / Simulation.SMOOTHING_RADIUS))) * Simulation.CELL_AMOUNT_X);
    }
    /**
     * Gets the cell key based on a point 
     * @return The cell key
     */
    public static int FindCell(Point p) {
        return (int)(Math.max(0, Math.min(Simulation.CELL_AMOUNT_X - 1, Math.floor(p.x / Simulation.SMOOTHING_RADIUS))) + 
            Math.max(0, Math.min(Simulation.CELL_AMOUNT_Y - 1, Math.floor(p.y / Simulation.SMOOTHING_RADIUS))) * Simulation.CELL_AMOUNT_X);
    }
    /**
     * Gets the cell key based on a pair of coordinates 
     * @return The cell key
     */
    public static int FindCell(int x, int y) {
        return (int)(Math.max(0, Math.min(Simulation.CELL_AMOUNT_X - 1, Math.floor(x / Simulation.SMOOTHING_RADIUS))) + 
            Math.max(0, Math.min(Simulation.CELL_AMOUNT_Y - 1, Math.floor(y / Simulation.SMOOTHING_RADIUS))) * Simulation.CELL_AMOUNT_X);
    }
    /**
     * Gets the cell key based on cell coords 
     * @return The cell key
     */
    public static int FindCellFromCoords(int x, int y) {
        return (int)(Math.max(0, Math.min(Simulation.CELL_AMOUNT_X - 1, x)) + 
            Math.max(0, Math.min(Simulation.CELL_AMOUNT_Y - 1, y)) * Simulation.CELL_AMOUNT_X);
    }

    public String toString() {
        return ParticleDensity + " | " + Positionx + ", " + Positiony + " | " + Velocityx + ", " + Velocityy;
    }
}