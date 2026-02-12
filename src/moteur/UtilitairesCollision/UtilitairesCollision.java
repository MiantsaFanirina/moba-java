package moteur.UtilitairesCollision;

import moteur.Vecteur2;

/**
 * Utilitaires pour la détection de collisions.
 */
public class UtilitairesCollision {
    
    /**
     * Vérifie si deux rectangles s'intersectent.
     */
    public static boolean rectanglesIntersectent(Rectangle rect1, Rectangle rect2) {
        return rect1.x < rect2.x + rect2.largeur &&
               rect1.x + rect1.largeur > rect2.x &&
               rect1.y < rect2.y + rect2.hauteur &&
               rect1.y + rect1.hauteur > rect2.y;
    }
    
    /**
     * Vérifie si un point est à l'intérieur d'un rectangle.
     */
    public static boolean pointDansRectangle(Vecteur2 point, Rectangle rectangle) {
        return point.x >= rectangle.x && point.x <= rectangle.x + rectangle.largeur &&
               point.y >= rectangle.y && point.y <= rectangle.y + rectangle.hauteur;
    }
    
    /**
     * Vérifie si deux cercles s'intersectent.
     */
    public static boolean cerclesIntersectent(Vecteur2 centre1, double rayon1, 
                                             Vecteur2 centre2, double rayon2) {
        double distance = centre1.distance(centre2);
        return distance <= rayon1 + rayon2;
    }
    
    /**
     * Vérifie si un point est à l'intérieur d'un cercle.
     */
    public static boolean pointDansCercle(Vecteur2 point, Vecteur2 centre, double rayon) {
        return point.distance(centre) <= rayon;
    }
    
    /**
     * Résout une collision entre deux rectangles en poussant le premier.
     */
    public static void resoudreCollisionRectangle(Rectangle rect1, Rectangle rect2, 
                                                 Vecteur2 vitesse) {
        // Calculer le chevauchement sur chaque axe
        double chevauchementX = Math.min(rect1.x + rect1.largeur - rect2.x, 
                                        rect2.x + rect2.largeur - rect1.x);
        double chevauchementY = Math.min(rect1.y + rect1.hauteur - rect2.y, 
                                        rect2.y + rect2.hauteur - rect1.y);
        
        // Résoudre sur l'axe avec le moins de chevauchement
        if (chevauchementX < chevauchementY) {
            // Résolution horizontale
            if (rect1.x < rect2.x) {
                rect1.x -= chevauchementX;
            } else {
                rect1.x += chevauchementX;
            }
            vitesse.x = 0; // Arrêter la vitesse horizontale
        } else {
            // Résolution verticale
            if (rect1.y < rect2.y) {
                rect1.y -= chevauchementY;
            } else {
                rect1.y += chevauchementY;
            }
            vitesse.y = 0; // Arrêter la vitesse verticale
        }
    }
}