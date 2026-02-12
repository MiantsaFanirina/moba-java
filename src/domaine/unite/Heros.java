package domaine.unite;

import java.util.ArrayList;
import java.util.List;
import domaine.sort.Sort;
import domaine.equipement.Equipement;
import domaine.structures.Fontaine;
import moteur.Vecteur2;
import moteur.SystemeCollision;
import moteur.SuiveurDeChemin;

/**
 * Héros contrôlé par le joueur avec système de pathfinding avancé.
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
    
    // Système de déplacement avancé
    private Vecteur2 destination;
    private List<Vecteur2> cheminActuel;
    private SuiveurDeChemin suiveur;
    private double vitesseIndividuelle;
    private boolean estEnDeplacement;
    private SystemeCollision systemeCollision;
    private Vecteur2 vitesseActuelle; // Vélocité actuelle pour mouvement fluide
    
    // Types de héros avec vitesses différentes
    public enum TypeHeros {
        TANK("Tank", 0.8),
        MAGE("Mage", 1.0),
        ASSASSIN("Assassin", 1.4),
        TIREUR("Tireur", 1.2),
        SUPPORT("Support", 1.1);
        
        private final String nom;
        private final double vitesseBase;
        
        TypeHeros(String nom, double vitesseBase) {
            this.nom = nom;
            this.vitesseBase = vitesseBase;
        }
        
        public String getNom() { return nom; }
        public double getVitesseBase() { return vitesseBase; }
    }
    
    private TypeHeros typeHeros;
    
    public Heros(int idUnite, int equipeId, String nom, TypeHeros typeHeros, double x, double y) {
        super(idUnite, equipeId, x, y);
        this.nom = nom;
        this.typeHeros = typeHeros;
        this.categorie = typeHeros.getNom();
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
        
        // Système de déplacement
        this.destination = null;
        this.cheminActuel = new ArrayList<>();
        this.estEnDeplacement = false;
        this.vitesseIndividuelle = typeHeros.getVitesseBase();
        this.vitesseActuelle = new Vecteur2(0, 0);
        
        // Stats de base selon le type
        switch (typeHeros) {
            case TANK:
                this.hpMax = 150;
                this.attaque = 12;
                this.defense = 15;
                break;
            case MAGE:
                this.hpMax = 80;
                this.attaque = 18;
                this.defense = 5;
                this.manaMax = 150;
                break;
            case ASSASSIN:
                this.hpMax = 90;
                this.attaque = 20;
                this.defense = 6;
                break;
            case TIREUR:
                this.hpMax = 100;
                this.attaque = 16;
                this.defense = 8;
                this.portee = 3.0;
                break;
            case SUPPORT:
                this.hpMax = 110;
                this.attaque = 10;
                this.defense = 10;
                this.manaMax = 120;
                break;
        }
        
        this.hp = hpMax;
        this.mana = manaMax;
        this.vitesse = vitesseIndividuelle;
        this.vitesseAttaque = 0.8;
    }
    
    /**
     * Définit le système de collision pour le pathfinding.
     */
    public void setSystemeCollision(SystemeCollision systemeCollision) {
        this.systemeCollision = systemeCollision;
    }
    
    /**
     * Déplace le héros vers une position en évitant les obstacles avec vélocité progressive.
     */
    public void deplacerVers(Vecteur2 destinationCible) {
        if (systemeCollision == null) {
            // Si pas de système de collision, déplacement direct
            this.destination = destinationCible;
            this.estEnDeplacement = true;
            this.vitesseActuelle = destinationCible.soustraire(this.position).normaliser().multiplier(vitesseIndividuelle);
            return;
        }
        
        // Trouver le chemin optimal
        List<Vecteur2> chemin = systemeCollision.trouverChemin(
            this.position, destinationCible, 10.0 // Rayon du héros
        );
        
        if (!chemin.isEmpty()) {
            this.destination = destinationCible;
            this.cheminActuel = chemin;
            this.suiveur = new SuiveurDeChemin(chemin, (int)(vitesseIndividuelle * 100));
            this.estEnDeplacement = true;
            // Calculer la vitesse initiale vers le premier point du chemin
            if (chemin.size() > 0) {
                Vecteur2 prochainPoint = chemin.get(0);
                Vecteur2 direction = prochainPoint.soustraire(this.position).normaliser();
                this.vitesseActuelle = direction.multiplier(vitesseIndividuelle);
            }
        }
    }
    
    /**
     * Met à jour la position du héros avec mouvement fluide et vélocité.
     */
    public void mettreAJourDeplacement(double deltaTime) {
        if (!estEnDeplacement || destination == null) {
            // Arrêter progressivement le mouvement
            vitesseActuelle = vitesseActuelle.multiplier(0.9);
            if (vitesseActuelle.longueur() < 0.1) {
                vitesseActuelle = new Vecteur2(0, 0);
            }
            return;
        }
        
        // Vérifier si on est arrivé à destination
        double distance = position.distance(destination);
        if (distance < 10.0) {
            // Ralentir à l'approche
            vitesseActuelle = vitesseActuelle.multiplier(Math.max(0.1, distance / 10.0));
            if (distance < 5.0) {
                arretDeplacement();
                return;
            }
        }
        
        if (systemeCollision != null && suiveur != null) {
            // Suivre le chemin calculé avec vélocité fluide
            Vecteur2 positionVoulue = suiveur.mettreAJour(position, deltaTime);
            
            if (positionVoulue != null) {
                // Calculer la vélocité désirée
                Vecteur2 vitesseDesiree = positionVoulue.soustraire(position).multiplier(1.0 / deltaTime);
                
                // Limiter la vélocité maximale
                if (vitesseDesiree.longueur() > vitesseIndividuelle) {
                    vitesseDesiree = vitesseDesiree.normaliser().multiplier(vitesseIndividuelle);
                }
                
                // Interpolation fluide de la vélocité
                double facteurLissage = 0.15; // Plus petit = plus fluide mais moins réactif
                vitesseActuelle = vitesseActuelle.multiplier(1 - facteurLissage).ajouter(vitesseDesiree.multiplier(facteurLissage));
                
                // Calculer la nouvelle position
                Vecteur2 deplacement = vitesseActuelle.multiplier(deltaTime);
                Vecteur2 nouvellePosition = position.ajouter(deplacement);
                
                // Vérifier que la nouvelle position est valide
                if (systemeCollision.estPositionValide(nouvellePosition, 10.0)) {
                    position = nouvellePosition;
                } else {
                    // Recalculer le chemin si bloqué
                    recalculerChemin();
                }
            } else {
                // Le chemin est terminé, aller directement à destination
                deplacementDirectAvecVelocite(deltaTime);
            }
        } else {
            // Déplacement direct avec vélocité progressive
            deplacementDirectAvecVelocite(deltaTime);
        }
    }
    
    /**
     * Déplacement direct vers la destination.
     */
    private void deplacementDirect(double deltaTime) {
        Vecteur2 direction = destination.soustraire(position).normaliser();
        vitesseActuelle = direction.multiplier(vitesseIndividuelle);
        
        if (systemeCollision != null) {
            // Utiliser le système de collision pour résoudre les collisions
            Vecteur2 vitesse = vitesseActuelle.multiplier(deltaTime * 100);
            
            Vecteur2 nouvellePosition = systemeCollision.resoudreCollision(
                position, 10.0, vitesse
            );
            
            position = nouvellePosition;
        } else {
            // Déplacement simple
            position = position.ajouter(vitesseActuelle.multiplier(deltaTime * 100));
        }
    }
    
    /**
     * Déplacement direct avec vélocité progressive.
     */
    private void deplacementDirectAvecVelocite(double deltaTime) {
        Vecteur2 direction = destination.soustraire(position).normaliser();
        Vecteur2 vitesseDesiree = direction.multiplier(vitesseIndividuelle);
        
        // Interpolation fluide
        double facteurLissage = 0.2;
        vitesseActuelle = vitesseActuelle.multiplier(1 - facteurLissage).ajouter(vitesseDesiree.multiplier(facteurLissage));
        
        if (systemeCollision != null) {
            Vecteur2 vitesse = vitesseActuelle.multiplier(deltaTime * 100);
            Vecteur2 nouvellePosition = systemeCollision.resoudreCollision(
                position, 10.0, vitesse
            );
            position = nouvellePosition;
        } else {
            position = position.ajouter(vitesseActuelle.multiplier(deltaTime * 100));
        }
    }
    
    /**
     * Recalcule le chemin si l'actuel est bloqué.
     */
    private void recalculerChemin() {
        if (systemeCollision != null && destination != null) {
            List<Vecteur2> nouveauChemin = systemeCollision.trouverChemin(
                position, destination, 10.0
            );
            
            if (!nouveauChemin.isEmpty()) {
                this.cheminActuel = nouveauChemin;
                this.suiveur = new SuiveurDeChemin(nouveauChemin, (int)(vitesseIndividuelle * 100));
            } else {
                arretDeplacement();
            }
        }
    }
    
    /**
     * Arrête le déplacement du héros.
     */
    public void arretDeplacement() {
        this.estEnDeplacement = false;
        this.destination = null;
        this.cheminActuel.clear();
        this.suiveur = null;
        this.vitesseActuelle = new Vecteur2(0, 0); // Arrêter la vélocité
    }
    
    // Méthodes de progression (identiques à avant)
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
        if (manaMax > 0) manaMax += 15;
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
        arretDeplacement(); // Arrêter le déplacement à la mort
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
    
    public TypeHeros getTypeHeros() { return typeHeros; }
    public double getVitesseIndividuelle() { return vitesseIndividuelle; }
    public Vecteur2 getDestination() { return destination; }
    public boolean estEnDeplacement() { return estEnDeplacement; }
    public List<Vecteur2> getCheminActuel() { return new ArrayList<>(cheminActuel); }
    public Vecteur2 getVitesseActuelle() { return vitesseActuelle; }
    
    public void setOr(double or) { this.or = or; }
    public void setAssists(int assists) { this.assists = assists; }
    public void setFontaineReapparition(Fontaine fontaine) { this.fontaineReapparition = fontaine; }
}