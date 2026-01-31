package Engine;

/**
 * Représente un vecteur 2D (coordonnées x et y).
 * Utilisé pour les positions, vitesses et directions dans le jeu.
 */
public class Vector2 {

    public double x;
    public double y;

    public Vector2() {
        this(0, 0);
    }

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /** Retourne la somme de ce vecteur et d'un autre */
    public Vector2 add(Vector2 other) {
        return new Vector2(this.x + other.x, this.y + other.y);
    }

    /** Retourne la différence entre ce vecteur et un autre */
    public Vector2 subtract(Vector2 other) {
        return new Vector2(this.x - other.x, this.y - other.y);
    }

    /** Multiplie le vecteur par un nombre */
    public Vector2 multiply(double scalar) {
        return new Vector2(this.x * scalar, this.y * scalar);
    }

    /** Retourne la longueur (magnitude) du vecteur */
    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    /** Retourne la longueur au carré (plus rapide, évite sqrt) */
    public double lengthSquared() {
        return x * x + y * y;
    }

    /** Retourne le vecteur normalisé (longueur = 1) */
    public Vector2 normalize() {
        double len = length();
        if (len == 0) return new Vector2(0, 0);
        return new Vector2(x / len, y / len);
    }

    /** Calcule la distance entre ce point et un autre */
    public double distance(Vector2 other) {
        return subtract(other).length();
    }

    /** Interpolation linéaire vers une cible (t entre 0 et 1) */
    public Vector2 lerp(Vector2 target, double t) {
        return new Vector2(
                x + (target.x - x) * t,
                y + (target.y - y) * t
        );
    }

    /** Copie du vecteur */
    public Vector2 copy() {
        return new Vector2(x, y);
    }

    @Override
    public String toString() {
        return "Vector2(" + x + ", " + y + ")";
    }
}
