package Engine;

import java.util.*;

/**
 * Utilitaires de détection de collision (rectangles, cercles).
 */
public final class CollisionUtils {

    private CollisionUtils() {}

    /** Rectangle axis-aligned (AABB) */
    public static class Rect {
        public float x, y, width, height;

        public Rect(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    public static class Circle {
        public float x, y, radius;

        public Circle(float x, float y, float radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }
    }

    /** Teste si deux rectangles se chevauchent */
    public static boolean rectIntersects(Rect a, Rect b) {
        return a.x < b.x + b.width &&
               a.x + a.width > b.x &&
               a.y < b.y + b.height &&
               a.y + a.height > b.y;
    }

    public static boolean circleIntersects(Circle a, Circle b) {
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        float distSq = dx * dx + dy * dy;
        float radiusSum = a.radius + b.radius;
        return distSq <= radiusSum * radiusSum;
    }

    public static boolean rectCircleIntersects(Rect rect, Circle circle) {
        float closestX = clamp(circle.x, rect.x, rect.x + rect.width);
        float closestY = clamp(circle.y, rect.y, rect.y + rect.height);
        float dx = circle.x - closestX;
        float dy = circle.y - closestY;
        return (dx * dx + dy * dy) <= circle.radius * circle.radius;
    }

    public static List<Pair<Rect, Rect>> findRectCollisions(List<Rect> rects) {
        List<Pair<Rect, Rect>> collisions = new ArrayList<>();
        for (int i = 0; i < rects.size(); i++) {
            for (int j = i + 1; j < rects.size(); j++) {
                Rect a = rects.get(i);
                Rect b = rects.get(j);
                if (rectIntersects(a, b)) {
                    collisions.add(new Pair<>(a, b));
                }
            }
        }
        return collisions;
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static class Pair<A, B> {
        public final A first;
        public final B second;

        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
    }
}
