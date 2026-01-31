package Engine;

/**
 * Caméra 2D qui suit le joueur.
 * Gère le zoom (molette souris) et les conversions coordonnées monde/écran.
 */
public class Camera2D {

    private Vector2 position;
    private double viewportWidth;
    private double viewportHeight;
    private double zoom = 1.0;
    private static final double ZOOM_MIN = 0.4;
    private static final double ZOOM_MAX = 2.5;

    private Vector2 target;
    private double smoothing = 0.1;

    private double worldWidth = -1;
    private double worldHeight = -1;

    public Camera2D(double viewportWidth, double viewportHeight) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.position = new Vector2(0, 0);
    }

    public void setViewportSize(double width, double height) {
        this.viewportWidth = width;
        this.viewportHeight = height;
    }

    /** Définit l'objet à suivre (généralement le joueur) */
    public void follow(Vector2 target) {
        this.target = target;
    }

    public void setSmoothing(double smoothing) {
        this.smoothing = Math.max(0, Math.min(1, smoothing));
    }

    public void setWorldBounds(double worldWidth, double worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    /** Modifie le zoom (delta positif = zoom avant) */
    public void addZoom(double delta) {
        zoom = Math.max(ZOOM_MIN, Math.min(ZOOM_MAX, zoom + delta));
    }

    public double getZoom() {
        return zoom;
    }

    public void update(double deltaTime) {
        if (target == null) return;
        double effW = viewportWidth / zoom;
        double effH = viewportHeight / zoom;
        Vector2 desired = new Vector2(
                target.x - effW / 2,
                target.y - effH / 2
        );
        position = position.lerp(desired, smoothing);
        clampToWorld();
    }

    private void clampToWorld() {
        if (worldWidth <= 0 || worldHeight <= 0) return;
        double effW = viewportWidth / zoom;
        double effH = viewportHeight / zoom;
        position.x = Math.max(0, Math.min(position.x, worldWidth - effW));
        position.y = Math.max(0, Math.min(position.y, worldHeight - effH));
    }

    /** Convertit une position monde en position écran */
    public Vector2 worldToScreen(Vector2 worldPos) {
        Vector2 p = worldPos.subtract(position);
        return new Vector2(p.x * zoom, p.y * zoom);
    }

    /** Convertit une position écran en position monde */
    public Vector2 screenToWorld(Vector2 screenPos) {
        return new Vector2(
                position.x + screenPos.x / zoom,
                position.y + screenPos.y / zoom
        );
    }

    public Vector2 getPosition() {
        return position.copy();
    }
}
