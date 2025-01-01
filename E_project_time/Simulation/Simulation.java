package E_project_time.Simulation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import E_project_time.Display;
import E_project_time.Simulation.Objects.*;

public class Simulation {
    // Forces
    public static final double GRAVITY = 0.06;
    public static final double COLLISION_DAMPING = 0.2;

    public static final double SMOOTHING_RADIUS = 5;
    public static final double PARTICLE_MASS = 1;

    public static final double TARGET_DENSITY = 0.5;
    public static final double PRESSURE_MULTIPLIER = 30;
    public static final double NEAR_PRESSURE_MULTIPLIER = 50;

    public static final int MAX_PARTICLES = 7000;

    public static final int CELL_AMOUNT_X = (int)Math.ceil(Display.VIEWPORT_WIDTH / Simulation.SMOOTHING_RADIUS);
    public static final int CELL_AMOUNT_Y = (int)Math.ceil(Display.VIEWPORT_HEIGHT / Simulation.SMOOTHING_RADIUS);
    public static final int CELL_AMOUNT = CELL_AMOUNT_X * CELL_AMOUNT_Y;

    // Simulation Objects
    public static ArrayList<Drawable> SimulationObjects;
    public static ArrayList<Collider> SimulationColliders;
    public static ArrayList<Drawable> QueuedObjects;
    public static ArrayList<Drawable> RemovalQueuedObjects;

    public static ArrayList<Particle> SimulatedParticles;
    public static int[] ParticleReferenceLookup = new int[CELL_AMOUNT];

    public static Color[] CellColors = new Color[] { Color.WHITE, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PINK, Color.ORANGE, Color.MAGENTA, Color.GRAY, Color.CYAN };

    public Simulation() {
        SimulationObjects = new ArrayList<>();
        SimulationColliders = new ArrayList<>();
        SimulatedParticles = new ArrayList<>();

        QueuedObjects = new ArrayList<>();
        RemovalQueuedObjects = new ArrayList<>();

        AddObjectToQueue(new WaterGun(Display.VIEWPORT_WIDTH / 2, Display.VIEWPORT_HEIGHT / 2));
    }

    // region Queueing
    /**
     * Add object to queue to be added in the next frame to the simulation
     * @param obj - The object (drawable)
     */
    public static void AddObjectToQueue(Drawable obj) {
        if (obj instanceof Particle && SimulatedParticles.size() >= MAX_PARTICLES) return;
        QueuedObjects.add(obj);
    }

    /**
     * Did you know that queue is the only word in the english language with 4 consectutive vowels
     * @param obj - just kidding i lied look it up
     */
    public static void AddObjectToRemovalQueue(Drawable obj) {
        RemovalQueuedObjects.add(obj);
    }

    /**
     * Update the objects from the different queues
     */
    public static void CommitQueuedObjectsToSimulation() {
        for (Drawable obj : QueuedObjects) {
            if (obj instanceof Particle) SimulatedParticles.add((Particle)obj);
            if (obj instanceof Collider) SimulationColliders.add((Collider)obj);
            SimulationObjects.add(obj);
        }

        for (Drawable obj : RemovalQueuedObjects) {
            if (obj instanceof Particle) SimulatedParticles.remove((Particle)obj);
            if (obj instanceof Collider) SimulationColliders.remove((Collider)obj);
            SimulationObjects.remove(obj);
        }

        QueuedObjects.clear();
        RemovalQueuedObjects.clear();
    }
    // endregion

    /**
     * The function that contains and runs all logic for the Simulation (i.e. the logic for each drawable)
     */
    public void UpdateSimulationTick() {
        // Mark Particles for Rendering
        Particle.HasRenderedFluid = false;

        // Calculate pre update (velocities)
        SimulationObjects.parallelStream().forEach(Drawable::PreUpdate);

        SetupParticleLookups();

        // Calculate densities of particles
        SimulatedParticles.parallelStream().forEach(Simulation::CalculateDensity);

        // Use force for particles
        SimulatedParticles.parallelStream().forEach(p -> {
            if (p.ParticleDensity == 0) return;

            Vector pressureForce = CalculatePressureForce(p);
            p.Velocityx += pressureForce.x / p.ParticleDensity * 10;
            p.Velocityy += pressureForce.y / p.ParticleDensity * 10;
        });

        // Calculate update (positions and collisions)
        SimulationObjects.parallelStream().forEach(Drawable::Update);

        // Add Queued Objects
        CommitQueuedObjectsToSimulation();
    }

    // region Spatial Lookups for Optimization
    public interface ParticleCallback {
        public void function(Particle p);        
    }

    /**
     * Handle the setup for creating the cells and organizing the lookup table (ParticleReferenceLookup)
     */
    private static void SetupParticleLookups() {
        // Sort particles
        SimulatedParticles.sort((a, b) -> a.Cell - b.Cell);

        // Create the reference array
        Arrays.fill(ParticleReferenceLookup, -1);
        int currentCell = -1;

        for (int i = 0; i < SimulatedParticles.size(); i++) {
            if (SimulatedParticles.get(i).Cell != currentCell)
                ParticleReferenceLookup[(currentCell = SimulatedParticles.get(i).Cell)] = i;
        }
    }

    /**
     * Loops through all points that are within the radius
     * @param p - The parent particle
     * @param callback - The callback (contents of for loop)
     */
    public static void ForeachPointWithinRadius(Particle p, ParticleCallback callback) {
        double sqrRadius = SMOOTHING_RADIUS * SMOOTHING_RADIUS;
        int centerX = (int)Math.max(0, Math.floor(p.Predictedx / SMOOTHING_RADIUS)),
            centerY = (int)Math.max(0, Math.floor(p.Predictedy / SMOOTHING_RADIUS));

        for (int x = -1; x <= 1; x++) {
           for (int y = -1; y <= 1; y++) {
                if (centerX + x < 0 || centerX + x >= CELL_AMOUNT_X ||
                    centerY + y < 0 || centerY + y >= CELL_AMOUNT_Y) continue;

                int key = Particle.FindCellFromCoords(centerX + x, centerY + y);
                int startIndex = ParticleReferenceLookup[key];
                if (startIndex == -1) continue;

                for (int i = startIndex; i < SimulatedParticles.size(); i++) {
                    if (SimulatedParticles.get(i).Cell != key) break;

                    if (Utilities.CalculatePredictedSquaredDistance(SimulatedParticles.get(i), p) <= sqrRadius) {
                        callback.function(SimulatedParticles.get(i));
                    }
                }
            }
        }
    }

    /**
     * Loops through all points that are within a given cell key
     * @param key - The cell key
     * @param callback - The callback (contents of for loop)
     */
    public static void ForeachPointWithinCell(int key, ParticleCallback callback) {
        int startIndex = ParticleReferenceLookup[key];
        if (startIndex == -1) return;

        for (int i = startIndex; i < SimulatedParticles.size(); i++) {
            if (SimulatedParticles.get(i).Cell != key) break;
            callback.function(SimulatedParticles.get(i));
        }
    }
    // endregion

    // region Liquid Simulation Logic
    /**
     * Calculate and sets the density and nearDensity of a particular particle
     * @param particle - The given particle to calculate
     */
    protected static void CalculateDensity(Particle particle) {
        particle.ParticleDensity = 0.0;
        particle.ParticleNearDensity = 0;

        ForeachPointWithinRadius(particle, p -> {
            double dst = Utilities.CalculatePredictedDistance(particle, p);
            particle.ParticleDensity += Simulation.PARTICLE_MASS * Utilities.SmoothingKernel(Simulation.SMOOTHING_RADIUS, dst);
            particle.ParticleNearDensity += Simulation.PARTICLE_MASS * Utilities.SmoothingKernelNear(Simulation.SMOOTHING_RADIUS, dst);
        });
    }

    /**
     * Calculate the force for the fluid (BIG BRAIN)
     * @param particle - Input particle
     * @return The pressure direction Vector
     */
    protected static Vector CalculatePressureForce(Particle particle) {
        Vector gradient = new Vector();

        ForeachPointWithinRadius(particle, p -> {
            if (particle.id == p.id || p.ParticleDensity == 0) return;
            double dst = Utilities.CalculatePredictedDistance(particle, p);
            Vector dir = Utilities.CalculatePredictedNormalizedVector(particle, p, dst);

            double slope = Utilities.SmoothingKernelDerivative(Simulation.SMOOTHING_RADIUS, dst);
            double nearSlope = Utilities.SmoothingKernelNearDerivative(Simulation.SMOOTHING_RADIUS, dst);
            double sharedPressure = (DensityToPressure(p.ParticleDensity) + 
                DensityToPressure(particle.ParticleDensity)) / 2.0;
            double sharedNearPressure = (CalculateNearDensity(p.ParticleNearDensity) + 
                CalculateNearDensity(particle.ParticleNearDensity)) / 2.0;

            gradient.add(sharedPressure * dir.x * slope * Simulation.PARTICLE_MASS / p.ParticleDensity,
                sharedPressure * dir.y * slope * Simulation.PARTICLE_MASS / p.ParticleDensity);
            gradient.add(sharedNearPressure * dir.x * nearSlope * Simulation.PARTICLE_MASS / p.ParticleNearDensity,
                sharedNearPressure * dir.y * nearSlope * Simulation.PARTICLE_MASS / p.ParticleNearDensity);
        });

        return gradient;
    }

    /**
     * A simplified version of the conversion between density and pressure
     * @param density - The input density of the particle
     * @return Returns the pressure
     */
    protected static double DensityToPressure(double density) {
        return (density - TARGET_DENSITY) * PRESSURE_MULTIPLIER * 1000;
    }

    /**
     * A simplified version of the conversion between density and pressure
     * @param density - The input density of the particle
     * @return Returns the pressure
     */
    protected static double CalculateNearDensity(double nearDensity) {
        return nearDensity * NEAR_PRESSURE_MULTIPLIER * 1000;
    }
    // endregion
}
