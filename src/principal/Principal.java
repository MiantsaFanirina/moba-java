package principal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Point d'entrée principal du jeu MOBA.
 * Lance la fenêtre et initialise le panneau de jeu.
 */
public class Principal {
    public static void main(String[] args) {
        int largeurEcran = 1920;
        int hauteurEcran = 1080;
        
        try {
            Dimension ecran = Toolkit.getDefaultToolkit().getScreenSize();
            largeurEcran = (int) ecran.getWidth();
            hauteurEcran = (int) ecran.getHeight();
        } catch (Exception ignored) {}
        
        JFrame fenetre = new JFrame();
        fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fenetre.setResizable(true);
        fenetre.setTitle("MOBA Bataille - Français");
        fenetre.setPreferredSize(new Dimension(largeurEcran, hauteurEcran));
        
        PanneauJeu panneauJeu = new PanneauJeu(largeurEcran, hauteurEcran);
        fenetre.add(panneauJeu);
        
        // Ajouter un menu pour contrôler le jeu
        JMenuBar barreMenu = creerMenu();
        fenetre.setJMenuBar(barreMenu);
        
        fenetre.pack();
        fenetre.setExtendedState(JFrame.MAXIMIZED_BOTH);
        fenetre.setLocationRelativeTo(null);
        fenetre.setVisible(true);
    }
    
    // Créer le menu principal
    private static JMenuBar creerMenu() {
        JMenuBar barreMenu = new JMenuBar();
        
        // Menu Partie
        JMenu menuPartie = new JMenu("Partie");
        JMenuItem demarrer = new JMenuItem("Démarrer");
        JMenuItem pause = new JMenuItem("Pause");
        JMenuItem arreter = new JMenuItem("Arrêter");
        JMenuItem nouvelle = new JMenuItem("Nouvelle Partie");
        
        menuPartie.add(demarrer);
        menuPartie.add(pause);
        menuPartie.add(arreter);
        menuPartie.addSeparator();
        menuPartie.add(nouvelle);
        
        // Menu Options
        JMenu menuOptions = new JMenu("Options");
        JMenuItem pleinEcran = new JMenuItem("Plein Écran");
        JMenuItem fenetree = new JMenuItem("Fenêtré");
        JMenuItem quitter = new JMenuItem("Quitter");
        
        menuOptions.add(pleinEcran);
        menuOptions.add(fenetree);
        menuOptions.addSeparator();
        menuOptions.add(quitter);
        
        // Menu Aide
        JMenu menuAide = new JMenu("Aide");
        JMenuItem controles = new JMenuItem("Contrôles");
        JMenuItem aPropos = new JMenuItem("À Propos");
        
        menuAide.add(controles);
        menuAide.add(aPropos);
        
        barreMenu.add(menuPartie);
        barreMenu.add(menuOptions);
        barreMenu.add(menuAide);
        
        // Ajouter les actions (placeholder pour l'instant)
        demarrer.addActionListener(e -> System.out.println("Démarrer partie"));
        pause.addActionListener(e -> System.out.println("Pause partie"));
        arreter.addActionListener(e -> System.out.println("Arrêter partie"));
        nouvelle.addActionListener(e -> System.out.println("Nouvelle partie"));
        
        pleinEcran.addActionListener(e -> System.out.println("Mode plein écran"));
        fenetree.addActionListener(e -> System.out.println("Mode fenêtré"));
        quitter.addActionListener(e -> System.exit(0));
        
        controles.addActionListener(e -> afficherControles());
        aPropos.addActionListener(e -> afficherAPropos());
        
        return barreMenu;
    }
    
    // Afficher les contrôles
    private static void afficherControles() {
        JOptionPane.showMessageDialog(null,
                "=== CONTRÔLES ===\n\n" +
                "Souris:\n" +
                "  • Clic Droit: Déplacer vers une position\n" +
                "  • Molette: Zoomer/Dézoomer\n\n" +
                "Clavier:\n" +
                "  • Q: Sort 1 (Dash)\n" +
                "  • W: Sort 2 (Boost)\n" +
                "  • E: Sort 3 (Soin)\n" +
                "  • B: Acheter équipement\n" +
                "  • Espace: Démarrer/Arrêter\n\n" +
                "  • Esc: Quitter\n",
                "Contrôles du Jeu",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Afficher les informations À Propos
    private static void afficherAPropos() {
        JOptionPane.showMessageDialog(null,
                "MOBA Légendes Mobile - Version 1.0\n\n" +
                "Jeu de bataille en ligne multijoueur\n" +
                "avec héros, scuris, et objectifs.\n\n" +
                "Developpe en Java avec Swing\n" +
                "Architecture Francaise\n" +
                "Tous les noms sont en francais\n\n" +
                "© 2024 - Équipe de Développement",
                "À Propos",
                JOptionPane.INFORMATION_MESSAGE);
    }
}