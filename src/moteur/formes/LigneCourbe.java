package moteur.formes;

import moteur.Vecteur2;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

/**
 * Ligne courbe (bézier) pour les chemins courbes.
 */
public class LigneCourbe extends Forme {
    
    private Vecteur2 debut;
    private Vecteur2 controle1;
    private Vecteur2 controle2;
    private Vecteur2 fin;
    private int resolution;
    
    public LigneCourbe(Vecteur2 debut, Vecteur2 controle1, Vecteur2 controle2, 
                       Vecteur2 fin, int resolution) {
        super(debut, Color.CYAN, false, 2.0);
        this.debut = debut;
        this.controle1 = controle1;
        this.controle2 = controle2;
        this.fin = fin;
        this.resolution = Math.max(10, resolution); // Minimum 10 points
    }
    
    public LigneCourbe(Vecteur2 debut, Vecteur2 controle, Vecteur2 fin, int resolution) {
        this(debut, controle, controle, fin, resolution);
    }
    
    @Override
    public void dessiner(Graphics2D g2) {
        g2.setColor(couleur);
        g2.setStroke(new java.awt.BasicStroke((float) epaisseur));
        
        Path2D path = new Path2D.Double();
        path.moveTo(debut.x, debut.y);
        
        // Courbe de Bézier cubique
        for (int i = 1; i <= resolution; i++) {
            double t = (double) i / resolution;
            Vecteur2 point = calculerPointBezier(t);
            path.lineTo(point.x, point.y);
        }
        
        g2.draw(path);
        
        // Dessiner les points de contrôle (optionnel)
        g2.setColor(Color.GRAY);
        g2.fillOval((int) debut.x - 2, (int) debut.y - 2, 4, 4);
        g2.fillOval((int) fin.x - 2, (int) fin.y - 2, 4, 4);
        
        g2.setColor(Color.ORANGE);
        g2.fillOval((int) controle1.x - 2, (int) controle1.y - 2, 4, 4);
        if (controle2 != controle1) {
            g2.fillOval((int) controle2.x - 2, (int) controle2.y - 2, 4, 4);
        }
    }
    
    /**
     * Calcule un point sur la courbe de Bézier en fonction du paramètre t (0-1).
     */
    private Vecteur2 calculerPointBezier(double t) {
        double u = 1 - t;
        double tt = t * t;
        double uu = u * u;
        double uuu = uu * u;
        double ttt = tt * t;
        
        // Courbe de Bézier cubique
        Vecteur2 point = new Vecteur2(
            uuu * debut.x + 3 * uu * t * controle1.x + 3 * u * tt * controle2.x + ttt * fin.x,
            uuu * debut.y + 3 * uu * t * controle1.y + 3 * u * tt * controle2.y + ttt * fin.y
        );
        
        return point;
    }
    
    @Override
    public boolean contientPoint(Vecteur2 point) {
        // Simplifié : vérifier si le point est proche de la courbe
        for (int i = 0; i <= resolution; i++) {
            double t = (double) i / resolution;
            Vecteur2 pointCourbe = calculerPointBezier(t);
            if (point.distance(pointCourbe) < 5.0) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public double calculerAire() {
        return 0; // Une ligne n'a pas d'aire
    }
    
    @Override
    public double calculerPerimetre() {
        double perimetre = 0;
        Vecteur2 pointPrecedent = debut;
        
        for (int i = 1; i <= resolution; i++) {
            double t = (double) i / resolution;
            Vecteur2 pointActuel = calculerPointBezier(t);
            perimetre += pointPrecedent.distance(pointActuel);
            pointPrecedent = pointActuel;
        }
        
        return perimetre;
    }
    
    /**
     * Obtient la liste des points le long de la courbe.
     */
    public java.util.List<Vecteur2> getPoints() {
        java.util.List<Vecteur2> points = new java.util.ArrayList<>();
        for (int i = 0; i <= resolution; i++) {
            double t = (double) i / resolution;
            points.add(calculerPointBezier(t));
        }
        return points;
    }
    
    /**
     * Simplifie la courbe en une ligne droite si elle est assez proche.
     */
    public boolean peutEtreSimplifiee(double tolerance) {
        return debut.distance(fin) < tolerance && 
               controle1.distance(debut) < tolerance && 
               controle2.distance(fin) < tolerance;
    }
    
    // Getters et setters
    public Vecteur2 getDebut() { return debut; }
    public Vecteur2 getControle1() { return controle1; }
    public Vecteur2 getControle2() { return controle2; }
    public Vecteur2 getFin() { return fin; }
    public int getResolution() { return resolution; }
    
    public void setDebut(Vecteur2 debut) { this.debut = debut; }
    public void setControle1(Vecteur2 controle1) { this.controle1 = controle1; }
    public void setControle2(Vecteur2 controle2) { this.controle2 = controle2; }
    public void setFin(Vecteur2 fin) { this.fin = fin; }
    public void setResolution(int resolution) { 
        this.resolution = Math.max(10, resolution); 
    }
}