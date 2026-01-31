package Engine;

import java.util.ArrayList;
import java.util.List;

/**
 * Héros contrôlé par le joueur.
 */
public class Player extends Entity {

    private PathFollower pathFollower;
    private List<Vector2> currentPath = new ArrayList<>();
    private Vector2 destinationGoal;
    private PathRecalculator pathRecalculator;

    public interface PathRecalculator {
        List<Vector2> recalculate(Vector2 from, Vector2 to);
    }

    public Player(double x, double y) {
        super(x, y, 32, 32);
    }

    public void setPathRecalculator(PathRecalculator recalculator) {
        this.pathRecalculator = recalculator;
    }

    public void moveTo(List<Vector2> path, double speed) {
        this.currentPath = path != null ? new ArrayList<>(path) : new ArrayList<>();
        this.destinationGoal = path != null && !path.isEmpty()
                ? path.get(path.size() - 1).copy()
                : null;
        this.pathFollower = path != null && !path.isEmpty()
                ? new PathFollower(currentPath, speed)
                : null;

        if (pathFollower != null && pathRecalculator != null && destinationGoal != null) {
            pathFollower.setOnStuckCallback((pos, dest) -> pathRecalculator.recalculate(pos, dest));
            pathFollower.setOnPathUpdated(newPath -> currentPath = newPath);
        }
    }

    public List<Vector2> getCurrentPath() {
        return currentPath;
    }

    @Override
    protected void onUpdate(double deltaTime, World world) {
        if (pathFollower != null && !pathFollower.isFinished()) {
            Vector2 desiredPos = pathFollower.update(position.copy(), deltaTime);
            Vector2 displacement = desiredPos.subtract(position);
            if (deltaTime > 0) {
                velocity.x = displacement.x / deltaTime;
                velocity.y = displacement.y / deltaTime;
            } else {
                velocity.x = 0;
                velocity.y = 0;
            }
        } else {
            velocity.x = 0;
            velocity.y = 0;
        }
    }

    @Override
    public void render(Renderer renderer, Camera2D camera) {
        Vector2 screen = camera.worldToScreen(position);
        double zoom = camera.getZoom();
        double w = width * zoom;
        double h = height * zoom;
        renderer.setColor(new java.awt.Color(220, 50, 50));
        renderer.drawRect(screen.x, screen.y, w, h);
        renderer.setColor(new java.awt.Color(180, 30, 30));
        renderer.drawRectOutline(screen.x, screen.y, w, h);
    }
}
