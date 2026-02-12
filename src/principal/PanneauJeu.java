package principal;

import domaine.partie.Partie;
import domaine.equipe.Equipe;
import domaine.structures.Base;
import domaine.structures.Tour;
import domaine.unite.Heros;
import domaine.unite.Unite;
import domaine.carte.Arene;
import moteur.*;
import moteur.formes.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panneau principal du jeu MOBA avec nouvelle carte et systèmes avancés.
 */
public class PanneauJeu extends JPanel {

    // Dimensions et constantes
    private final int TAILLE_TUILE = 40;
    private final double LARGEUR_CARTE = 2400;
    private final double HAUTEUR_CARTE = 1800;
    
    // Composants du jeu
    private Partie partie;
    private Arene arene;
    private Heros joueur;
    private Camera2D camera;
    private Rendu rendu;
    private BoucleJeu boucleJeu;
    private SystemeCollision systemeCollision;
    private Minimap minimap;
    
    // État du jeu
    private boolean estEnCours;
    private double tempsJeu;
    
    public PanneauJeu(int largeurInitiale, int hauteurInitiale) {
        this.arene = new Arene();
        this.tempsJeu = 0;
        this.estEnCours = false;
        
        // Configuration du panneau
        setPreferredSize(new Dimension(largeurInitiale, hauteurInitiale));
        setBackground(new Color(45, 45, 55));
        setFocusable(true);
        
        // Initialiser les composants
        initialiserJeu();
        initialiserCamera();
        initialiserSystemes();
        initialiserInput();
        initialiserBoucleJeu();
    }
    
    // Initialiser le jeu (avec configuration par défaut)
    private void initialiserJeu() {
        creerPartieParDefaut();
    }
    
    // Créer une partie avec la configuration par défaut
    private void creerPartieParDefaut() {
        // Créer les équipes avec les bases de l'arène
        Equipe equipe1 = new Equipe(1, "Équipe Bleue", "#4169E1", arene.getBaseEquipe1());
        Equipe equipe2 = new Equipe(2, "Équipe Rouge", "#DC143L", arene.getBaseEquipe2());
        
        // Créer les héros avec des types différents
        this.joueur = new Heros(1, 1, "Joueur", Heros.TypeHeros.ASSASSIN, 300, 900);
        Heros heroEnnemi = new Heros(2, 2, "Ennemi", Heros.TypeHeros.TANK, 2100, 900);
        
        equipe1.ajouterHeros(joueur);
        equipe2.ajouterHeros(heroEnnemi);
        
        // Ajouter plus de héros pour tester
        Heros heroSupport1 = new Heros(3, 1, "Support1", Heros.TypeHeros.SUPPORT, 400, 600);
        Heros heroMage2 = new Heros(4, 2, "Mage2", Heros.TypeHeros.MAGE, 2000, 1200);
        
        equipe1.ajouterHeros(heroSupport1);
        equipe2.ajouterHeros(heroMage2);
        
        // Créer la partie
        this.partie = new Partie(1, equipe1, equipe2);
        
        // Ajouter des sorts au joueur
        ajouterSortsJoueur();
    }
    
    // Configurer le héros depuis le menu
    public void configurerHero(Heros.TypeHeros typeHero, double multiplicateurVitesse) {
        // Créer les équipes avec les bases de l'arène
        Equipe equipe1 = new Equipe(1, "Équipe Bleue", "#4169E1", arene.getBaseEquipe1());
        Equipe equipe2 = new Equipe(2, "Équipe Rouge", "#DC143L", arene.getBaseEquipe2());
        
        // Créer le nouveau joueur
        this.joueur = new Heros(1, 1, "Joueur", typeHero, 300, 900);
        
        // Appliquer le multiplicateur de vitesse
        this.joueur.vitesseIndividuelle = typeHero.getVitesseBase() * multiplicateurVitesse;
        
        // Créer les héros ennemis
        Heros heroEnnemi = new Heros(2, 2, "Ennemi", Heros.TypeHeros.TANK, 2100, 900);
        Heros heroSupport1 = new Heros(3, 1, "Support1", Heros.TypeHeros.SUPPORT, 400, 600);
        Heros heroMage2 = new Heros(4, 2, "Mage2", Heros.TypeHeros.MAGE, 2000, 1200);
        
        equipe1.ajouterHeros(joueur);
        equipe1.ajouterHeros(heroSupport1);
        equipe2.ajouterHeros(heroEnnemi);
        equipe2.ajouterHeros(heroMage2);
        
        // Créer la partie
        this.partie = new Partie(1, equipe1, equipe2);
        
        // Ajouter des sorts au joueur
        ajouterSortsJoueur();
        
        System.out.println("🎮 Héros configuré: " + typeHero.getNom() + " (vitesse: " + this.joueur.vitesseIndividuelle + ")");
    }
    
    // Ajouter des sorts au joueur
    private void ajouterSortsJoueur() {
        // Sort 1: Dash
        domaine.sort.Sort dash = new domaine.sort.Sort(
            1, "Dash", "DEPLACEMENT", 30, 8.0, 50, 3.0
        );
        
        // Sort 2: Attack Boost
        domaine.sort.Sort boost = new domaine.sort.Sort(
            2, "Boost", "BUFF", 40, 12.0, 0, 2.0
        );
        
        // Sort 3: Heal
        domaine.sort.Sort soin = new domaine.sort.Sort(
            3, "Soin", "SOIN", 50, 15.0, 80, 1.5
        );
        
        joueur.apprendreSort(dash);
        joueur.apprendreSort(boost);
        joueur.apprendreSort(soin);
    }
    
    // Initialiser la caméra
    private void initialiserCamera() {
        this.camera = new Camera2D(getWidth(), getHeight());
        camera.suivre(joueur.getPosition());
        camera.setLimitesMonde(arene.getLargeur(), arene.getHauteur());
        camera.setLissage(0.1);
    }
    
    // Initialiser les systèmes avancés
    private void initialiserSystemes() {
        // Initialiser le système de collision
        this.systemeCollision = new SystemeCollision(
            arene.getObstacles(), 
            arene.getZonesInterdites()
        );
        
        // Assigner le système de collision à tous les héros
        for (Equipe equipe : List.of(partie.getEquipe1(), partie.getEquipe2())) {
            for (Heros hero : equipe.getHeros()) {
                hero.setSystemeCollision(systemeCollision);
            }
        }
        
        // Initialiser la minimap
        this.minimap = new Minimap(arene, 200, 200);
    }
    
    // Initialiser les entrées utilisateur
    private void initialiserInput() {
        // Gestionnaire de clic droit pour le déplacement
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) { // Clic droit
                    gererClicDroit(e.getX(), e.getY());
                } else if (e.getButton() == MouseEvent.BUTTON1) { // Clic gauche
                    gererClicGauche(e.getX(), e.getY());
                }
            }
        });
        
        // Gestionnaire de mouvement de souris pour la minimap
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // Mettre à jour l'état de la minimap si nécessaire
            }
        });
        
        // Gestionnaire de molette pour le zoom
        addMouseWheelListener(e -> {
            int rotation = e.getWheelRotation();
            camera.ajouterZoom(rotation > 0 ? -0.1 : 0.1);
        });
        
        // Gestionnaire de clavier pour les sorts
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                gererTouche(e.getKeyCode());
            }
        });
    }
    
    // Gérer le clic droit - déplacement du héros
    private void gererClicDroit(int x, int y) {
        if (!joueur.estVivant()) return;
        
        // Convertir les coordonnées écran en monde
        Vecteur2 clicMonde = camera.ecranVersMonde(new Vecteur2(x, y));
        
        // Faire déplacer le joueur avec le nouveau système de pathfinding
        joueur.deplacerVers(clicMonde);
        
        System.out.println("🎯 Déplacement vers: " + clicMonde);
    }
    
    // Gérer le clic gauche - actions diverses
    private void gererClicGauche(int x, int y) {
        Vecteur2 clicMonde = camera.ecranVersMonde(new Vecteur2(x, y));
        
        // Vérifier si on clique sur la minimap
        if (estClicMinimap(x, y)) {
            gererClicMinimap(x, y);
        }
    }
    
    // Vérifier si le clic est sur la minimap
    private boolean estClicMinimap(int x, int y) {
        int minimapX = getWidth() - 220;
        int minimapY = getHeight() - 220;
        return x >= minimapX && x <= minimapX + 200 && 
               y >= minimapY && y <= minimapY + 200;
    }
    
    // Gérer le clic sur la minimap
    private void gererClicMinimap(int x, int y) {
        // Convertir les coordonnées de la minimap vers le monde
        int minimapX = getWidth() - 220;
        int minimapY = getHeight() - 220;
        
        Vecteur2 clicMinimap = new Vecteur2(x - minimapX, y - minimapY);
        Vecteur2 clicMonde = minimap.convertirPositionMonde(clicMinimap);
        
        // Déplacer la caméra vers cette position
        camera.setTarget(clicMonde);
    }
    
    // Gérer les touches du clavier
    private void gererTouche(int keyCode) {
        double tempsActuel = System.currentTimeMillis() / 1000.0;
        
        switch (keyCode) {
            case KeyEvent.VK_Q: // Sort 1
                if (joueur.peutLancerSort(0, tempsActuel)) {
                    lancerSort(0);
                }
                break;
                
            case KeyEvent.VK_W: // Sort 2
                if (joueur.peutLancerSort(1, tempsActuel)) {
                    lancerSort(1);
                }
                break;
                
            case KeyEvent.VK_E: // Sort 3
                if (joueur.peutLancerSort(2, tempsActuel)) {
                    lancerSort(2);
                }
                break;
                
            case KeyEvent.VK_B: // Buy
                acheterEquipement();
                break;
                
            case KeyEvent.VK_SPACE: // Start/Stop
                if (!estEnCours) {
                    demarrerPartie();
                } else {
                    arreterPartie();
                }
                break;
                
            case KeyEvent.VK_R: // Rappel
                joueur.lancerRappel();
                break;
        }
    }
    
    // Lancer un sort
    private void lancerSort(int indexSort) {
        double tempsActuel = System.currentTimeMillis() / 1000.0;
        
        // Trouver une cible (pour l'instant, le joueur lui-même pour les sorts de buff)
        Unite cible = joueur;
        
        // Si c'est un sort de dégâts, chercher un ennemi proche
        if (joueur.getSorts().get(indexSort).getTypeEffet().equals("DEGAT")) {
            cible = trouverEnnemiProche();
        }
        
        if (cible != null) {
            joueur.lancerSort(indexSort, cible, tempsActuel);
        }
    }
    
    // Trouver un ennemi proche
    private Unite trouverEnnemiProche() {
        Unite ennemiPlusProche = null;
        double distanceMinimale = Double.MAX_VALUE;
        
        List<Equipe> equipes = List.of(partie.getEquipe1(), partie.getEquipe2());
        for (Equipe equipe : equipes) {
            if (equipe.getIdEquipe() != joueur.getEquipeId()) {
                for (Heros hero : equipe.getHeros()) {
                    if (hero.estVivant()) {
                        double distance = joueur.getPosition().distance(hero.getPosition());
                        if (distance < distanceMinimale) {
                            distanceMinimale = distance;
                            ennemiPlusProche = hero;
                        }
                    }
                }
            }
        }
        
        return ennemiPlusProche;
    }
    
    // Acheter un équipement
    private void acheterEquipement() {
        domaine.equipement.Equipement epee = new domaine.equipement.Equipement(
            1, "Épée de Fer", "Arme", 300, 15, 5, true
        );
        
        if (joueur.acheterEquipement(epee)) {
            System.out.println("✅ " + joueur.getNom() + " a acheté " + epee.getNom());
        }
    }
    
    // Initialiser la boucle de jeu
    private void initialiserBoucleJeu() {
        this.rendu = new Rendu();
        this.boucleJeu = new BoucleJeu(60);
        
        boucleJeu.setMiseAJour(tempsDelta -> mettreAJour(tempsDelta));
        boucleJeu.setRendu(() -> repaint());
        boucleJeu.demarrer();
    }
    
    // Mettre à jour le jeu
    private void mettreAJour(double tempsDelta) {
        if (!estEnCours) return;
        
        tempsJeu += tempsDelta;
        
        // Mettre à jour les déplacements des héros
        List<Equipe> equipes = List.of(partie.getEquipe1(), partie.getEquipe2());
        for (Equipe equipe : equipes) {
            for (Heros hero : equipe.getHeros()) {
                hero.mettreAJourDeplacement(tempsDelta);
            }
        }
        
        // Mettre à jour la partie
        partie.mettreAJour(tempsDelta);
        
        // Mettre à jour la caméra
        camera.mettreAJour(tempsDelta);
        camera.suivre(joueur.getPosition());
    }
    
    // Démarrer la partie
    private void demarrerPartie() {
        partie.demarrer();
        estEnCours = true;
        System.out.println("🎮 Partie démarrée!");
    }
    
    // Arrêter la partie
    private void arreterPartie() {
        estEnCours = false;
        System.out.println("Partie en pause");
    }
    
    // Dessiner le panneau
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // Activer l'anti-aliasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Définir le contexte de rendu
        rendu.definirGraphiques(g2);
        
        // Dessiner la carte
        dessinerCarte(g2);
        
        // Dessiner les unités
        dessinerUnites(g2);
        
        // Dessiner l'interface utilisateur
        dessinerInterface(g2);
    }
    
    // Dessiner la carte
    private void dessinerCarte(Graphics2D g2) {
        // Dessiner les zones de l'arène
        dessinerZonesArene(g2);
        
        // Dessiner les obstacles
        dessinerObstacles(g2);
        
        // Dessiner les structures
        dessinerStructures(g2);
    }
    
    // Dessiner les zones de l'arène
    private void dessinerZonesArene(Graphics2D g2) {
        // Dessiner les zones de jungle
        for (Rectangle zoneJungle : arene.getZonesJungle()) {
            dessinerFormeCamera(g2, zoneJungle);
        }
    }
    
    // Dessiner les obstacles
    private void dessinerObstacles(Graphics2D g2) {
        for (moteur.formes.Forme obstacle : arene.getObstacles()) {
            dessinerFormeCamera(g2, obstacle);
        }
    }
    
    // Dessiner les structures (bases, tours, fontaines)
    private void dessinerStructures(Graphics2D g2) {
        // Dessiner les bases
        dessinerBase(g2, arene.getBaseEquipe1(), new Color(100, 100, 255));
        dessinerBase(g2, arene.getBaseEquipe2(), new Color(255, 100, 100));
        
        // Dessiner les fontaines
        dessinerFontaine(g2, arene.getFontaineEquipe1(), new Color(150, 150, 255));
        dessinerFontaine(g2, arene.getFontaineEquipe2(), new Color(255, 150, 150));
        
        // Dessiner les tours
        for (Tour tour : arene.getToursEquipe1()) {
            dessinerTour(g2, tour, new Color(100, 100, 255));
        }
        for (Tour tour : arene.getToursEquipe2()) {
            dessinerTour(g2, tour, new Color(255, 100, 100));
        }
    }
    
    // Dessiner une base
    private void dessinerBase(Graphics2D g2, Base base, Color couleur) {
        Vecteur2 posEcran = camera.mondeVersEcran(new Vecteur2(100, arene.getHauteur()/2));
        double zoom = camera.getZoom();
        
        Rectangle baseRect = new Rectangle(posEcran.soustraire(new Vecteur2(50, 50)), 100, 100);
        baseRect.setCouleur(couleur);
        baseRect.setEstRemplie(true);
        dessinerForme(g2, baseRect);
    }
    
    // Dessiner une fontaine
    private void dessinerFontaine(Graphics2D g2, domaine.structures.Fontaine fontaine, Color couleur) {
        Vecteur2 posEcran = camera.mondeVersEcran(fontaine.getPosition());
        double zoom = camera.getZoom();
        
        Cercle cercleFontaine = new Cercle(posEcran, 15 * zoom);
        cercleFontaine.setCouleur(couleur);
        cercleFontaine.setEstRemplie(true);
        dessinerForme(g2, cercleFontaine);
    }
    
    // Dessiner une tour
    private void dessinerTour(Graphics2D g2, Tour tour, Color couleur) {
        Vecteur2 posEcran = camera.mondeVersEcran(tour.getPosition());
        double zoom = camera.getZoom();
        
        if (!tour.estDetruite()) {
            Rectangle tourRect = new Rectangle(posEcran.soustraire(new Vecteur2(20, 20)), 40, 40);
            tourRect.setCouleur(couleur);
            tourRect.setEstRemplie(true);
            dessinerForme(g2, tourRect);
        } else {
            // Tour détruite
            g2.setColor(Color.GRAY);
            g2.drawLine((int)(posEcran.x - 10), (int)(posEcran.y - 10), 
                       (int)(posEcran.x + 10), (int)(posEcran.y + 10));
            g2.drawLine((int)(posEcran.x - 10), (int)(posEcran.y + 10), 
                       (int)(posEcran.x + 10), (int)(posEcran.y - 10));
        }
    }
    
    // Dessiner les unités
    private void dessinerUnites(Graphics2D g2) {
        List<Equipe> equipes = List.of(partie.getEquipe1(), partie.getEquipe2());
        
        for (Equipe equipe : equipes) {
            for (Heros hero : equipe.getHeros()) {
                dessinerHeros(g2, hero);
            }
        }
    }
    
    // Dessiner un héros avec forme complexe
    private void dessinerHeros(Graphics2D g2, Heros hero) {
        if (!hero.estVivant()) return;
        
        // Convertir en coordonnées écran
        Vecteur2 posEcran = camera.mondeVersEcran(hero.getPosition());
        double zoom = camera.getZoom();
        
        // Utiliser le rendu complexe pour les héros
        RenduHerosComplexe.dessinerHeros(g2, hero, posEcran, zoom);
        
        // Dessiner le chemin si le héros se déplace
        if (hero.estEnDeplacement() && !hero.getCheminActuel().isEmpty()) {
            dessinerChemin(g2, hero);
        }
        
        // Dessiner le nom et le type
        dessinerInfoHeros(g2, posEcran, hero, zoom);
    }
    
    // Dessiner le chemin du héros
    private void dessinerChemin(Graphics2D g2, Heros hero) {
        g2.setColor(Color.YELLOW);
        g2.setStroke(new BasicStroke(2));
        
        Vecteur2 posPrecedente = camera.mondeVersEcran(hero.getPosition());
        
        for (Vecteur2 pointChemin : hero.getCheminActuel()) {
            Vecteur2 posEcran = camera.mondeVersEcran(pointChemin);
            g2.drawLine((int)posPrecedente.x, (int)posPrecedente.y, 
                       (int)posEcran.x, (int)posEcran.y);
            posPrecedente = posEcran;
        }
        
        g2.setStroke(new BasicStroke(1));
    }
    
    // Dessiner les informations du héros
    private void dessinerInfoHeros(Graphics2D g2, Vecteur2 position, Heros hero, double zoom) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, (int)(10 * zoom)));
        
        String info = hero.getTypeHeros().getNom().substring(0, 3);
        g2.drawString(info, (int)(position.x - 15), (int)(position.y + 5));
    }
    
    // Dessiner une barre de vie
    private void dessinerBarreVie(Graphics2D g2, Vecteur2 position, double hp, double hpMax, double zoom) {
        double largeurBarre = 40 * zoom;
        double hauteurBarre = 4 * zoom;
        double x = position.x - largeurBarre / 2;
        double y = position.y - 30 * zoom;
        
        // Fond
        moteur.formes.Rectangle fond = new moteur.formes.Rectangle(new Vecteur2(x, y), largeurBarre, hauteurBarre);
        fond.setCouleur(Color.RED);
        fond.setEstRemplie(true);
        dessinerForme(g2, fond);
        
        // Vie
        double pourcentageVie = hp / hpMax;
        moteur.formes.Rectangle vie = new moteur.formes.Rectangle(new Vecteur2(x, y), largeurBarre * pourcentageVie, hauteurBarre);
        vie.setCouleur(Color.GREEN);
        vie.setEstRemplie(true);
        dessinerForme(g2, vie);
    }
    
    // Dessiner l'interface utilisateur
    private void dessinerInterface(Graphics2D g2) {
        // Informations du joueur
        dessinerInfosJoueur(g2);
        
        // Minim carte
        dessinerMinimap(g2);
        
        // Contrôles
        dessinerControles(g2);
    }
    
    // Dessiner les informations du joueur
    private void dessinerInfosJoueur(Graphics2D g2) {
        int x = 20;
        int y = 20;
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        
        String infos = String.format(
            "Joueur: %s | %s | Niveau: %d | Or: %.0f | Vitesse: %.1f",
            joueur.getNom(),
            joueur.getTypeHeros().getNom(),
            joueur.getNiveau(),
            joueur.getOr(),
            joueur.getVitesseIndividuelle()
        );
        
        g2.drawString(infos, x, y);
        
        // Stats K/D/A
        y += 25;
        String stats = String.format("K/D/A: %d/%d/%d", 
            joueur.getKills(), joueur.getDeaths(), joueur.getAssists());
        g2.drawString(stats, x, y);
    }
    
    // Dessiner la minim carte
    private void dessinerMinimap(Graphics2D g2) {
        int taille = 200;
        int x = getWidth() - taille - 20;
        int y = getHeight() - taille - 20;
        
        // Sauvegarder l'état actuel du graphics
        Graphics2D g2Minimap = (Graphics2D) g2.create();
        
        // Collecter tous les héros
        List<Heros> tousHeros = new ArrayList<>();
        for (Equipe equipe : List.of(partie.getEquipe1(), partie.getEquipe2())) {
            tousHeros.addAll(equipe.getHeros());
        }
        
        // Dessiner la minimap avec les éléments dynamiques
        minimap.dessinerElementsDynamiques(g2Minimap, tousHeros, 
                                          camera.getPosition(), camera.getChampVision());
        
        // Restaurer l'état du graphics
        g2Minimap.dispose();
    }
    
    // Dessiner les contrôles
    private void dessinerControles(Graphics2D g2) {
        int x = 20;
        int y = getHeight() - 120;
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        
        String[] controles = {
            "Clic droit: Déplacer",
            "Clic gauche minimap: Téléporter caméra",
            "Q/W/E: Sorts",
            "R: Rappel",
            "B: Acheter équipement",
            "Espace: Démarrer/Arrêter"
        };
        
        for (int i = 0; i < controles.length; i++) {
            g2.drawString(controles[i], x, y + i * 18);
        }
    }
    
    // Dessiner une forme avec transformation de caméra
    private void dessinerFormeCamera(Graphics2D g2, moteur.formes.Forme forme) {
        if (forme instanceof Rectangle) {
            Rectangle rect = (Rectangle) forme;
            Vecteur2 posEcran = camera.mondeVersEcran(new Vecteur2(rect.x, rect.y));
            double zoom = camera.getZoom();
            
            Rectangle rectEcran = new Rectangle(posEcran, rect.largeur * zoom, rect.hauteur * zoom);
            rectEcran.setCouleur(rect.getCouleur());
            rectEcran.setEstRemplie(rect.estRemplie());
            rectEcran.dessiner(g2);
        } else if (forme instanceof Cercle) {
            Cercle cercle = (Cercle) forme;
            Vecteur2 posEcran = camera.mondeVersEcran(cercle.getCentre());
            double zoom = camera.getZoom();
            
            Cercle cercleEcran = new Cercle(posEcran, cercle.getRayon() * zoom);
            cercleEcran.setCouleur(cercle.getCouleur());
            cercleEcran.setEstRemplie(cercle.estRemplie());
            cercleEcran.dessiner(g2);
        }
    }
    
    // Dessiner une forme (sans transformation)
    private void dessinerForme(Graphics2D g2, moteur.formes.Forme forme) {
        forme.dessiner(g2);
    }
}