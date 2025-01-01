package E_project_time.Simulation;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import E_project_time.Simulation.Objects.Particle;

public class Utilities {
    /**
     * Calculates the distance between particles
     * @param a - Particle A
     * @param b - Particle B
     * @return Returns the distance
     */
    public static double CalculateDistance(Particle a, Particle b) {
        return Math.sqrt(Math.pow(a.Positionx - b.Positionx, 2) +
            Math.pow(a.Positiony - b.Positiony, 2));
    }

    /**
     * Calculates the predicted squared distance between particles
     * @param a - Particle A
     * @param b - Particle B
     * @return Returns the predicted squared distance
     */
    public static double CalculatePredictedSquaredDistance(Particle a, Particle b) {
        return Math.pow(a.Predictedx - b.Predictedx, 2) +
            Math.pow(a.Predictedy - b.Predictedy, 2);
    }

    /**
     * Calculates the distance between the prediction of two particles
     * @param a - Particle A
     * @param b - Particle B
     * @return Returns the predicted distance
     */
    public static double CalculatePredictedDistance(Particle a, Particle b) {
        return Math.sqrt(Math.pow(a.Predictedx - b.Predictedx, 2) +
            Math.pow(a.Predictedy - b.Predictedy, 2));
    }

    /**
     * Calculate the normalized direction Vector between to points
     * @param from - The origin point
     * @param to - The direction point
     * @param dst - The given distance (-1 if unknown)
     * @return Returns a normalized direction Vector
     */
    public static Vector CalculateNormalizedVector(Particle from, Particle to, double dst) {
        if (dst == -1) dst = CalculateDistance(from, to);
        if (dst == 0) return RandomUnitVector();

        return new Vector((to.Positionx - from.Positionx) / dst,
            (to.Positiony - from.Positiony) / dst);
    }

    /**
     * Calculate the predicted normalized direction Vector between to points
     * @param from - The origin point
     * @param to - The direction point
     * @param dst - The given distance (-1 if unknown)
     * @return Returns a normalized direction Vector
     */
    public static Vector CalculatePredictedNormalizedVector(Particle from, Particle to, double dst) {
        if (dst == -1) dst = CalculatePredictedDistance(from, to);
        if (dst == 0) return RandomUnitVector();

        return new Vector((to.Predictedx - from.Predictedx) / dst,
            (to.Predictedy - from.Predictedy) / dst);
    }

    /**
     * Generates a random, normalized direction Vector
     * @return Returns a normalized Vector
     */
    public static Vector RandomUnitVector() {
        double a = Math.random() * 2 * Math.PI; // I 💖 the unit circle but radians are the 👿
        return new Vector(Math.sin(a), Math.cos(a));
    }

    /**
     * Smooth the effect between two particles using a curve similar to tanh
     * @param radius - The target radius of effect
     * @param dst - The actual distance between the two particles
     * @return Returns the magnitude of effect
     */
    public static double SmoothingKernel(double radius, double dst) {
        if (dst >= radius) return 0;

        double volume = Math.PI * Math.pow(radius, 4) / 6.0; // Thank you sebastion lague 💖💖💖💖💖💖
        return Math.pow(radius - dst, 2.0) / volume;
    }
    /**
     * Smooth the effect between two particles using a curve similar to tanh
     * @param radius - The target radius of effect
     * @param dst - The actual distance between the two particles
     * @return Returns the derivative of the Smoothing Function
     */
    public static double SmoothingKernelDerivative(double radius, double dst) {
        if (dst >= radius) return 0;

        double scale = 12 / (Math.pow(radius, 4 * Math.PI));
        return (dst - radius) * scale;
    }

    /**
     * Smooth the effect between two particles using a curve similar to tanh (FOR NEAR DENSITY)
     * @param radius - The target radius of effect
     * @param dst - The actual distance between the two particles
     * @return Returns the magnitude of effect
     */
    public static double SmoothingKernelNear(double radius, double dst) {
        if (dst >= radius) return 0;

        double volume = Math.PI * Math.pow(radius, 4) / 6.0; // Thank you sebastion lague 💖💖💖💖💖💖
        return Math.pow(radius - dst, 3.0) / volume;
    }
    /**
     * Smooth the effect between two particles using a curve similar to tanh (FOR NEAR DENSITY)
     * @param radius - The target radius of effect
     * @param dst - The actual distance between the two particles
     * @return Returns the derivative of the Smoothing Function
     */
    public static double SmoothingKernelNearDerivative(double radius, double dst) {
        if (dst > radius) return 0;

        double scale = 12 / (Math.pow(radius, 4 * Math.PI));
        return Math.pow(radius - dst, 2) * -scale;
    }

    /**
     * Rotate BufferedImage
     * @param original - Original image
     * @param angle - Angle in Rad
     * @param scale - Scale for funsies
     * @return The rotated image
     */
    public static BufferedImage RotateBufferedImage(BufferedImage original, double angle, double scale) {
        double sin = Math.abs(Math.sin(angle)), cos = Math.abs(Math.cos(angle));
        int w = original.getWidth(), h = original.getHeight();
        int newW = (int) Math.floor(w * cos + h * sin), newH = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage((int)(newW * scale), (int)(newH * scale), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = rotated.createGraphics();

        g.translate((newW - w) / 2, (newH - h) / 2);
        g.scale(scale, scale);
        g.rotate(-angle, w/2, h/2);
        g.drawRenderedImage(original, null);

        g.dispose(); // garabage collector 💘💘💝💖💖😍😍

        return rotated;
    }
}
