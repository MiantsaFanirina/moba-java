package principal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import domaine.unite.Heros;

/**
 * Menu principal avant le début de la partie.
 */
public class MenuPrincipal extends JPanel {
    
    private PanneauJeu panneauJeu;
    private CardLayout cardLayout;
    private JPanel containerPanel;
    
    // Composants du menu
    private JButton boutonDemarrer;
    private JButton boutonOptions;
    private JButton boutonQuitter;
    private JComboBox<String> comboHeroType;
    private JSlider sliderVitesse;
    private JLabel labelVitesse;
    
    // Types de héros disponibles
    private static final String[] TYPES_HEROS = {
        "Tank - Vitesse: 0.8",
        "Mage - Vitesse: 1.0", 
        "Assassin - Vitesse: 1.4",
        "Tireur - Vitesse: 1.2",
        "Support - Vitesse: 1.1"
    };
    
    public MenuPrincipal(PanneauJeu panneauJeu, CardLayout cardLayout, JPanel containerPanel) {
        this.panneauJeu = panneauJeu;
        this.cardLayout = cardLayout;
        this.containerPanel = containerPanel;
        
        initialiserMenu();
        configurerEvenements();
    }
    
    public void setPanneauJeu(PanneauJeu panneauJeu) {
        this.panneauJeu = panneauJeu;
    }
    
    private void initialiserMenu() {
        setLayout(new BorderLayout());
        setBackground(new Color(20, 25, 40));
        
        // Panneau central
        JPanel panneauCentre = new JPanel(new GridBagLayout());
        panneauCentre.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Titre
        JLabel titre = creerLabelTitre("*** MOBA JAVA ***");
        panneauCentre.add(titre, gbc);
        
        // Panneau de sélection de héros
        JPanel panneauHero = creerPanneauSelectionHero();
        gbc.insets = new Insets(20, 10, 10, 10);
        panneauCentre.add(panneauHero, gbc);
        
        // Panneau de boutons
        JPanel panneauBoutons = creerPanneauBoutons();
        gbc.insets = new Insets(30, 10, 10, 10);
        panneauCentre.add(panneauBoutons, gbc);
        
        // Instructions
        JLabel instructions = creerLabelInstructions();
        gbc.insets = new Insets(20, 10, 10, 10);
        panneauCentre.add(instructions, gbc);
        
        add(panneauCentre, BorderLayout.CENTER);
    }
    
    private JLabel creerLabelTitre(String texte) {
        JLabel label = new JLabel(texte, SwingConstants.CENTER);
        label.setFont(new Font("Arial Black", Font.BOLD, 48));
        label.setForeground(new Color(255, 215, 0));
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        return label;
    }
    
    private JPanel creerPanneauSelectionHero() {
        JPanel panneau = new JPanel(new GridBagLayout());
        panneau.setOpaque(false);
        panneau.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 150, 200), 2),
            "Sélection du Héros",
            SwingConstants.CENTER,
            SwingConstants.TOP,
            new Font("Arial", Font.BOLD, 16),
            new Color(100, 150, 200)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel labelHero = new JLabel("Type de Héros:");
        labelHero.setFont(new Font("Arial", Font.BOLD, 14));
        labelHero.setForeground(Color.WHITE);
        panneau.add(labelHero, gbc);
        
        comboHeroType = new JComboBox<>(TYPES_HEROS);
        comboHeroType.setFont(new Font("Arial", Font.PLAIN, 14));
        comboHeroType.setBackground(new Color(50, 50, 70));
        comboHeroType.setForeground(Color.WHITE);
        comboHeroType.setMaximumRowCount(5);
        panneau.add(comboHeroType, gbc);
        
        // Slider de vitesse
        gbc.insets = new Insets(20, 10, 5, 10);
        labelVitesse = new JLabel("Vitesse de déplacement: 1.0x");
        labelVitesse.setFont(new Font("Arial", Font.BOLD, 14));
        labelVitesse.setForeground(Color.WHITE);
        panneau.add(labelVitesse, gbc);
        
        gbc.insets = new Insets(5, 10, 10, 10);
        sliderVitesse = new JSlider(50, 200, 100);
        sliderVitesse.setBackground(new Color(50, 50, 70));
        sliderVitesse.setForeground(new Color(100, 150, 200));
        sliderVitesse.setMajorTickSpacing(50);
        sliderVitesse.setMinorTickSpacing(10);
        sliderVitesse.setPaintTicks(true);
        sliderVitesse.setPaintLabels(true);
        panneau.add(sliderVitesse, gbc);
        
        return panneau;
    }
    
    private JPanel creerPanneauBoutons() {
        JPanel panneau = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panneau.setOpaque(false);
        
        boutonDemarrer = creerBouton(">> Démarrer la Partie", new Color(0, 150, 0));
        boutonOptions = creerBouton("-- Options --", new Color(100, 100, 150));
        boutonQuitter = creerBouton("XX Quitter XX", new Color(200, 50, 50));
        
        panneau.add(boutonDemarrer);
        panneau.add(boutonOptions);
        panneau.add(boutonQuitter);
        
        return panneau;
    }
    
    private JButton creerBouton(String texte, Color couleurFond) {
        JButton bouton = new JButton(texte);
        bouton.setFont(new Font("Arial", Font.BOLD, 16));
        bouton.setBackground(couleurFond);
        bouton.setForeground(Color.WHITE);
        bouton.setOpaque(true);
        bouton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(couleurFond.darker(), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        bouton.setFocusPainted(false);
        bouton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effet de survol
        bouton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                bouton.setBackground(couleurFond.brighter());
            }
            
            public void mouseExited(MouseEvent e) {
                bouton.setBackground(couleurFond);
            }
        });
        
        return bouton;
    }
    
    private JLabel creerLabelInstructions() {
        String texte = "<html><center>" +
            "<font color='white' font='Arial' size='3'>" +
            "<b>Instructions:</b><br><br>" +
            "-> <b>Clic droit:</b> Déplacer le héros<br>" +
            "[] <b>Q/W/E:</b> Lancer les sorts<br>" +
            "[] <b>R:</b> Rappel à la base<br>" +
            "[] <b>B:</b> Acheter équipement<br>" +
            "[] <b>Espace:</b> Pause/Reprendre<br><br>" +
            "<b>Utilisez la minimap pour naviguer rapidement!</b>" +
            "</font></center></html>";
            
        JLabel label = new JLabel(texte, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(Color.LIGHT_GRAY);
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return label;
    }
    
    private void configurerEvenements() {
        boutonDemarrer.addActionListener(e -> demarrerPartie());
        boutonOptions.addActionListener(e -> afficherOptions());
        boutonQuitter.addActionListener(e -> quitterJeu());
        
        sliderVitesse.addChangeListener(e -> {
            int valeur = sliderVitesse.getValue();
            double multiplicateur = valeur / 100.0;
            labelVitesse.setText(String.format("Vitesse de déplacement: %.1fx", multiplicateur));
        });
    }
    
    private void demarrerPartie() {
        // Récupérer le type de héros sélectionné
        int indexHero = comboHeroType.getSelectedIndex();
        Heros.TypeHeros typeHero = getTypeHeroFromIndex(indexHero);
        
        // Appliquer le multiplicateur de vitesse
        double multiplicateurVitesse = sliderVitesse.getValue() / 100.0;
        
        // Configurer le héros du panneau de jeu
        panneauJeu.configurerHero(typeHero, multiplicateurVitesse);
        
        // Passer à l'écran de jeu
        cardLayout.show(containerPanel, "JEU");
        panneauJeu.requestFocus();
        
        System.out.println("🎮 Partie démarrée avec héros: " + typeHero.getNom());
    }
    
    private Heros.TypeHeros getTypeHeroFromIndex(int index) {
        switch (index) {
            case 0: return Heros.TypeHeros.TANK;
            case 1: return Heros.TypeHeros.MAGE;
            case 2: return Heros.TypeHeros.ASSASSIN;
            case 3: return Heros.TypeHeros.TIREUR;
            case 4: return Heros.TypeHeros.SUPPORT;
            default: return Heros.TypeHeros.ASSASSIN;
        }
    }
    
    private void afficherOptions() {
        JOptionPane.showMessageDialog(this,
            "Options de jeu:\n\n" +
            "• Graphismes: Élevés\n" +
            "• Son: Activé\n" +
            "• Difficulté: Normale\n\n" +
            "Plus d'options bientôt disponibles!",
            "Options",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void quitterJeu() {
        int confirmation = JOptionPane.showConfirmDialog(
            this,
            "Voulez-vous vraiment quitter le jeu?",
            "Quitter",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirmation == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    public void reinitialiserMenu() {
        comboHeroType.setSelectedIndex(0);
        sliderVitesse.setValue(100);
        labelVitesse.setText("Vitesse de déplacement: 1.0x");
    }
}