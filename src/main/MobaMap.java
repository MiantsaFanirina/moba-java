package main;

import Engine.CollisionUtils;
import Engine.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Carte MOBA : 3 voies connectées par la jungle et la rivière.
 * Chaque voie a 3 tours. Radiant bas-gauche, Dire haut-droite.
 */
public class MobaMap {

    private static final int MARGIN = 2;

    public final int worldCols;
    public final int worldRows;
    public final int tileSize;
    public final double worldWidth;
    public final double worldHeight;

    private final boolean[][] walkable;
    private final List<CollisionUtils.Rect> colliders;
    private final int cellsPerTile;
    public final int gridCols;
    public final int gridRows;

    public enum TileType { LANE, JUNGLE, RIVER, TREE, TOWER, BASE, ANCIENT, BOUNDARY }

    private final TileType[][] tileTypes;
    private final List<TowerInfo> towers = new ArrayList<>();

    public static class TowerInfo {
        public final int tx, ty;

        public TowerInfo(int tx, int ty) {
            this.tx = tx;
            this.ty = ty;
        }
    }

    public MobaMap(int worldCols, int worldRows, int tileSize, int pathfindingCellSize) {
        this.worldCols = worldCols;
        this.worldRows = worldRows;
        this.tileSize = tileSize;
        this.worldWidth = worldCols * tileSize;
        this.worldHeight = worldRows * tileSize;
        this.cellsPerTile = tileSize / pathfindingCellSize;
        this.gridCols = worldCols * cellsPerTile;
        this.gridRows = worldRows * cellsPerTile;

        this.walkable = new boolean[gridCols][gridRows];
        this.colliders = new ArrayList<>();
        this.tileTypes = new TileType[worldCols][worldRows];
        buildMap();
    }

    private void buildMap() {
        int rx = MARGIN;
        int ry = MARGIN;
        int rw = worldCols - 2 * MARGIN;
        int rh = worldRows - 2 * MARGIN;

        // Tout en jungle (blanc/marchable) par défaut
        for (int ty = ry; ty < ry + rh; ty++) {
            for (int tx = rx; tx < rx + rw; tx++) {
                setTile(tx, ty, TileType.JUNGLE, true);
            }
        }

        int laneW = 4;
        int midX = rx + rw / 2;
        int midY = ry + rh / 2;

        // === RIVIÈRE (diagonale, bleue, traverse le centre) ===
        int riverW = 10;
        for (int ty = ry; ty < ry + rh; ty++) {
            for (int tx = rx; tx < rx + rw; tx++) {
                double dist = Math.abs((tx - midX) - (ty - midY));
                if (dist < riverW) {
                    setTile(tx, ty, TileType.RIVER, true);  // rivière marchable
                }
            }
        }

        // === VOIE DU HAUT ===
        int topY = ry + rh / 8;
        for (int tx = rx; tx < rx + rw; tx++) {
            for (int dy = -laneW / 2; dy <= laneW / 2; dy++) {
                setTile(tx, topY + dy, TileType.LANE, true);
            }
        }

        // === VOIE DU BAS ===
        int botY = ry + rh - rh / 8;
        for (int tx = rx; tx < rx + rw; tx++) {
            for (int dy = -laneW / 2; dy <= laneW / 2; dy++) {
                setTile(tx, botY + dy, TileType.LANE, true);
            }
        }

        // === VOIE DU MILIEU (diagonale) ===
        for (int i = 0; i <= rw; i++) {
            double t = i / (double) rw;
            int cx = rx + (int) (rw * t);
            int cy = ry + (int) (rh - rh * t);
            for (int d = -laneW / 2; d <= laneW / 2; d++) {
                setTile(cx + d, cy, TileType.LANE, true);
                setTile(cx, cy + d, TileType.LANE, true);
            }
        }

        // === CONNEXIONS JUNGLE (relient les 3 voies) ===
        int connW = 4;
        fillRect(rx + rw / 4 - connW / 2, ry, connW, rh, TileType.JUNGLE, true);
        fillRect(rx + 3 * rw / 4 - connW / 2, ry, connW, rh, TileType.JUNGLE, true);
        fillRect(rx, ry + rh / 4 - connW / 2, rw, connW, TileType.JUNGLE, true);
        fillRect(rx, ry + 3 * rh / 4 - connW / 2, rw, connW, TileType.JUNGLE, true);

        // === ARBRES dans la jungle (obstacles verts) ===
        placeTrees(rx, ry, rw, rh, midX, midY, riverW);

        // === 3 TOURS PAR VOIE ===
        // Voie haut - 3 tours
        placeTower(rx + rw / 6, topY);
        placeTower(rx + rw / 2, topY);
        placeTower(rx + 5 * rw / 6, topY);

        // Voie bas - 3 tours
        placeTower(rx + rw / 6, botY);
        placeTower(rx + rw / 2, botY);
        placeTower(rx + 5 * rw / 6, botY);

        // Voie milieu - 3 tours
        placeTower(rx + rw / 6, ry + (int) (rh - rh / 6));
        placeTower(midX, midY);
        placeTower(rx + 5 * rw / 6, ry + (int) (rh / 6));

        // === BASES ET ANCIENS ===
        fillRect(rx, ry + rh - 22, 28, 22, TileType.BASE, true);
        fillRect(rx + 8, ry + rh - 16, 8, 8, TileType.ANCIENT, true);

        fillRect(rx + rw - 28, ry, 28, 22, TileType.BASE, true);
        fillRect(rx + rw - 18, ry + 8, 8, 8, TileType.ANCIENT, true);

        // === BORDURES (gris, non marchable) ===
        for (int ty = 0; ty < worldRows; ty++) {
            for (int tx = 0; tx < worldCols; tx++) {
                if (tx < rx || tx >= rx + rw || ty < ry || ty >= ry + rh) {
                    setTile(tx, ty, TileType.BOUNDARY, false);
                }
            }
        }

        buildMergedColliders();
    }

    private void placeTrees(int rx, int ry, int rw, int rh, int midX, int midY, int riverW) {
        java.util.Random r = new java.util.Random(54321);
        for (int i = 0; i < (rw * rh) / 40; i++) {
            int tx = rx + r.nextInt(rw);
            int ty = ry + r.nextInt(rh);
            double distToRiver = Math.abs((tx - midX) - (ty - midY));
            if (distToRiver > riverW + 5 && tileTypes[tx][ty] == TileType.JUNGLE) {
                setTile(tx, ty, TileType.TREE, false);
            }
        }
    }

    private void placeTower(int tx, int ty) {
        if (tx < 0 || ty < 0 || tx >= worldCols || ty >= worldRows) return;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = tx + dx, ny = ty + dy;
                if (nx >= 0 && nx < worldCols && ny >= 0 && ny < worldRows) {
                    setTile(nx, ny, TileType.TOWER, false);
                }
            }
        }
        towers.add(new TowerInfo(tx, ty));
    }

    private void setTile(int tx, int ty, TileType type, boolean walk) {
        if (tx < 0 || tx >= worldCols || ty < 0 || ty >= worldRows) return;
        tileTypes[tx][ty] = type;
        setTileWalkable(tx, ty, walk);
    }

    private void fillRect(int tx, int ty, int w, int h, TileType type, boolean walk) {
        for (int dy = 0; dy < h; dy++) {
            for (int dx = 0; dx < w; dx++) {
                setTile(tx + dx, ty + dy, type, walk);
            }
        }
    }

    private void setTileWalkable(int tx, int ty, boolean value) {
        if (tx < 0 || tx >= worldCols || ty < 0 || ty >= worldRows) return;
        int gx0 = tx * cellsPerTile;
        int gy0 = ty * cellsPerTile;
        for (int gx = 0; gx < cellsPerTile; gx++) {
            for (int gy = 0; gy < cellsPerTile; gy++) {
                int ix = gx0 + gx;
                int iy = gy0 + gy;
                if (ix < gridCols && iy < gridRows) walkable[ix][iy] = value;
            }
        }
    }

    private boolean isTileWalkable(int tx, int ty) {
        if (tx < 0 || tx >= worldCols || ty < 0 || ty >= worldRows) return false;
        int gx = tx * cellsPerTile;
        int gy = ty * cellsPerTile;
        return gx < gridCols && gy < gridRows && walkable[gx][gy];
    }

    private void buildMergedColliders() {
        boolean[][] blocked = new boolean[worldCols][worldRows];
        for (int ty = 0; ty < worldRows; ty++) {
            for (int tx = 0; tx < worldCols; tx++) {
                blocked[tx][ty] = !isTileWalkable(tx, ty);
            }
        }
        for (int ty = 0; ty < worldRows; ty++) {
            for (int tx = 0; tx < worldCols; ) {
                if (!blocked[tx][ty]) {
                    tx++;
                    continue;
                }
                int w = 0;
                while (tx + w < worldCols && blocked[tx + w][ty]) w++;
                int h = 1;
                while (ty + h < worldRows) {
                    boolean full = true;
                    for (int dx = 0; dx < w && full; dx++) {
                        if (!blocked[tx + dx][ty + h]) full = false;
                    }
                    if (!full) break;
                    for (int dx = 0; dx < w; dx++) blocked[tx + dx][ty + h] = false;
                    h++;
                }
                for (int dx = 0; dx < w; dx++) blocked[tx + dx][ty] = false;
                colliders.add(new CollisionUtils.Rect(tx * tileSize, ty * tileSize, w * tileSize, h * tileSize));
                tx += w;
            }
        }
    }

    public boolean[][] getWalkableGrid() {
        return walkable;
    }

    public List<CollisionUtils.Rect> getColliders() {
        return colliders;
    }

    public TileType getTileType(int tx, int ty) {
        if (tx < 0 || tx >= worldCols || ty < 0 || ty >= worldRows) return TileType.BOUNDARY;
        return tileTypes[tx][ty];
    }

    public List<TowerInfo> getTowers() {
        return towers;
    }

    public Vector2 getAllySpawn() {
        return new Vector2((MARGIN + 12) * tileSize, (worldRows - MARGIN - 14) * tileSize);
    }
}
