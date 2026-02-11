package domaine.carte;

import domaine.unite.Creep;
import moteur.Vecteur2;
import java.util.ArrayList;
import java.util.List;

/**
 * Forêt (jungle) contenant les camps de monstres neutres.
 * Gère l'apparition et le comportement des creeps.
 */
public class Foret {
    
    private List<Vecteur2> positionsCamp;
    private List<Creep> creepsActifs;
    private double delaiSpawnMonstresNeutres;
    private double dernierSpawnGlobal;
    private String nom;
    
    public Foret(String nom, double delaiSpawnMonstresNeutres) {
        this.nom = nom;
        this.delaiSpawnMonstresNeutres = delaiSpawnMonstresNeutres;
        this.positionsCamp = new ArrayList<>();
        this.creepsActifs = new ArrayList<>();
        this.dernierSpawnGlobal = 0;
        
        initialiserPositionsCamps();
        spawnerCreepsInitiaux();
    }
    
    // Initialiser les positions des camps de monstres
    private void initialiserPositionsCamps() {
        // Camps dans la jungle supérieure
        positionsCamp.add(new Vecteur2(800, 300));
        positionsCamp.add(new Vecteur2(1200, 400));
        positionsCamp.add(new Vecteur2(1000, 500));
        
        // Camps dans la jungle inférieure
        positionsCamp.add(new Vecteur2(800, 1400));
        positionsCamp.add(new Vecteur2(400, 1200));
        positionsCamp.add(new Vecteur2(1200, 1400));
        
        // Camps centraux (buffs)
        positionsCamp.add(new Vecteur2(600, 800));
        positionsCamp.add(new Vecteur2(1400, 800));
        
        // Camps de dragon/baron
        positionsCamp.add(new Vecteur2(1000, 200));
        positionsCamp.add(new Vecteur2(1000, 1500));
    }
    
    // Spawner les monstres initiaux
    private void spawnerCreepsInitiaux() {
        for (int i = 0; i < positionsCamp.size(); i++) {
            Vecteur2 position = positionsCamp.get(i);
            Creep creep = creerCreepPourPosition(position, i);
            creepsActifs.add(creep);
        }
    }
    
    // Créer le type de creep approprié pour une position
    private Creep creerCreepPourPosition(Vecteur2 position, int indexCamp) {
        String type;
        
        // Camps centraux = loups
        if ((indexCamp == 6 || indexCamp == 7)) {
            type = "Loup";
        }
        // Camps de dragon/baron
        else if ((indexCamp == 8 || indexCamp == 9)) {
            type = indexCamp == 8 ? "Dragon" : "Golem";
        }
        // Camps normaux = golems
        else {
            type = (indexCamp % 2 == 0) ? "Golem" : "Loup";
        }
        
        return new Creep(indexCamp, position.x, position.y, type);
    }
    
    // Gérer le spawn automatique des monstres
    public void mettreAJour(double tempsActuel) {
        // Nettoyer les creeps morts
        nettoyerCreepsMorts();
        
        // Tenter de faire réapparaître les creeps
        for (Creep creep : creepsActifs) {
            if (!creep.estVivant()) {
                if (creep.tenterReapparition()) {
                    // Le creep a réapparu, ne pas le supprimer
                    continue;
                } else {
                    // Le creep ne peut pas encore réapparaître
                }
            }
        }
        
        // Spawn global de nouveaux monstres
        if (tempsActuel - dernierSpawnGlobal >= delaiSpawnMonstresNeutres) {
            spawnNouveauxMonstres();
            dernierSpawnGlobal = tempsActuel;
        }
        
        // Mettre à jour le comportement des creeps
        for (Creep creep : creepsActifs) {
            if (creep.estVivant()) {
                creep.patrouiller(0.016); // ~60 FPS
            }
        }
    }
    
    // Nettoyer les creeps qui sont morts depuis longtemps
    private void nettoyerCreepsMorts() {
        creepsActifs.removeIf(creep -> 
            !creep.estVivant() && creep.getTempsProchaineReapparition() <= -60
        );
    }
    
    // Spawn de nouveaux monstres selon le besoin
    private void spawnNouveauxMonstres() {
        for (int i = 0; i < positionsCamp.size(); i++) {
            Vecteur2 position = positionsCamp.get(i);
            boolean campOccupe = false;
            
            // Vérifier si un creep est déjà à cette position
            for (Creep creep : creepsActifs) {
                if (creep.estVivant() && 
                    creep.getPositionCamp().distance(position) < 50) {
                    campOccupe = true;
                    break;
                }
            }
            
            // Spawner un nouveau creep si le camp est libre
            if (!campOccupe) {
                Creep nouveauCreep = creerCreepPourPosition(position, i);
                creepsActifs.add(nouveauCreep);
            }
        }
    }
    
    // Obtenir les creeps à proximité d'une position
    public List<Creep> obtenirCreepsProches(Vecteur2 position, double rayon) {
        List<Creep> creepsProches = new ArrayList<>();
        
        for (Creep creep : creepsActifs) {
            if (creep.estVivant() && 
                creep.getPosition().distance(position) <= rayon) {
                creepsProches.add(creep);
            }
        }
        
        return creepsProches;
    }
    
    // Réinitialiser la forêt pour une nouvelle partie
    public void reinitialiser() {
        creepsActifs.clear();
        dernierSpawnGlobal = 0;
        spawnerCreepsInitiaux();
    }
    
    // Gérer l'attaque des héros sur les creeps
    public void gererAttaquesHeros(java.util.List<domaine.unite.Heros> heros) {
        for (Creep creep : creepsActifs) {
            if (!creep.estVivant()) continue;
            
            for (domaine.unite.Heros hero : heros) {
                if (hero.estVivant()) {
                    creep.verifierEtAttaquer(hero);
                }
            }
        }
    }
    
    // Obtenir des informations sur l'état de la forêt
    public String obtenirStatut() {
        long creepsVivants = creepsActifs.stream().filter(Creep::estVivant).count();
        long campsOccupes = positionsCamp.stream().mapToLong(pos -> 
            creepsActifs.stream().filter(creep -> 
                creep.estVivant() && creep.getPositionCamp().distance(pos) < 50
            ).count()
        ).sum();
        
        return String.format("Forêt: %d/%d creeps vivants, %d/%d camps occupés", 
            creepsVivants, creepsActifs.size(), campsOccupes, positionsCamp.size());
    }
    
    // Getters et setters
    public List<Vecteur2> getPositionsCamp() { return new ArrayList<>(positionsCamp); }
    public List<Creep> getCreepsActifs() { return new ArrayList<>(creepsActifs); }
    public double getDelaiSpawnMonstresNeutres() { return delaiSpawnMonstresNeutres; }
    public String getNom() { return nom; }
    
    public void setDelaiSpawnMonstresNeutres(double delai) { this.delaiSpawnMonstresNeutres = delai; }
}