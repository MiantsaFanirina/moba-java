package Engine;

import java.util.List;
import java.util.function.Consumer;

/**
 * Fait suivre une liste de points (chemin) à une entité.
 * Si bloqué, peut demander un recalcul du chemin via callback.
 */
public class PathFollower {

    private List<Vector2> path;
    private int currentIndex = 0;
    private final double speed;
    private final double arriveDistance;
    private double lastDistanceToTarget = Double.MAX_VALUE;
    private double stuckTime = 0;
    private static final double STUCK_THRESHOLD = 0.35;

    /** Callback: quand bloqué, (position, destination) -> nouveau chemin ou null */
    public interface OnStuckCallback {
        List<Vector2> onStuck(Vector2 currentPos, Vector2 destination);
    }

    private OnStuckCallback onStuckCallback;
    private Consumer<List<Vector2>> onPathUpdated;
    private Vector2 destination;

    public void setOnPathUpdated(Consumer<List<Vector2>> cb) {
        this.onPathUpdated = cb;
    }

    public PathFollower(List<Vector2> path, double speed) {
        this(path, speed, 5.0);
    }

    public PathFollower(List<Vector2> path, double speed, double arriveDistance) {
        this.path = path;
        this.speed = speed;
        this.arriveDistance = arriveDistance;
        this.destination = path != null && !path.isEmpty() ? path.get(path.size() - 1).copy() : null;
    }

    public void setOnStuckCallback(OnStuckCallback cb) {
        this.onStuckCallback = cb;
    }

    public boolean isFinished() {
        return path == null || currentIndex >= path.size();
    }

    public Vector2 update(Vector2 position, double deltaTime) {
        if (isFinished()) return position;

        Vector2 target = path.get(currentIndex);
        Vector2 direction = target.subtract(position);
        double distance = direction.length();

        if (distance < arriveDistance) {
            currentIndex++;
            stuckTime = 0;
            lastDistanceToTarget = Double.MAX_VALUE;
            return position;
        }

        if (distance < lastDistanceToTarget - 2.0) {
            stuckTime = 0;
        } else {
            stuckTime += deltaTime;
            if (stuckTime > STUCK_THRESHOLD) {
                if (onStuckCallback != null && destination != null) {
                    List<Vector2> newPath = onStuckCallback.onStuck(position, destination);
                    if (newPath != null && !newPath.isEmpty()) {
                        path = new java.util.ArrayList<>(newPath);
                        currentIndex = 0;
                        stuckTime = 0;
                        lastDistanceToTarget = Double.MAX_VALUE;
                        if (onPathUpdated != null) onPathUpdated.accept(path);
                        return position;
                    }
                }
                currentIndex++;
                stuckTime = 0;
                lastDistanceToTarget = Double.MAX_VALUE;
                return position;
            }
        }
        lastDistanceToTarget = distance;

        Vector2 velocity = direction.normalize().multiply(speed * deltaTime);
        return position.add(velocity);
    }
}
