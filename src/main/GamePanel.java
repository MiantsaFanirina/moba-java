package main;

import Engine.Camera2D;
import Engine.CollisionUtils;
import Engine.GameLoop;
import Engine.PathfindingUtils;
import Engine.Player;
import Engine.Renderer;
import Engine.Vector2;
import Engine.World;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panneau principal du jeu.
 * Clic droit pour déplacer, molette pour zoomer.
 */
public class GamePanel extends JPanel {

    final int tileSize = 40;
    final int pathfindingCellSize = 8;

    final int worldCols = 128;
    final int worldRows = 128;
    final double worldWidth;
    final double worldHeight;

    private int viewWidth;
    private int viewHeight;
    private static final int MINIMAP_SIZE = 200;
    private static final int MINIMAP_MARGIN = 16;

    private World world;
    private Player player;
    private Camera2D camera;
    private Renderer renderer;
    private GameLoop gameLoop;
    private MobaMap mobaMap;

    private boolean[][] walkableGrid;
    private final int gridCols;
    private final int gridRows;
    private BufferedImage minimapCache;

    public GamePanel(int largeurInitiale, int hauteurInitiale) {
        this.viewWidth = largeurInitiale;
        this.viewHeight = hauteurInitiale;
        this.worldWidth = worldCols * tileSize;
        this.worldHeight = worldRows * tileSize;
        this.gridCols = worldCols * tileSize / pathfindingCellSize;
        this.gridRows = worldRows * tileSize / pathfindingCellSize;

        mobaMap = new MobaMap(worldCols, worldRows, tileSize, pathfindingCellSize);
        walkableGrid = mobaMap.getWalkableGrid();

        this.setPreferredSize(new Dimension(viewWidth, viewHeight));
        this.setBackground(Color.WHITE);
        setFocusable(true);

        initWorld();
        initPlayer();
        initCamera();
        initInput();
        initGameLoop();
        initResizeHandler();
    }

    private void initResizeHandler() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                if (w > 0 && h > 0) {
                    viewWidth = w;
                    viewHeight = h;
                    camera.setViewportSize(getLargeurVue(), getHauteurVue());
                }
            }
        });
    }

    private int getLargeurVue() { return viewWidth; }
    private int getHauteurVue() { return viewHeight; }

    private void initWorld() {
        world = new World();
        for (CollisionUtils.Rect r : mobaMap.getColliders()) {
            world.addCollider(r);
        }
    }

    private void initPlayer() {
        Vector2 spawn = mobaMap.getAllySpawn();
        player = new Player(spawn.x, spawn.y);
        player.setPathRecalculator(this::recalculerChemin);
        world.addEntity(player);
    }

    private void initCamera() {
        camera = new Camera2D(getLargeurVue(), getHauteurVue());
        camera.follow(player.getPosition());
        camera.setWorldBounds(worldWidth, worldHeight);
        camera.setSmoothing(0.12);
    }

    private void initInput() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON3) return;

                int mx = e.getX();
                int my = e.getY();
                Vector2 worldClick;

                if (estDansMinimap(mx, my)) {
                    worldClick = minimapVersMonde(mx, my);
                } else {
                    worldClick = camera.screenToWorld(new Vector2(mx, my));
                }

                demanderDeplacement(worldClick);
            }
        });

        addMouseWheelListener(e -> {
            int rot = e.getWheelRotation();
            camera.addZoom(rot > 0 ? -0.15 : 0.15);
        });
    }

    /** Recalcule un chemin de 'from' vers 'to' (utilisé quand le héros est bloqué) */
    private List<Vector2> recalculerChemin(Vector2 from, Vector2 to) {
        int px = (int) (from.x / pathfindingCellSize);
        int py = (int) (from.y / pathfindingCellSize);
        int gx = (int) (to.x / pathfindingCellSize);
        int gy = (int) (to.y / pathfindingCellSize);

        px = Math.max(0, Math.min(px, gridCols - 1));
        py = Math.max(0, Math.min(py, gridRows - 1));
        gx = Math.max(0, Math.min(gx, gridCols - 1));
        gy = Math.max(0, Math.min(gy, gridRows - 1));

        PathfindingUtils.Node start = walkableGrid[px][py]
                ? new PathfindingUtils.Node(px, py)
                : trouverProcheMarchable(px, py);
        PathfindingUtils.Node goal = walkableGrid[gx][gy]
                ? new PathfindingUtils.Node(gx, gy)
                : trouverProcheMarchable(gx, gy);

        List<PathfindingUtils.Node> nodePath = PathfindingUtils.findPath(walkableGrid, start, goal);
        if (nodePath.isEmpty()) return null;
        return noeudsVersCheminMonde(nodePath);
    }

    private void demanderDeplacement(Vector2 worldClick) {
        int gx = (int) (worldClick.x / pathfindingCellSize);
        int gy = (int) (worldClick.y / pathfindingCellSize);
        gx = Math.max(0, Math.min(gx, gridCols - 1));
        gy = Math.max(0, Math.min(gy, gridRows - 1));

        Vector2 posJoueur = player.getPosition();
        int px = (int) (posJoueur.x / pathfindingCellSize);
        int py = (int) (posJoueur.y / pathfindingCellSize);
        px = Math.max(0, Math.min(px, gridCols - 1));
        py = Math.max(0, Math.min(py, gridRows - 1));

        PathfindingUtils.Node start = walkableGrid[px][py]
                ? new PathfindingUtils.Node(px, py)
                : trouverProcheMarchable(px, py);
        PathfindingUtils.Node goal = walkableGrid[gx][gy]
                ? new PathfindingUtils.Node(gx, gy)
                : trouverProcheMarchable(gx, gy);

        List<PathfindingUtils.Node> nodePath = PathfindingUtils.findPath(walkableGrid, start, goal);
        List<Vector2> worldPath = noeudsVersCheminMonde(nodePath);
        player.moveTo(worldPath, 220);
    }

    private boolean estDansMinimap(int x, int y) {
        int mmX = viewWidth - MINIMAP_SIZE - MINIMAP_MARGIN;
        int mmY = viewHeight - MINIMAP_SIZE - MINIMAP_MARGIN;
        return x >= mmX && x < mmX + MINIMAP_SIZE && y >= mmY && y < mmY + MINIMAP_SIZE;
    }

    private Vector2 minimapVersMonde(int screenX, int screenY) {
        int mmX = viewWidth - MINIMAP_SIZE - MINIMAP_MARGIN;
        int mmY = viewHeight - MINIMAP_SIZE - MINIMAP_MARGIN;
        double normX = (screenX - mmX) / (double) MINIMAP_SIZE;
        double normY = (screenY - mmY) / (double) MINIMAP_SIZE;
        normX = Math.max(0, Math.min(1, normX));
        normY = Math.max(0, Math.min(1, normY));
        return new Vector2(normX * worldWidth, normY * worldHeight);
    }

    private PathfindingUtils.Node trouverProcheMarchable(int gx, int gy) {
        int r = 1;
        int maxR = Math.max(gridCols, gridRows);
        while (r < maxR) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dy = -r; dy <= r; dy++) {
                    if (Math.abs(dx) != r && Math.abs(dy) != r) continue;
                    int nx = gx + dx;
                    int ny = gy + dy;
                    if (nx >= 0 && nx < gridCols && ny >= 0 && ny < gridRows && walkableGrid[nx][ny]) {
                        return new PathfindingUtils.Node(nx, ny);
                    }
                }
            }
            r++;
        }
        return new PathfindingUtils.Node(gx, gy);
    }

    private List<Vector2> noeudsVersCheminMonde(List<PathfindingUtils.Node> nodes) {
        List<Vector2> path = new ArrayList<>();
        double half = pathfindingCellSize / 2.0;
        for (PathfindingUtils.Node n : nodes) {
            path.add(new Vector2(n.x * pathfindingCellSize + half, n.y * pathfindingCellSize + half));
        }
        return path;
    }

    private void initGameLoop() {
        renderer = new Renderer();
        gameLoop = new GameLoop(60);
        gameLoop.start(this::update, this::repaint);
    }

    private void update(double deltaTime) {
        camera.follow(player.getPosition());
        camera.update(deltaTime);
        world.update(deltaTime);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int mw = getLargeurVue();
        int mh = getHauteurVue();

        renderer.setGraphics(g2);

        g2.clipRect(0, 0, mw, mh);
        dessinerMonde(g2, mw, mh);
        dessinerChemin(g2);
        world.render(renderer, camera);
        g2.setClip(null);

        dessinerMinimap(g2);
    }

    private void dessinerMonde(Graphics2D g2, int viewW, int viewH) {
        Vector2 camPos = camera.getPosition();
        double zoom = camera.getZoom();
        int startCol = (int) (camPos.x / tileSize);
        int startRow = (int) (camPos.y / tileSize);
        int endCol = startCol + (int) (viewW / (tileSize * zoom)) + 2;
        int endRow = startRow + (int) (viewH / (tileSize * zoom)) + 2;

        for (int row = Math.max(0, startRow); row < Math.min(worldRows, endRow); row++) {
            for (int col = Math.max(0, startCol); col < Math.min(worldCols, endCol); col++) {
                double x = (col * tileSize - camPos.x) * zoom;
                double y = (row * tileSize - camPos.y) * zoom;
                int size = (int) (tileSize * zoom) + 1;

                MobaMap.TileType type = mobaMap.getTileType(col, row);
                Color c = getCouleurTuile(type, col, row);
                g2.setColor(c);
                g2.fillRect((int) x, (int) y, size, size);
            }
        }

        for (MobaMap.TowerInfo t : mobaMap.getTowers()) {
            double sx = (t.tx * tileSize - camPos.x) * zoom;
            double sy = (t.ty * tileSize - camPos.y) * zoom;
            if (sx + tileSize * 3 * zoom >= -50 && sx <= viewW + 50 && sy + tileSize * 3 * zoom >= -50 && sy <= viewH + 50) {
                g2.setColor(new Color(220, 200, 80));
                g2.fillRect((int) sx - (int) (tileSize * zoom), (int) sy - (int) (tileSize * zoom),
                        (int) (tileSize * 3 * zoom), (int) (tileSize * 3 * zoom));
                g2.setColor(new Color(180, 160, 50));
                g2.drawRect((int) sx - (int) (tileSize * zoom), (int) sy - (int) (tileSize * zoom),
                        (int) (tileSize * 3 * zoom), (int) (tileSize * 3 * zoom));
            }
        }

        for (CollisionUtils.Rect r : world.getColliders()) {
            double sx = (r.x - camPos.x) * zoom;
            double sy = (r.y - camPos.y) * zoom;
            int rw = (int) (r.width * zoom);
            int rh = (int) (r.height * zoom);
            if (sx + rw >= -tileSize && sx <= viewW + tileSize && sy + rh >= -tileSize && sy <= viewH + tileSize) {
                g2.setColor(new Color(140, 140, 140));
                g2.fillRect((int) sx, (int) sy, rw, rh);
            }
        }
    }

    private Color getCouleurTuile(MobaMap.TileType type, int col, int row) {
        switch (type) {
            case RIVER: return new Color(80, 140, 220);
            case LANE: return new Color(210, 180, 140);
            case JUNGLE: return Color.WHITE;
            case BASE:
            case ANCIENT:
            case TOWER: return new Color(220, 200, 80);
            case TREE: return new Color(70, 140, 80);
            case BOUNDARY:
            default: return new Color(140, 140, 140);
        }
    }

    private void dessinerChemin(Graphics2D g2) {
        List<Vector2> path = player.getCurrentPath();
        if (path == null || path.size() < 2) return;

        List<Vector2> screenPath = new ArrayList<>();
        for (Vector2 w : path) {
            screenPath.add(camera.worldToScreen(w));
        }
        g2.setColor(new Color(100, 180, 100, 200));
        g2.setStroke(new BasicStroke(3));
        renderer.drawCurvedPath(screenPath);
        g2.setStroke(new BasicStroke(1));
    }

    private void dessinerMinimap(Graphics2D g2) {
        int mmX = viewWidth - MINIMAP_SIZE - MINIMAP_MARGIN;
        int mmY = viewHeight - MINIMAP_SIZE - MINIMAP_MARGIN;

        double scaleX = MINIMAP_SIZE / worldWidth;
        double scaleY = MINIMAP_SIZE / worldHeight;

        if (minimapCache == null) {
            minimapCache = new BufferedImage(MINIMAP_SIZE, MINIMAP_SIZE, BufferedImage.TYPE_INT_ARGB);
            Graphics2D mg = minimapCache.createGraphics();
            for (int ty = 0; ty < worldRows; ty++) {
                for (int tx = 0; tx < worldCols; tx++) {
                    int gx = tx * tileSize / pathfindingCellSize;
                    int gy = ty * tileSize / pathfindingCellSize;
                    if (gx >= gridCols || gy >= gridRows) continue;
                    int px = (int) ((tx * tileSize + tileSize / 2.0) * scaleX);
                    int py = (int) ((ty * tileSize + tileSize / 2.0) * scaleY);
                    px = Math.max(0, Math.min(MINIMAP_SIZE - 1, px));
                    py = Math.max(0, Math.min(MINIMAP_SIZE - 1, py));
                    MobaMap.TileType type = mobaMap.getTileType(tx, ty);
                    Color c = walkableGrid[gx][gy] ? Color.WHITE : new Color(140, 140, 140);
                    if (type == MobaMap.TileType.RIVER) c = new Color(80, 140, 220);
                    if (type == MobaMap.TileType.LANE) c = new Color(210, 180, 140);
                    if (type == MobaMap.TileType.TREE) c = new Color(70, 140, 80);
                    if (type == MobaMap.TileType.BOUNDARY) c = new Color(140, 140, 140);
                    if (type == MobaMap.TileType.BASE || type == MobaMap.TileType.ANCIENT || type == MobaMap.TileType.TOWER)
                        c = new Color(220, 200, 80);
                    mg.setColor(c);
                    mg.fillRect(px, py, 2, 2);
                }
            }
            mg.dispose();
        }

        g2.setColor(new Color(240, 240, 240));
        g2.fillRoundRect(mmX - 3, mmY - 3, MINIMAP_SIZE + 6, MINIMAP_SIZE + 6, 10, 10);
        if (minimapCache != null) {
            g2.drawImage(minimapCache, mmX, mmY, MINIMAP_SIZE, MINIMAP_SIZE, null);
        }
        g2.setColor(new Color(120, 120, 120));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(mmX - 3, mmY - 3, MINIMAP_SIZE + 6, MINIMAP_SIZE + 6, 10, 10);
        g2.setStroke(new BasicStroke(1));

        Vector2 playerPos = player.getPosition();
        int ppx = mmX + (int) (playerPos.x * scaleX);
        int ppy = mmY + (int) (playerPos.y * scaleY);
        ppx = Math.max(mmX, Math.min(mmX + MINIMAP_SIZE - 1, ppx));
        ppy = Math.max(mmY, Math.min(mmY + MINIMAP_SIZE - 1, ppy));
        g2.setColor(new Color(220, 50, 50));
        g2.fillOval(ppx - 4, ppy - 4, 8, 8);
        g2.setColor(Color.BLACK);
        g2.drawOval(ppx - 4, ppy - 4, 8, 8);
    }
}
