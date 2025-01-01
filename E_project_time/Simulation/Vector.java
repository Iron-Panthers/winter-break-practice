package E_project_time.Simulation;

/**
 * A very simple Vector class for input and output of functions, just don't use them a lot for memory reasons
 */
public class Vector {
    public double x = 0;
    public double y = 0;

    public Vector() { }

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public void add(Vector b) {
        x += b.x;
        y += b.y;
    }

    public void add(double x, double y) {
        this.x += x;
        this.y += y; 
    }

    public void divide(double f) {
        x /= f;
        y /= f;
    }

    public void multiply(double x, double y) {
        this.x *= x;
        this.y *= y;
    }
}
