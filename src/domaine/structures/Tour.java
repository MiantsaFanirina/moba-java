package domaine.structures;

import domaine.unite.Unite;

/**
 * Tour de défense sur une voie.
 * Attaque automatiquement les unités ennemies.
 */
public class Tour {
    
    private int idTour;
    private int equipeId;
    private double pointsVie;
    private double pointsVieMax;
    private double portee;
    private double degats;
    private boolean estDetruite;
    private double vitesseAttaque;
    private double dernierTempsAttaque;
    private Unite cibleActuelle;
    
    public Tour(int idTour, int equipeId, double x, double y) {
        this.idTour = idTour;
        this.equipeId = equipeId;
        this.pointsVieMax = 1500;
        this.pointsVie = pointsVieMax;
        this.portee = 8.0;
        this.degats = 80;
        this.vitesseAttaque = 0.5; // 1 attaque par 2 secondes
        this.dernierTempsAttaque = 0;
        this.cibleActuelle = null;
        this.estDetruite = false;
    }
    
    // Trouver et attaquer les ennemies à portée
    public void attaquer(double tempsActuel, java.util.List<Unite> unites) {
        if (estDetruite) return;
        
        // Vérifier le cooldown
        if (tempsActuel - dernierTempsAttaque < 2.0 / vitesseAttaque) {
            return;
        }
        
        // Trouver la cible la plus proche
        Unite cibleOptimale = trouverCibleOptimale(unites);
        
        if (cibleOptimale != null) {
            // Vérifier si la cible actuelle est toujours valide
            if (cibleActuelle == null || !cibleActuelle.estVivant() || 
                getPosition().distance(cibleActuelle.getPosition()) > portee) {
                cibleActuelle = cibleOptimale;
            }
            
            // Attaquer la cible
            double distance = getPosition().distance(cibleActuelle.getPosition());
            if (distance <= portee) {
                cibleActuelle.subirDegats(degats);
                dernierTempsAttaque = tempsActuel;
            }
        } else {
            cibleActuelle = null;
        }
    }
    
    // Trouver la meilleure cible (priorité héros > scuris > creeps)
    private Unite trouverCibleOptimale(java.util.List<Unite> unites) {
        Unite meilleureCible = null;
        double meilleurScore = -1;
        
        for (Unite unite : unites) {
            if (!estCibleValide(unite)) continue;
            
            double distance = getPosition().distance(unite.getPosition());
            if (distance > portee) continue;
            
            double score = calculerScoreCible(unite, distance);
            if (score > meilleurScore) {
                meilleurScore = score;
                meilleureCible = unite;
            }
        }
        
        return meilleureCible;
    }
    
    // Vérifier si une unité peut être ciblée
    private boolean estCibleValide(Unite unite) {
        return unite.estVivant() && 
               unite.getEquipeId() != equipeId &&
               unite.getEquipeId() != 0; // Ne pas cibler les neutres
    }
    
    // Calculer le score de priorité d'une cible
    private double calculerScoreCible(Unite unite, double distance) {
        double score = 1000 - distance; // Base sur la distance
        
        // Bonus pour les héros
        if (unite instanceof domaine.unite.Heros) {
            score += 500;
        }
        // Bonus pour les unités avec peu de vie
        double pourcentageVie = unite.getHp() / unite.getHpMax();
        score += (1 - pourcentageVie) * 200;
        
        return score;
    }
    
    // Gestion des dégâts
    public void subirDegats(double degats) {
        if (estDetruite) return;
        
        pointsVie = Math.max(0, pointsVie - degats);
        
        if (pointsVie <= 0) {
            detruire();
        }
    }
    
    // Détruire la tour
    private void detruire() {
        estDetruite = true;
        pointsVie = 0;
        cibleActuelle = null;
    }
    
    // Réparer la tour
    public void reparer(double montant) {
        if (!estDetruite) {
            pointsVie = Math.min(pointsVieMax, pointsVie + montant);
            if (pointsVie > 0) {
                estDetruite = false;
            }
        }
    }
    
    // Getters et setters
    public int getIdTour() { return idTour; }
    public int getEquipeId() { return equipeId; }
    public double getPointsVie() { return pointsVie; }
    public double getPointsVieMax() { return pointsVieMax; }
    public double getPortee() { return portee; }
    public double getDegats() { return degats; }
    public boolean estDetruite() { return estDetruite; }
    public double getVitesseAttaque() { return vitesseAttaque; }
    public Unite getCibleActuelle() { return cibleActuelle; }
    
    public void setPortee(double portee) { this.portee = portee; }
    public void setDegats(double degats) { this.degats = degats; }
    public void setVitesseAttaque(double vitesse) { this.vitesseAttaque = vitesse; }
    
    // Position de la tour (pour les calculs de distance)
    public moteur.Vecteur2 getPosition() {
        // TODO: Retourner la position réelle de la tour
        return new moteur.Vecteur2(0, 0); // Placeholder
    }
}