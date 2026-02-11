package principal;

import domaine.partie.Partie;
import domaine.equipe.Equipe;
import domaine.structures.Base;
import domaine.structures.Tour;
import domaine.unite.Heros;
import domaine.unite.Unite;
import moteur.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panneau principal du jeu MOBA.
 * Gère l'affichage et les interactions.
 */
public class PanneauJeu extends JPanel {

    // Dimensions et constantes
    private final int TAILLE_TUILE = 40;
    private final double LARGEUR_CARTE = 2400;
    private final double HAUTEUR_CARTE = 1800;
    
    // Composants du jeu
    private Partie partie;
    private CarteBataille carte;
    private Heros joueur;
    private Camera2D camera;
    private Rendu rendu;
    private BoucleJeu boucleJeu;
    
    // État du jeu
    private boolean estEnCours;
    private double tempsJeu;
    
    public PanneauJeu(int largeurInitiale, int hauteurInitiale) {
        this.carte = new CarteBataille();
        this.tempsJeu = 0;
        this.estEnCours = false;
        
        // Configuration du panneau
        setPreferredSize(new Dimension(largeurInitiale, hauteurInitiale));
        setBackground(new Color(45, 45, 55));
        setFocusable(true);
        
        // Initialiser les composants
        initialiserJeu();
        initialiserCamera();
        initialiserInput();
        initialiserBoucleJeu();
    }
    
    // Initialiser le jeu
    private void initialiserJeu() {
        // Créer les équipes
        Base base1 = new Base(1, null, 200, 1400);
        Base base2 = new Base(2, null, 2200, 400);
        
        Equipe equipe1 = new Equipe(1, "Équipe Bleue", "#4169E1", base1);
        Equipe equipe2 = new Equipe(2, "Équipe Rouge", "#DC143C", base2);
        
        // Créer les héros
        Heros heroJoueur = new Heros(1, 1, "Joueur", "Assassin", 300, 1500);
        Heros heroEnnemi = new Heros(2, 2, "Ennemi", "Tank", 2100, 300);
        
        equipe1.ajouterHeros(heroJoueur);
        equipe2.ajouterHeros(heroEnnemi);
        
        // Créer la partie
        this.partie = new Partie(1, equipe1, equipe2);
        this.joueur = heroJoueur;
        
        // Ajouter des sorts au joueur
        ajouterSortsJoueur();
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
        camera.setLimitesMonde(LARGEUR_CARTE, HAUTEUR_CARTE);
        camera.setLissage(0.1);
    }
    
    // Initialiser les entrées utilisateur
    private void initialiserInput() {
        // Gestionnaire de clic droit pour le déplacement
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) { // Clic droit
                    gererClicDroit(e.getX(), e.getY());
                }
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
    
    // Gérer le clic droit
    private void gererClicDroit(int x, int y) {
        if (!joueur.estVivant()) return;
        
        // Convertir les coordonnées écran en monde
        Vecteur2 clicMonde = camera.ecranVersMonde(new Vecteur2(x, y));
        
        // Créer un chemin simple (pour l'instant)
        List<Vecteur2> chemin = new ArrayList<>();
        chemin.add(joueur.getPosition());
        chemin.add(clicMonde);
        
        // Faire déplacer le joueur
        joueur.deplacerVers(chemin, 300);
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
        
        boucleJeu.demarrer(
            new BoucleJeu.MiseAJour() {
                @Override
                public void miseAJour(double tempsDelta) {
                    mettreAJour(tempsDelta);
                }
            },
            new BoucleJeu.Rendu() {
                @Override
                public void rendre() {
                    repaint();
                }
            }
        );
    }
    
    // Mettre à jour le jeu
    private void mettreAJour(double tempsDelta) {
        if (!estEnCours) return;
        
        tempsJeu += tempsDelta;
        
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
        // Dessiner les zones de la carte
        dessinerZonesCarte(g2);
        
        // Dessiner les obstacles
        dessinerObstacles(g2);
        
        // Dessiner les objectifs
        dessinerObjectifs(g2);
    }
    
    // Dessiner les zones de la carte
    private void dessinerZonesCarte(Graphics2D g2) {
        // Zones des voies
        for (Rectangle zone : carte.getZonesVoies()) {
            dessinerForme(g2, zone);
        }
        
        // Zones de jungle
        for (Rectangle zone : carte.getZonesJungle()) {
            dessinerForme(g2, zone);
        }
        
        // Zones d'objectifs
        for (Cercle zone : carte.getZonesObjectifs()) {
            dessinerForme(g2, zone);
        }
    }
    
    // Dessiner les obstacles
    private void dessinerObstacles(Graphics2D g2) {
        for (moteur.formes.Forme obstacle : carte.getObstacles()) {
            dessinerForme(g2, obstacle);
        }
    }
    
    // Dessiner les objectifs
    private void dessinerObjectifs(Graphics2D g2) {
        // Bases
        for (Vecteur2 basePos : carte.getPositionsBases()) {
            Cercle base = new Cercle(basePos, 100);
            base.setCouleur(basePos.x < LARGEUR_CARTE / 2 ? 
                new Color(100, 100, 255) : new Color(255, 100, 100));
            base.setEstRemplie(true);
            dessinerForme(g2, base);
        }
        
        // Tours
        for (Vecteur2 tourPos : carte.getPositionsTours()) {
            Rectangle tour = new Rectangle(tourPos, 60, 60);
            tour.setCouleur(new Color(150, 150, 150));
            tour.setEstRemplie(true);
            dessinerForme(g2, tour);
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
    
    // Dessiner un héros
    private void dessinerHeros(Graphics2D g2, Heros hero) {
        if (!hero.estVivant()) return;
        
        // Convertir en coordonnées écran
        Vecteur2 posEcran = camera.mondeVersEcran(hero.getPosition());
        double zoom = camera.getZoom();
        
        // Dessiner le cercle du héros
        Cercle cercleHero = new Cercle(posEcran, 20 * zoom);
        cercleHero.setCouleur(hero.getEquipeId() == 1 ? 
            new Color(100, 100, 255) : new Color(255, 100, 100));
        cercleHero.setEstRemplie(true);
        dessinerForme(g2, cercleHero);
        
        // Dessiner la barre de vie
        dessinerBarreVie(g2, posEcran, hero.getHp(), hero.getHpMax(), zoom);
    }
    
    // Dessiner une barre de vie
    private void dessinerBarreVie(Graphics2D g2, Vecteur2 position, double hp, double hpMax, double zoom) {
        double largeurBarre = 40 * zoom;
        double hauteurBarre = 4 * zoom;
        double x = position.x - largeurBarre / 2;
        double y = position.y - 30 * zoom;
        
        // Fond
        Rectangle fond = new Rectangle(new Vecteur2(x, y), largeurBarre, hauteurBarre);
        fond.setCouleur(Color.RED);
        fond.setEstRemplie(true);
        dessinerForme(g2, fond);
        
        // Vie
        double pourcentageVie = hp / hpMax;
        Rectangle vie = new Rectangle(new Vecteur2(x, y), largeurBarre * pourcentageVie, hauteurBarre);
        vie.setCouleur(Color.GREEN);
        vie.setEstRemplie(true);
        dessinerForme(g2, vie);
    }
    
    // Dessiner l'interface utilisateur
    private void dessinerInterface(Graphics2D g2) {
        // Informations du joueur
        dessinerInfosJoueur(g2);
        
        // Minim carte
        dessinerMinimCarte(g2);
        
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
            "Joueur: %s | Niveau: %d | Or: %.0f | K/D/A: %d/%d/%d",
            joueur.getNom(),
            joueur.getNiveau(),
            joueur.getOr(),
            joueur.getKills(),
            joueur.getDeaths(),
            joueur.getAssists()
        );
        
        g2.drawString(infos, x, y);
    }
    
    // Dessiner la minim carte
    private void dessinerMinimCarte(Graphics2D g2) {
        int taille = 200;
        int x = getWidth() - taille - 20;
        int y = getHeight() - taille - 20;
        
        // Fond
        Rectangle fond = new Rectangle(new Vecteur2(x, y), taille, taille);
        fond.setCouleur(new Color(0, 0, 0, 150));
        fond.setEstRemplie(true);
        dessinerForme(g2, fond);
        
        // Bordure
        fond.setCouleur(Color.WHITE);
        fond.setEstRemplie(false);
        dessinerForme(g2, fond);
        
        // Position du joueur
        Vecteur2 posJoueur = camera.mondeVersEcran(joueur.getPosition());
        double echelle = taille / LARGEUR_CARTE;
        int joueurX = x + (int) (joueur.getPosition().x * echelle);
        int joueurY = y + (int) (joueur.getPosition().y * echelle);
        
        Cercle pointJoueur = new Cercle(new Vecteur2(joueurX, joueurY), 3);
        pointJoueur.setCouleur(Color.YELLOW);
        pointJoueur.setEstRemplie(true);
        dessinerForme(g2, pointJoueur);
    }
    
    // Dessiner les contrôles
    private void dessinerControles(Graphics2D g2) {
        int x = 20;
        int y = getHeight() - 100;
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        
        String[] controles = {
            "Clic droit: Déplacer",
            "Q/W/E: Sorts",
            "B: Acheter équipement",
            "Espace: Démarrer/Arrêter"
        };
        
        for (int i = 0; i < controles.length; i++) {
            g2.drawString(controles[i], x, y + i * 20);
        }
    }
    
    // Dessiner une forme
    private void dessinerForme(Graphics2D g2, moteur.formes.Forme forme) {
        forme.dessiner(g2);
    }
}