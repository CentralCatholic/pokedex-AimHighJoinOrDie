import java.io.File;
import javax.imageio.ImageIO;

public class Main {
    public static void main(String[] args) throws Exception {
        PokeEntry gloom = new PokeEntry("gloom");
        System.out.printf("Name: %s\n", gloom.getName());
        System.out.printf("Height: %s\n", gloom.getHeight());
        System.out.printf("Weight: %s\n", gloom.getWeight());
        System.out.printf("ID: %d\n", gloom.getID());
        for(String type : gloom.getTypes()) {
            System.out.printf("%s type\n", type);
        }
        for(String text : gloom.getFlavorTexts()) {
            System.out.printf("%s\n", text);
        }
        for(PokeEntry prior : gloom.priorEvolutions()) {
            System.out.printf("Evolves from %s\n", prior.getName());
        }
        for(PokeEntry future : gloom.evolutions()) {
            System.out.printf("Evolves into %s\n", future.getName());
        }
        File gloomFile = new File("gloom.png");
        ImageIO.write(gloom.getImage(), "png", gloomFile);
    }
}
