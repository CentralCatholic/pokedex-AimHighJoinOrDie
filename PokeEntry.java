import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonArray;
import java.util.LinkedList;
import java.util.List;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.URL;

import java.io.File;
import java.util.ArrayList; 
import java.awt.*;

public class PokeEntry {

    private final static String baseURL = "http://pokeapi.co/api/v2/pokemon";
    private JsonObject pokemon;
    private String name;
    private int weight;
    private int height;
    private int id;
    private List<String> types;
    private BufferedImage image;
    private List<String> flavorTexts;
    private List<PokeEntry> evolvesFrom;
    private List<PokeEntry> evolvesTo;

    private static PokeEntry[] pokeCache = new PokeEntry[802];

    PokeEntry(String name) throws Exception {
        String finalURL = String.format("%s/%s", baseURL, name);
        String jsonBlob = getResp(finalURL);
        this.serealize(jsonBlob);
    }

    PokeEntry(int id) throws Exception {
        String finalURL = String.format("%s/%d", baseURL, id);
        String jsonBlob = getResp(finalURL);
        this.serealize(jsonBlob);
    }

    PokeEntry (boolean test) {
        name = "gloom";
        weight = 86;
        height = 8; 
        id = 44; 
        types = new ArrayList <String> (); 
        types.add("grass");
        types.add("poison"); 
        //URL path = PokedexGUI.class.getResource("gloom.png");
        File gloomImg = new File ("gloom.png"); 
        BufferedImage in = null;
        try {
            in = ImageIO.read(gloomImg);
        } catch (Exception e) {}
        BufferedImage newImage = new BufferedImage(
                in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = newImage.createGraphics();
        g.drawImage(in, 0, 0, null);
        g.dispose();
        image = in;
        
        flavorTexts = new ArrayList <String> (); 
        flavorTexts.add("From its mouth Gloom drips honey that smells absolutely horrible. Apparently, it loves the horrid stench. It sniffs the noxious fumes and then drools even more of its honey."); 
        flavorTexts.add("Gloom releases a foul fragrance from the pistil of its flower. When faced with danger, the stench worsens. If this Pokémon is feeling calm and secure, it does not release its usual stinky aroma.");
        flavorTexts.add("The honey it drools from its mouth smells so atrocious, it can curl noses more than a mile away."); 
        //evolvesFrom
        //evolvesTo
    }

    private void serealize(String jsonBlob) throws Exception {
        this.evolvesFrom = new LinkedList<>();
        this.evolvesTo = new LinkedList<>();
        this.flavorTexts = new LinkedList<>();
        this.types = new LinkedList<>();
        JsonReader reader = Json.createReader(new StringReader(jsonBlob));
        this.pokemon = reader.readObject();
        this.name = this.pokemon.getString("name");
        this.weight = this.pokemon.getInt("weight");
        this.height = this.pokemon.getInt("height");
        this.id = this.pokemon.getInt("id");
        pokeCache[this.id] = this;
        this.setTypes();
        this.loadImage();
        this.loadSpeciesData();
    }

    private void loadSpeciesData() throws Exception {
        String speciesURI = this
            .pokemon
            .getJsonObject("species")
            .getString("url");
        String speciesBlob = this.getResp(speciesURI);
        JsonReader reader = Json.createReader(new StringReader(speciesBlob));
        JsonObject speciesObj = reader.readObject();
        this.loadSpeciesSubfields(speciesObj);
    }

    private void loadSpeciesSubfields(JsonObject species) throws Exception {
        loadFlavorText(species);
        loadEvolutionData(species);
    }

    private void loadEvolutionData(JsonObject species) throws Exception {
        String evolutionURI = species
            .getJsonObject("evolution_chain")
            .getString("url");
        String evolutionChainBlob = getResp(evolutionURI);
        JsonReader reader = Json.createReader(new StringReader(evolutionChainBlob));
        JsonObject evoObj = reader.readObject();
        JsonObject evoChain = evoObj.getJsonObject("chain");

        processEvolution(evoChain);
    }

    private void processEvolution(JsonObject chainLink) throws Exception {
        int speciesID = this.stripIDFromChainLink(chainLink);
        if (pokeCache[speciesID] == null) {
            pokeCache[speciesID] = new PokeEntry(speciesID);
        }
        chainLink.getJsonArray("evolves_to").forEach(
            x-> {
                JsonObject chainObj = x.asJsonObject();
                try {
                    processEvolution(chainObj);
                    int nextID = this.stripIDFromChainLink(chainObj);
                    if (nextID == this.id) {
                        this.evolvesFrom.add(pokeCache[speciesID]);
                    } else if(speciesID == this.id) {
                        this.evolvesTo.add(pokeCache[nextID]);
                    }
                } catch(Exception e) {e.printStackTrace(); System.exit(1);}
            }
        );
    }

    private int stripIDFromChainLink(JsonObject chainLink) {
        String speciesURI = chainLink.getJsonObject("species").getString("url");
        String prefix = "http://pokeapi.co/api/v2/pokemon-species/";
        speciesURI = speciesURI.substring(prefix.length()+1, speciesURI.length()-1);
        int speciesID = Integer.parseInt(speciesURI);
        return speciesID;
    }

    private void loadFlavorText(JsonObject species) {
        JsonArray array = species.getJsonArray("flavor_text_entries");
        array.forEach(
            x-> {
                JsonObject flavorObj = x.asJsonObject();
                String text = flavorObj.getString("flavor_text");
                text = text.replace("\n", " ");
                text = text.replace("\f", " ");
                String lang = flavorObj
                    .getJsonObject("language")
                    .getString("name");
                if(this.flavorTexts.contains(text)) {
                    return;
                }
                if(lang.equals("en")) {
                    this.flavorTexts.add(text);
                }
            }
        );
        this.flavorTexts = flavorTexts;
    }

    private void setTypes() {
        JsonArray array = this.pokemon.getJsonArray("types");
        array.forEach(
            x -> this.types.add(
                    x.asJsonObject()
                    .getJsonObject("type")
                    .getString("name")
                )
        );
    }

    private void loadImage() {
        String imageRef = this
            .pokemon
            .getJsonObject("sprites")
            .getString("front_default");
        try {
            URL url = new URL(imageRef);
            this.image = ImageIO.read(url);
        } catch(Exception e) {}
    }

    public String getName() {
        return this.name;
    }

    public int getWeight() {
        return this.weight;
    }

    public int getHeight() {
        return this.height;
    }

    public int getID() {
        return this.id;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public List<String> getTypes() {
        return this.types;
    }

    public List<String> getFlavorTexts() {
        return this.flavorTexts;
    }

    public List<PokeEntry> priorEvolutions() {
        return this.evolvesFrom;
    }

    public List<PokeEntry> evolutions() {
        return this.evolvesTo;
    }

    // Garbage code. It's magic. Please disregard.
    private String getResp(String urlVal) throws Exception {
        URL url = new URL(urlVal);
        HttpURLConnection.setFollowRedirects(true);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int status = con.getResponseCode();
        if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM) {
            String location = con.getHeaderField("Location");
            URL newUrl = new URL(location);
            con = (HttpURLConnection) newUrl.openConnection();
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();
        return content.toString();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
