package moteur;

import moteur.Vecteur2;
import java.util.ArrayList;
import java.util.List;

/**
 * Suiveur de chemin pour le déplacement automatique.
 * Gère le suivi d'un chemin et les blocages.
 */
public class SuiveurDeChemin {
    
    private List<Vecteur2> chemin;
    private double vitesse;
    private int indexPointActuel;
    private double distanceArrivee;
    private boolean estTermine;
    private OnStuckCallback onStuckCallback;
    private OnPathUpdatedCallback onPathUpdatedCallback;
    
    public interface OnStuckCallback {
        List<Vecteur2> onStuck(Vecteur2 positionActuelle, Vecteur2 destination);
    }
    
    public interface OnPathUpdatedCallback {
        void onPathUpdated(List<Vecteur2> nouveauChemin);
    }
    
    public SuiveurDeChemin(List<Vecteur2> chemin, double vitesse) {
        this.chemin = chemin != null ? new ArrayList<>(chemin) : new ArrayList<>();
        this.vitesse = vitesse;
        this.indexPointActuel = 0;
        this.distanceArrivee = 10; // Distance pour considérer un point atteint
        this.estTermine = chemin == null || chemin.isEmpty();
        this.onStuckCallback = null;
        this.onPathUpdatedCallback = null;
    }
    
    // Mettre à jour le suivi
    public Vecteur2 mettreAJour(Vecteur2 positionActuelle, double tempsDelta) {
        if (estTermine) {
            return positionActuelle;
        }
        
        Vecteur2 pointActuel = obtenirPointActuel();
        
        if (pointActuel == null) {
            estTermine = true;
            return positionActuelle;
        }
        
        // Calculer le déplacement vers le point actuel
        Vecteur2 direction = pointActuel.soustraire(positionActuelle).normaliser();
        Vecteur2 deplacement = direction.multiplier(vitesse * tempsDelta);
        
        // Vérifier si on a atteint le point actuel
        if (deplacement.longueur() >= positionActuelle.distance(pointActuel) - distanceArrivee) {
            return deplacerVersPointSuivant();
        }
        
        return positionActuelle.ajouter(deplacement);
    }
    
    // Passer au point suivant du chemin
    private Vecteur2 deplacerVersPointSuivant() {
        indexPointActuel++;
        
        // Vérifier si on a atteint la fin du chemin
        if (indexPointActuel >= chemin.size()) {
            estTermine = true;
            return obtenirPointActuel();
        }
        
        return obtenirPointActuel();
    }
    
    // Obtenir le point actuel du chemin
    private Vecteur2 obtenirPointActuel() {
        if (chemin == null || chemin.isEmpty() || indexPointActuel >= chemin.size()) {
            return null;
        }
        
        return chemin.get(indexPointActuel);
    }
    
    // Vérifier si le personnage est bloqué
    public boolean estBloque(Vecteur2 positionActuelle, double tempsInactivite) {
        if (chemin == null || chemin.isEmpty() || estTermine) {
            return false;
        }
        
        return tempsInactivite > 2.0; // Bloqué si immobile plus de 2 secondes
    }
    
    // Définir les callbacks
    public void setOnStuckCallback(OnStuckCallback callback) {
        this.onStuckCallback = callback;
    }
    
    public void setOnPathUpdatedCallback(OnPathUpdatedCallback callback) {
        this.onPathUpdatedCallback = callback;
    }
    
    public void setOnPathUpdated(OnPathUpdatedCallback callback) {
        this.onPathUpdatedCallback = callback;
    }
    
    // Obtenir des informations sur le chemin
    public List<Vecteur2> getChemin() {
        return new ArrayList<>(chemin);
    }
    
    public boolean estTermine() {
        return estTermine;
    }
    
    public double getVitesse() {
        return vitesse;
    }
    
    public double getProgression() {
        if (chemin == null || chemin.isEmpty()) {
            return 0;
        }
        
        return (double) indexPointActuel / chemin.size();
    }
    
    // Modifier le chemin
    public void setChemin(List<Vecteur2> nouveauChemin) {
        this.chemin = nouveauChemin != null ? new ArrayList<>(nouveauChemin) : new ArrayList<>();
        this.indexPointActuel = 0;
        this.estTermine = this.chemin.isEmpty();
        
        if (onPathUpdatedCallback != null) {
            onPathUpdatedCallback.onPathUpdated(this.chemin);
        }
    }
    
    // Réinitialiser le suiveur
    public void reinitialiser() {
        this.chemin.clear();
        this.indexPointActuel = 0;
        this.estTermine = true;
    }
    
    // Getters et setters
    public double getDistanceArrivee() { return distanceArrivee; }
    public int getIndexPointActuel() { return indexPointActuel; }
    
    public void setVitesse(double vitesse) { this.vitesse = vitesse; }
    public void setDistanceArrivee(double distance) { this.distanceArrivee = distance; }
}