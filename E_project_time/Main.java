package E_project_time;

import E_project_time.Simulation.Simulation;

public class Main {
    public static Simulation MainSimulation;
    public static int Points = 0;

    /*
     * Welcome to The Skibidi Toilet Fluid Simulation Defense
     * 
     * Spray toilet water onto the evil cameramen and speakermen
     * 100% Lore accurate (trust) 🔥🔥
     * 
     * Get points and win (jk theres no ending)
     * 
     * You can adjust the amount of particles and general fluid simulation rules in the Simulation Class 
     * 
     * I wrote all the code here and ported most of the fluid simulation code from sebastion lague 😍😍😍
     * Almost all the functions are documented i think so please read them all, i diddyed them myself 😈
     * Also i had no Copilot so i wrote all of the documentation and comments and stuff and that took so much time cuz i never do that lol
     * 
     * The overall structure with the Drawables was pretty cool i think as i just came up with that and it worked rlly well
     * 
     * Enjoy the skibidi toilet simulation 🚽🪠
     */

    public static void main(String[] args) {
        try {
            // Setup simulation and run Display
            MainSimulation = new Simulation();
            new Display();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
