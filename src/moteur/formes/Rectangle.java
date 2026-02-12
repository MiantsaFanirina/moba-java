package moteur.formes;

import moteur.Vecteur2;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Rectangle géométrique avec position et dimensions.
 */
public class Rectangle extends Forme {
    
    private double largeur;
    private double hauteur;
    
    public Rectangle(Vecteur2 position, double largeur, double hauteur) {
        super(position, Color.BLUE, true, 1.0);
        this.largeur = largeur;
        this.hauteur = hauteur;
    }
    
    @Override
    public void dessiner(Graphics2D g2) {
        g2.setColor(couleur);
        
        if (estRemplie) {
            g2.fillRect(
                (int) position.x, 
                (int) position.y, 
                (int) largeur, 
                (int) hauteur
            );
        }
        
        g2.setColor(couleur.darker());
        g2.drawRect(
            (int) position.x, 
            (int) position.y, 
            (int) largeur, 
            (int) hauteur
        );
    }
    
    @Override
    public boolean contientPoint(Vecteur2 point) {
        return point.x >= position.x && 
               point.x <= position.x + largeur &&
               point.y >= position.y && 
               point.y <= position.y + hauteur;
    }
    
    @Override
    public double calculerAire() {
        return largeur * hauteur;
    }
    
    @Override
    public double calculerPerimetre() {
        return 2 * (largeur + hauteur);
    }
    
    // Méthodes d'accès aux coordonnées pour la compatibilité
    public double getX() { return position.x; }
    public double getY() { return position.y; }
    public void setX(double x) { position.x = x; }
    public void setY(double y) { position.y = y; }
    
    // Méthode d'intersection pour les collisions
    public boolean intersecte(Rectangle autre) {
        return position.x < autre.position.x + autre.largeur &&
               position.x + largeur > autre.position.x &&
               position.y < autre.position.y + autre.hauteur &&
               position.y + hauteur > autre.position.y;
    }
    
    // Getters et setters
    public double getLargeur() { return largeur; }
    public double getHauteur() { return hauteur; }
    
    public void setLargeur(double largeur) { this.largeur = largeur; }
    public void setHauteur(double hauteur) { this.hauteur = hauteur; }
}