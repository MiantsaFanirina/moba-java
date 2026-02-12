package moteur;

import java.util.ArrayList;
import java.util.List;

/**
 * Monde contenant toutes les entités et les obstacles.
 * Gère les collisions et les mises à jour.
 */
public class Monde {
    
    private List<Entite> entites;
    private double largeur;
    private double hauteur;
    
    public Monde() {
        this.entites = new ArrayList<>();
        this.largeur = 2400;
        this.hauteur = 1800;
    }
    
    // Ajouter une entité au monde
    public void ajouterEntite(Entite entite) {
        entites.add(entite);
    }
    
    // Retirer une entité du monde
    public void retirerEntite(Entite entite) {
        entites.remove(entite);
    }
    
    // Mettre à jour toutes les entités
    public void mettreAJour(double tempsDelta) {
        // Mettre à jour les entités actives
        for (Entite entite : entites) {
            if (entite.estActive()) {
                entite.mettreAJour(tempsDelta, this);
            }
        }
        
        // Nettoyer les entités inactives
        entites.removeIf(entite -> !entite.estActive());
    }
    
    // Rendre toutes les entités
    public void rendre(Rendu rendu, Camera2D camera) {
        for (Entite entite : entites) {
            if (entite.estActive()) {
                entite.rendre(rendu, camera);
            }
        }
    }
    
    // Obtenir les entités à proximité
    public List<Entite> obtenirEntitesProches(Vecteur2 position, double rayon) {
        List<Entite> proches = new ArrayList<>();
        
        for (Entite entite : entites) {
            if (entite.estActive()) {
                double distance = entite.obtenirPosition().distance(position);
                if (distance <= rayon) {
                    proches.add(entite);
                }
            }
        }
        
        return proches;
    }
    
    // Obtenir toutes les entités
    public List<Entite> obtenirEntites() {
        return new ArrayList<>(entites);
    }
    
    // Nettoyer le monde
    public void nettoyer() {
        entites.clear();
    }
    
    // Getters et setters
    public double getLargeur() { return largeur; }
    public double getHauteur() { return hauteur; }
    
    public void setLargeur(double largeur) { this.largeur = largeur; }
    public void setHauteur(double hauteur) { this.hauteur = hauteur; }
}