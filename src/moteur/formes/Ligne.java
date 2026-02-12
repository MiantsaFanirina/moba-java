package moteur.formes;

import moteur.Vecteur2;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.QuadCurve2D;

/**
 * Ligne droite entre deux points.
 */
public class Ligne extends Forme {
    
    private Vecteur2 pointFin;
    private boolean estFlechee;
    private double tailleFleche;
    
    public Ligne(Vecteur2 pointDepart, Vecteur2 pointFin) {
        super(pointDepart, Color.BLACK, false, 1.0);
        this.pointFin = pointFin;
        this.estFlechee = false;
        this.tailleFleche = 10.0;
    }
    
    public Ligne(Vecteur2 pointDepart, Vecteur2 pointFin, boolean estFlechee) {
        this(pointDepart, pointFin);
        this.estFlechee = estFlechee;
        this.tailleFleche = 10.0;
    }
    
    @Override
    public void dessiner(Graphics2D g2) {
        g2.setColor(couleur);
        g2.setStroke(new java.awt.BasicStroke((float) epaisseur));
        
        // Dessiner la ligne principale
        g2.drawLine(
            (int) position.x, (int) position.y,
            (int) pointFin.x, (int) pointFin.y
        );
        
        // Dessiner la flèche si nécessaire
        if (estFlechee) {
            dessinerFleche(g2);
        }
    }
    
    // Dessiner une flèche à la fin de la ligne
    private void dessinerFleche(Graphics2D g2) {
        double angle = Math.atan2(pointFin.y - position.y, pointFin.x - position.x);
        
        double angleFleche1 = angle - Math.PI / 6;
        double angleFleche2 = angle + Math.PI / 6;
        
        // Calculer les points de la flèche
        Vecteur2 flechePoint1 = new Vecteur2(
            pointFin.x - tailleFleche * Math.cos(angleFleche1),
            pointFin.y - tailleFleche * Math.sin(angleFleche1)
        );
        
        Vecteur2 flechePoint2 = new Vecteur2(
            pointFin.x - tailleFleche * Math.cos(angleFleche2),
            pointFin.y - tailleFleche * Math.sin(angleFleche2)
        );
        
        // Dessiner la flèche
        int[] xPoints = {
            (int) pointFin.x, (int) pointFin.y,
            (int) flechePoint1.x, (int) flechePoint1.y,
            (int) flechePoint2.x, (int) flechePoint2.y,
            (int) pointFin.x, (int) pointFin.y
        };
        int[] yPoints = {
            (int) pointFin.y, (int) pointFin.y,
            (int) flechePoint1.y, (int) flechePoint1.y,
            (int) flechePoint2.y, (int) flechePoint2.y,
            (int) pointFin.y, (int) pointFin.y
        };
        
        g2.fillPolygon(xPoints, yPoints, 4);
    }
    
    @Override
    public boolean contientPoint(Vecteur2 point) {
        return distancePointLigne(point) < epaisseur * 2;
    }
    
    @Override
    public double calculerAire() {
        return 0; // Une ligne n'a pas d'aire
    }
    
    @Override
    public double calculerPerimetre() {
        return position.distance(pointFin);
    }
    
    // Calculer la distance d'un point à la ligne
    private double distancePointLigne(Vecteur2 point) {
        double longueurLigne = position.distance(pointFin);
        
        if (longueurLigne == 0) {
            return point.distance(position);
        }
        
        double t = ((point.x - position.x) * (pointFin.x - position.x) + 
                   (point.y - position.y) * (pointFin.y - position.y)) / 
                  (longueurLigne * longueurLigne);
        
        t = Math.max(0, Math.min(1, t));
        
        Vecteur2 pointPlusProche = new Vecteur2(
            position.x + t * (pointFin.x - position.x),
            position.y + t * (pointFin.y - position.y)
        );
        
        return point.distance(pointPlusProche);
    }
    
    // Obtenir le point milieu de la ligne
    public Vecteur2 obtenirPointMilieu() {
        return new Vecteur2(
            (position.x + pointFin.x) / 2,
            (position.y + pointFin.y) / 2
        );
    }
    
    // Obtenir la direction de la ligne
    public Vecteur2 obtenirDirection() {
        return pointFin.soustraire(position).normaliser();
    }
    
    // Étendre la ligne
    public Ligne etendre(double distance) {
        Vecteur2 direction = obtenirDirection();
        Vecteur2 nouveauPointFin = pointFin.ajouter(direction.multiplier(distance));
        return new Ligne(position, nouveauPointFin);
    }
    
    // Getters et setters
    public Vecteur2 getPointFin() { return pointFin; }
    public boolean estFlechee() { return estFlechee; }
    public double getTailleFleche() { return tailleFleche; }
    
    public void setPointFin(Vecteur2 point) { this.pointFin = point; }
    public void setEstFlechee(boolean estFlechee) { this.estFlechee = estFlechee; }
    public void setTailleFleche(double taille) { this.tailleFleche = Math.max(5, taille); }
}