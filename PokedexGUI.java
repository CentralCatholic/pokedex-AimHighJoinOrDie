/**
 * @author Sean Hazlett
 * @version 2.1
 * 
 * Last update: 5.8.18
 * 
 * Graphical Interface that makes use of PokeEntry
 * 	to create a virtual Pokedex
 * 
 * To Run (Windows): 
 * 	java -classpath '.;javax.json.jar' Main
 */

import java.util.List;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.print.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.net.*;
import java.io.*;
import java.util.concurrent.TimeUnit; 

public class PokedexGUI extends JFrame {
    // instance variables
    private String nameOrID;
    private JPanel panel; 
	
    //buttons and lables that I change the color of
    //	throughout the program
    private JLabel welcome; 
    private JButton continueButton;
    private JLabel home;
    private JLabel name; 
    private JButton goToLast;
    private JButton goToNext;
    private JButton returnHome; 
    private PokeEntry entry;

    // position of current favor text
    private int currentFlavorText;

    // thread for color changing
    private Thread colorChangerThread;

    // custom fonts and colors 
    protected static Color customRed = new Color (209, 62, 46);
    protected static Font largeFont = new Font("Serif", Font.BOLD, 60);
    protected static Font mediumFont = new Font("Serif", Font.BOLD, 40);
    protected static Font smallFont = new Font("Serif", Font.BOLD, 25);
    protected static Font microFont = new Font("Serif", Font.BOLD, 15);

    // paths to commonly used images
    protected static URL logoPath = 
        PokedexGUI.class.getResource("pokedexLogo.jpg");
    protected static URL homePath = 
        PokedexGUI.class.getResource("home icon.png");

    // progress indicators while loading
    private boolean loadingSuccessful;
    private boolean loadingComplete; 

    public PokedexGUI () {
        try {
            this.setTitle("Pokedex"); 
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        } catch (Exception e) {
            // if that doesn't work, blow it up!
            e.printStackTrace();
            System.exit(1);
        }
    }

    //intro screen 
    public void welcomeScreen() {
        panel = new JPanel (); 
        panel.setBackground(customRed); 
        panel.setLayout(new BorderLayout());
        // get file path relative to current .java file's directory
        URL path = PokedexGUI.class.getResource("pokedexFront.jpg");
        JLabel background = new JLabel(new ImageIcon(path));
        panel.add(background, BorderLayout.CENTER); 

        welcome = new JLabel (" Welcome ! "); 
        welcome.setForeground(Color.WHITE); 
        welcome.setFont(largeFont);
        welcome.setHorizontalAlignment(JLabel.CENTER); 
        continueButton = new JButton("Click To Continue"); 
        continueButton = styleButton(continueButton); 

        continueButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // interrupt thread when I want to change the 
                    //	screen 
                    colorChangerThread.interrupt(); 
                    homeScreen(); 
                }
            });

        panel.add(welcome, BorderLayout.NORTH); 
        panel.add(continueButton, BorderLayout.SOUTH); 
        this.add(panel); 
        this.setVisible(true); 

        colorChangerThread = new Thread(new Runnable() {
                public void run () {
                    if (panel == null) return; 
                    cycleColorsWelcome ();
                }
            });
        colorChangerThread.start();
    }

    // cycle colors on welcome screen 
    private void cycleColorsWelcome () {
        Random rnd = new Random (); 
        while (panel != null) {
            try {
                // wait a second
                TimeUnit.SECONDS.sleep(1);
                // generate new color 
                Color randomColor = new Color (rnd.nextInt(256), 
                        rnd.nextInt(256), rnd.nextInt(256));
                welcome.setForeground(randomColor); 
                continueButton.setForeground(randomColor); 
            } catch (Exception e) {
                return; 
            }
        }
    }

    // home screen 
    public void homeScreen () {
        // remove last panel
        this.remove(panel); 

        // and build a new one
        panel = new JPanel(); 
        panel.setLayout(new GridLayout(3,1)); 
        panel.setBackground(customRed); 

        JPanel searchPanel = new JPanel(); 
        searchPanel.setBackground(customRed);
        searchPanel.setLayout(new GridLayout(3,1)); 
        JLabel prompt = new JLabel ("Search by Name or ID in "+
                "the Box Below:"); 
        prompt.setHorizontalAlignment(JLabel.CENTER);
        prompt.setForeground(Color.WHITE);
        prompt.setFont(smallFont); 
        JTextField getNameOrID = new JTextField ();
        // listen for enter key
        getNameOrID.addActionListener(new ActionListener() {
                public void actionPerformed (ActionEvent e) {
                    nameOrID = getNameOrID.getText(); 
                    colorChangerThread.interrupt(); 
                    boolean complete = searchScreen(); 
                    if (!complete) homeScreen(); 
                }
            });
        getNameOrID.setFont(smallFont); 
        JButton search = new JButton ("Search"); 
        search = styleButton(search);
        search.addActionListener(new ActionListener() {
                public void actionPerformed (ActionEvent e) {
                    nameOrID = getNameOrID.getText(); 
                    colorChangerThread.interrupt(); 
                    boolean complete = searchScreen(); 
                    if (!complete) homeScreen(); 
                }
            });
        searchPanel.add(prompt);
        searchPanel.add(getNameOrID); 
        searchPanel.add(search); 

        JPanel homePanel = new JPanel(); 
        homePanel.setLayout(new GridLayout (1,3)); 
        homePanel.setBackground(customRed); 
        JLabel homeLogo = new JLabel(new ImageIcon(homePath));
        // unfortunately, you can't just add one JLabel twice
        JLabel homeDup = new JLabel(new ImageIcon(homePath));
        home = new JLabel ("Home"); 
        home.setFont(largeFont);
        home.setHorizontalAlignment(JLabel.CENTER);
        home.setForeground(Color.WHITE);
        homePanel.add(homeLogo); 
        homePanel.add(home); 
        homePanel.add(homeDup);

        panel.add(homePanel);

        JLabel logo = new JLabel(new ImageIcon(logoPath));
        panel.add(logo); 

        panel.add(searchPanel); 
        this.add(panel);
        this.setVisible(true); 

        colorChangerThread = new Thread(new Runnable() {
                public void run () {
                    if (panel == null) return; 
                    cycleColorsHome ();
                }
            });
        colorChangerThread.start();
    }

    private void cycleColorsHome () {
        Random rnd = new Random (); 
        while (panel != null) {
            try {
                TimeUnit.SECONDS.sleep(1);
                Color randomColor = new Color (rnd.nextInt(256), 
                        rnd.nextInt(256), rnd.nextInt(256));
                home.setForeground(randomColor); 
            } catch (Exception e) {
                return; 
            }
        }
    }

    // a stylizer for buttons 
    private JButton styleButton (JButton b) {
        b.setBackground(Color.BLACK); 
        b.setForeground(Color.WHITE); 
        b.setFont(mediumFont);
        return b; 
    }

    // search for entry and display it 
    // returned boolean gives completion status
    public boolean searchScreen () {
        this.remove(panel); 

        PokeEntry entry = buildEntry();
        if (entry == null) {
            return false; 
        }

        panel = new JPanel(); 
        panel.setLayout(new GridLayout(5,1)); 
        panel.setBackground(customRed); 

        JPanel entryHeader = new JPanel (); 
        entryHeader.setBackground(customRed); 
        entryHeader.setLayout(new GridLayout (2,1)); 
        name = new JLabel (entry.getName().toUpperCase()); 
        if (entry.getName().length() <= 10)
            name.setFont(largeFont); 
        else if (entry.getName().length() <= 15)
            name.setFont(mediumFont);
        else name.setFont(smallFont);
        name.setHorizontalAlignment(JLabel.CENTER);
        name.setForeground(Color.WHITE); 
        entryHeader.add(name);
        JLabel image = new JLabel(new ImageIcon(entry.getImage()));
        entryHeader.add(image); 
        panel.add(entryHeader);

        JPanel infoAndTypes = new JPanel (); 
        infoAndTypes.setLayout(new GridLayout(1,2)); 
        infoAndTypes.setBackground(customRed); 

        JPanel infoPanel = new JPanel (); 
        infoPanel.setLayout(new GridLayout(3,1)); 
        infoPanel.setBackground(customRed); 
        JLabel height = new JLabel ("Height: " + entry.getHeight());
        height.setForeground(Color.WHITE);
        height.setFont(smallFont); 
        JLabel weight = new JLabel ("Weight: " + entry.getWeight()); 
        weight.setForeground(Color.WHITE);
        weight.setFont(smallFont); 
        JLabel ID = new JLabel ("ID: " + entry.getID()); 
        ID.setForeground(Color.WHITE);
        ID.setFont(smallFont); 
        infoPanel.add(height);
        infoPanel.add(weight);
        infoPanel.add(ID);
        infoAndTypes.add(infoPanel); 

        JPanel typesPanel = new JPanel (); 
        List <String> types = entry.getTypes(); 
        typesPanel.setLayout(new GridLayout(types.size()+1, 1)); 
        JLabel typesHeader = null;
        // get that pluralization right! 
        if (types.size() > 1) 
            typesHeader = new JLabel ("Types:"); 
        else if (types.size() == 1)
            typesHeader = new JLabel ("Type:");  
        else typesHeader = new JLabel ("No Known Types");  
        typesHeader.setForeground(Color.WHITE);
        typesHeader.setFont(smallFont); 
        typesPanel.add(typesHeader);
        for (String s : types) {
            s = s.substring(0, 1).toUpperCase() + s.substring(1);
            JLabel temp = new JLabel ("- " + s); 
            temp.setForeground(Color.WHITE);
            temp.setFont(smallFont); 
            typesPanel.add(temp);
        }
        typesPanel.setBackground(customRed); 

        infoAndTypes.add(typesPanel); 
        panel.add(infoAndTypes); 

        JPanel flavorTextsPanel = new JPanel (); 
        flavorTextsPanel.setLayout(new BorderLayout ()); 
        flavorTextsPanel.setBackground(customRed); 
        // label - display text in first position
        currentFlavorText = 0; 
        List <String> flavorTexts = 
            entry.getFlavorTexts();
        JLabel displayFlavorText = 
            new JLabel ("<html><p>" + flavorTexts.get(0) +
                "</p></html>"); 
        displayFlavorText.setFont(microFont); 
        displayFlavorText.setForeground(Color.WHITE); 
        // button - goto last
        goToLast = new JButton ("<"); 
        goToLast = styleButton(goToLast); 
        goToLast.addActionListener(new ActionListener() {
                public void actionPerformed (ActionEvent e) {
                    if (currentFlavorText != 0) 
                        displayFlavorText.setText("<html><p>" +
                            flavorTexts.get(--currentFlavorText) +
                            "</p></html>");
                }
            });
        // button - goto next
        goToNext = new JButton (">"); 
        goToNext = styleButton(goToNext); 
        goToNext.addActionListener(new ActionListener() {
                public void actionPerformed (ActionEvent e) {
                    if (currentFlavorText != flavorTexts.size()-1) 
                        displayFlavorText.setText("<html><p>" +
                            flavorTexts.get(++currentFlavorText) +
                            "</p></html>");
                }
            });

        flavorTextsPanel.add(goToLast, BorderLayout.WEST); 
        flavorTextsPanel.add(displayFlavorText, BorderLayout.CENTER); 
        flavorTextsPanel.add(goToNext, BorderLayout.EAST); 
        panel.add(flavorTextsPanel); 

        JMenuBar evolutionsBar = new JMenuBar (); 
        evolutionsBar.setLayout(new GridLayout (1,2)); 

        JMenu priorEvolutions = new JMenu ("Prior Evolutions"); 
        priorEvolutions.setForeground(customRed);
        priorEvolutions.setFont(smallFont);
        List <PokeEntry> prior = entry.priorEvolutions(); 
        if (prior != null && prior.size() != 0) {
            for (PokeEntry p : prior) {
                JMenuItem temp = new JMenuItem (p.getName()); 
                temp.setForeground(customRed);
                temp.setFont(smallFont);
                temp.addActionListener(new ActionListener() {
                        public void actionPerformed (ActionEvent e) {
                            colorChangerThread.interrupt(); 
                            nameOrID = p.getName(); 
                            boolean complete = searchScreen(); 
                        }
                    });
                priorEvolutions.add(temp);
            }
        } else {
            JMenuItem nullPast = 
            	new JMenuItem ("No Prior Evolutions"); 
            nullPast.setForeground(customRed);
            nullPast.setFont(smallFont);
            priorEvolutions.add(nullPast);
        }

        JMenu futureEvolutions = new JMenu ("Future Evolutions"); 
        futureEvolutions.setForeground(customRed);
        futureEvolutions.setFont(smallFont);
        List <PokeEntry> future = entry.evolutions(); 
        if (future != null && future.size() != 0) {
            for (PokeEntry p : future) {
                JMenuItem temp = new JMenuItem (p.getName()); 
                temp.setForeground(customRed);
                temp.setFont(smallFont);
                temp.addActionListener(new ActionListener() {
                        public void actionPerformed (ActionEvent e) {
                            colorChangerThread.interrupt(); 
                            nameOrID = p.getName(); 
                            boolean complete = searchScreen(); 
                        }
                    });
                futureEvolutions.add(temp);
            }
        } else {
            JMenuItem nullFuture = 
            	new JMenuItem ("No Future Evolutions"); 
            nullFuture.setForeground(customRed);
            nullFuture.setFont(smallFont);
            futureEvolutions.add(nullFuture);
        }

        evolutionsBar.add(priorEvolutions); 
        evolutionsBar.add(futureEvolutions);
        panel.add(evolutionsBar); 

        returnHome = new JButton ("Return Home");
        returnHome = styleButton(returnHome); 
        returnHome.addActionListener(new ActionListener() {
                public void actionPerformed (ActionEvent e) {
                    colorChangerThread.interrupt(); 
                    homeScreen(); 
                }
            });
            
       	// must be last added (for my formatting)
        panel.add(returnHome); 

        this.add(panel);
        this.setVisible(true); 

        colorChangerThread = new Thread(new Runnable() {
                public void run () {
                    if (panel == null) return; 
                    cycleColorsSearch ();
                }
            });
        colorChangerThread.start();

        return true; 
    }

    private PokeEntry buildEntry () {
        entry = null; 
        if (nameOrID == null) return null; 
	
        /*
         * Overall Strategy:
         * 1. try to convert to int
         * 2. if its not an int -> its a string
         * 3. else throw error
         */
        
        try {
            int ID = Integer.parseInt(nameOrID);
            if (ID > 802 || ID < 1) throw new Exception ();
            loadingScreen(ID); 
            if (!loadingSuccessful) throw new Exception(); 		
            JOptionPane.showMessageDialog(null, "Entry created successfully!");
        } catch (NumberFormatException nfe) {
            String name = nameOrID.toLowerCase().trim(); 
            try {
                // allows for offline testing
                // entry = new PokeEntry (true);
                loadingScreen(name); 
                if (!loadingSuccessful) throw new Exception(); 	
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error Creating Entry!");
                return null; 
            }
            JOptionPane.showMessageDialog(null, "Entry created successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error Creating Entry!");
            return null; 
        }

        return entry; 
    }

    private void cycleColorsSearch () {
        Random rnd = new Random (); 
        while (panel != null) {
            try {
                TimeUnit.SECONDS.sleep(1);
                Color randomColor = new Color (rnd.nextInt(256), 
                        rnd.nextInt(256), rnd.nextInt(256));
                name.setForeground(randomColor); 
                goToLast.setForeground(randomColor);
                goToNext.setForeground(randomColor);
                returnHome.setForeground(randomColor);
            } catch (Exception e) {
                return; 
            }
        }
    }

    // The following methods do the same thing, but just have 
    //	different parameters.  
    // Is this the best way to implement this? Probably not.
    // However, producing a frame or JOptionPane in a thread 
    //	(opposite of shown) and fetching info from the api 
    //	throws some interesting errors. It does not allow the 
    // 	frames to be updated or display any information.
    // After testing with multiple people, I found the loading 
    //	screen to be necessary - most people believed that the 
    //	program had died and closed it instead of waiting.  
    
    private void loadingScreen (int ID) {
        loadingComplete = false; 
        new Thread(new Runnable() {
                public void run () {
                    try {
                        entry = new PokeEntry (ID);
                        loadingSuccessful = true; 
                        loadingComplete = true; 
                    } catch (Exception e){
                        loadingSuccessful = false; 
                        loadingComplete = true; 
                    } finally {
                        JOptionPane.getRootFrame().dispose();
                    }
                }
            }).start();
            
        while (!loadingComplete) {
            URL loadingPath = 
                PokedexGUI.class.getResource("nowLoading.gif");
            ImageIcon loadingAn = new ImageIcon(loadingPath);
            JOptionPane.showMessageDialog(null, "<html>Please Wait 5 -" +
                " 10 Seconds:<br>Loading Entry " +
                "Information</html>", "Loading", 
                JOptionPane.INFORMATION_MESSAGE,loadingAn);
        }
    }

    private void loadingScreen (String name) {
        loadingComplete = false; 
        new Thread(new Runnable() {
                public void run () {
                    try {
                        entry = new PokeEntry (name);
                        loadingSuccessful = true; 
                        loadingComplete = true; 
                    } catch (Exception e){
                        loadingSuccessful = false; 
                        loadingComplete = true; 
                    } finally {
                        JOptionPane.getRootFrame().dispose();
                    }
                }
            }).start();

        while (!loadingComplete) {
            URL loadingPath = 
                PokedexGUI.class.getResource("nowLoading.gif");
            ImageIcon loadingAn = new ImageIcon(loadingPath);
            JOptionPane.showMessageDialog(null, "<html>Please Wait 5 -" +
                " 10 Seconds:<br>Loading Entry " +
                "Information</html>", "Loading", 
                JOptionPane.INFORMATION_MESSAGE,loadingAn);
        }    
    }

}