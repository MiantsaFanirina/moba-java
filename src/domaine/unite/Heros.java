package domaine.unite;

import java.util.ArrayList;
import java.util.List;
import domaine.sort.Sort;
import domaine.equipement.Equipement;
import domaine.structures.Fontaine;

/**
 * Héros contrôlé par le joueur.
 * Hérite de Unite avec des capacités supplémentaires.
 */
public class Heros extends Unite {
    
    // Attributs de progression
    private String nom;
    private String categorie;
    private int niveau;
    private double xp;
    private double xpPourNiveauSuivant;
    private double or;
    
    // Statistiques de jeu
    private int kills;
    private int deaths;
    private int assists;
    
    // Systèmes du héros
    private List<Sort> sorts;
    private List<Equipement> equipements;
    
    // Gestion du rappel et respawn
    private boolean estRappel;
    private double tempsReapparitionRestant;
    private Fontaine fontaineReapparition;
    
    public Heros(int idUnite, int equipeId, String nom, String categorie, double x, double y) {
        super(idUnite, equipeId, x, y);
        this.nom = nom;
        this.categorie = categorie;
        this.niveau = 1;
        this.xp = 0;
        this.xpPourNiveauSuivant = 100;
        this.or = 500;
        
        this.kills = 0;
        this.deaths = 0;
        this.assists = 0;
        
        this.sorts = new ArrayList<>();
        this.equipements = new ArrayList<>(6); // 6 slots d'équipement max
        
        this.estRappel = false;
        this.tempsReapparitionRestant = 0;
        
        // Stats de base améliorées pour héros
        this.hpMax = 120;
        this.hp = hpMax;
        this.manaMax = 100;
        this.mana = manaMax;
        this.attaque = 15;
        this.defense = 8;
        this.vitesse = 1.2;
        this.vitesseAttaque = 0.8;
        this.portee = 1.5;
    }
    
    // Méthodes de progression
    public void gagnerXP(double montant) {
        xp += montant;
        while (xp >= xpPourNiveauSuivant) {
            monterNiveau();
        }
    }
    
    public void gagnerOr(double montant) {
        or += montant;
    }
    
    private void monterNiveau() {
        niveau++;
        xp -= xpPourNiveauSuivant;
        xpPourNiveauSuivant = 100 * Math.pow(1.2, niveau - 1);
        
        // Amélioration des stats au niveau sup
        hpMax += 20;
        manaMax += 15;
        attaque += 3;
        defense += 2;
        
        // Restoration complète au niveau sup
        hp = hpMax;
        mana = manaMax;
    }
    
    // Gestion des sorts
    public boolean peutLancerSort(int indexSort, double tempsActuel) {
        if (indexSort < 0 || indexSort >= sorts.size()) return false;
        if (mana < sorts.get(indexSort).getCoutMana()) return false;
        return sorts.get(indexSort).peutEtreLance(tempsActuel);
    }
    
    public void lancerSort(int indexSort, Unite cible, double tempsActuel) {
        if (!peutLancerSort(indexSort, tempsActuel)) return;
        
        Sort sort = sorts.get(indexSort);
        if (sort.lancer(this, cible, tempsActuel)) {
            mana -= sort.getCoutMana();
        }
    }
    
    public void apprendreSort(Sort sort) {
        if (sorts.size() < 3) {
            sorts.add(sort);
        }
    }
    
    public void mettreAJourCooldowns(double deltaTime) {
        for (Sort sort : sorts) {
            sort.mettreAJourCooldown(deltaTime);
        }
    }
    
    // Gestion de l'équipement
    public boolean acheterEquipement(Equipement equipement) {
        if (or >= equipement.getPrixOr() && equipements.size() < 6) {
            or -= equipement.getPrixOr();
            equipements.add(equipement);
            equipement.appliquer(this);
            return true;
        }
        return false;
    }
    
    public void retirerEquipement(Equipement equipement) {
        equipement.retirer(this);
        equipements.remove(equipement);
    }
    
    public boolean fusionnerEquipements(int index1, int index2) {
        if (index1 < 0 || index1 >= equipements.size() || 
            index2 < 0 || index2 >= equipements.size()) {
            return false;
        }
        
        Equipement eq1 = equipements.get(index1);
        Equipement eq2 = equipements.get(index2);
        
        if (eq1.estFusionnable() && eq2.estFusionnable() && 
            eq1.getType().equals(eq2.getType())) {
            
            Equipement fusionne = eq1.fusionner(eq2);
            if (fusionne != null) {
                retirerEquipement(eq1);
                retirerEquipement(eq2);
                acheterEquipement(fusionne);
                return true;
            }
        }
        return false;
    }
    
    // Gestion du rappel et respawn
    public void lancerRappel() {
        if (!estRappel && estVivant) {
            estRappel = true;
            tempsReapparitionRestant = 8.0; // 8 secondes de rappel
            estVivant = false; // devient invulnérable
        }
    }
    
    public void mettreAJourRappel(double deltaTime) {
        if (estRappel) {
            tempsReapparitionRestant -= deltaTime;
            if (tempsReapparitionRestant <= 0) {
                reapparaitre(fontaineReapparition);
            }
        }
    }
    
    public void reapparaitre(Fontaine fontaine) {
        this.fontaineReapparition = fontaine;
        if (fontaine != null) {
            position = new Vecteur2(fontaine.getPosition().x, fontaine.getPosition().y);
            estRappel = false;
            estVivant = true;
            hp = hpMax;
            mana = manaMax;
        }
    }
    
    @Override
    protected void mourir() {
        super.mourir();
        deaths++;
        tempsReapparitionRestant = 15.0; // 15 secondes de respawn
    }
    
    // Méthodes de combat spécialisées
    public void attaquer(Unite cible, double tempsActuel) {
        super.attaquer(cible, tempsActuel);
        if (!cible.estVivant()) {
            kills++;
            gagnerOr(50 + 20 * niveau); // Or basé sur niveau
            gagnerXP(30 + 10 * niveau); // XP basée sur niveau
        }
    }
    
    // Getters et setters étendus
    public String getNom() { return nom; }
    public String getCategorie() { return categorie; }
    public int getNiveau() { return niveau; }
    public double getXp() { return xp; }
    public double getXpPourNiveauSuivant() { return xpPourNiveauSuivant; }
    public double getOr() { return or; }
    public int getKills() { return kills; }
    public int getDeaths() { return deaths; }
    public int getAssists() { return assists; }
    public List<Sort> getSorts() { return new ArrayList<>(sorts); }
    public List<Equipement> getEquipements() { return new ArrayList<>(equipements); }
    public boolean estRappel() { return estRappel; }
    public double getTempsReapparitionRestant() { return tempsReapparitionRestant; }
    
    public void setOr(double or) { this.or = or; }
    public void setAssists(int assists) { this.assists = assists; }
    public void setFontaineReapparition(Fontaine fontaine) { this.fontaineReapparition = fontaine; }
}