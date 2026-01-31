package Engine;

import java.util.List;

/**
 * Résout les collisions d'un rectangle en mouvement contre des obstacles fixes.
 * Sépare les axes X et Y pour un comportement correct.
 */
public final class CollisionResolver {

    private CollisionResolver() {}

    /**
     * Déplace le rectangle et le repousse en cas de collision.
     * @param mover l'objet qui bouge
     * @param obstacles les obstacles fixes
     * @param velocity le déplacement souhaité
     */
    public static void resolve(
            CollisionUtils.Rect mover,
            List<CollisionUtils.Rect> obstacles,
            Vector2 velocity
    ) {
        mover.x += velocity.x;
        for (CollisionUtils.Rect obstacle : obstacles) {
            if (CollisionUtils.rectIntersects(mover, obstacle)) {
                if (velocity.x > 0) mover.x = obstacle.x - mover.width;
                else if (velocity.x < 0) mover.x = obstacle.x + obstacle.width;
            }
        }

        mover.y += velocity.y;
        for (CollisionUtils.Rect obstacle : obstacles) {
            if (CollisionUtils.rectIntersects(mover, obstacle)) {
                if (velocity.y > 0) mover.y = obstacle.y - mover.height;
                else if (velocity.y < 0) mover.y = obstacle.y + obstacle.height;
            }
        }
    }
}
