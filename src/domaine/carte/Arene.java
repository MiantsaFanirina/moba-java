package domaine.carte;

import domaine.carte.Voie;
import domaine.equipe.Equipe;
import domaine.unite.Unite;
import domaine.structures.Tour;
import domaine.structures.Base;
import domaine.structures.Fontaine;
import moteur.Vecteur2;
import moteur.formes.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Nouvelle arène (map) avec 3 voies, jungle entre les voies, et bases opposées.
 */
public class Arene {
    
    private static final double LARGEUR = 2400;
    private static final double HAUTEUR = 1800;
    
    // Composants de l'arène
    private List<Voie> voies;
    private Base baseEquipe1;
    private Base baseEquipe2;
    private Fontaine fontaineEquipe1;
    private Fontaine fontaineEquipe2;
    private List<Tour> toursEquipe1;
    private List<Tour> toursEquipe2;
    
    // Éléments de collision
    private List<Forme> obstacles;
    private List<Rectangle> zonesInterdites;
    
    // Zones de la carte
    private Rectangle voieSuperieure;
    private Rectangle voieCentrale;
    private Rectangle voieInferieure;
    private List<Rectangle> zonesJungle;
    
    public Arene() {
        this.voies = new ArrayList<>();
        this.toursEquipe1 = new ArrayList<>();
        this.toursEquipe2 = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        this.zonesInterdites = new ArrayList<>();
        this.zonesJungle = new ArrayList<>();
        
        initialiser();
    }
    
    private void initialiser() {
        creerBases();
        creerVoies();
        creerJungle();
        creerObstacles();
        creerTours();
    }
    
    // Créer les bases avec cores et fontaines
    private void creerBases() {
        // Base Équipe 1 (à gauche)
        baseEquipe1 = new Base(1, null, 100, HAUTEUR/2);
        fontaineEquipe1 = new Fontaine(1, null, 200, HAUTEUR/2);
        
        // Base Équipe 2 (à droite)
        baseEquipe2 = new Base(2, null, LARGEUR-100, HAUTEUR/2);
        fontaineEquipe2 = new Fontaine(2, null, LARGEUR-200, HAUTEUR/2);
        
        // Zones de base (interdites aux joueurs)
        Rectangle zoneBase1 = new Rectangle(new Vecteur2(50, HAUTEUR/2 - 100), 200, 200);
        zoneBase1.setCouleur(new java.awt.Color(100, 100, 255, 100));
        zoneBase1.setEstRemplie(true);
        zonesInterdites.add(zoneBase1);
        
        Rectangle zoneBase2 = new Rectangle(new Vecteur2(LARGEUR-250, HAUTEUR/2 - 100), 200, 200);
        zoneBase2.setCouleur(new java.awt.Color(255, 100, 100, 100));
        zoneBase2.setEstRemplie(true);
        zonesInterdites.add(zoneBase2);
    }
    
    // Créer les 3 voies
    private void creerVoies() {
        // Voie Supérieure (Top)
        voieSuperieure = new Rectangle(new Vecteur2(0, 0), LARGEUR, HAUTEUR/3);
        voieSuperieure.setCouleur(new java.awt.Color(200, 200, 150, 30));
        voieSuperieure.setEstRemplie(true);
        
        // Voie Centrale (Mid)
        voieCentrale = new Rectangle(new Vecteur2(0, HAUTEUR/3), LARGEUR, HAUTEUR/3);
        voieCentrale.setCouleur(new java.awt.Color(200, 200, 150, 30));
        voieCentrale.setEstRemplie(true);
        
        // Voie Inférieure (Bot)
        voieInferieure = new Rectangle(new Vecteur2(0, 2*HAUTEUR/3), LARGEUR, HAUTEUR/3);
        voieInferieure.setCouleur(new java.awt.Color(200, 200, 150, 30));
        voieInferieure.setEstRemplie(true);
        
        // Créer les objets Voie
        voies.add(new Voie(0, "Voie Supérieure", LARGEUR));
        voies.add(new Voie(1, "Voie Centrale", LARGEUR));
        voies.add(new Voie(2, "Voie Inférieure", LARGEUR));
    }
    
    // Créer la jungle entre les voies
    private void creerJungle() {
        // Jungle entre voie sup et centrale
        Rectangle jungle1 = new Rectangle(new Vecteur2(200, HAUTEUR/3 - 50), LARGEUR - 400, 100);
        jungle1.setCouleur(new java.awt.Color(50, 150, 50, 50));
        jungle1.setEstRemplie(true);
        zonesJungle.add(jungle1);
        
        // Jungle entre voie centrale et inférieure
        Rectangle jungle2 = new Rectangle(new Vecteur2(200, 2*HAUTEUR/3 - 50), LARGEUR - 400, 100);
        jungle2.setCouleur(new java.awt.Color(50, 150, 50, 50));
        jungle2.setEstRemplie(true);
        zonesJungle.add(jungle2);
        
        // Jungle latérales
        Rectangle jungleGauche = new Rectangle(new Vecteur2(0, HAUTEUR/3), 200, HAUTEUR/3);
        jungleGauche.setCouleur(new java.awt.Color(50, 150, 50, 50));
        jungleGauche.setEstRemplie(true);
        zonesJungle.add(jungleGauche);
        
        Rectangle jungleDroite = new Rectangle(new Vecteur2(LARGEUR-200, HAUTEUR/3), 200, HAUTEUR/3);
        jungleDroite.setCouleur(new java.awt.Color(50, 150, 50, 50));
        jungleDroite.setEstRemplie(true);
        zonesJungle.add(jungleDroite);
    }
    
    // Créer les obstacles ultra-complexes avec des formes simples
    private void creerObstacles() {
        // Arbres complexes dans la jungle
        creerArbresComplexes();
        
        // Rochers géométriques sur les voies
        creerRochersComplexes();
        
        // Structures défensives élaborées
        creerStructuresDefensives();
        
        // Éléments décoratifs complexes
        creerElementsDecoratifs();
    }
    
    // Créer des arbres avec formes complexes
    private void creerArbresComplexes() {
        for (Rectangle zoneJungle : zonesJungle) {
            int nombreArbres = (int)(zoneJungle.largeur * zoneJungle.hauteur / 8000);
            for (int i = 0; i < nombreArbres; i++) {
                double x = zoneJungle.x + Math.random() * zoneJungle.largeur;
                double y = zoneJungle.y + Math.random() * zoneJungle.hauteur;
                creerArbreComplexe(x, y);
            }
        }
    }
    
    // Créer un arbre complexe
    private void creerArbreComplexe(double x, double y) {
        // Tronc principal
        Rectangle tronc = new Rectangle(new Vecteur2(x-3, y-5), 6, 15);
        tronc.setCouleur(new Color(101, 67, 33));
        tronc.setEstRemplie(true);
        obstacles.add(tronc);
        
        // Couronne avec plusieurs formes
        FormesComplexes.Fleur couronne = new FormesComplexes.Fleur(
            new Vecteur2(x, y-10), 8, 8, 15, new Color(34, 139, 34)
        );
        obstacles.add(couronne);
        
        // Racines partielles
        for (int i = 0; i < 3; i++) {
            double offsetX = (Math.random() - 0.5) * 20;
            Rectangle racine = new Rectangle(new Vecteur2(x + offsetX, y + 5), 2, 8);
            racine.setCouleur(new Color(80, 50, 20));
            racine.setEstRemplie(true);
            obstacles.add(racine);
        }
    }
    
    // Créer des rochers complexes
    private void creerRochersComplexes() {
        creerRochersVoie(voieSuperieure, 12);
        creerRochersVoie(voieCentrale, 8);
        creerRochersVoie(voieInferieure, 12);
    }
    
    private void creerRochersVoie(Rectangle voie, int nombre) {
        for (int i = 0; i < nombre; i++) {
            double x = voie.x + Math.random() * voie.largeur;
            double y = voie.y + Math.random() * voie.hauteur;
            
            // Rocher hexagonal complexe
            FormesComplexes.Hexagone rocher = new FormesComplexes.Hexagone(
                new Vecteur2(x, y), 8 + Math.random() * 8, 
                new Color(128, 128, 128), true
            );
            obstacles.add(rocher);
            
            // Ajouter des cristaux
            if (Math.random() > 0.6) {
                FormesComplexes.Etoile cristal = new FormesComplexes.Etoile(
                    new Vecteur2(x + 10, y - 5), 4, 3, 6,
                    new Color(150, 150, 255)
                );
                obstacles.add(cristal);
            }
        }
    }
    
    // Créer des structures défensives élaborées
    private void creerStructuresDefensives() {
        creerMursDefensifs();
        creerToursAmeliorees();
        creerBarrieresJungle();
    }
    
    // Murs défensifs améliorés
    private void creerMursDefensifs() {
        creerMurBase(baseEquipe1, 1);
        creerMurBase(baseEquipe2, 2);
    }
    
    private void creerMurBase(Base base, int equipeId) {
        Vecteur2 centre = equipeId == 1 ? 
            new Vecteur2(150, HAUTEUR/2) : 
            new Vecteur2(LARGEUR-150, HAUTEUR/2);
        
        // Créer un mur en spirale complexe
        FormesComplexes.Spirale murSpirale = new FormesComplexes.Spirale(
            centre, 100, 3, 50, 
            equipeId == 1 ? new Color(100, 100, 200) : new Color(200, 100, 100)
        );
        obstacles.add(murSpirale);
        
        // Piliers décoratifs
        for (int i = 0; i < 6; i++) {
            double angle = i * Math.PI / 3;
            Vecteur2 posPilier = new Vecteur2(
                centre.x + 120 * Math.cos(angle),
                centre.y + 120 * Math.sin(angle)
            );
            
            FormesComplexes.Losange pilier = new FormesComplexes.Losange(
                posPilier, 15, 25,
                equipeId == 1 ? new Color(120, 120, 220) : new Color(220, 120, 120),
                true
            );
            obstacles.add(pilier);
            zonesInterdites.add(new Rectangle(new Vecteur2(posPilier.x - 10, posPilier.y - 15), 20, 30));
        }
    }
    
    // Tours améliorées
    private void creerToursAmeliorees() {
        for (int i = 0; i < toursEquipe1.size(); i++) {
            creerTourAmelioree(toursEquipe1.get(i), 1);
        }
        for (int i = 0; i < toursEquipe2.size(); i++) {
            creerTourAmelioree(toursEquipe2.get(i), 2);
        }
    }
    
    private void creerTourAmelioree(Tour tour, int equipeId) {
        Vecteur2 pos = tour.getPosition();
        
        // Base triangulaire arrondie
        FormesComplexes.TriangleArrondi base = new FormesComplexes.TriangleArrondi(
            pos, 25, 5,
            equipeId == 1 ? new Color(100, 100, 255) : new Color(255, 100, 100),
            true
        );
        obstacles.add(base);
        
        // Cristaux énergétiques rotatifs
        double temps = System.currentTimeMillis() / 1000.0;
        for (int j = 0; j < 3; j++) {
            double angle = temps * 2 + (j * 2 * Math.PI / 3);
            Vecteur2 posCristal = new Vecteur2(
                pos.x + 30 * Math.cos(angle),
                pos.y + 30 * Math.sin(angle)
            );
            
            FormesComplexes.Etoile cristal = new FormesComplexes.Etoile(
                posCristal, 6, 4, 8,
                new Color(200, 200 + j * 20, 255)
            );
            obstacles.add(cristal);
        }
    }
    
    // Barrières de jungle
    private void creerBarrieresJungle() {
        for (Rectangle zoneJungle : zonesJungle) {
            int nombreBarrieres = 3;
            for (int i = 0; i < nombreBarrieres; i++) {
                double x = zoneJungle.x + (i + 1) * zoneJungle.largeur / (nombreBarrieres + 1);
                double y = zoneJungle.y + zoneJungle.hauteur / 2;
                
                // Barrière en forme de croix complexe
                FormesComplexes.Croix barriere = new FormesComplexes.Croix(
                    new Vecteur2(x, y), 20, 3, new Color(139, 69, 19)
                );
                obstacles.add(barriere);
                
                // Ajouter des chaînes
                for (int j = 0; j < 2; j++) {
                    double offsetY = (j - 0.5) * 40;
                    Rectangle chaine = new Rectangle(new Vecteur2(x - 2, y + offsetY), 4, 15);
                    chaine.setCouleur(new Color(100, 50, 0));
                    chaine.setEstRemplie(true);
                    obstacles.add(chaine);
                }
            }
        }
    }
    
    // Éléments décoratifs complexes
    private void creerElementsDecoratifs() {
        creerFontainesComplexes();
        creerStatuesJungle();
    }
    
    // Fontaines améliorées
    private void creerFontainesComplexes() {
        // Fontaine Équipe 1
        Vecteur2 posFont1 = fontaineEquipe1.getPosition();
        creerFontaineComplexe(posFont1, 1);
        
        // Fontaine Équipe 2
        Vecteur2 posFont2 = fontaineEquipe2.getPosition();
        creerFontaineComplexe(posFont2, 2);
    }
    
    private void creerFontaineComplexe(Vecteur2 position, int equipeId) {
        // Base octogonale
        FormesComplexes.Hexagone base = new FormesComplexes.Hexagone(
            position, 20, 
            equipeId == 1 ? new Color(100, 150, 255) : new Color(255, 150, 100),
            true
        );
        obstacles.add(base);
        
        // Jets d'eau animés
        double temps = System.currentTimeMillis() / 1000.0;
        for (int i = 0; i < 8; i++) {
            double angle = (i * Math.PI / 4) + temps;
            Vecteur2 posGoutte = new Vecteur2(
                position.x + 25 * Math.cos(angle),
                position.y + 25 * Math.sin(angle)
            );
            
            Cercle goutte = new Cercle(posGoutte, 2 + Math.sin(temps * 5 + i) * 1);
            goutte.setCouleur(new Color(150, 200, 255, 180));
            goutte.setEstRemplie(true);
            obstacles.add(goutte);
        }
    }
    
    // Statues dans la jungle
    private void creerStatuesJungle() {
        int nombreStatues = 4;
        for (int i = 0; i < nombreStatues; i++) {
            double x = 400 + i * 400;
            double y = 450 + (i % 2) * 900;
            
            // Statue avec plusieurs formes
            Rectangle base = new Rectangle(new Vecteur2(x - 10, y + 20), 20, 8);
            base.setCouleur(new Color(80, 80, 80));
            base.setEstRemplie(true);
            obstacles.add(base);
            
            // Corps triangulaire
            FormesComplexes.TriangleArrondi corps = new FormesComplexes.TriangleArrondi(
                new Vecteur2(x, y), 18, 3, new Color(150, 150, 150), true
            );
            obstacles.add(corps);
            
            // Aura mystique
            FormesComplexes.Spirale aura = new FormesComplexes.Spirale(
                new Vecteur2(x, y), 25, 2, 30, new Color(200, 150, 255, 100)
            );
            obstacles.add(aura);
        }
    }
        }
        
        // Rochers sur les voies
        creerRochersVoie(voieSuperieure, 15);
        creerRochersVoie(voieCentrale, 10);
        creerRochersVoie(voieInferieure, 15);
        
        // Murs défensifs complexes
        creerMursDefensifs();
    }
    
    private void creerRochersVoie(Rectangle voie, int nombre) {
        for (int i = 0; i < nombre; i++) {
            double x = voie.x + Math.random() * voie.largeur;
            double y = voie.y + Math.random() * voie.hauteur;
            
            // Rocher composé de plusieurs rectangles
            Rectangle base = new Rectangle(new Vecteur2(x, y), 25, 20);
            base.setCouleur(new java.awt.Color(128, 128, 128));
            base.setEstRemplie(true);
            obstacles.add(base);
            
            Rectangle pic = new Rectangle(new Vecteur2(x+5, y-5), 15, 10);
            pic.setCouleur(new java.awt.Color(160, 160, 160));
            pic.setEstRemplie(true);
            obstacles.add(pic);
        }
    }
    
    private void creerMursDefensifs() {
        // Murs autour de chaque base (formes complexes)
        creerMurBase(baseEquipe1, 1);
        creerMurBase(baseEquipe2, 2);
    }
    
    private void creerMurBase(Base base, int equipeId) {
        Vecteur2 centre = equipeId == 1 ? 
            new Vecteur2(150, HAUTEUR/2) : 
            new Vecteur2(LARGEUR-150, HAUTEUR/2);
        
        // Créer un mur hexagonal
        for (int i = 0; i < 6; i++) {
            double angle = i * Math.PI / 3;
            double x1 = centre.x + 80 * Math.cos(angle);
            double y1 = centre.y + 80 * Math.sin(angle);
            double x2 = centre.x + 80 * Math.cos(angle + Math.PI/3);
            double y2 = centre.y + 80 * Math.sin(angle + Math.PI/3);
            
            Rectangle mur = new Rectangle(new Vecteur2((x1+x2)/2 - 20, (y1+y2)/2 - 10), 40, 20);
            mur.setCouleur(equipeId == 1 ? 
                new java.awt.Color(100, 100, 200) : 
                new java.awt.Color(200, 100, 100));
            mur.setEstRemplie(true);
            obstacles.add(mur);
            zonesInterdites.add(mur);
        }
    }
    
    // Créer les tours de défense
    private void creerTours() {
        // Tours Équipe 1
        creerTourEquipe(1, 0, 300, HAUTEUR/6);      // Voie sup
        creerTourEquipe(1, 0, 600, HAUTEUR/6);      // Voie sup
        creerTourEquipe(1, 1, 400, HAUTEUR/2);      // Voie mid
        creerTourEquipe(1, 2, 300, 5*HAUTEUR/6);   // Voie bot
        creerTourEquipe(1, 2, 600, 5*HAUTEUR/6);   // Voie bot
        
        // Tours Équipe 2
        creerTourEquipe(2, 0, LARGEUR-300, HAUTEUR/6);    // Voie sup
        creerTourEquipe(2, 0, LARGEUR-600, HAUTEUR/6);    // Voie sup
        creerTourEquipe(2, 1, LARGEUR-400, HAUTEUR/2);    // Voie mid
        creerTourEquipe(2, 2, LARGEUR-300, 5*HAUTEUR/6); // Voie bot
        creerTourEquipe(2, 2, LARGEUR-600, 5*HAUTEUR/6); // Voie bot
    }
    
    private void creerTourEquipe(int equipeId, int voieId, double x, double y) {
        Tour tour = new Tour(toursEquipe1.size() + toursEquipe2.size(), equipeId, x, y);
        
        if (equipeId == 1) {
            toursEquipe1.add(tour);
        } else {
            toursEquipe2.add(tour);
        }
        
        // Ajouter la zone de la tour comme obstacle
        Rectangle zoneTour = new Rectangle(new Vecteur2(x-30, y-30), 60, 60);
        zoneTour.setCouleur(equipeId == 1 ? 
            new java.awt.Color(100, 100, 255, 100) : 
            new java.awt.Color(255, 100, 100, 100));
        zoneTour.setEstRemplie(true);
        obstacles.add(zoneTour);
        zonesInterdites.add(zoneTour);
    }
    
    // Vérification des collisions
    public boolean estPositionValide(Vecteur2 position, double rayon) {
        Rectangle zone = new Rectangle(
            new Vecteur2(position.x - rayon, position.y - rayon), 
            rayon * 2, 
            rayon * 2
        );
        
        // Vérifier les obstacles
        for (Forme obstacle : obstacles) {
            if (obstacle instanceof Rectangle) {
                Rectangle rect = (Rectangle) obstacle;
                if (zone.intersecte(rect)) {
                    return false;
                }
            } else if (obstacle instanceof Cercle) {
                Cercle cercle = (Cercle) obstacle;
                double distance = position.distance(cercle.getCentre());
                if (distance < rayon + cercle.getRayon()) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public boolean estZoneInterdite(Vecteur2 position, double rayon) {
        Rectangle zone = new Rectangle(
            new Vecteur2(position.x - rayon, position.y - rayon), 
            rayon * 2, 
            rayon * 2
        );
        
        for (Rectangle zoneInterdite : zonesInterdites) {
            if (zone.intersecte(zoneInterdite)) {
                return true;
            }
        }
        return false;
    }
    
    // Pathfinding simple avec évitement d'obstacles
    public List<Vecteur2> trouverChemin(Vecteur2 debut, Vecteur2 fin, double rayon) {
        List<Vecteur2> chemin = new ArrayList<>();
        
        // Chemin direct si possible
        if (estCheminLibre(debut, fin, rayon)) {
            chemin.add(fin);
            return chemin;
        }
        
        // Sinon, contourner les obstacles
        chemin.addAll(contournerObstacles(debut, fin, rayon));
        return chemin;
    }
    
    private boolean estCheminLibre(Vecteur2 debut, Vecteur2 fin, double rayon) {
        double steps = 20;
        for (int i = 0; i <= steps; i++) {
            double t = i / steps;
            Vecteur2 point = new Vecteur2(
                debut.x + t * (fin.x - debut.x),
                debut.y + t * (fin.y - debut.y)
            );
            
            if (!estPositionValide(point, rayon)) {
                return false;
            }
        }
        return true;
    }
    
    private List<Vecteur2> contournerObstacles(Vecteur2 debut, Vecteur2 fin, double rayon) {
        List<Vecteur2> chemin = new ArrayList<>();
        
        // Essayer plusieurs points intermédiaires
        Vecteur2[] pointsIntermediaires = {
            new Vecteur2(debut.x, fin.y),
            new Vecteur2(fin.x, debut.y),
            new Vecteur2(LARGEUR/2, debut.y),
            new Vecteur2(debut.x, HAUTEUR/2),
            new Vecteur2(LARGEUR/2, HAUTEUR/2)
        };
        
        for (Vecteur2 intermediaire : pointsIntermediaires) {
            if (estPositionValide(intermediaire, rayon)) {
                if (estCheminLibre(debut, intermediaire, rayon) && 
                    estCheminLibre(intermediaire, fin, rayon)) {
                    chemin.add(intermediaire);
                    chemin.add(fin);
                    return chemin;
                }
            }
        }
        
        // Dernier recours: chemin direct
        chemin.add(fin);
        return chemin;
    }
    
    // Getters
    public List<Voie> getVoies() { return new ArrayList<>(voies); }
    public Base getBaseEquipe1() { return baseEquipe1; }
    public Base getBaseEquipe2() { return baseEquipe2; }
    public Fontaine getFontaineEquipe1() { return fontaineEquipe1; }
    public Fontaine getFontaineEquipe2() { return fontaineEquipe2; }
    public List<Tour> getToursEquipe1() { return new ArrayList<>(toursEquipe1); }
    public List<Tour> getToursEquipe2() { return new ArrayList<>(toursEquipe2); }
    public List<Forme> getObstacles() { return new ArrayList<>(obstacles); }
    public List<Rectangle> getZonesInterdites() { return new ArrayList<>(zonesInterdites); }
    public List<Rectangle> getZonesJungle() { return new ArrayList<>(zonesJungle); }
    
    public double getLargeur() { return LARGEUR; }
    public double getHauteur() { return HAUTEUR; }
    
    public Vecteur2 getPositionSpawnEquipe(int equipeId) {
        return equipeId == 1 ? 
            fontaineEquipe1.getPosition() : 
            fontaineEquipe2.getPosition();
    }
}