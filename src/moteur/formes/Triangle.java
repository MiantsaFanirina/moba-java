package moteur.formes;

import moteur.Vecteur2;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

/**
 * Triangle géométrique avec trois sommets.
 */
public class Triangle extends Forme {
    
    private Vecteur2 sommet1;
    private Vecteur2 sommet2;
    private Vecteur2 sommet3;
    
    public Triangle(Vecteur2 sommet1, Vecteur2 sommet2, Vecteur2 sommet3) {
        super(new Vecteur2(
            (sommet1.x + sommet2.x + sommet3.x) / 3,
            (sommet1.y + sommet2.y + sommet3.y) / 3
        ), Color.YELLOW, true, 1.0);
        
        this.sommet1 = sommet1;
        this.sommet2 = sommet2;
        this.sommet3 = sommet3;
    }
    
    @Override
    public void dessiner(Graphics2D g2) {
        g2.setColor(couleur);
        
        int[] xPoints = {
            (int) sommet1.x,
            (int) sommet2.x,
            (int) sommet3.x
        };
        int[] yPoints = {
            (int) sommet1.y,
            (int) sommet2.y,
            (int) sommet3.y
        };
        
        if (estRemplie) {
            g2.fillPolygon(xPoints, yPoints, 3);
        }
        
        g2.setColor(couleur.darker());
        g2.setStroke(new java.awt.BasicStroke((float) epaisseur));
        g2.drawPolygon(xPoints, yPoints, 3);
    }
    
    @Override
    public boolean contientPoint(Vecteur2 point) {
        return pointDansTriangle(point, sommet1, sommet2, sommet3);
    }
    
    @Override
    public double calculerAire() {
        // Utiliser la formule de l'aire d'un triangle
        return Math.abs(
            (sommet1.x * (sommet2.y - sommet3.y) +
             sommet2.x * (sommet3.y - sommet1.y) +
             sommet3.x * (sommet1.y - sommet2.y)) / 2
        );
    }
    
    @Override
    public double calculerPerimetre() {
        double cote1 = sommet1.distance(sommet2);
        double cote2 = sommet2.distance(sommet3);
        double cote3 = sommet3.distance(sommet1);
        return cote1 + cote2 + cote3;
    }
    
    // Vérifier si un point est dans un triangle (produit vectoriel)
    private boolean pointDansTriangle(Vecteur2 p, Vecteur2 a, Vecteur2 b, Vecteur2 c) {
        double d1 = signe(p, a, b);
        double d2 = signe(p, b, c);
        double d3 = signe(p, c, a);
        
        boolean hasNeg = (d1 < 0) || (d2 < 0) || (d3 < 0);
        boolean hasPos = (d1 > 0) || (d2 > 0) || (d3 > 0);
        
        return !(hasNeg && hasPos);
    }
    
    // Calculer le signe pour le produit vectoriel
    private double signe(Vecteur2 p1, Vecteur2 p2, Vecteur2 p3) {
        return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
    }
    
    // Obtenir le centre de gravité du triangle
    public Vecteur2 obtenirCentreGravite() {
        return new Vecteur2(
            (sommet1.x + sommet2.x + sommet3.x) / 3,
            (sommet1.y + sommet2.y + sommet3.y) / 3
        );
    }
    
    // Obtenir les angles du triangle
    public double[] obtenirAngles() {
        double angle1 = Math.atan2(sommet2.y - sommet1.y, sommet2.x - sommet1.x);
        double angle2 = Math.atan2(sommet3.y - sommet2.y, sommet3.x - sommet2.x);
        double angle3 = Math.atan2(sommet1.y - sommet3.y, sommet1.x - sommet3.x);
        
        return new double[]{angle1, angle2, angle3};
    }
    
    // Vérifier si le triangle est équilatéral
    public boolean estEquilateral() {
        double cote1 = sommet1.distance(sommet2);
        double cote2 = sommet2.distance(sommet3);
        double cote3 = sommet3.distance(sommet1);
        
        double tolerance = 0.01;
        return Math.abs(cote1 - cote2) < tolerance &&
               Math.abs(cote2 - cote3) < tolerance &&
               Math.abs(cote3 - cote1) < tolerance;
    }
    
    // Obtenir le plus grand côté
    public double obtenirPlusGrandCote() {
        double cote1 = sommet1.distance(sommet2);
        double cote2 = sommet2.distance(sommet3);
        double cote3 = sommet3.distance(sommet1);
        
        return Math.max(cote1, Math.max(cote2, cote3));
    }
    
    // Getters et setters
    public Vecteur2 getSommet1() { return sommet1; }
    public Vecteur2 getSommet2() { return sommet2; }
    public Vecteur2 getSommet3() { return sommet3; }
    
    public void setSommet1(Vecteur2 sommet) { this.sommet1 = sommet; }
    public void setSommet2(Vecteur2 sommet) { this.sommet2 = sommet; }
    public void setSommet3(Vecteur2 sommet) { this.sommet3 = sommet; }
}