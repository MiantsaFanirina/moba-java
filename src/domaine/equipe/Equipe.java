package domaine.equipe;

import domaine.unite.Heros;
import domaine.unite.Minion;
import domaine.structures.Base;
import domaine.structures.Fontaine;
import domaine.partie.Partie;
import java.util.ArrayList;
import java.util.List;

/**
 * Équipe de joueurs participant à une partie.
 * Gère les héros, la base, et les scuris.
 */
public class Equipe {
    
    private int idEquipe;
    private String nom;
    private String couleur;
    private Base base;
    private List<Heros> heros;
    private List<Minion> minionsActifs;
    private Partie partie;
    private boolean aSurrendu;
    
    // Statistiques de l'équipe
    private int toursEnnemiesDetruites;
    private int monstresNeutresTues;
    private int goldTotal;
    
    public Equipe(int idEquipe, String nom, String couleur, Base base) {
        this.idEquipe = idEquipe;
        this.nom = nom;
        this.couleur = couleur;
        this.base = base;
        this.heros = new ArrayList<>();
        this.minionsActifs = new ArrayList<>();
        this.partie = null;
        this.aSurrendu = false;
        
        // Initialiser les statistiques
        this.toursEnnemiesDetruites = 0;
        this.monstresNeutresTues = 0;
        this.goldTotal = 0;
    }
    
    // Ajouter un héros à l'équipe
    public void ajouterHeros(Heros hero) {
        if (hero != null && !heros.contains(hero)) {
            heros.add(hero);
            hero.setFontaineReapparition(base.obtenirFontaine());
        }
    }
    
    // Retirer un héros de l'équipe
    public void retirerHeros(Heros hero) {
        heros.remove(hero);
        hero.setFontaineReapparition(null);
    }
    
    // Obtenir la base de l'équipe
    public Base obtenirBase() {
        return base;
    }
    
    // Obtenir les héros de l'équipe
    public List<Heros> obtenirHeros() {
        return new ArrayList<>(heros);
    }
    
    // Gérer les scuris actifs
    public void ajouterMinionsActifs(List<Minion> nouveauxMinions) {
        minionsActifs.addAll(nouveauxMinions);
    }
    
    public void retirerMinionsMorts() {
        minionsActifs.removeIf(minion -> !minion.estVivant());
    }
    
    // Mettre à jour l'équipe
    public void mettreAJour(double deltaSecondes) {
        // Mettre à jour la base
        base.mettreAJour(deltaSecondes);
        
        // Mettre à jour les héros
        for (Heros hero : heros) {
            hero.mettreAJourRappel(deltaSecondes);
            hero.mettreAJourCooldowns(deltaSecondes);
        }
        
        // Nettoyer les scuris morts
        retirerMinionsMorts();
        
        // Vérifier la surrendement
        verifierSurrendement();
    }
    
    // Vérifier si l'équipe doit surrendre
    private void verifierSurrendement() {
        if (!aSurrendu) {
            int herosVivants = (int) heros.stream().filter(Heros::estVivant).count();
            int toursDebout = 0;
            
            // Compter les tours alliées encore debout
            // TODO: Implémenter le comptage des tours
            
            // Condition de surrendement: moins de 2 héros vivants et moins de tours que l'ennemi
            if (herosVivants < 2 || toursDebout < 3) {
                surrendre();
            }
        }
    }
    
    // Surrendre
    public void surrendre() {
        aSurrendu = true;
        System.out.println("=== " + nom + " a surrendu! ===");
    }
    
    // Notifier la destruction de la base
    public void notifierBaseDetruite() {
        System.out.println("*** Base de " + nom + " détruite! ***");
    }
    
    // Notifier la réapparition d'un héros
    public void notifierReapparition(Heros hero) {
        System.out.println("+++ " + hero.getNom() + " est réapparu! +++");
    }
    
    // Notifier la fin de partie
    public void notifierFinPartie(Integer equipeGagnanteId) {
        boolean aGagne = equipeGagnanteId != null && equipeGagnanteId.equals(idEquipe);
        
        if (aGagne) {
            System.out.println("*** " + nom + " a gagné la partie! ***");
        } else if (equipeGagnanteId != null) {
            System.out.println("--- " + nom + " a perdu la partie. ---");
        } else {
            System.out.println("=== " + nom + ": match nul. ===");
        }
        
        // Calculer les récompenses de fin de partie
        calculerRecompensesFinPartie(aGagne);
    }
    
    // Calculer les récompenses de fin de partie
    private void calculerRecompensesFinPartie(boolean aGagne) {
        double multiplicateurOr = aGagne ? 1.5 : 0.8;
        
        for (Heros hero : heros) {
            double orBonus = hero.getKills() * 50 + hero.getAssists() * 25;
            orBonus *= multiplicateurOr;
            hero.setOr(hero.getOr() + orBonus);
        }
    }
    
    // Obtenir des statistiques
    public int getToursEnnemiesDetruites() { return toursEnnemiesDetruites; }
    public int getMonstresNeutresTues() { return monstresNeutresTues; }
    public int getGoldTotal() { return goldTotal; }
    
    // Statistiques pour la partie
    public int getToursAllieesRestantes() {
        // TODO: Compter les tours alliées encore debout
        return 3; // Placeholder
    }
    
    public boolean aSurrendu() {
        return aSurrendu;
    }
    
    // Getters et setters principaux
    public int getIdEquipe() { return idEquipe; }
    public String getNom() { return nom; }
    public String getCouleur() { return couleur; }
    public List<Heros> getHeros() { return new ArrayList<>(heros); }
    public List<Minion> getMinionsActifs() { return new ArrayList<>(minionsActifs); }
    public Base getBase() { return base; }
    
    public void setPartie(Partie partie) { 
        this.partie = partie;
        base.setEquipe(this);
    }
    
    public void ajouterToursEnnemiesDetruites(int nombre) {
        this.toursEnnemiesDetruites += nombre;
    }
    
    public void ajouterMonstresNeutresTues(int nombre) {
        this.monstresNeutresTues += nombre;
    }
    
    public void ajouterGoldTotal(double montant) {
        this.goldTotal += montant;
    }
}