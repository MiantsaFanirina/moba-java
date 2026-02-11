package domaine.carte;

import domaine.carte.Voie;
import domaine.carte.Foret;
import domaine.equipe.Equipe;
import domaine.unite.Unite;
import domaine.structures.Tour;
import java.util.ArrayList;
import java.util.List;

/**
 * Arène (map) principale du jeu.
 * Contient les voies, la jungle et gère les unités.
 */
public class Arene {
    
    private int nombreVoies;
    private double delaiReapparitionCreeps;
    private double delaiSpawnMonstresNeutres;
    
    // Composants de l'arène
    private List<Voie> voies;
    private Foret foret;
    private List<Tour> toutesLesTours;
    
    // Dimensions de l'arène
    private double largeur;
    private double hauteur;
    
    public Arene(int nombreVoies, double delaiReapparitionCreeps, double delaiSpawnMonstresNeutres) {
        this.nombreVoies = nombreVoies;
        this.delaiReapparitionCreeps = delaiReapparitionCreeps;
        this.delaiSpawnMonstresNeutres = delaiSpawnMonstresNeutres;
        this.largeur = 2000; // Largeur standard
        this.hauteur = 1500; // Hauteur standard
        
        this.voies = new ArrayList<>();
        this.toutesLesTours = new ArrayList<>();
        
        initialiser();
    }
    
    // Initialiser l'arène
    private void initialiser() {
        creerVoies();
        creerForet();
        creerTours();
    }
    
    // Créer les voies de l'arène
    private void creerVoies() {
        for (int i = 0; i < nombreVoies; i++) {
            String nomVoie;
            double longueurVoie;
            
            switch (i) {
                case 0:
                    nomVoie = "Voie Supérieure";
                    longueurVoie = largeur;
                    break;
                case 1:
                    nomVoie = "Voie Centrale";
                    longueurVoie = largeur;
                    break;
                case 2:
                    nomVoie = "Voie Inférieure";
                    longueurVoie = largeur;
                    break;
                default:
                    nomVoie = "Voie " + (i + 1);
                    longueurVoie = largeur;
                    break;
            }
            
            Voie voie = new Voie(i, nomVoie, longueurVoie);
            voies.add(voie);
        }
    }
    
    // Créer la forêt
    private void creerForet() {
        foret = new Foret("Jungle Principale", delaiSpawnMonstresNeutres);
    }
    
    // Créer les tours de défense
    private void creerTours() {
        // Créer 3 tours par équipe par voie
        for (int voieId = 0; voieId < nombreVoies; voieId++) {
            for (int equipeId = 1; equipeId <= 2; equipeId++) {
                creerTourSurVoie(voieId, equipeId);
            }
        }
    }
    
    // Créer les tours sur une voie spécifique
    private void creerTourSurVoie(int voieId, int equipeId) {
        double x, y;
        int idTour = toutesLesTours.size();
        
        // Positionner les tours selon la voie et l'équipe
        switch (voieId) {
            case 0: // Voie supérieure
                if (equipeId == 1) {
                    x = 200;
                    y = 100;
                } else {
                    x = 1800;
                    y = 1400;
                }
                break;
                
            case 1: // Voie centrale
                if (equipeId == 1) {
                    x = 600;
                    y = 750;
                } else {
                    x = 1400;
                    y = 750;
                }
                break;
                
            case 2: // Voie inférieure
                if (equipeId == 1) {
                    x = 200;
                    y = 1400;
                } else {
                    x = 1800;
                    y = 100;
                }
                break;
                
            default:
                x = equipeId == 1 ? 100 : 1900;
                y = hauteur / 2;
                break;
        }
        
        Tour tour = new Tour(idTour, equipeId, x, y);
        toutesLesTours.add(tour);
        
        // Ajouter la tour à la voie appropriée
        if (voieId < voies.size()) {
            voies.get(voieId).ajouterTour(tour, equipeId);
        }
    }
    
    // Mettre à jour l'arène
    public void mettreAJour(double deltaSecondes, List<Equipe> equipes, List<Unite> toutesUnites) {
        // Mettre à jour les voies
        for (Voie voie : voies) {
            voie.spawnerMinions(System.currentTimeMillis() / 1000.0);
            
            // Rafraîchir les scuris
            List<Unite> unitesAlliees = obtenirUnitesAlliees(voie.getIdVoie(), equipes);
            voie.rafraichirMinions(deltaSecondes, unitesAlliees);
        }
        
        // Mettre à jour la forêt
        foret.mettreAJour(System.currentTimeMillis() / 1000.0);
        
        // Gérer les attaques de monstres sur les héros
        List<domaine.unite.Heros> tousHeros = new ArrayList<>();
        for (Equipe equipe : equipes) {
            tousHeros.addAll(equipe.getHeros());
        }
        foret.gererAttaquesHeros(tousHeros);
        
        // Mettre à jour les tours
        mettreAJourTours(deltaSecondes, toutesUnites);
    }
    
    // Obtenir les unités alliées pour une voie
    private List<Unite> obtenirUnitesAlliees(int idVoie, List<Equipe> equipes) {
        List<Unite> unitesAlliees = new ArrayList<>();
        
        for (Equipe equipe : equipes) {
            // Ajouter les héros de l'équipe
            unitesAlliees.addAll(equipe.getHeros());
            
            // Ajouter les scuris de la voie
            unitesAlliees.addAll(equipe.getMinionsActifs().stream()
                .filter(minion -> estMinionSurVoie(minion, idVoie))
                .toList());
        }
        
        return unitesAlliees;
    }
    
    // Vérifier si un scuri est sur une voie spécifique
    private boolean estMinionSurVoie(domaine.unite.Minion minion, int idVoie) {
        // TODO: Implémenter la logique pour déterminer la voie d'un scuri
        return true; // Placeholder
    }
    
    // Mettre à jour les tours
    private void mettreAJourTours(double deltaSecondes, List<Unite> toutesUnites) {
        for (Tour tour : toutesLesTours) {
            tour.attaquer(System.currentTimeMillis() / 1000.0, toutesUnites);
        }
    }
    
    // Obtenir des informations sur l'arène
    public List<Voie> obtenirVoies() {
        return new ArrayList<>(voies);
    }
    
    public Foret obtenirForet() {
        return foret;
    }
    
    public List<Tour> obtenirToutesLesTours() {
        return new ArrayList<>(toutesLesTours);
    }
    
    public List<Tour> obtenirToursEquipe(int equipeId) {
        return toutesLesTours.stream()
                .filter(tour -> tour.getEquipeId() == equipeId)
                .toList();
    }
    
    // Obtenir des statistiques
    public String obtenirStatutGeneral() {
        long toursEquipe1 = obtenirToursEquipe(1).stream().filter(t -> !t.estDetruite()).count();
        long toursEquipe2 = obtenirToursEquipe(2).stream().filter(t -> !t.estDetruite()).count();
        
        return String.format("Arène: %d voies, Tours Équipe1: %d, Tours Équipe2: %d, %s",
                voies.size(), toursEquipe1, toursEquipe2, foret.obtenirStatut());
    }
    
    // Réinitialiser l'arène pour une nouvelle partie
    public void reinitialiser() {
        voies.clear();
        toutesLesTours.clear();
        initialiser();
    }
    
    // Getters et setters
    public int getNombreVoies() { return nombreVoies; }
    public double getDelaiReapparitionCreeps() { return delaiReapparitionCreeps; }
    public double getDelaiSpawnMonstresNeutres() { return delaiSpawnMonstresNeutres; }
    public double getLargeur() { return largeur; }
    public double getHauteur() { return hauteur; }
    
    public void setDelaiReapparitionCreeps(double delai) { this.delaiReapparitionCreeps = delai; }
    public void setDelaiSpawnMonstresNeutres(double delai) { this.delaiSpawnMonstresNeutres = delai; }
}