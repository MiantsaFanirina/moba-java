package Engine;

import java.util.ArrayList;
import java.util.List;

/**
 * Contient toutes les entités et les obstacles (colliders).
 * Met à jour les entités et gère le rendu.
 */
public class World {

    private final List<Entity> entities = new ArrayList<>();
    private final List<CollisionUtils.Rect> colliders = new ArrayList<>();

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void addCollider(CollisionUtils.Rect rect) {
        colliders.add(rect);
    }

    public List<CollisionUtils.Rect> getColliders() {
        return colliders;
    }

    /** Retourne les colliders proches d'une position (optimisation) */
    public List<CollisionUtils.Rect> getCollidersNear(double x, double y, double margin) {
        List<CollisionUtils.Rect> near = new ArrayList<>();
        float mx = (float) x;
        float my = (float) y;
        float m = (float) margin;
        for (CollisionUtils.Rect r : colliders) {
            if (r.x + r.width >= mx - m && r.x <= mx + m &&
                    r.y + r.height >= my - m && r.y <= my + m) {
                near.add(r);
            }
        }
        return near;
    }

    public void update(double deltaTime) {
        for (Entity e : entities) {
            e.update(deltaTime, this);
        }
        entities.removeIf(e -> !e.isActive());
    }

    public void render(Renderer renderer, Camera2D camera) {
        for (Entity e : entities) {
            e.render(renderer, camera);
        }
    }
}
