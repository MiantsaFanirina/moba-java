package moteur.UtilitairesCollision;

import moteur.Vecteur2;

/**
 * Rectangle pour la détection de collisions.
 */
public class Rectangle {
    public float x, y;
    public float largeur, hauteur;
    
    public Rectangle(float x, float y, float largeur, float hauteur) {
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }
    
    public Rectangle(Vecteur2 position, float largeur, float hauteur) {
        this.x = (float) position.x;
        this.y = (float) position.y;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }
    
    /**
     * Vérifie si ce rectangle intersecte un autre rectangle.
     */
    public boolean intersecte(Rectangle autre) {
        return rectanglesIntersectent(this, autre);
    }
    
    /**
     * Vérifie si un point est à l'intérieur de ce rectangle.
     */
    public boolean contientPoint(Vecteur2 point) {
        return point.x >= x && point.x <= x + largeur &&
               point.y >= y && point.y <= y + hauteur;
    }
    
    /**
     * Obtient le centre du rectangle.
     */
    public Vecteur2 obtenirCentre() {
        return new Vecteur2(x + largeur / 2, y + hauteur / 2);
    }
    
    // Getters et setters
    public float getX() { return x; }
    public float getY() { return y; }
    public float getLargeur() { return largeur; }
    public float getHauteur() { return hauteur; }
    
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setLargeur(float largeur) { this.largeur = largeur; }
    public void setHauteur(float hauteur) { this.hauteur = hauteur; }
    
    /**
     * Vérifie si deux rectangles s'intersectent.
     */
    public static boolean rectanglesIntersectent(Rectangle rect1, Rectangle rect2) {
        return rect1.x < rect2.x + rect2.largeur &&
               rect1.x + rect1.largeur > rect2.x &&
               rect1.y < rect2.y + rect2.hauteur &&
               rect1.y + rect1.hauteur > rect2.y;
    }
}