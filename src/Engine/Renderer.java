package Engine;

import java.awt.*;

/**
 * Rendu simple qui dessine dans un contexte Graphics2D.
 * Utilisé par les entités et le monde pour l'affichage.
 */
public class Renderer {

    private Graphics2D g2;

    public void setGraphics(Graphics2D g2) {
        this.g2 = g2;
    }

    public void drawRect(double x, double y, double w, double h) {
        if (g2 == null) return;
        g2.fillRect((int) x, (int) y, (int) w, (int) h);
    }

    public void drawRectOutline(double x, double y, double w, double h) {
        if (g2 == null) return;
        g2.drawRect((int) x, (int) y, (int) w, (int) h);
    }

    public void setColor(Color c) {
        if (g2 != null) g2.setColor(c);
    }

    /** Dessine un chemin courbe à travers les points (Bezier quadratique) */
    public void drawCurvedPath(java.util.List<Vector2> points) {
        if (g2 == null || points == null || points.size() < 2) return;
        java.awt.geom.Path2D.Double path = new java.awt.geom.Path2D.Double();
        path.moveTo(points.get(0).x, points.get(0).y);

        for (int i = 1; i < points.size(); i++) {
            Vector2 curr = points.get(i);
            if (i < points.size() - 1) {
                Vector2 next = points.get(i + 1);
                path.quadTo(curr.x, curr.y, (curr.x + next.x) / 2, (curr.y + next.y) / 2);
            } else {
                path.lineTo(curr.x, curr.y);
            }
        }
        g2.draw(path);
    }

    public void drawLine(double x1, double y1, double x2, double y2) {
        if (g2 == null) return;
        g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
    }
}
