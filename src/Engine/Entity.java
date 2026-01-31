package Engine;

/**
 * Classe de base pour toutes les entités du jeu (joueur, ennemis, etc.).
 * Gère position, vélocité, collisions et rendu.
 */
public abstract class Entity {

    protected Vector2 position;
    protected Vector2 velocity;
    protected double width;
    protected double height;
    protected boolean active = true;

    protected CollisionUtils.Rect collider;

    public Entity(double x, double y, double width, double height) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2();
        this.width = width;
        this.height = height;
        this.collider = new CollisionUtils.Rect((float) x, (float) y, (float) width, (float) height);
    }

    public final void update(double deltaTime, World world) {
        if (!active) return;
        onUpdate(deltaTime, world);
        moveAndCollide(deltaTime, world);
        syncCollider();
    }

    /** Logique spécifique à chaque entité (surcharger) */
    protected abstract void onUpdate(double deltaTime, World world);

    /** Applique la vélocité et résout les collisions */
    protected void moveAndCollide(double deltaTime, World world) {
        Vector2 frameVelocity = velocity.multiply(deltaTime);
        double margin = Math.max(width, height) * 2 + Math.abs(frameVelocity.x) + Math.abs(frameVelocity.y) + 32;
        CollisionResolver.resolve(collider, world.getCollidersNear(position.x, position.y, margin), frameVelocity);
        position.x = collider.x;
        position.y = collider.y;
    }

    protected void syncCollider() {
        collider.x = (float) position.x;
        collider.y = (float) position.y;
    }

    public abstract void render(Renderer renderer, Camera2D camera);

    public Vector2 getPosition() {
        return position;
    }

    public boolean isActive() {
        return active;
    }

    public void destroy() {
        active = false;
    }
}
