package Engine;

import java.util.*;

/**
 * Utilitaires de pathfinding A* pour trouver un chemin entre deux points.
 * Utilise une grille où true = marchable, false = obstacle.
 */
public final class PathfindingUtils {

    private PathfindingUtils() {}

    /** Représente un nœud dans la grille (coordonnées entières) */
    public static class Node {
        public final int x;
        public final int y;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;
            Node node = (Node) o;
            return x == node.x && y == node.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private static class NodeData {
        Node node;
        NodeData parent;
        int g, h, f;

        NodeData(Node node, NodeData parent, int g, int h) {
            this.node = node;
            this.parent = parent;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }
    }

    /**
     * Trouve un chemin entre start et goal sur la grille.
     * @param grid grille [x][y] où true = marchable
     */
    public static List<Node> findPath(boolean[][] grid, Node start, Node goal) {
        int w = grid.length;
        int h = grid[0].length;

        PriorityQueue<NodeData> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        Map<Node, NodeData> allNodes = new HashMap<>();
        Set<Node> closedSet = new HashSet<>();

        NodeData startData = new NodeData(start, null, 0, heuristic(start, goal));
        openSet.add(startData);
        allNodes.put(start, startData);

        while (!openSet.isEmpty()) {
            NodeData current = openSet.poll();

            if (current.node.equals(goal)) {
                return reconstructPath(current);
            }

            closedSet.add(current.node);

            for (Neighbor neighbor : getNeighbors(current.node, grid)) {
                if (closedSet.contains(neighbor.node)) continue;

                int tentativeG = current.g + neighbor.cost;
                NodeData existing = allNodes.get(neighbor.node);

                if (existing == null || tentativeG < existing.g) {
                    NodeData next = new NodeData(neighbor.node, current, tentativeG,
                            heuristic(neighbor.node, goal));
                    allNodes.put(neighbor.node, next);
                    openSet.add(next);
                }
            }
        }
        return Collections.emptyList();
    }

    private static List<Node> reconstructPath(NodeData end) {
        List<Node> path = new ArrayList<>();
        NodeData current = end;
        while (current != null) {
            path.add(current.node);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private static int heuristic(Node a, Node b) {
        int dx = Math.abs(a.x - b.x);
        int dy = Math.abs(a.y - b.y);
        return 10 * (dx + dy) + (14 - 2 * 10) * Math.min(dx, dy);
    }

    private static class Neighbor {
        Node node;
        int cost;
        Neighbor(Node node, int cost) {
            this.node = node;
            this.cost = cost;
        }
    }

    private static List<Neighbor> getNeighbors(Node node, boolean[][] grid) {
        int x = node.x, y = node.y;
        int w = grid.length, h = grid[0].length;
        List<Neighbor> neighbors = new ArrayList<>();

        addIfWalkable(neighbors, grid, x - 1, y, 10);
        addIfWalkable(neighbors, grid, x + 1, y, 10);
        addIfWalkable(neighbors, grid, x, y - 1, 10);
        addIfWalkable(neighbors, grid, x, y + 1, 10);

        addDiagonal(neighbors, grid, x, y, -1, -1);
        addDiagonal(neighbors, grid, x, y, 1, -1);
        addDiagonal(neighbors, grid, x, y, -1, 1);
        addDiagonal(neighbors, grid, x, y, 1, 1);

        return neighbors;
    }

    private static void addIfWalkable(List<Neighbor> list, boolean[][] grid, int x, int y, int cost) {
        if (x >= 0 && y >= 0 && x < grid.length && y < grid[0].length && grid[x][y]) {
            list.add(new Neighbor(new Node(x, y), cost));
        }
    }

    private static void addDiagonal(List<Neighbor> list, boolean[][] grid, int x, int y, int dx, int dy) {
        int nx = x + dx, ny = y + dy;
        if (nx < 0 || ny < 0 || nx >= grid.length || ny >= grid[0].length) return;
        if (!grid[nx][ny]) return;
        if (x + dx < 0 || x + dx >= grid.length || y + dy < 0 || y + dy >= grid[0].length) return;
        if (!grid[x + dx][y] || !grid[x][y + dy]) return;
        list.add(new Neighbor(new Node(nx, ny), 14));
    }
}
