package moteur;

import moteur.Vecteur2;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstraite de base pour toutes les entités du jeu.
 * Héritée par Joueur et autres entités.
 */
public abstract class Entite {
    
    protected moteur.Vecteur2 position;
    protected moteur.Vecteur2 vitesse;
    protected double largeur;
    protected double hauteur;
    protected boolean active;
    
    public Entite(double x, double y, double largeur, double hauteur) {
        this.position = new moteur.Vecteur2(x, y);
        this.vitesse = new moteur.Vecteur2();
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.active = true;
    }
    
    // Mettre à jour l'entité
    public void mettreAJour(double tempsDelta, Monde monde) {
        if (!active) return;
        
        onUpdate(tempsDelta, monde);
    }
    
    // Logique spécifique à chaque entité (surcharger)
    protected abstract void onUpdate(double tempsDelta, Monde monde);
    
    // Rendre l'entité
    public abstract void rendre(Rendu rendu, Camera2D camera);
    
    // Obtenir la position
    public Vecteur2 obtenirPosition() {
        return position;
    }
    
    // Définir la position
    public void setPosition(Vecteur2 position) {
        this.position = position;
    }
    
    // Vérifier si l'entité est active
    public boolean estActive() {
        return active;
    }
    
    // Détruire l'entité
    public void detruire() {
        active = false;
    }
    
    // Getters et setters
    public Vecteur2 getVitesse() { return vitesse; }
    public void setVitesse(Vecteur2 vitesse) { this.vitesse = vitesse; }
    public double getLargeur() { return largeur; }
    public double getHauteur() { return hauteur; }
    public void setLargeur(double largeur) { this.largeur = largeur; }
    public void setHauteur(double hauteur) { this.hauteur = hauteur; }
}