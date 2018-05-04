/**
 * @author Sean Hazlett
 * @version 1.2
 * 
 * Last update: 4.28.18
 */

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
    private String nameOrID;
    private JPanel panel; 

    private JLabel welcome; 
    private JButton continueButton;
    private JLabel home;
    private JLabel name; 
    private JButton goToLast;
    private JButton goToNext;
    private JButton returnHome; 
    
    private int currentFlavorText;

    private Thread colorChangerThread;

    protected static Color customRed = new Color (209, 62, 46);
    protected static Font largeFont = new Font("Serif", Font.BOLD, 60);
    protected static Font mediumFont = new Font("Serif", Font.BOLD, 40);
    protected static Font smallFont = new Font("Serif", Font.BOLD, 25);
    protected static Font microFont = new Font("Serif", Font.BOLD, 15);
    
    protected static URL logoPath = 
    	PokedexGUI.class.getResource("pokedexLogo.jpg");
    protected static URL homePath = 
    	PokedexGUI.class.getResource("home icon.png");

    public PokedexGUI () {
        try {
            this.setTitle("Pokedex"); 
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

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

    private void cycleColorsWelcome () {
        Random rnd = new Random (); 
        while (panel != null) {
            try {
                TimeUnit.SECONDS.sleep(1);
                Color randomColor = new Color (rnd.nextInt(256), 
                        rnd.nextInt(256), rnd.nextInt(256));
                welcome.setForeground(randomColor); 
                continueButton.setForeground(randomColor); 
            } catch (Exception e) {
                return; 
            }
        }
    }

    public void homeScreen () {
        // remove last panel
        //Thread.interrupt(); 
        this.remove(panel); 

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

    private JButton styleButton (JButton b) {
        b.setBackground(Color.BLACK); 
        b.setForeground(Color.WHITE); 
        b.setFont(mediumFont);
        return b; 
    }

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
        ArrayList <String> types = (ArrayList <String>)entry.getTypes(); 
        typesPanel.setLayout(new GridLayout(types.size()+1, 1)); 
        JLabel typesHeader = null;
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
        // label - diaply text
        currentFlavorText = 0; 
        ArrayList <String> flavorTexts = 
        	(ArrayList <String>)entry.getFlavorTexts();
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
        //evolutionsBar.setBackground(customRed); 
        //evolutionsBar.setOpaque(true); 

        JMenu priorEvolutions = new JMenu ("Prior Evolutions"); 
        priorEvolutions.setForeground(customRed);
        priorEvolutions.setFont(smallFont);
        ArrayList <PokeEntry> prior = (ArrayList <PokeEntry>)entry.priorEvolutions(); 
        if (prior != null) {
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
            JMenuItem nullPast = new JMenuItem ("No Prior Evolutions"); 
            nullPast.setForeground(customRed);
            nullPast.setFont(smallFont);
            priorEvolutions.add(nullPast);
        }
        //priorEvolutions.setBackground(customRed); 
        //priorEvolutions.setOpaque(true); 

        JMenu futureEvolutions = new JMenu ("Future Evolutions"); 
        futureEvolutions.setForeground(customRed);
        futureEvolutions.setFont(smallFont);
        ArrayList <PokeEntry> future = (ArrayList <PokeEntry>)entry.evolutions(); 
        if (future != null) {
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
                priorEvolutions.add(temp);
            }
        } else {
            JMenuItem nullFuture = new JMenuItem ("No Future Evolutions"); 
            nullFuture.setForeground(customRed);
            nullFuture.setFont(smallFont);
            futureEvolutions.add(nullFuture);
        }
        //futureEvolutions.setBackground(customRed);
        //futureEvolutions.setOpaque(true);

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
        panel.add(returnHome); // must be last added

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
        PokeEntry entry = null; 

        if (nameOrID == null) return null; 

        try {
            int ID = Integer.parseInt(nameOrID);
            if (ID > 802 || ID < 1) throw new Exception ();
            entry = new PokeEntry (ID);
            JOptionPane.showMessageDialog(null, "Entry created successfully!");
        } catch (NumberFormatException nfe) {
            String name = nameOrID.toLowerCase().trim(); 
            try {
                // entry = new PokeEntry (true);
                entry = new PokeEntry (name); // Real Code 
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
}

/* 
 * Prompt: 
 * Allow the user to search for a Pokemon by ID or by name, using only one text field.
 * Show an image of that Pokemon in the GUI.
 * Use a Layout other than the default FlowLayout i.e. consider using GridLayout or BorderLayout
 *      , and/or use advanced features of FlowLayout.
 * You must allow the user to see both the prior evolutions (if any) and the future 
 *      evolutions (if any). Additionally, you must allow a user to navigate to those
 *      evolutions with the mouse.
 * You must use more than one color. The default color is not sufficient. See Gaddis, 
 *      748 for more information.
 * You are expected to use either the anonymous type approach to implementing ActionListener
 *      or the Java 8 lambda approach. Only implementing ActionListerners as inner 
 *      classes will cost you a few points.
 * You will be graded not only on the above functionality, but also on the look 
 *      and feel of your program. Better looking programs will receive better grades. 
 *      Programs that are unpleasantly looking, make insufficient use of the API,
 *      or that fail to be ergonomic will lose points. I don't expect everyone
 *      to get an A on this assignment. If you don't care significant care to
 *      make your Pokedex look clean and colorful, display as much information
 *      as possible, and be easy and intuitive to use, you can expect a B or 
 *      lower on this assignment.
 * Your Pokedex is expected to make use of the different flavor texts; given 
 *      that there are usually more than one, at very least you should randomly
 *      select a flavor text to show to the user each time they visit that
 *      Pokemon's entry. Alternatively, you can allow them to cycle through
 *      the different flavor texts. My guess is that you'll find there are 
 *      more flavor texts for Pokemon released earlier, so don't be surprised
 *      if newer Pokemon only have a single flavor text.
 */