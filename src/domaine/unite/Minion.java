package domaine.unite;

import java.util.List;

/**
 * Scuri généré automatiquement par les bases.
 * Hérite de Unite avec comportement simple.
 */
public class Minion extends Unite {
    
    // Attributs spécifiques aux scuris
    private double orDonne;
    private double xpDonne;
    private double vitesseDeplacement;
    private double tempsDepuisCreation;
    
    public Minion(int idUnite, int equipeId, double x, double y, String type) {
        super(idUnite, equipeId, x, y);
        
        // Stats basées sur le type de scuri
        switch (type) {
            case "Melee":
                this.orDonne = 25;
                this.xpDonne = 15;
                this.attaque = 12;
                this.defense = 3;
                this.vitesse = 0.8;
                this.portee = 0.5;
                break;
                
            case "Caster":
                this.orDonne = 20;
                this.xpDonne = 20;
                this.attaque = 8;
                this.defense = 2;
                this.vitesse = 0.6;
                this.portee = 2.0;
                break;
                
            case "Siege":
                this.orDonne = 40;
                this.xpDonne = 25;
                this.attaque = 20;
                this.defense = 1;
                this.vitesse = 0.4;
                this.portee = 3.0;
                break;
                
            default:
                this.orDonne = 25;
                this.xpDonne = 15;
                this.attaque = 10;
                this.defense = 3;
                this.vitesse = 0.7;
                this.portee = 1.0;
        }
        
        this.vitesseDeplacement = this.vitesse;
        this.tempsDepuisCreation = 0;
        
        // Stats de vie pour scuris
        this.hpMax = 80;
        this.hp = hpMax;
        this.manaMax = 0;
        this.mana = 0;
        this.vitesseAttaque = 0.6;
    }
    
    // Comportement de déplacement en vague
    public void avancerVersCible(Vecteur2 cible, List<Unite> unitesAlliees, double deltaTime) {
        if (!estVivant) return;
        
        // Trouver la cible la plus proche (ennemie ou structure)
        Unite ciblePlusProche = trouverCiblePlusProche(cible, unitesAlliees);
        
        if (ciblePlusProche != null) {
            // Se diriger vers la cible
            double distance = position.distance(ciblePlusProche.position);
            if (distance > portee) {
                Vecteur2 direction = ciblePlusProche.position.soustraire(position).normaliser();
                Vecteur2 deplacement = direction.multiplier(vitesseDeplacement * deltaTime);
                position = position.ajouter(deplacement);
            } else {
                // Attaquer si à portée
                attaquer(ciblePlusProche, System.currentTimeMillis() / 1000.0);
            }
        } else {
            // Continuer vers la cible de la voie
            seDeplacer(cible, deltaTime);
        }
    }
    
    private Unite trouverCiblePlusProche(Vecteur2 cibleVoie, List<Unite> unitesAlliees) {
        Unite ciblePlusProche = null;
        double distanceMinimale = Double.MAX_VALUE;
        
        // Chercher les unités ennemies à portée
        for (Unite unite : unitesAlliees) {
            if (unite.equipeId != this.equipeId && unite.estVivant()) {
                double distance = position.distance(unite.position);
                if (distance <= portee * 2 && distance < distanceMinimale) {
                    distanceMinimale = distance;
                    ciblePlusProche = unite;
                }
            }
        }
        
        return ciblePlusProche;
    }
    
    @Override
    public void mourir() {
        super.mourir();
        // La récompense est donnée par le tueur
    }
    
    // Méthodes d'IA simple
    public void mettreAJourComportement(double deltaTime, List<Unite> unitesAlliees, Vecteur2 cibleVoie) {
        tempsDepuisCreation += deltaTime;
        
        if (tempsDepuisCreation > 120) { // Durée de vie de 2 minutes
            estVivant = false;
            return;
        }
        
        avancerVersCible(cibleVoie, unitesAlliees, deltaTime);
    }
    
    // Getters spécifiques
    public double getOrDonne() { return orDonne; }
    public double getXpDonne() { return xpDonne; }
    public double getVitesseDeplacement() { return vitesseDeplacement; }
    public double getTempsDepuisCreation() { return tempsDepuisCreation; }
}