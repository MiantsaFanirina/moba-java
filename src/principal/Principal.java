package principal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Point d'entrée principal du jeu MOBA avec menu.
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
        fenetre.setTitle("*** MOBA BATAILLE - Menu Principal ***");
        fenetre.setPreferredSize(new Dimension(largeurEcran, hauteurEcran));
        
        // Utiliser CardLayout pour basculer entre menu et jeu
        CardLayout cardLayout = new CardLayout();
        JPanel containerPanel = new JPanel(cardLayout);
        
        // Créer le menu et le panneau de jeu
        MenuPrincipal menuPrincipal = new MenuPrincipal(null, cardLayout, containerPanel);
        PanneauJeu panneauJeu = new PanneauJeu(largeurEcran, hauteurEcran);
        
        // Configurer les références croisées
        menuPrincipal.setPanneauJeu(panneauJeu);
        
        // Ajouter les panneaux au conteneur
        containerPanel.add(menuPrincipal, "MENU");
        containerPanel.add(panneauJeu, "JEU");
        
        fenetre.add(containerPanel);
        
        // Ajouter la barre de menu
        fenetre.setJMenuBar(creerMenuBarre(panneauJeu, cardLayout, containerPanel));
        
        fenetre.pack();
        fenetre.setExtendedState(JFrame.MAXIMIZED_BOTH);
        fenetre.setLocationRelativeTo(null);
        fenetre.setVisible(true);
        
        // Afficher le menu par défaut
        cardLayout.show(containerPanel, "MENU");
    }
    
    /**
     * Créer la barre de menu principale
     */
    private static JMenuBar creerMenuBarre(PanneauJeu panneauJeu, CardLayout cardLayout, JPanel containerPanel) {
        JMenuBar barreMenu = new JMenuBar();
        
        // Menu Partie
        JMenu menuPartie = new JMenu("Partie");
        
        JMenuItem nouvellePartie = new JMenuItem("Nouvelle Partie");
        JMenuItem retourMenu = new JMenuItem("Retour au Menu");
        JMenuItem pleineEcran = new JMenuItem("Plein Écran");
        JMenuItem fenetree = new JMenuItem("Fenêtré");
        JMenuItem quitter = new JMenuItem("Quitter");
        
        menuPartie.add(nouvellePartie);
        menuPartie.addSeparator();
        menuPartie.add(retourMenu);
        menuPartie.addSeparator();
        menuPartie.add(pleineEcran);
        menuPartie.add(fenetree);
        menuPartie.addSeparator();
        menuPartie.add(quitter);
        
        // Menu Options
        JMenu menuOptions = new JMenu("Options");
        
        JMenuItem graphismes = new JMenuItem("Graphismes");
        JMenuItem son = new JMenuItem("Son");
        JMenuItem controles = new JMenuItem("Contrôles");
        
        menuOptions.add(graphismes);
        menuOptions.add(son);
        menuOptions.add(controles);
        
        // Menu Aide
        JMenu menuAide = new JMenu("Aide");
        
        JMenuItem aideJeu = new JMenuItem("Aide du Jeu");
        JMenuItem aPropos = new JMenuItem("À Propos");
        
        menuAide.add(aideJeu);
        menuAide.add(aPropos);
        
        // Ajouter les menus à la barre
        barreMenu.add(menuPartie);
        barreMenu.add(menuOptions);
        barreMenu.add(menuAide);
        
        // Configurer les actions
        nouvellePartie.addActionListener(e -> {
            cardLayout.show(containerPanel, "MENU");
        });
        
        retourMenu.addActionListener(e -> {
            cardLayout.show(containerPanel, "MENU");
        });
        
        pleineEcran.addActionListener(e -> {
            fenetre = (JFrame) SwingUtilities.getWindowAncestor(containerPanel);
            fenetre.setExtendedState(JFrame.MAXIMIZED_BOTH);
        });
        
        fenetree.addActionListener(e -> {
            fenetre = (JFrame) SwingUtilities.getWindowAncestor(containerPanel);
            fenetre.setExtendedState(JFrame.NORMAL);
        });
        
        quitter.addActionListener(e -> {
            int confirmation = JOptionPane.showConfirmDialog(
                null,
                "Voulez-vous vraiment quitter le jeu?",
                "Quitter",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (confirmation == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        
        graphismes.addActionListener(e -> {
            JOptionPane.showMessageDialog(null,
                "Options Graphismes:\n\n" +
                "• Qualité: Élevée\n" +
                "• Résolution: Adaptative\n" +
                "• Effets: Activés\n" +
                "• Anti-aliasing: Activé\n\n" +
                "Les graphismes sont actuellement réglés au maximum.",
                "Graphismes",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        son.addActionListener(e -> {
            JOptionPane.showMessageDialog(null,
                "Options Sonores:\n\n" +
                "• Musique: Activée\n" +
                "• Effets sonores: Activés\n" +
                "• Volume: 80%\n\n" +
                "Le son est entièrement activé.",
                "Son",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        controles.addActionListener(e -> afficherControles());
        aideJeu.addActionListener(e -> afficherAide());
        aPropos.addActionListener(e -> afficherAPropos());
        
        return barreMenu;
    }
    
    /**
     * Afficher la liste des contrôles
     */
    private static void afficherControles() {
        JOptionPane.showMessageDialog(null,
                ">> CONTRÔLES DU JEU <<\n\n" +
                "=== SOURIS ===\n" +
                "• Clic Droit: Déplacer le héros\n" +
                "• Molette: Zoomer/Dézoomer\n" +
                "• Clic Gauche minimap: Téléporter caméra\n\n" +
                "=== CLAVIER ===\n" +
                "• Q: Sort 1 (Dash/Attaque rapide)\n" +
                "• W: Sort 2 (Boost de vitesse)\n" +
                "• E: Sort 3 (Soin/Protection)\n" +
                "• R: Rappel à la base\n" +
                "• B: Acheter équipement\n" +
                "• Espace: Pause/Reprendre la partie\n" +
                "• ESC: Retour au menu\n\n" +
                "=== CONSEILS ===\n" +
                "• Utilisez la minimap pour naviguer rapidement\n" +
                "• Les héros ont des vitesses différentes selon leur type\n" +
                "• Évitez les obstacles dans la jungle\n" +
                "• Protégez vos alliés et attaquez les ennemis",
                "Contrôles",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Afficher l'aide du jeu
     */
    private static void afficherAide() {
        JOptionPane.showMessageDialog(null,
                ">> AIDE DU JEU <<\n\n" +
                "=== OBJECTIFS ===\n" +
                "• Détruisez la base ennemie pour gagner\n" +
                "• Protégez votre base et vos tours\n" +
                "• Éliminez les héros ennemis pour gagner de l'or\n" +
                "• Contrôlez la jungle pour obtenir des bonus\n" +
                "=== TYPES DE HÉROS ===\n" +
                "• TANK: Lent mais résistant, idéal pour défendre\n" +
                "• MAGE: Vitesse moyenne, sorts puissants\n" +
                "• ASSASSIN: Très rapide, dégâts élevés mais fragile\n" +
                "• TIREUR: Bon à distance, vitesse moyenne\n" +
                "• SUPPORT: Soigne les alliés, vitesse moyenne\n\n" +
                "=== STRATÉGIES ===\n" +
                "• Travaillez en équipe pour être plus efficace\n" +
                "• Contrôlez la jungle pour obtenir un avantage\n" +
                "• Adaptez votre style de jeu à votre type de héros\n" +
                "• Ne vous séparez jamais de votre équipe!",
                "Aide",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Afficher les informations à propos
     */
    private static void afficherAPropos() {
        JOptionPane.showMessageDialog(null,
                "*** MOBA BATAILLE - VERSION 2.0 ***\n\n" +
                "Jeu de bataille multijoueur en ligne\n" +
                "Développé entièrement en Java avec Swing\n" +
                "=== CARACTÉRISTIQUES ===\n" +
                "• Carte 3 voies avec jungle\n" +
                "• 5 types de héros uniques\n" +
                "• Système de collision avancé\n" +
                "• Pathfinding intelligent\n" +
                "• Formes complexes et détaillées\n" +
                "• Mouvement fluide et réaliste\n" +
                "• Minimap interactive\n" +
                "• Menu principal complet\n" +
                "=== CRÉDITS ===\n" +
                "Développement: Équipe MOBA Java\n" +
                "Graphismes: Moteur de formes 2D\n" +
                "Architecture: 100% Française\n" +
                "Version: 2.0 - Édition Avancée\n\n" +
                "© 2024 - Tous droits réservés\n" +
                "Made with Java",
                "À Propos",
                JOptionPane.INFORMATION_MESSAGE);
    }
}