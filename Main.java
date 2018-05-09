/**
 * @author Sean Hazlett 
 * @version 1.3
 * 
 * Last Update: 4.28.18
 * 
 * Pokedex GUI Runner 
 */

import java.io.File;
import javax.imageio.ImageIO;

public class Main {
    public static void main (String [] args) {
        // initialize window
        PokedexGUI run = new PokedexGUI (); 
        run.setSize(500,700); 
        run.setLocationRelativeTo(null);
        run.setVisible(true); 
        // start program
        run.welcomeScreen(); 
    }
}