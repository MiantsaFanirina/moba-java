package domaine.equipement;

import domaine.unite.Unite;

/**
 * Équipement pouvant être porté par un héros.
 * Peut être fusionné avec d'autres équipements.
 */
public class Equipement {
    
    private int idEquipement;
    private String nom;
    private String type;
    private double prixOr;
    private double bonusAttaque;
    private double bonusDefense;
    private boolean estFusionnable;
    private int niveau;
    
    public Equipement(int idEquipement, String nom, String type, double prixOr,
                  double bonusAttaque, double bonusDefense, boolean estFusionnable) {
        this.idEquipement = idEquipement;
        this.nom = nom;
        this.type = type;
        this.prixOr = prixOr;
        this.bonusAttaque = bonusAttaque;
        this.bonusDefense = bonusDefense;
        this.estFusionnable = estFusionnable;
        this.niveau = 1;
    }
    
    // Appliquer les bonus à une unité
    public void appliquer(Unite cible) {
        cible.setAttaque(cible.getAttaque() + bonusAttaque);
        cible.setDefense(cible.getDefense() + bonusDefense);
    }
    
    // Retirer les bonus d'une unité
    public void retirer(Unite cible) {
        cible.setAttaque(cible.getAttaque() - bonusAttaque);
        cible.setDefense(cible.getDefense() - bonusDefense);
    }
    
    // Fusionner avec un autre équipement
    public Equipement fusionner(Equipement autre) {
        if (!peutFusionnerAvec(autre)) {
            return null;
        }
        
        // Créer un équipement fusionné avec des stats améliorées
        String nomFusionne = nom + " " + autre.getNom();
        double nouveauBonusAttaque = (bonusAttaque + autre.getBonusAttaque()) * 1.5;
        double nouveauBonusDefense = (bonusDefense + autre.getBonusDefense()) * 1.5;
        double nouveauPrix = (prixOr + autre.getPrixOr()) * 0.8; // Légèrement moins cher
        
        return new Equipement(
            Math.max(idEquipement, autre.getIdEquipement()),
            nomFusionne,
            type,
            nouveauPrix,
            nouveauBonusAttaque,
            nouveauBonusDefense,
            true
        );
    }
    
    // Vérifier si la fusion est possible
    private boolean peutFusionnerAvec(Equipement autre) {
        return estFusionnable && autre.estFusionnable() &&
               type.equals(autre.getType()) &&
               niveau == 1 && autre.getNiveau() == 1; // Uniquement niveau 1
    }
    
    // Améliorer l'équipement
    public void ameliorer() {
        if (niveau < 3) {
            niveau++;
            bonusAttaque *= 1.4;
            bonusDefense *= 1.3;
            estFusionnable = false; // Plus fusionnable après amélioration
        }
    }
    
    // Getters et setters
    public int getIdEquipement() { return idEquipement; }
    public String getNom() { return nom; }
    public String getType() { return type; }
    public double getPrixOr() { return prixOr; }
    public double getBonusAttaque() { return bonusAttaque; }
    public double getBonusDefense() { return bonusDefense; }
    public boolean estFusionnable() { return estFusionnable; }
    public int getNiveau() { return niveau; }
    
    public void setNiveau(int niveau) { this.niveau = niveau; }
    public void setEstFusionnable(boolean estFusionnable) { this.estFusionnable = estFusionnable; }
}