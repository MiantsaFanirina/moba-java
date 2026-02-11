package domaine.sort;

import domaine.unite.Unite;

/**
 * Sort pouvant être lancé par un héros.
 * Contient les informations de coût, cooldown, et effets.
 */
public class Sort {
    
    private int idSort;
    private String nom;
    private String typeEffet;
    private double coutMana;
    private double cooldownSecondes;
    private double cooldownRestant;
    private int niveau;
    private double degats;
    private double portee;
    
    public Sort(int idSort, String nom, String typeEffet, double coutMana, 
                double cooldownSecondes, double degats, double portee) {
        this.idSort = idSort;
        this.nom = nom;
        this.typeEffet = typeEffet;
        this.coutMana = coutMana;
        this.cooldownSecondes = cooldownSecondes;
        this.cooldownRestant = 0;
        this.niveau = 1;
        this.degats = degats;
        this.portee = portee;
    }
    
    // Vérifier si le sort peut être lancé
    public boolean peutEtreLance(double tempsActuel) {
        return cooldownRestant <= 0;
    }
    
    // Lancer le sort sur une cible
    public boolean lancer(Unite lanceur, Unite cible, double tempsActuel) {
        if (!peutEtreLance(tempsActuel)) {
            return false;
        }
        
        if (lanceur.getMana() < coutMana) {
            return false;
        }
        
        // Vérifier la portée
        double distance = lanceur.getPosition().distance(cible.getPosition());
        if (distance > portee) {
            return false;
        }
        
        // Appliquer l'effet du sort
        appliquerEffet(lanceur, cible);
        
        // Consommer le mana et démarrer le cooldown
        lanceur.regenererMana(-coutMana);
        cooldownRestant = cooldownSecondes;
        
        return true;
    }
    
    // Appliquer l'effet spécifique du sort
    private void appliquerEffet(Unite lanceur, Unite cible) {
        switch (typeEffet) {
            case "DEGAT":
                double degatsFinaux = calculerDegats(lanceur);
                cible.subirDegats(degatsFinaux);
                break;
                
            case "SOIN":
                double montantSoin = degats * 1.5;
                cible.soigner(montantSoin);
                break;
                
            case "RALENTISSEMENT":
                // TODO: Implémenter système de ralentissement
                cible.subirDegats(degats * 0.3);
                break;
                
            case "STUN":
                // TODO: Implémenter système d'étourdissement
                cible.subirDegats(degats * 0.5);
                break;
                
            case "BOUCLIER":
                // TODO: Implémenter système de bouclier
                lanceur.soigner(degats * 0.8); // Soin partiel
                break;
                
            default:
                cible.subirDegats(degats);
                break;
        }
    }
    
    // Calculer les dégâts en fonction des stats du lanceur
    private double calculerDegats(Unite lanceur) {
        double multiplicateur = 1.0 + (niveau - 1) * 0.2;
        return degats * multiplicateur * (1 + lanceur.getAttaque() * 0.1);
    }
    
    // Mettre à jour le cooldown
    public void mettreAJourCooldown(double deltaTime) {
        if (cooldownRestant > 0) {
            cooldownRestant = Math.max(0, cooldownRestant - deltaTime);
        }
    }
    
    // Améliorer le sort
    public void ameliorer() {
        if (niveau < 3) {
            niveau++;
            degats *= 1.3;
            coutMana *= 1.2;
            portee *= 1.1;
        }
    }
    
    // Getters et setters
    public int getIdSort() { return idSort; }
    public String getNom() { return nom; }
    public String getTypeEffet() { return typeEffet; }
    public double getCoutMana() { return coutMana; }
    public double getCooldownSecondes() { return cooldownSecondes; }
    public double getCooldownRestant() { return cooldownRestant; }
    public int getNiveau() { return niveau; }
    public double getDegats() { return degats; }
    public double getPortee() { return portee; }
    
    public void setCoutMana(double coutMana) { this.coutMana = coutMana; }
    public void setCooldownSecondes(double cooldownSecondes) { this.cooldownSecondes = cooldownSecondes; }
    public void setDegats(double degats) { this.degats = degats; }
    public void setPortee(double portee) { this.portee = portee; }
}