package moteur;

/**
 * Représente un vecteur 2D (coordonnées x et y).
 * Utilisé pour les positions, vitesses et directions dans le jeu.
 */
public class Vecteur2 {

    public double x;
    public double y;

    public Vecteur2() {
        this(0, 0);
    }

    public Vecteur2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /** Retourne la somme de ce vecteur et d'un autre */
    public Vecteur2 ajouter(Vecteur2 autre) {
        return new Vecteur2(this.x + autre.x, this.y + autre.y);
    }

    /** Retourne la différence entre ce vecteur et un autre */
    public Vecteur2 soustraire(Vecteur2 autre) {
        return new Vecteur2(this.x - autre.x, this.y - autre.y);
    }

    /** Multiplie le vecteur par un nombre */
    public Vecteur2 multiplier(double scalaire) {
        return new Vecteur2(this.x * scalaire, this.y * scalaire);
    }

    /** Retourne la longueur (magnitude) du vecteur */
    public double longueur() {
        return Math.sqrt(x * x + y * y);
    }
    
    /** Alias pour longueur() - pour compatibilité */
    public double norme() {
        return longueur();
    }

    /** Retourne la longueur au carré (plus rapide, évite sqrt) */
    public double longueurCarre() {
        return x * x + y * y;
    }

    /** Retourne le vecteur normalisé (longueur = 1) */
    public Vecteur2 normaliser() {
        double longueur = longueur();
        if (longueur == 0) return new Vecteur2(0, 0);
        return new Vecteur2(x / longueur, y / longueur);
    }

    /** Calcule la distance entre ce point et un autre */
    public double distance(Vecteur2 autre) {
        return soustraire(autre).longueur();
    }

    /** Interpolation linéaire vers une cible (t entre 0 et 1) */
    public Vecteur2 interpolationLineaire(Vecteur2 cible, double t) {
        return new Vecteur2(
                x + (cible.x - x) * t,
                y + (cible.y - y) * t
        );
    }

    /** Copie du vecteur */
    public Vecteur2 copier() {
        return new Vecteur2(x, y);
    }

    @Override
    public String toString() {
        return "Vecteur2(" + x + ", " + y + ")";
    }
}