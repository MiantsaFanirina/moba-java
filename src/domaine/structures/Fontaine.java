package domaine.structures;

import domaine.unite.Heros;
import domaine.equipe.Equipe;
import moteur.Vecteur2;

/**
 * Fontaine de régénération et de réapparition.
 * Soigne les héros et gère leur respawn.
 */
public class Fontaine {
    
    private int idFontaine;
    private Equipe equipe;
    private Vecteur2 position;
    private double tauxRegenParSeconde;
    private double portee;
    private double protectionTempsSecondes;
    
    public Fontaine(int idFontaine, Equipe equipe, double x, double y) {
        this.idFontaine = idFontaine;
        this.equipe = equipe;
        this.position = new Vecteur2(x, y);
        this.tauxRegenParSeconde = 5.0; // 5 HP/sec + 2 MP/sec
        this.portee = 15.0;
        this.protectionTempsSecondes = 3.0; // 3 secondes d'invulnérabilité au respawn
    }
    
    // Régénérer un héros s'il est à portée
    public void regenerer(Heros heros) {
        if (!heros.estVivant()) return;
        
        double distance = position.distance(heros.getPosition());
        if (distance <= portee) {
            // Régénération plus forte dans la fontaine
            double bonusRegen = distance < 5.0 ? 2.0 : 1.0;
            heros.soigner(tauxRegenParSeconde * bonusRegen);
            heros.regenererMana(tauxRegenParSeconde * 0.4 * bonusRegen);
        }
    }
    
    // Faire réapparaître un héros
    public void faireReapparaitre(Heros heros) {
        if (heros.getEquipeId() != equipe.getIdEquipe()) {
            return; // Uniquement les héros de l'équipe
        }
        
        // Positionner le héros à la fontaine
        heros.setPosition(new Vecteur2(position.x, position.y));
        
        // Restaurer la vie et le mana
        heros.soigner(heros.getHpMax());
        heros.regenererMana(heros.getManaMax());
        
        // Donner une protection temporaire
        // TODO: Implémenter système de protection/invulnérabilité
        
        // Notifier le respawn
        if (equipe != null) {
            equipe.notifierReapparition(heros);
        }
    }
    
    // Vérifier si un héros est dans la zone de protection
    public boolean estDansZoneProtection(Heros heros) {
        if (!heros.estVivant()) return false;
        
        double distance = position.distance(heros.getPosition());
        return distance <= portee;
    }
    
    // Mettre à jour tous les héros à portée
    public void mettreAJour(java.util.List<Heros> tousHeros) {
        for (Heros heros : tousHeros) {
            if (heros.getEquipeId() == equipe.getIdEquipe()) {
                regenerer(heros);
            }
        }
    }
    
    // Améliorer la fontaine (basé sur le temps de jeu)
    public void ameliorer(double tempsJeu) {
        // Amélioration progressive de la régénération
        double niveauAmelioration = Math.floor(tempsJeu / 300); // Tous les 5 minutes
        tauxRegenParSeconde = 5.0 + niveauAmelioration * 1.0;
    }
    
    // Getters et setters
    public int getIdFontaine() { return idFontaine; }
    public Equipe getEquipe() { return equipe; }
    public Vecteur2 getPosition() { return position; }
    public double getTauxRegenParSeconde() { return tauxRegenParSeconde; }
    public double getPortee() { return portee; }
    public double getProtectionTempsSecondes() { return protectionTempsSecondes; }
    
    public void setTauxRegenParSeconde(double taux) { this.tauxRegenParSeconde = taux; }
    public void setPortee(double portee) { this.portee = portee; }
}