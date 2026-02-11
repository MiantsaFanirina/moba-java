package principal;

import moteur.Vecteur2;
import moteur.formes.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Carte de bataille MOBA avec 3 voies, jungle et objectifs stratégiques.
 */
public class CarteBataille {
    
    // Dimensions de la carte
    private static final double LARGEUR_CARTE = 2400;
    private static final double HAUTEUR_CARTE = 1800;
    private static final double TAILLE_TUILE = 40;
    
    // Composants de la carte
    private List<Forme> obstacles;
    private List<Vecteur2> positionsBases;
    private List<Vecteur2> positionsTours;
    private List<Vecteur2> positionsCampsJungle;
    private List<Vecteur2> positionsObjectifs;
    
    // Zones de la carte
    private List<Rectangle> zonesVoies;
    private List<Rectangle> zonesJungle;
    private List<Cercle> zonesObjectifs;
    
    public CarteBataille() {
        this.obstacles = new ArrayList<>();
        this.positionsBases = new ArrayList<>();
        this.positionsTours = new ArrayList<>();
        this.positionsCampsJungle = new ArrayList<>();
        this.positionsObjectifs = new ArrayList<>();
        
        this.zonesVoies = new ArrayList<>();
        this.zonesJungle = new ArrayList<>();
        this.zonesObjectifs = new ArrayList<>();
        
        initialiserCarte();
    }
    
    // Initialiser la carte complète
    private void initialiserCarte() {
        creerBases();
        creerVoies();
        creerJungle();
        creerObjectifsStrategiques();
        creerObstaclesNaturels();
    }
    
    // Créer les bases des équipes
    private void creerBases() {
        // Base Équipe 1 (en bas à gauche)
        Vecteur2 base1 = new Vecteur2(200, 1400);
        positionsBases.add(base1);
        
        // Base Équipe 2 (en haut à droite)
        Vecteur2 base2 = new Vecteur2(2200, 400);
        positionsBases.add(base2);
        
        // Créer les zones de base
        Rectangle zoneBase1 = new Rectangle(new Vecteur2(100, 1300), 200, 200);
        zoneBase1.setCouleur(new Color(100, 100, 255, 100));
        zoneBase1.setEstRemplie(true);
        zonesObjectifs.add(new Cercle(base1, 150));
        
        Rectangle zoneBase2 = new Rectangle(new Vecteur2(2100, 300), 200, 200);
        zoneBase2.setCouleur(new Color(255, 100, 100, 100));
        zoneBase2.setEstRemplie(true);
        zonesObjectifs.add(new Cercle(base2, 150));
    }
    
    // Créer les 3 voies principales
    private void creerVoies() {
        // Voie Supérieure (Top Lane)
creerVoieSuperieure();
        creerVoieCentrale();
        creerVoieInferieure();
    }
    
    // Voie Superieure
    private void creerVoieSuperieure() {
        // Définir la zone de la voie
        Rectangle voieSup = new Rectangle(new Vecteur2(100, 100), 2200, 300);
        voieSup.setCouleur(new Color(200, 200, 150, 50));
        voieSup.setEstRemplie(true);
        zonesVoies.add(voieSup);
        
        // Tours Équipe 1
        positionsTours.add(new Vecteur2(400, 250));  // Tour 1
        positionsTours.add(new Vecteur2(1000, 250)); // Tour 2
        positionsTours.add(new Vecteur2(1600, 250)); // Tour 3
        
        // Tours Équipe 2
        positionsTours.add(new Vecteur2(800, 250));  // Tour 4
        positionsTours.add(new Vecteur2(1400, 250)); // Tour 5
        positionsTours.add(new Vecteur2(2000, 250)); // Tour 6
    }
    
    // Voie Centrale
    private void creerVoieCentrale() {
        // Définir la zone de la voie
        Rectangle voieMid = new Rectangle(new Vecteur2(900, 750), 600, 300);
        voieMid.setCouleur(new Color(200, 200, 150, 50));
        voieMid.setEstRemplie(true);
        zonesVoies.add(voieMid);
        
        // Tours Équipe 1
        positionsTours.add(new Vecteur2(1000, 900)); // Tour 7
        
        // Tours Équipe 2
        positionsTours.add(new Vecteur2(1400, 900)); // Tour 8
    }
    
    // Voie Inferieure
    private void creerVoieInferieure() {
        // Définir la zone de la voie
        Rectangle voieBot = new Rectangle(new Vecteur2(100, 1400), 2200, 300);
        voieBot.setCouleur(new Color(200, 200, 150, 50));
        voieBot.setEstRemplie(true);
        zonesVoies.add(voieBot);
        
        // Tours Équipe 1
        positionsTours.add(new Vecteur2(400, 1550)); // Tour 9
        positionsTours.add(new Vecteur2(1000, 1550)); // Tour 10
        positionsTours.add(new Vecteur2(1600, 1550)); // Tour 11
        
        // Tours Équipe 2
        positionsTours.add(new Vecteur2(800, 1550)); // Tour 12
        positionsTours.add(new Vecteur2(1400, 1550)); // Tour 13
        positionsTours.add(new Vecteur2(2000, 1550)); // Tour 14
    }
    
    // Créer la jungle
    private void creerJungle() {
        // Jungle Superieure
        Rectangle jungleSup = new Rectangle(new Vecteur2(100, 400), 800, 300);
        jungleSup.setCouleur(new Color(50, 150, 50, 100));
        jungleSup.setEstRemplie(true);
        zonesJungle.add(jungleSup);
        
        // Jungle Inferieure
        Rectangle jungleInf = new Rectangle(new Vecteur2(1500, 1100), 800, 300);
        jungleInf.setCouleur(new Color(50, 150, 50, 100));
        jungleInf.setEstRemplie(true);
        zonesJungle.add(jungleInf);
        
        // Jungle Centrale
        Rectangle jungleCentre = new Rectangle(new Vecteur2(900, 400), 600, 400);
        jungleCentre.setCouleur(new Color(50, 150, 50, 100));
        jungleCentre.setEstRemplie(true);
        zonesJungle.add(jungleCentre);
        
        // Créer les camps de jungle
        creerCampsJungle();
    }
    
    // Créer les camps de jungle
    private void creerCampsJungle() {
// Jungle Superieure
        positionsCampsJungle.add(new Vecteur2(300, 500));  // Camp Loups
        positionsCampsJungle.add(new Vecteur2(600, 550));  // Camp Golems
        positionsCampsJungle.add(new Vecteur2(450, 650));  // Camp Buff
        
        // Jungle Inferieure
        positionsCampsJungle.add(new Vecteur2(2100, 1250)); // Camp Loups
        positionsCampsJungle.add(new Vecteur2(1800, 1200)); // Camp Golems
        positionsCampsJungle.add(new Vecteur2(1950, 1100)); // Camp Buff
        
        // Jungle Centrale
        positionsCampsJungle.add(new Vecteur2(1200, 600));  // Camp Buff Rouge
        positionsCampsJungle.add(new Vecteur2(1200, 1000)); // Camp Buff Bleu
    }
    
    // Créer les objectifs stratégiques
    private void creerObjectifsStrategiques() {
        // Lord (Baron) - en haut
        Vecteur2 lordPosition = new Vecteur2(1200, 200);
        positionsObjectifs.add(lordPosition);
        Cercle zoneLord = new Cercle(lordPosition, 100);
        zoneLord.setCouleur(new Color(150, 50, 200, 150));
        zoneLord.setEstRemplie(true);
        zonesObjectifs.add(zoneLord);
        
        // Turtle (Dragon) - en bas
        Vecteur2 turtlePosition = new Vecteur2(1200, 1600);
        positionsObjectifs.add(turtlePosition);
        Cercle zoneTurtle = new Cercle(turtlePosition, 80);
        zoneTurtle.setCouleur(new Color(50, 150, 200, 150));
        zoneTurtle.setEstRemplie(true);
        zonesObjectifs.add(zoneTurtle);
    }
    
    // Créer les obstacles naturels
    private void creerObstaclesNaturels() {
        // Arbres dans la jungle
        creerArbresJungle();
        
        // Rochers sur les voies
        creerRochersVoies();
        
        // Murs décoratifs
        creerMurs();
    }
    
    // Créer des arbres dans la jungle
    private void creerArbresJungle() {
        for (int i = 0; i < 50; i++) {
            double x = 100 + Math.random() * 2200;
            double y = 100 + Math.random() * 1600;
            
            // Éviter les voies principales
            if (!estSurVoie(x, y)) {
                Cercle arbre = new Cercle(new Vecteur2(x, y), 15 + Math.random() * 10);
                arbre.setCouleur(new Color(34, 139, 34));
                arbre.setEstRemplie(true);
                obstacles.add(arbre);
            }
        }
    }
    
    // Créer des rochers sur les voies
    private void creerRochersVoies() {
        // Rochers stratégiques près des tours
        for (Vecteur2 tourPosition : positionsTours) {
            if (Math.random() > 0.7) { // 30% de chance
                double offsetX = (Math.random() - 0.5) * 100;
                double offsetY = (Math.random() - 0.5) * 100;
                
                Rectangle rocher = new Rectangle(
                    new Vecteur2(tourPosition.x + offsetX, tourPosition.y + offsetY),
                    30, 30
                );
                rocher.setCouleur(new Color(128, 128, 128));
                rocher.setEstRemplie(true);
                obstacles.add(rocher);
            }
        }
    }
    
    // Créer des murs décoratifs
    private void creerMurs() {
        // Murs autour des bases
        creerMursBase(positionsBases.get(0));
        creerMursBase(positionsBases.get(1));
    }
    
    // Créer des murs autour d'une base
    private void creerMursBase(Vecteur2 basePosition) {
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            double distance = 180;
            
            Vecteur2 murPosition = new Vecteur2(
                basePosition.x + distance * Math.cos(angle),
                basePosition.y + distance * Math.sin(angle)
            );
            
            Rectangle mur = new Rectangle(murPosition, 40, 20);
            mur.setCouleur(new Color(100, 100, 100));
            mur.setEstRemplie(true);
            obstacles.add(mur);
        }
    }
    
    // Vérifier si une position est sur une voie
    private boolean estSurVoie(double x, double y) {
        // Voie Supérieure
        if (y >= 100 && y <= 400) return true;
        // Voie Centrale
        if (y >= 750 && y <= 1050 && x >= 900 && x <= 1500) return true;
        // Voie Inférieure
        if (y >= 1400 && y <= 1700) return true;
        
        return false;
    }
    
    // Obtenir les informations de la carte
    public List<Forme> getObstacles() { return new ArrayList<>(obstacles); }
    public List<Vecteur2> getPositionsBases() { return new ArrayList<>(positionsBases); }
    public List<Vecteur2> getPositionsTours() { return new ArrayList<>(positionsTours); }
    public List<Vecteur2> getPositionsCampsJungle() { return new ArrayList<>(positionsCampsJungle); }
    public List<Vecteur2> getPositionsObjectifs() { return new ArrayList<>(positionsObjectifs); }
    
    public List<Rectangle> getZonesVoies() { return new ArrayList<>(zonesVoies); }
    public List<Rectangle> getZonesJungle() { return new ArrayList<>(zonesJungle); }
    public List<Cercle> getZonesObjectifs() { return new ArrayList<>(zonesObjectifs); }
    
    public double getLargeurCarte() { return LARGEUR_CARTE; }
    public double getHauteurCarte() { return HAUTEUR_CARTE; }
    public double getTailleTuile() { return TAILLE_TUILE; }
    
    // Obtenir le spawn d'une équipe
    public Vecteur2 obtenirSpawnEquipe(int equipeId) {
        return equipeId == 1 ? positionsBases.get(0) : positionsBases.get(1);
    }
}