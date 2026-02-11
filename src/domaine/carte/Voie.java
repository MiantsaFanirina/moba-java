package domaine.carte;

import domaine.structures.Tour;
import domaine.unite.Minion;
import java.util.ArrayList;
import java.util.List;

/**
 * Voie (lane) du champ de bataille.
 * Contient les tours et gère les scuris.
 */
public class Voie {
    
    private int idVoie;
    private String nom;
    private double longueur;
    private List<Tour> toursEquipe1;
    private List<Tour> toursEquipe2;
    private List<Minion> minionsActifs;
    private double dernierSpawnMinions;
    private double intervalleSpawnMinions;
    
    public Voie(int idVoie, String nom, double longueur) {
        this.idVoie = idVoie;
        this.nom = nom;
        this.longueur = longueur;
        this.toursEquipe1 = new ArrayList<>();
        this.toursEquipe2 = new ArrayList<>();
        this.minionsActifs = new ArrayList<>();
        this.dernierSpawnMinions = 0;
        this.intervalleSpawnMinions = 30.0; // 30 secondes entre les vagues
    }
    
    // Ajouter une tour à une équipe
    public void ajouterTour(Tour tour, int equipeId) {
        if (equipeId == 1) {
            toursEquipe1.add(tour);
        } else {
            toursEquipe2.add(tour);
        }
    }
    
    // Obtenir les tours d'une équipe spécifique
    public List<Tour> obtenirTours(int equipeId) {
        return equipeId == 1 ? new ArrayList<>(toursEquipe1) : new ArrayList<>(toursEquipe2);
    }
    
    // Spawner des scuris
    public void spawnerMinions(double tempsActuel) {
        if (tempsActuel - dernierSpawnMinions >= intervalleSpawnMinions) {
            creerVagueMinions();
            dernierSpawnMinions = tempsActuel;
        }
    }
    
    // Créer une vague de scuris
    private void creerVagueMinions() {
        // Vague pour l'équipe 1
        creerVagueEquipe(1);
        // Vague pour l'équipe 2  
        creerVagueEquipe(2);
    }
    
    private void creerVagueEquipe(int equipeId) {
        // Position de spawn basée sur l'équipe
        double spawnX = equipeId == 1 ? 100 : longueur - 100;
        double spawnY = 200 + idVoie * 150; // Espacement entre voies
        
        // Créer 3 scuris par vague
        for (int i = 0; i < 3; i++) {
            String type = (i == 0) ? "Melee" : (i == 1) ? "Caster" : "Siege";
            Minion minion = new Minion(
                (equipeId * 1000) + (idVoie * 100) + i,
                equipeId,
                spawnX + i * 20,
                spawnY,
                type
            );
            minionsActifs.add(minion);
        }
    }
    
    // Mettre à jour les scuris
    public void rafraichirMinions(double deltaTime, List<domaine.unite.Unite> unitesAlliees) {
        // Nettoyer les scuris morts
        minionsActifs.removeIf(minion -> !minion.estVivant());
        
        // Mettre à jour le comportement des scuris
        for (Minion minion : minionsActifs) {
            Vecteur2 cibleVoie = obtenirCibleVoie(minion.getEquipeId());
            minion.mettreAJourComportement(deltaTime, unitesAlliees, cibleVoie);
        }
    }
    
    // Obtenir la cible de la voie pour une équipe
    private Vecteur2 obtenirCibleVoie(int equipeId) {
        double cibleX = equipeId == 1 ? longueur - 100 : 100;
        double cibleY = 200 + idVoie * 150;
        return new Vecteur2(cibleX, cibleY);
    }
    
    // Vérifier si la voie est contrôlée par une équipe
    public boolean estControleePar(int equipeId) {
        List<Tour> toursEquipe = obtenirTours(equipeId);
        List<Tour> toursEnnemies = obtenirTours(equipeId == 1 ? 2 : 1);
        
        // Compter les tours actives
        long toursEquipeActives = toursEquipe.stream().filter(t -> !t.estDetruite()).count();
        long toursEnnemiesActives = toursEnnemies.stream().filter(t -> !t.estDetruite()).count();
        
        return toursEquipeActives > toursEnnemiesActives;
    }
    
    // Obtenir le statut de la voie
    public String obtenirStatut() {
        long tours1Actives = toursEquipe1.stream().filter(t -> !t.estDetruite()).count();
        long tours2Actives = toursEquipe2.stream().filter(t -> !t.estDetruite()).count();
        
        if (tours1Actives > tours2Actives) {
            return "Contrôlée par Équipe 1";
        } else if (tours2Actives > tours1Actives) {
            return "Contrôlée par Équipe 2";
        } else {
            return "Contestée";
        }
    }
    
    // Getters et setters
    public int getIdVoie() { return idVoie; }
    public String getNom() { return nom; }
    public double getLongueur() { return longueur; }
    public List<Minion> getMinionsActifs() { return new ArrayList<>(minionsActifs); }
    public double getIntervalleSpawnMinions() { return intervalleSpawnMinions; }
    
    public void setIntervalleSpawnMinions(double intervalle) { this.intervalleSpawnMinions = intervalle; }
}