package domaine.unite;

import moteur.Vecteur2;

/**
 * Monstre neutre de la jungle.
 * Hérite de Unite avec comportement de patrouille.
 */
public class Creep extends Unite {
    
    // Attributs spécifiques aux creeps
    private double orDonne;
    private double xpDonne;
    private double delaiReapparitionSecondes;
    private double tempsProchaineReapparition;
    private String type;
    private Vecteur2 positionCamp;
    private double rayonPatrouille;
    private boolean estEnPatrouille;
    
    public Creep(int idUnite, double x, double y, String type) {
        super(idUnite, 0, x, y); // equipeId = 0 pour neutres
        this.type = type;
        this.positionCamp = new Vecteur2(x, y);
        this.estEnPatrouille = true;
        this.tempsProchaineReapparition = 0;
        
        // Stats basées sur le type de creep
        switch (type) {
            case "Loup":
                this.orDonne = 35;
                this.xpDonne = 25;
                this.attaque = 18;
                this.defense = 4;
                this.vitesse = 1.1;
                this.portee = 1.2;
                this.delaiReapparitionSecondes = 60;
                this.rayonPatrouille = 200;
                break;
                
            case "Golem":
                this.orDonne = 80;
                this.xpDonne = 60;
                this.attaque = 25;
                this.defense = 8;
                this.vitesse = 0.6;
                this.portee = 1.5;
                this.delaiReapparitionSecondes = 120;
                this.rayonPatrouille = 150;
                break;
                
            case "Dragon":
                this.orDonne = 150;
                this.xpDonne = 120;
                this.attaque = 40;
                this.defense = 15;
                this.vitesse = 0.8;
                this.portee = 2.5;
                this.delaiReapparitionSecondes = 300; // 5 minutes
                this.rayonPatrouille = 100;
                break;
                
            default:
                this.orDonne = 30;
                this.xpDonne = 20;
                this.attaque = 15;
                this.defense = 3;
                this.vitesse = 1.0;
                this.portee = 1.0;
                this.delaiReapparitionSecondes = 90;
                this.rayonPatrouille = 180;
        }
        
        // Stats de vie pour creeps
        this.hpMax = 150 + (this.type.equals("Dragon") ? 200 : 0);
        this.hp = hpMax;
        this.manaMax = 0;
        this.mana = 0;
        this.vitesseAttaque = 0.4;
    }
    
    // Comportement de patrouille autour du camp
    public void patrouiller(double deltaTime) {
        if (!estVivant || !estEnPatrouille) return;
        
        tempsProchaineReapparition = Math.max(0, tempsProchaineReapparition - deltaTime);
        
        if (tempsProchaineReapparition > 0) return;
        
        // Mouvement simple de patrouille
        double angle = System.currentTimeMillis() / 10000.0; // Rotation lente
        double deltaX = Math.cos(angle) * rayonPatrouille;
        double deltaY = Math.sin(angle) * rayonPatrouille;
        
        Vecteur2 ciblePatrouille = new Vecteur2(
            positionCamp.x + deltaX,
            positionCamp.y + deltaY
        );
        
        if (position.distance(ciblePatrouille) > 10) {
            seDeplacer(ciblePatrouille, deltaTime);
        }
    }
    
    // Planifier la réapparition après la mort
    public void planifierReapparition() {
        if (!estVivant) {
            tempsProchaineReapparition = delaiReapparitionSecondes;
            estEnPatrouille = false;
        }
    }
    
    @Override
    public void mourir() {
        super.mourir();
        planifierReapparition();
    }
    
    // Réapparaître si le temps est écoulé
    public boolean tenterReapparition() {
        if (tempsProchaineReapparition <= 0) {
            estVivant = true;
            hp = hpMax;
            estEnPatrouille = true;
            position = new Vecteur2(positionCamp.x, positionCamp.y);
            return true;
        }
        return false;
    }
    
    // Comportement d'attaque défensif
    public void verifierEtAttaquer(Unite menace) {
        if (!estVivant || !estEnPatrouille) return;
        
        double distance = position.distance(menace.position);
        if (distance <= portee && menace.equipeId != 0) { // Ne pas attaquer les neutres
            attaquer(menace, System.currentTimeMillis() / 1000.0);
        }
    }
    
    // Getters spécifiques
    public double getOrDonne() { return orDonne; }
    public double getXpDonne() { return xpDonne; }
    public double getDelaiReapparitionSecondes() { return delaiReapparitionSecondes; }
    public double getTempsProchaineReapparition() { return tempsProchaineReapparition; }
    public String getType() { return type; }
    public Vecteur2 getPositionCamp() { return positionCamp; }
    public double getRayonPatrouille() { return rayonPatrouille; }
    public boolean estEnPatrouille() { return estEnPatrouille; }
    
    public void setTempsProchaineReapparition(double temps) { this.tempsProchaineReapparition = temps; }
}