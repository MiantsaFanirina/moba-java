package domaine.unite;

import moteur.Vecteur2;

/**
 * Classe abstraite de base pour toutes les unités combattantes.
 * Héritée par Heros, Minion, Creep.
 */
public abstract class Unite {
    
    // Attributs de base
    protected int idUnite;
    protected int equipeId;
    protected Vecteur2 position;
    protected double hp;
    protected double hpMax;
    protected double mana;
    protected double manaMax;
    protected boolean estVivant;
    protected double vitesse;
    
    // Attributs de combat
    protected double attaque;
    protected double defense;
    protected double portee;
    protected double vitesseAttaque;
    protected double dernierTempsAttaque;
    
    public Unite(int idUnite, int equipeId, double x, double y) {
        this.idUnite = idUnite;
        this.equipeId = equipeId;
        this.position = new Vecteur2(x, y);
        this.estVivant = true;
        this.vitesse = 1.0;
        this.portee = 1.0;
        this.vitesseAttaque = 1.0;
        this.dernierTempsAttaque = 0;
        
        // Valeurs par défaut
        this.hpMax = 100;
        this.hp = hpMax;
        this.manaMax = 50;
        this.mana = manaMax;
        this.attaque = 10;
        this.defense = 5;
    }
    
    // Méthodes de déplacement
    public void seDeplacer(Vecteur2 destination, double deltaTime) {
        if (!estVivant) return;
        
        Vecteur2 direction = destination.soustraire(position).normaliser();
        Vecteur2 deplacement = direction.multiplier(vitesse * deltaTime);
        position = position.ajouter(deplacement);
    }
    
    // Méthodes de combat
    public void attaquer(Unite cible, double tempsActuel) {
        if (!peutAttaquer(cible, tempsActuel)) return;
        
        double degats = calculerDegats(cible);
        cible.subirDegats(degats);
        dernierTempsAttaque = tempsActuel;
    }
    
    protected boolean peutAttaquer(Unite cible, double tempsActuel) {
        if (!estVivant || !cible.estVivant) return false;
        if (equipeId == cible.equipeId) return false;
        if (position.distance(cible.position) > portee) return false;
        
        double tempsRecharge = 1.0 / vitesseAttaque;
        return tempsActuel - dernierTempsAttaque >= tempsRecharge;
    }
    
    protected double calculerDegats(Unite cible) {
        return Math.max(1, attaque - cible.defense);
    }
    
    // Méthodes de dégâts et vie
    public void subirDegats(double degats) {
        hp = Math.max(0, hp - degats);
        if (hp <= 0) {
            mourir();
        }
    }
    
    public void soigner(double montant) {
        hp = Math.min(hpMax, hp + montant);
    }
    
    public void regenererMana(double montant) {
        mana = Math.min(manaMax, mana + montant);
    }
    
    protected void mourir() {
        estVivant = false;
        hp = 0;
        mana = 0;
    }
    
    // Getters et setters
    public int getIdUnite() { return idUnite; }
    public int getEquipeId() { return equipeId; }
    public Vecteur2 getPosition() { return position; }
    public double getHp() { return hp; }
    public double getHpMax() { return hpMax; }
    public double getMana() { return mana; }
    public double getManaMax() { return manaMax; }
    public boolean estVivant() { return estVivant; }
    public double getVitesse() { return vitesse; }
    public double getAttaque() { return attaque; }
    public double getDefense() { return defense; }
    public double getPortee() { return portee; }
    
    public void setPosition(Vecteur2 position) { this.position = position; }
    public void setVitesse(double vitesse) { this.vitesse = vitesse; }
    public void setAttaque(double attaque) { this.attaque = attaque; }
    public void setDefense(double defense) { this.defense = defense; }
    public void setPortee(double portee) { this.portee = portee; }
}