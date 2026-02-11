package moteur;

import java.util.ArrayList;
import java.util.List;

/**
 * Monde contenant toutes les entités et les obstacles.
 * Gère les collisions et les mises à jour.
 */
public class Monde {
    
    private List<Entite> entites;
    private List<UtilitairesCollision.Rectangle> collisionneurs;
    private double largeur;
    private double hauteur;
    
    public Monde() {
        this.entites = new ArrayList<>();
        this.collisionneurs = new ArrayList<>();
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
    
    // Ajouter un collisionneur
    public void ajouterCollisionneur(UtilitairesCollision.Rectangle collisionneur) {
        collisionneurs.add(collisionneur);
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
    
    // Obtenir les collisionneurs près d'une position
    public List<UtilitairesCollision.Rectangle> obtenirCollisionneursProches(double x, double y, double rayon) {
        List<UtilitairesCollision.Rectangle> proches = new ArrayList<>();
        
        for (UtilitairesCollision.Rectangle collisionneur : collisionneurs) {
            double distance = Math.sqrt(
                Math.pow(collisionneur.x + collisionneur.width/2 - x, 2) +
                Math.pow(collisionneur.y + collisionneur.height/2 - y, 2)
            );
            
            if (distance <= rayon) {
                proches.add(collisionneur);
            }
        }
        
        return proches;
    }
    
    // Vérifier si une position est valide (pas dans un obstacle)
    public boolean estPositionValide(Vecteur2 position, double largeur, double hauteur) {
        // Vérifier les limites du monde
        if (position.x < 0 || position.x + largeur > this.largeur ||
            position.y < 0 || position.y + hauteur > this.hauteur) {
            return false;
        }
        
        // Vérifier les collisions avec les obstacles
        UtilitairesCollision.Rectangle testRect = 
            new UtilitairesCollision.Rectangle((float) position.x, (float) position.y, 
                                             (float) largeur, (float) hauteur);
        
        for (UtilitairesCollision.Rectangle obstacle : collisionneurs) {
            if (UtilitairesCollision.rectanglesIntersectent(testRect, obstacle)) {
                return false;
            }
        }
        
        return true;
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
    
    // Obtenir tous les collisionneurs
    public List<UtilitairesCollision.Rectangle> obtenirCollisionneurs() {
        return new ArrayList<>(collisionneurs);
    }
    
    // Nettoyer le monde
    public void nettoyer() {
        entites.clear();
        collisionneurs.clear();
    }
    
    // Getters et setters
    public double getLargeur() { return largeur; }
    public double getHauteur() { return hauteur; }
    
    public void setLargeur(double largeur) { this.largeur = largeur; }
    public void setHauteur(double hauteur) { this.hauteur = hauteur; }
}