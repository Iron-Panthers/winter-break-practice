package E_project_time;

import javax.swing.*;

import E_project_time.Simulation.Simulation;
import E_project_time.Simulation.Objects.Cameraman;

import java.awt.*;
import java.awt.event.ActionEvent;

public class Display {
    public static final int VIEWPORT_WIDTH = 300;
    public static final int VIEWPORT_HEIGHT = 300;
    public static final int VIEWPORT_SCALE = 2;
    public static final int UPDATE_TICK = 20;

    public static JFrame MAIN_FRAME;
    public static PanelGrid Grid;

    public Display() throws Exception {
        // Setup Main JFrame and the BinaryGrid respectively
        SetupMainFrame();

        Grid = new PanelGrid(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        AppendComponentToFrame(Grid);

        // Redraw (Update ticker)
        SwingUtilities.invokeLater(() -> new Timer(Display.UPDATE_TICK, 
            this::UpdateTick).start());
            
        // Push Frame to screen
        MAIN_FRAME.setVisible(true);
    }

    /**
     * Setup the main JFrame (window) of the sim
     * @exception JFrameException - thrown if method called when JFrame has already been created
     */
    public void SetupMainFrame() throws Exception {
        if (MAIN_FRAME != null)
            throw new Exception("Main frame already created!");

        MAIN_FRAME = new JFrame("Fluid Simulator");
        MAIN_FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension size = new Dimension(VIEWPORT_WIDTH * VIEWPORT_SCALE + 12, VIEWPORT_HEIGHT * VIEWPORT_SCALE + 38);
        MAIN_FRAME.setSize(size);
        MAIN_FRAME.setMinimumSize(size);
        MAIN_FRAME.setMaximumSize(size);
    }

    /**
     * Update logic (i.e. for fluid calculations)
     * @param e - action event given by the timer
     */
    private void UpdateTick(ActionEvent e) {
        // Main Sim Logic
        Main.MainSimulation.UpdateSimulationTick();

        // Make Cameramen
        if (Math.random() < 0.04) Simulation.AddObjectToQueue(new Cameraman());

        // Repaint grid (VERY NECESSARY)
        Grid.repaint();
    } 

    /**
     * Adds component to the main JFrame
     * @param comp - the component to be added
     */
    public static void AppendComponentToFrame(Component comp) {
        MAIN_FRAME.add(comp);
    }
}
