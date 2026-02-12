package moteur.formes;

import moteur.Vecteur2;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Classe abstraite de base pour toutes les formes géométriques.
 */
public abstract class Forme {
    
    protected Vecteur2 position;
    protected Color couleur;
    protected boolean estRemplie;
    protected double epaisseur;
    
    public Forme(Vecteur2 position, Color couleur, boolean estRemplie, double epaisseur) {
        this.position = position;
        this.couleur = couleur;
        this.estRemplie = estRemplie;
        this.epaisseur = epaisseur;
    }
    
    // Méthodes abstraites à implémenter
    public abstract void dessiner(Graphics2D g2);
    public abstract boolean contientPoint(Vecteur2 point);
    public abstract double calculerAire();
    public abstract double calculerPerimetre();
    
    // Méthodes communes
    public void deplacer(Vecteur2 deplacement) {
        position = position.ajouter(deplacement);
    }
    
    public void setPosition(Vecteur2 position) {
        this.position = position;
    }
    
    public void setCouleur(Color couleur) {
        this.couleur = couleur;
    }
    
    public void setEstRemplie(boolean estRemplie) {
        this.estRemplie = estRemplie;
    }
    
    public void setEpaisseur(double epaisseur) {
        this.epaisseur = epaisseur;
    }
    
    // Getters
    public Vecteur2 getPosition() { return position; }
    public Color getCouleur() { return couleur; }
    public boolean estRemplie() { return estRemplie; }
    public double getEpaisseur() { return epaisseur; }
}