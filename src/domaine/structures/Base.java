package domaine.structures;

import domaine.unite.Unite;
import domaine.unite.Heros;
import domaine.equipe.Equipe;

/**
 * Base d'une équipe.
 * Si détruite, la partie se termine.
 */
public class Base {
    
    private int idBase;
    private Equipe equipe;
    private double pointsVieStructure;
    private double pointsVieMax;
    private boolean estDetruite;
    private Fontaine fontaine;
    private double regenParSeconde;
    
    public Base(int idBase, Equipe equipe, double x, double y) {
        this.idBase = idBase;
        this.equipe = equipe;
        this.pointsVieMax = 5000;
        this.pointsVieStructure = pointsVieMax;
        this.estDetruite = false;
        this.regenParSeconde = 2.0;
        
        // Créer la fontaine associée
        this.fontaine = new Fontaine(idBase * 10, equipe, x + 50, y + 50);
    }
    
    // Subir des dégâts
    public void subirDegats(double degats) {
        if (estDetruite) return;
        
        pointsVieStructure = Math.max(0, pointsVieStructure - degats);
        
        if (pointsVieStructure <= 0) {
            detruire();
        }
    }
    
    // Détruire la base
    private void detruire() {
        estDetruite = true;
        pointsVieStructure = 0;
        
        // Notifier la fin de partie
        if (equipe != null) {
            equipe.notifierBaseDetruite();
        }
    }
    
    // Régénération automatique
    public void mettreAJour(double deltaTime) {
        if (!estDetruite && pointsVieStructure < pointsVieMax) {
            pointsVieStructure = Math.min(pointsVieMax, 
                pointsVieStructure + regenParSeconde * deltaTime);
        }
    }
    
    // Réparer la base
    public void reparer(double montant) {
        if (!estDetruite) {
            pointsVieStructure = Math.min(pointsVieMax, pointsVieStructure + montant);
        }
    }
    
    // Getters et setters
    public int getIdBase() { return idBase; }
    public Equipe getEquipe() { return equipe; }
    public double getPointsVieStructure() { return pointsVieStructure; }
    public double getPointsVieMax() { return pointsVieMax; }
    public boolean estDetruite() { return estDetruite; }
    public Fontaine obtenirFontaine() { return fontaine; }
    public double getRegenParSeconde() { return regenParSeconde; }
    
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }
    public void setRegenParSeconde(double regen) { this.regenParSeconde = regen; }
}