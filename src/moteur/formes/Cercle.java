package moteur.formes;

import moteur.Vecteur2;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Cercle géométrique avec position et rayon.
 */
public class Cercle extends Forme {
    
    private double rayon;
    
    public Cercle(Vecteur2 position, double rayon) {
        super(position, Color.RED, true, 1.0);
        this.rayon = rayon;
    }
    
    @Override
    public void dessiner(Graphics2D g2) {
        g2.setColor(couleur);
        
        if (estRemplie) {
            g2.fillOval(
                (int) (position.x - rayon), 
                (int) (position.y - rayon), 
                (int) (rayon * 2), 
                (int) (rayon * 2)
            );
        }
        
        g2.setColor(couleur.darker());
        g2.drawOval(
            (int) (position.x - rayon), 
            (int) (position.y - rayon), 
            (int) (rayon * 2), 
            (int) (rayon * 2)
        );
    }
    
    @Override
    public boolean contientPoint(Vecteur2 point) {
        double distance = position.distance(point);
        return distance <= rayon;
    }
    
    @Override
    public double calculerAire() {
        return Math.PI * rayon * rayon;
    }
    
    @Override
    public double calculerPerimetre() {
        return 2 * Math.PI * rayon;
    }
    
    // Méthode getCentre pour la compatibilité
    public Vecteur2 getCentre() { return position; }
    
    // Getters et setters
    public double getRayon() { return rayon; }
    
    public void setRayon(double rayon) { 
        this.rayon = Math.max(0, rayon); 
    }
}