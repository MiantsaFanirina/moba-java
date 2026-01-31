package Engine;

/**
 * Boucle de jeu à 60 FPS.
 * Appelle update et render à intervalle fixe.
 */
public final class GameLoop {

    public interface Update {
        void update(double deltaTime);
    }

    public interface Render {
        void render();
    }

    private boolean running = false;
    private final double frameTime;

    public GameLoop(int targetFPS) {
        this.frameTime = 1.0 / targetFPS;
    }

    public void start(Update update, Render render) {
        if (running) return;
        running = true;

        Thread gameThread = new Thread(() -> run(update, render));
        gameThread.setDaemon(true);
        gameThread.start();
    }

    public void stop() {
        running = false;
    }

    private void run(Update update, Render render) {
        long lastTime = System.nanoTime();
        double accumulator = 0.0;

        while (running) {
            long now = System.nanoTime();
            double deltaTime = (now - lastTime) / 1_000_000_000.0;
            lastTime = now;
            accumulator += deltaTime;

            while (accumulator >= frameTime) {
                update.update(frameTime);
                accumulator -= frameTime;
            }

            render.render();

            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {}
        }
    }
}
