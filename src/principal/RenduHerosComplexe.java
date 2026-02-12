package principal;

import domaine.unite.Heros;
import moteur.Vecteur2;
import moteur.formes.*;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Rendu complexe pour les héros en utilisant des formes primitives.
 */
public class RenduHerosComplexe {
    
    /**
     * Dessine un héros avec une forme complexe selon son type.
     */
    public static void dessinerHeros(Graphics2D g2, Heros hero, Vecteur2 position, double zoom) {
        if (!hero.estVivant()) return;
        
        // Couleur selon l'équipe
        Color couleurBase = hero.getEquipeId() == 1 ? 
            new Color(100, 100, 255) : new Color(255, 100, 100);
        Color couleurFonce = couleurBase.darker();
        Color couleurClair = couleurBase.brighter();
        
        switch (hero.getTypeHeros()) {
            case TANK:
                dessinerTank(g2, position, zoom, couleurBase, couleurFonce, couleurClair);
                break;
            case MAGE:
                dessinerMage(g2, position, zoom, couleurBase, couleurFonce, couleurClair);
                break;
            case ASSASSIN:
                dessinerAssassin(g2, position, zoom, couleurBase, couleurFonce, couleurClair);
                break;
            case TIREUR:
                dessinerTireur(g2, position, zoom, couleurBase, couleurFonce, couleurClair);
                break;
            case SUPPORT:
                dessinerSupport(g2, position, zoom, couleurBase, couleurFonce, couleurClair);
                break;
        }
        
        // Dessiner la barre de vie
        dessinerBarreVie(g2, hero, position, zoom);
        
        // Dessiner le niveau
        dessinerNiveau(g2, hero, position, zoom);
    }
    
    /**
     * Dessine un tank avec forme de hexagone + bouclier
     */
    private static void dessinerTank(Graphics2D g2, Vecteur2 position, double zoom, 
                                Color couleurBase, Color couleurFonce, Color couleurClair) {
        double taille = 25 * zoom;
        
        // Corps hexagonal
        FormesComplexes.Hexagone corps = new FormesComplexes.Hexagone(position, taille, couleurBase, true);
        corps.dessiner(g2);
        
        // Bouclier externe
        g2.setColor(new Color(couleurClair.getRed(), couleurClair.getGreen(), couleurClair.getBlue(), 100));
        Cercle bouclier = new Cercle(position, taille + 8);
        bouclier.setEstRemplie(false);
        bouclier.setEpaisseur(3);
        bouclier.dessiner(g2);
        
        // Points d'armure
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI / 3 * i;
            Vecteur2 ptArmure = new Vecteur2(
                position.x + taille * 0.7 * Math.cos(angle),
                position.y + taille * 0.7 * Math.sin(angle)
            );
            
            Cercle point = new Cercle(ptArmure, 3);
            point.setCouleur(couleurFonce);
            point.setEstRemplie(true);
            point.dessiner(g2);
        }
    }
    
    /**
     * Dessine un mage avec forme d'étoile + orbes magiques
     */
    private static void dessinerMage(Graphics2D g2, Vecteur2 position, double zoom,
                                Color couleurBase, Color couleurFonce, Color couleurClair) {
        double taille = 20 * zoom;
        
        // Robe en forme d'étoile
        FormesComplexes.Etoile robe = new FormesComplexes.Etoile(position, 5, taille, taille * 0.7, couleurBase);
        robe.dessiner(g2);
        
        // Chapeau pointu
        Vecteur2 hautChapeau = new Vecteur2(position.x, position.y - taille * 1.2);
        int[] xChapeau = {(int) position.x, (int) hautChapeau.x - taille * 0.3, (int) hautChapeau.x + taille * 0.3};
        int[] yChapeau = {(int) (position.y - taille * 0.8), (int) hautChapeau.y, (int) hautChapeau.y};
        g2.setColor(couleurFonce);
        g2.fillPolygon(xChapeau, yChapeau, 3);
        
        // Orbes magiques rotatifs
        double temps = System.currentTimeMillis() / 1000.0;
        for (int i = 0; i < 3; i++) {
            double angle = temps * 2 + (i * 2 * Math.PI / 3);
            Vecteur2 posOrbe = new Vecteur2(
                position.x + (taille + 15) * Math.cos(angle),
                position.y + (taille + 15) * Math.sin(angle)
            );
            
            Cercle orbe = new Cercle(posOrbe, 4);
            orbe.setCouleur(new Color(150, 150, 255, 150));
            orbe.setEstRemplie(true);
            orbe.dessiner(g2);
        }
    }
    
    /**
     * Dessine un assassin avec forme de losange + lames
     */
    private static void dessinerAssassin(Graphics2D g2, Vecteur2 position, double zoom,
                                   Color couleurBase, Color couleurFonce, Color couleurClair) {
        double taille = 22 * zoom;
        
        // Corps en losange
        FormesComplexes.Losange corps = new FormesComplexes.Losange(position, taille, taille * 1.5, couleurBase, true);
        corps.dessiner(g2);
        
        // Masque
        Rectangle masque = new Rectangle(new Vecteur2(position.x - taille * 0.3, position.y - taille * 0.8), 
                                   taille * 0.6, taille * 0.4);
        masque.setCouleur(couleurFonce);
        masque.setEstRemplie(true);
        masque.dessiner(g2);
        
        // Lames autour
        double temps = System.currentTimeMillis() / 1000.0;
        for (int i = 0; i < 4; i++) {
            double angle = temps * 3 + (i * Math.PI / 2);
            double distance = taille + 12;
            Vecteur2 centreLame = new Vecteur2(
                position.x + distance * Math.cos(angle),
                position.y + distance * Math.sin(angle)
            );
            
            // Lame courbe
            LigneCourbe lame = new LigneCourbe(
                centreLame.ajouter(new Vecteur2(-8, -8)),
                centreLame.ajouter(new Vecteur2(0, -15)),
                centreLame.ajouter(new Vecteur2(8, -8)),
                centreLame.ajouter(new Vecteur2(0, 5)),
                20
            );
            lame.setCouleur(new Color(200, 200, 255, 180));
            lame.setEpaisseur(2);
            lame.dessiner(g2);
        }
    }
    
    /**
     * Dessine un tireur avec forme de triangle arrondi + arc
     */
    private static void dessinerTireur(Graphics2D g2, Vecteur2 position, double zoom,
                                  Color couleurBase, Color couleurFonce, Color couleurClair) {
        double taille = 20 * zoom;
        
        // Corps triangle arrondi
        FormesComplexes.TriangleArrondi corps = new FormesComplexes.TriangleArrondi(position, taille, taille * 0.2, couleurBase, true);
        corps.dessiner(g2);
        
        // Arc d'archer
        Vecteur2 centreArc = new Vecteur2(position.x + taille * 0.8, position.y);
        Rectangle arc = new Rectangle(centreArc.ajouter(new Vecteur2(-taille * 0.3, -taille * 0.6)), 
                                  taille * 0.6, taille * 1.2);
        arc.setCouleur(couleurFonce);
        arc.setEstRemplie(false);
        arc.setEpaisseur(3);
        arc.dessiner(g2);
        
        // Flèche
        double temps = System.currentTimeMillis() / 1000.0;
        double oscillation = Math.sin(temps * 5) * 5;
        Vecteur2 flecheDepart = centreArc.ajouter(new Vecteur2(-taille * 0.3 + oscillation, -taille * 0.3));
        Vecteur2 flecheFin = flecheDepart.ajouter(new Vecteur2(taille * 1.2, 0));
        
        Ligne fleche = new Ligne(flecheDepart, flecheFin, true);
        fleche.setCouleur(couleurFonce);
        fleche.setEpaisseur(2);
        fleche.dessiner(g2);
    }
    
    /**
     * Dessine un support avec forme de fleur + auras
     */
    private static void dessinerSupport(Graphics2D g2, Vecteur2 position, double zoom,
                                  Color couleurBase, Color couleurFonce, Color couleurClair) {
        double taille = 20 * zoom;
        
        // Corps principal
        Cercle corps = new Cercle(position, taille * 0.6);
        corps.setCouleur(couleurBase);
        corps.setEstRemplie(true);
        corps.dessiner(g2);
        
        // Fleur de vie
        FormesComplexes.Fleur fleur = new FormesComplexes.Fleur(position, 6, taille * 0.3, taille * 0.4, couleurClair);
        fleur.dessiner(g2);
        
        // Auras de soin rotatives
        double temps = System.currentTimeMillis() / 1000.0;
        for (int i = 0; i < 2; i++) {
            double angle = temps + (i * Math.PI);
            double rayon = taille + 8 + Math.sin(temps * 2) * 3;
            
            g2.setColor(new Color(100, 255, 100, 50));
            Cercle aura = new Cercle(new Vecteur2(
                position.x + rayon * Math.cos(angle),
                position.y + rayon * Math.sin(angle)
            ), 3);
            aura.setEstRemplie(true);
            aura.dessiner(g2);
        }
        
        // Symbole de cœur
        dessinerCoeur(g2, position, taille * 0.3, couleurFonce);
    }
    
    /**
     * Dessine une forme de cœur complexe
     */
    private static void dessinerCoeur(Graphics2D g2, Vecteur2 centre, double taille, Color couleur) {
        g2.setColor(couleur);
        
        // Utilise des courbes de Bézier pour créer un cœur
        int x = (int) centre.x;
        int y = (int) centre.y;
        int t = (int) taille;
        
        // Côté gauche du cœur
        LigneCourbe coeurGauche = new LigneCourbe(
            new Vecteur2(x, y - t/2),
            new Vecteur2(x - t/2, y - t/2),
            new Vecteur2(x - t/2, y),
            new Vecteur2(x, y + t/3),
            20
        );
        
        // Côté droit du cœur
        LigneCourbe coeurDroit = new LigneCourbe(
            new Vecteur2(x, y - t/2),
            new Vecteur2(x + t/2, y - t/2),
            new Vecteur2(x + t/2, y),
            new Vecteur2(x, y + t/3),
            20
        );
        
        coeurGauche.setCouleur(couleur);
        coeurGauche.setEpaisseur(2);
        coeurGauche.dessiner(g2);
        
        coeurDroit.setCouleur(couleur);
        coeurDroit.setEpaisseur(2);
        coeurDroit.dessiner(g2);
    }
    
    /**
     * Dessine la barre de vie avec style amélioré
     */
    private static void dessinerBarreVie(Graphics2D g2, Heros hero, Vecteur2 position, double zoom) {
        double largeurBarre = 50 * zoom;
        double hauteurBarre = 6 * zoom;
        double x = position.x - largeurBarre / 2;
        double y = position.y + 35 * zoom;
        
        // Fond avec bordure arrondie
        g2.setColor(new Color(50, 50, 50, 150));
        Rectangle fond = new Rectangle(new Vecteur2(x - 2, y - 2), largeurBarre + 4, hauteurBarre + 4);
        fond.setCouleur(new Color(50, 50, 50, 150));
        fond.setEstRemplie(true);
        fond.dessiner(g2);
        
        // Vie avec dégradé visuel
        double pourcentageVie = hero.getHp() / hero.getHpMax();
        Color couleurVie;
        if (pourcentageVie > 0.6) {
            couleurVie = new Color(0, 200, 0);
        } else if (pourcentageVie > 0.3) {
            couleurVie = new Color(255, 200, 0);
        } else {
            couleurVie = new Color(255, 50, 50);
        }
        
        Rectangle vie = new Rectangle(new Vecteur2(x, y), largeurBarre * pourcentageVie, hauteurBarre);
        vie.setCouleur(couleurVie);
        vie.setEstRemplie(true);
        vie.dessiner(g2);
        
        // Bordure
        Rectangle bordure = new Rectangle(new Vecteur2(x, y), largeurBarre, hauteurBarre);
        bordure.setCouleur(Color.WHITE);
        bordure.setEstRemplie(false);
        bordure.setEpaisseur(1);
        bordure.dessiner(g2);
        
        // Texte HP
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, (int)(10 * zoom)));
        String texteHP = String.format("%.0f/%.0f", hero.getHp(), hero.getHpMax());
        g2.drawString(texteHP, (int)(x + largeurBarre/2 - 20), (int)(y - 5));
    }
    
    /**
     * Dessine le niveau du héros
     */
    private static void dessinerNiveau(Graphics2D g2, Heros hero, Vecteur2 position, double zoom) {
        if (hero.getNiveau() <= 1) return;
        
        double tailleNiveau = 12 * zoom;
        Vecteur2 positionNiveau = new Vecteur2(position.x + 20 * zoom, position.y - 20 * zoom);
        
        // Fond du niveau
        Cercle fondNiveau = new Cercle(positionNiveau, tailleNiveau);
        fondNiveau.setCouleur(new Color(255, 215, 0, 200));
        fondNiveau.setEstRemplie(true);
        fondNiveau.dessiner(g2);
        
        // Texte du niveau
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial Black", Font.BOLD, (int)(8 * zoom)));
        String texteNiveau = String.valueOf(hero.getNiveau());
        
        // Centrer le texte
        int largeurTexte = g2.getFontMetrics().stringWidth(texteNiveau);
        int hauteurTexte = g2.getFontMetrics().getHeight();
        g2.drawString(texteNiveau, 
                    (int)(positionNiveau.x - largeurTexte/2), 
                    (int)(positionNiveau.y + hauteurTexte/3));
    }
}