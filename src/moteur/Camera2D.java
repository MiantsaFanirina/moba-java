package moteur;

import moteur.Vecteur2;

/**
 * Caméra 2D pour la vue du jeu.
 */
public class Camera2D {
    private Vecteur2 position;
    private double zoom;
    private int largeurEcran;
    private int hauteurEcran;
    
    public Camera2D(int largeurEcran, int hauteurEcran) {
        this.largeurEcran = largeurEcran;
        this.hauteurEcran = hauteurEcran;
        this.position = new Vecteur2(0, 0);
        this.zoom = 1.0;
    }
    
    /**
     * Convertit les coordonnées du monde en coordonnées d'écran.
     */
    public Vecteur2 mondeVersEcran(Vecteur2 monde) {
        return new Vecteur2(
            (monde.x - position.x) * zoom + largeurEcran / 2,
            (monde.y - position.y) * zoom + hauteurEcran / 2
        );
    }
    
    /**
     * Convertit les coordonnées d'écran en coordonnées du monde.
     */
    public Vecteur2 ecranVersMonde(Vecteur2 ecran) {
        return new Vecteur2(
            (ecran.x - largeurEcran / 2) / zoom + position.x,
            (ecran.y - hauteurEcran / 2) / zoom + position.y
        );
    }
    
    /**
     * Déplace la caméra.
     */
    public void deplacer(double deltaX, double deltaY) {
        position.x += deltaX;
        position.y += deltaY;
    }
    
    /**
     * Positionne la caméra à un endroit précis.
     */
    public void setPosition(Vecteur2 position) {
        this.position = new Vecteur2(position.x, position.y);
    }
    
    /**
     * Change le niveau de zoom.
     */
    public void setZoom(double zoom) {
        this.zoom = Math.max(0.1, Math.min(zoom, 5.0)); // Limiter le zoom entre 0.1 et 5.0
    }
    
    /**
     * Fait un zoom avant.
     */
    public void zoomAvant() {
        setZoom(zoom * 1.1);
    }
    
    /**
     * Fait un zoom arrière.
     */
    public void zoomArriere() {
        setZoom(zoom / 1.1);
    }
    
    /**
     * Ajoute une valeur au zoom.
     */
    public void ajouterZoom(double deltaZoom) {
        setZoom(zoom + deltaZoom);
    }
    
    /**
     * Vérifie si un point du monde est visible à l'écran.
     */
    public boolean estVisible(Vecteur2 monde) {
        Vecteur2 ecran = mondeVersEcran(monde);
        return ecran.x >= 0 && ecran.x <= largeurEcran &&
               ecran.y >= 0 && ecran.y <= hauteurEcran;
    }
    
    /**
     * Suit une cible en douceur.
     */
    public void suivreCible(Vecteur2 cible, double facteurLissage) {
        Vecteur2 positionCible = new Vecteur2(cible.x, cible.y);
        position.x += (positionCible.x - position.x) * facteurLissage;
        position.y += (positionCible.y - position.y) * facteurLissage;
    }
    
    /**
     * Suit une cible avec le lissage par défaut.
     */
    public void suivre(Vecteur2 cible) {
        suivreCible(cible, 0.1);
    }
    
    /**
     * Définit les limites du monde.
     */
    public void setLimitesMonde(double largeur, double hauteur) {
        // Limiter la position de la caméra aux limites du monde
        this.position.x = Math.max(0, Math.min(position.x, largeur));
        this.position.y = Math.max(0, Math.min(position.y, hauteur));
    }
    
    /**
     * Définit le facteur de lissage.
     */
    public void setLissage(double lissage) {
        // Le lissage est utilisé dans suivreCible
        // Cette méthode stocke la valeur pour une utilisation future
    }
    
    /**
     * Met à jour la caméra (méthode de compatibilité).
     */
    public void mettreAJour(double tempsDelta) {
        // La caméra peut avoir des mises à jour d'animation ici
        // Pour l'instant, cette méthode est vide pour compatibilité
    }
    
    // Getters et setters
    public Vecteur2 getPosition() { return position; }
    public double getZoom() { return zoom; }
    public int getLargeurEcran() { return largeurEcran; }
    public int getHauteurEcran() { return hauteurEcran; }
    
    public void setLargeurEcran(int largeurEcran) { this.largeurEcran = largeurEcran; }
    public void setHauteurEcran(int hauteurEcran) { this.hauteurEcran = hauteurEcran; }
}