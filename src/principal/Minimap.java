package principal;

import domaine.carte.Arene;
import moteur.Vecteur2;
import moteur.formes.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Système de minimap pour la nouvelle arène.
 */
public class Minimap {
    
    private Arene arene;
    private int largeurMinimap;
    private int hauteurMinimap;
    private double echelleX;
    private double echelleY;
    private BufferedImage imageMinimap;
    private boolean doitRecharger;
    
    // Couleurs pour différents éléments
    private static final Color COULEUR_VOIE = new Color(200, 200, 150, 180);
    private static final Color COULEUR_JUNGLE = new Color(50, 150, 50, 150);
    private static final Color COULEUR_BASE1 = new Color(100, 100, 255, 200);
    private static final Color COULEUR_BASE2 = new Color(255, 100, 100, 200);
    private static final Color COULEUR_TOUR1 = new Color(100, 100, 255, 255);
    private static final Color COULEUR_TOUR2 = new Color(255, 100, 100, 255);
    private static final Color COULEUR_OBSTACLE = new Color(80, 80, 80, 200);
    private static final Color COULEUR_HEROS1 = new Color(0, 100, 255);
    private static final Color COULEUR_HEROS2 = new Color(255, 50, 0);
    
    public Minimap(Arene arene, int largeurMinimap, int hauteurMinimap) {
        this.arene = arene;
        this.largeurMinimap = largeurMinimap;
        this.hauteurMinimap = hauteurMinimap;
        this.doitRecharger = true;
        
        // Calculer l'échelle
        this.echelleX = (double) largeurMinimap / arene.getLargeur();
        this.echelleY = (double) hauteurMinimap / arene.getHauteur();
        
        // Créer l'image de la minimap
        this.imageMinimap = new BufferedImage(largeurMinimap, hauteurMinimap, 
                                             BufferedImage.TYPE_INT_ARGB);
        
        // Générer la minimap initiale
        genererMinimap();
    }
    
    /**
     * Génère l'image de base de la minimap.
     */
    private void genererMinimap() {
        Graphics2D g2d = imageMinimap.createGraphics();
        
        // Activer l'anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                           RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fond de la minimap
        g2d.setColor(new Color(40, 40, 40, 200));
        g2d.fillRect(0, 0, largeurMinimap, hauteurMinimap);
        
        // Dessiner les voies
        dessinerVoies(g2d);
        
        // Dessiner la jungle
        dessinerJungle(g2d);
        
        // Dessiner les bases
        dessinerBases(g2d);
        
        // Dessiner les tours
        dessinerTours(g2d);
        
        // Dessiner les obstacles
        dessinerObstacles(g2d);
        
        // Bordure de la minimap
        g2d.setColor(Color.WHITE);
        g2d.drawRect(0, 0, largeurMinimap-1, hauteurMinimap-1);
        
        g2d.dispose();
    }
    
    /**
     * Dessine les voies sur la minimap.
     */
    private void dessinerVoies(Graphics2D g2d) {
        g2d.setColor(COULEUR_VOIE);
        
        // Voie supérieure
        int voieSupY = (int) ((0) * echelleY);
        int voieSupHauteur = (int) ((arene.getHauteur() / 3) * echelleY);
        g2d.fillRect(0, voieSupY, largeurMinimap, voieSupHauteur);
        
        // Voie centrale
        int voieMidY = (int) ((arene.getHauteur() / 3) * echelleY);
        int voieMidHauteur = (int) ((arene.getHauteur() / 3) * echelleY);
        g2d.fillRect(0, voieMidY, largeurMinimap, voieMidHauteur);
        
        // Voie inférieure
        int voieInfY = (int) ((2 * arene.getHauteur() / 3) * echelleY);
        int voieInfHauteur = (int) ((arene.getHauteur() / 3) * echelleY);
        g2d.fillRect(0, voieInfY, largeurMinimap, voieInfHauteur);
    }
    
    /**
     * Dessine les zones de jungle sur la minimap.
     */
    private void dessinerJungle(Graphics2D g2d) {
        g2d.setColor(COULEUR_JUNGLE);
        
        // Jungle entre voies
        int jungle1Y = (int) ((arene.getHauteur() / 3 - 50) * echelleY);
        int jungle1Hauteur = (int) (100 * echelleY);
        int jungleLargeur = (int) ((arene.getLargeur() - 400) * echelleX);
        int jungleX = (int) (200 * echelleX);
        g2d.fillRect(jungleX, jungle1Y, jungleLargeur, jungle1Hauteur);
        
        int jungle2Y = (int) ((2 * arene.getHauteur() / 3 - 50) * echelleY);
        g2d.fillRect(jungleX, jungle2Y, jungleLargeur, jungle1Hauteur);
        
        // Jungle latérales
        int jungleGaucheX = 0;
        int jungleGaucheLargeur = (int) (200 * echelleX);
        int jungleHauteur = (int) ((arene.getHauteur() / 3) * echelleY);
        int jungleGaucheY = (int) ((arene.getHauteur() / 3) * echelleY);
        g2d.fillRect(jungleGaucheX, jungleGaucheY, jungleGaucheLargeur, jungleHauteur);
        
        int jungleDroiteX = (int) ((arene.getLargeur() - 200) * echelleX);
        g2d.fillRect(jungleDroiteX, jungleGaucheY, jungleGaucheLargeur, jungleHauteur);
    }
    
    /**
     * Dessine les bases sur la minimap.
     */
    private void dessinerBases(Graphics2D g2d) {
        // Base Équipe 1
        g2d.setColor(COULEUR_BASE1);
        int base1X = (int) (50 * echelleX);
        int base1Y = (int) ((arene.getHauteur() / 2 - 100) * echelleY);
        int baseLargeur = (int) (200 * echelleX);
        int baseHauteur = (int) (200 * echelleY);
        g2d.fillRect(base1X, base1Y, baseLargeur, baseHauteur);
        
        // Base Équipe 2
        g2d.setColor(COULEUR_BASE2);
        int base2X = (int) ((arene.getLargeur() - 250) * echelleX);
        g2d.fillRect(base2X, base1Y, baseLargeur, baseHauteur);
        
        // Fontaines
        g2d.setColor(Color.WHITE);
        int font1X = (int) (200 * echelleX);
        int font1Y = (int) ((arene.getHauteur() / 2) * echelleY);
        g2d.fillOval(font1X - 3, font1Y - 3, 6, 6);
        
        int font2X = (int) ((arene.getLargeur() - 200) * echelleX);
        g2d.fillOval(font2X - 3, font1Y - 3, 6, 6);
    }
    
    /**
     * Dessine les tours sur la minimap.
     */
    private void dessinerTours(Graphics2D g2d) {
        // Tours Équipe 1
        g2d.setColor(COULEUR_TOUR1);
        dessinerToursEquipe(g2d, arene.getToursEquipe1());
        
        // Tours Équipe 2
        g2d.setColor(COULEUR_TOUR2);
        dessinerToursEquipe(g2d, arene.getToursEquipe2());
    }
    
    private void dessinerToursEquipe(Graphics2D g2d, List<domaine.structures.Tour> tours) {
        for (domaine.structures.Tour tour : tours) {
            int x = (int) (tour.getPosition().x * echelleX);
            int y = (int) (tour.getPosition().y * echelleY);
            
            if (!tour.estDetruite()) {
                g2d.fillRect(x - 2, y - 2, 4, 4);
            } else {
                // Tour détruite - croix
                g2d.drawLine(x - 2, y - 2, x + 2, y + 2);
                g2d.drawLine(x - 2, y + 2, x + 2, y - 2);
            }
        }
    }
    
    /**
     * Dessine les obstacles sur la minimap.
     */
    private void dessinerObstacles(Graphics2D g2d) {
        g2d.setColor(COULEUR_OBSTACLE);
        
        for (moteur.formes.Forme obstacle : arene.getObstacles()) {
            if (obstacle instanceof Rectangle) {
                Rectangle rect = (Rectangle) obstacle;
                int x = (int) (rect.x * echelleX);
                int y = (int) (rect.y * echelleY);
                int largeur = (int) (rect.largeur * echelleX);
                int hauteur = (int) (rect.hauteur * echelleY);
                
                if (largeur > 1 && hauteur > 1) {
                    g2d.fillRect(x, y, Math.max(largeur, 1), Math.max(hauteur, 1));
                }
            } else if (obstacle instanceof Cercle) {
                Cercle cercle = (Cercle) obstacle;
                int x = (int) (cercle.getCentre().x * echelleX);
                int y = (int) (cercle.getCentre().y * echelleY);
                int rayon = (int) (cercle.getRayon() * Math.min(echelleX, echelleY));
                
                if (rayon > 0) {
                    g2d.fillOval(x - rayon, y - rayon, rayon * 2, rayon * 2);
                }
            }
        }
    }
    
    /**
     * Dessine les éléments dynamiques sur la minimap (héros, etc.).
     */
    public void dessinerElementsDynamiques(Graphics2D g2d, List<domaine.unite.Heros> heros, 
                                          Vecteur2 positionCamera, double champVision) {
        // Créer une copie de l'image de base
        BufferedImage imageDynamique = new BufferedImage(largeurMinimap, hauteurMinimap, 
                                                       BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2dDyn = imageDynamique.createGraphics();
        
        // Copier l'image de base
        g2dDyn.drawImage(imageMinimap, 0, 0, null);
        
        // Activer l'anti-aliasing
        g2dDyn.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                               RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dessiner les héros
        dessinerHeros(g2dDyn, heros);
        
        // Dessiner la zone de vision de la caméra
        dessinerZoneVision(g2dDyn, positionCamera, champVision);
        
        // Dessiner l'image finale
        g2d.drawImage(imageDynamique, 0, 0, null);
        
        g2dDyn.dispose();
    }
    
    /**
     * Dessine les héros sur la minimap.
     */
    private void dessinerHeros(Graphics2D g2d, List<domaine.unite.Heros> heros) {
        for (domaine.unite.Heros hero : heros) {
            if (hero.estVivant()) {
                int x = (int) (hero.getPosition().x * echelleX);
                int y = (int) (hero.getPosition().y * echelleY);
                
                // Couleur selon l'équipe
                if (hero.getEquipeId() == 1) {
                    g2d.setColor(COULEUR_HEROS1);
                } else {
                    g2d.setColor(COULEUR_HEROS2);
                }
                
                // Dessiner le héros
                g2d.fillOval(x - 3, y - 3, 6, 6);
                
                // Indicateur de déplacement
                if (hero.estEnDeplacement() && hero.getDestination() != null) {
                    int destX = (int) (hero.getDestination().x * echelleX);
                    int destY = (int) (hero.getDestination().y * echelleY);
                    
                    g2d.setColor(Color.YELLOW);
                    g2d.drawLine(x, y, destX, destY);
                    g2d.fillOval(destX - 2, destY - 2, 4, 4);
                }
            }
        }
    }
    
    /**
     * Dessine la zone de vision de la caméra.
     */
    private void dessinerZoneVision(Graphics2D g2d, Vecteur2 positionCamera, double champVision) {
        int x = (int) (positionCamera.x * echelleX);
        int y = (int) (positionCamera.y * echelleY);
        int largeur = (int) (champVision * echelleX);
        int hauteur = (int) (champVision * echelleY);
        
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.drawRect(x - largeur/2, y - hauteur/2, largeur, hauteur);
    }
    
    /**
     * Convertit une position du monde vers la minimap.
     */
    public Vecteur2 convertirPositionMinimap(Vecteur2 positionMonde) {
        int x = (int) (positionMonde.x * echelleX);
        int y = (int) (positionMonde.y * echelleY);
        return new Vecteur2(x, y);
    }
    
    /**
     * Convertit une position de la minimap vers le monde.
     */
    public Vecteur2 convertirPositionMonde(Vecteur2 positionMinimap) {
        double x = positionMinimap.x / echelleX;
        double y = positionMinimap.y / echelleY;
        return new Vecteur2(x, y);
    }
    
    /**
     * Vérifie si un point de la minimap est valide.
     */
    public boolean estPointValide(Vecteur2 pointMinimap) {
        return pointMinimap.x >= 0 && pointMinimap.x < largeurMinimap &&
               pointMinimap.y >= 0 && pointMinimap.y < hauteurMinimap;
    }
    
    /**
     * Force le rechargement de la minimap.
     */
    public void recharger() {
        doitRecharger = true;
        genererMinimap();
    }
    
    // Getters
    public int getLargeurMinimap() { return largeurMinimap; }
    public int getHauteurMinimap() { return hauteurMinimap; }
    public BufferedImage getImageMinimap() { return imageMinimap; }
}