package main;

import javax.swing.*;
import java.awt.*;

/**
 * Point d'entrée principal du jeu MOBA.
 * Lance la fenêtre et initialise le panneau de jeu.
 */
public class Main {
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
        fenetre.setTitle("MOBA - Carte et Déplacement");
        fenetre.setPreferredSize(new Dimension(largeurEcran, hauteurEcran));

        GamePanel panneauJeu = new GamePanel(largeurEcran, hauteurEcran);
        fenetre.add(panneauJeu);
        fenetre.pack();
        fenetre.setExtendedState(JFrame.MAXIMIZED_BOTH);
        fenetre.setLocationRelativeTo(null);
        fenetre.setVisible(true);
    }
}
