package moteur.formes;

import moteur.Vecteur2;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilitaires pour créer des formes complexes à partir de primitives simples.
 */
public class FormesComplexes {
    
    /**
     * Crée une forme d'étoile complexe
     */
    public static class Etoile extends Forme {
        private int nombrePointes;
        private double rayonExterieur;
        private double rayonInterieur;
        
        public Etoile(Vecteur2 centre, int nombrePointes, double rayonExterieur, double rayonInterieur, Color couleur) {
            super(centre, couleur, true, 1.0);
            this.nombrePointes = nombrePointes;
            this.rayonExterieur = rayonExterieur;
            this.rayonInterieur = rayonInterieur;
        }
        
        @Override
        public void dessiner(Graphics2D g2) {
            g2.setColor(couleur);
            
            int[] xPoints = new int[nombrePointes * 2];
            int[] yPoints = new int[nombrePointes * 2];
            
            for (int i = 0; i < nombrePointes * 2; i++) {
                double angle = Math.PI * i / nombrePointes;
                double rayon = (i % 2 == 0) ? rayonExterieur : rayonInterieur;
                
                xPoints[i] = (int) (position.x + rayon * Math.cos(angle - Math.PI / 2));
                yPoints[i] = (int) (position.y + rayon * Math.sin(angle - Math.PI / 2));
            }
            
            if (estRemplie) {
                g2.fillPolygon(xPoints, yPoints, nombrePointes * 2);
            }
            g2.drawPolygon(xPoints, yPoints, nombrePointes * 2);
        }
        
        @Override
        public boolean contientPoint(Vecteur2 point) {
            return position.distance(point) <= rayonExterieur;
        }
        
        @Override
        public double calculerAire() {
            return nombrePointes * rayonExterieur * rayonInterieur * Math.sin(Math.PI / nombrePointes);
        }
        
        @Override
        public double calculerPerimetre() {
            return 2 * nombrePointes * (rayonExterieur + rayonInterieur);
        }
    }
    
    /**
     * Crée une forme d'hexagone complexe
     */
    public static class Hexagone extends Forme {
        private double taille;
        
        public Hexagone(Vecteur2 centre, double taille, Color couleur, boolean rempli) {
            super(centre, couleur, rempli, 2.0);
            this.taille = taille;
        }
        
        @Override
        public void dessiner(Graphics2D g2) {
            g2.setColor(couleur);
            
            int[] xPoints = new int[6];
            int[] yPoints = new int[6];
            
            for (int i = 0; i < 6; i++) {
                double angle = Math.PI / 3 * i;
                xPoints[i] = (int) (position.x + taille * Math.cos(angle));
                yPoints[i] = (int) (position.y + taille * Math.sin(angle));
            }
            
            if (estRemplie) {
                g2.fillPolygon(xPoints, yPoints, 6);
            }
            g2.drawPolygon(xPoints, yPoints, 6);
        }
        
        @Override
        public boolean contientPoint(Vecteur2 point) {
            return position.distance(point) <= taille;
        }
        
        @Override
        public double calculerAire() {
            return 3 * Math.sqrt(3) / 2 * taille * taille;
        }
        
        @Override
        public double calculerPerimetre() {
            return 6 * taille;
        }
    }
    
    /**
     * Crée une forme de losange (diamant)
     */
    public static class Losange extends Forme {
        private double largeur;
        private double hauteur;
        
        public Losange(Vecteur2 centre, double largeur, double hauteur, Color couleur, boolean rempli) {
            super(centre, couleur, rempli, 2.0);
            this.largeur = largeur;
            this.hauteur = hauteur;
        }
        
        @Override
        public void dessiner(Graphics2D g2) {
            g2.setColor(couleur);
            
            int[] xPoints = {
                (int) position.x,
                (int) (position.x + largeur / 2),
                (int) position.x,
                (int) (position.x - largeur / 2)
            };
            
            int[] yPoints = {
                (int) (position.y - hauteur / 2),
                (int) position.y,
                (int) (position.y + hauteur / 2),
                (int) position.y
            };
            
            if (estRemplie) {
                g2.fillPolygon(xPoints, yPoints, 4);
            }
            g2.drawPolygon(xPoints, yPoints, 4);
        }
        
        @Override
        public boolean contientPoint(Vecteur2 point) {
            double dx = Math.abs(point.x - position.x);
            double dy = Math.abs(point.y - position.y);
            return (dx / (largeur / 2) + dy / (hauteur / 2)) <= 1;
        }
        
        @Override
        public double calculerAire() {
            return largeur * hauteur / 2;
        }
        
        @Override
        public double calculerPerimetre() {
            return 2 * Math.sqrt(largeur * largeur / 4 + hauteur * hauteur / 4);
        }
    }
    
    /**
     * Crée une forme de croix complexe
     */
    public static class Croix extends Forme {
        private double taille;
        private double epaisseur;
        
        public Croix(Vecteur2 centre, double taille, double epaisseur, Color couleur) {
            super(centre, couleur, true, 1.0);
            this.taille = taille;
            this.epaisseur = epaisseur;
        }
        
        @Override
        public void dessiner(Graphics2D g2) {
            g2.setColor(couleur);
            g2.setStroke(new java.awt.BasicStroke((float) epaisseur));
            
            // Barre verticale
            g2.drawLine(
                (int) position.x,
                (int) (position.y - taille / 2),
                (int) position.x,
                (int) (position.y + taille / 2)
            );
            
            // Barre horizontale
            g2.drawLine(
                (int) (position.x - taille / 2),
                (int) position.y,
                (int) (position.x + taille / 2),
                (int) position.y
            );
            
            g2.setStroke(new java.awt.BasicStroke(1.0f));
        }
        
        @Override
        public boolean contientPoint(Vecteur2 point) {
            double dx = Math.abs(point.x - position.x);
            double dy = Math.abs(point.y - position.y);
            return dx <= epaisseur / 2 && dy <= taille / 2 ||
                   dy <= epaisseur / 2 && dx <= taille / 2;
        }
        
        @Override
        public double calculerAire() {
            return taille * epaisseur;
        }
        
        @Override
        public double calculerPerimetre() {
            return 4 * taille;
        }
    }
    
    /**
     * Crée une forme de triangle arrondi
     */
    public static class TriangleArrondi extends Forme {
        private double taille;
        private double rayonArrondi;
        
        public TriangleArrondi(Vecteur2 centre, double taille, double rayonArrondi, Color couleur, boolean rempli) {
            super(centre, couleur, rempli, 2.0);
            this.taille = taille;
            this.rayonArrondi = rayonArrondi;
        }
        
        @Override
        public void dessiner(Graphics2D g2) {
            g2.setColor(couleur);
            
            // Triangle principal
            int[] xPoints = {
                (int) position.x,
                (int) (position.x + taille / 2),
                (int) (position.x - taille / 2)
            };
            
            int[] yPoints = {
                (int) (position.y - taille / 2),
                (int) (position.y + taille / 2),
                (int) (position.y + taille / 2)
            };
            
            if (estRemplie) {
                g2.fillPolygon(xPoints, yPoints, 3);
            }
            g2.drawPolygon(xPoints, yPoints, 3);
            
            // Coins arrondis
            for (int i = 0; i < 3; i++) {
                g2.fillOval(
                    (int) (xPoints[i] - rayonArrondi),
                    (int) (yPoints[i] - rayonArrondi),
                    (int) (rayonArrondi * 2),
                    (int) (rayonArrondi * 2)
                );
            }
        }
        
        @Override
        public boolean contientPoint(Vecteur2 point) {
            return position.distance(point) <= taille;
        }
        
        @Override
        public double calculerAire() {
            return Math.sqrt(3) / 4 * taille * taille;
        }
        
        @Override
        public double calculerPerimetre() {
            return 3 * taille;
        }
    }
    
    /**
     * Crée une forme de spirale complexe
     */
    public static class Spirale extends Forme {
        private double rayonMax;
        private int nombreTours;
        private int pointsParTour;
        
        public Spirale(Vecteur2 centre, double rayonMax, int nombreTours, int pointsParTour, Color couleur) {
            super(centre, couleur, false, 2.0);
            this.rayonMax = rayonMax;
            this.nombreTours = nombreTours;
            this.pointsParTour = pointsParTour;
        }
        
        @Override
        public void dessiner(Graphics2D g2) {
            g2.setColor(couleur);
            g2.setStroke(new java.awt.BasicStroke(2.0f));
            
            int totalPoints = nombreTours * pointsParTour;
            int[] xPoints = new int[totalPoints];
            int[] yPoints = new int[totalPoints];
            
            for (int i = 0; i < totalPoints; i++) {
                double t = (double) i / pointsParTour;
                double rayon = (rayonMax * t) / nombreTours;
                double angle = 2 * Math.PI * t;
                
                xPoints[i] = (int) (position.x + rayon * Math.cos(angle));
                yPoints[i] = (int) (position.y + rayon * Math.sin(angle));
            }
            
            for (int i = 1; i < totalPoints; i++) {
                g2.drawLine(xPoints[i-1], yPoints[i-1], xPoints[i], yPoints[i]);
            }
            
            g2.setStroke(new java.awt.BasicStroke(1.0f));
        }
        
        @Override
        public boolean contientPoint(Vecteur2 point) {
            return position.distance(point) <= rayonMax;
        }
        
        @Override
        public double calculerAire() {
            return Math.PI * rayonMax * rayonMax;
        }
        
        @Override
        public double calculerPerimetre() {
            return 2 * Math.PI * rayonMax * nombreTours;
        }
    }
    
    /**
     * Crée une forme de fleur complexe
     */
    public static class Fleur extends Forme {
        private int nombrePetales;
        private double rayonCentre;
        private double taillePetales;
        
        public Fleur(Vecteur2 centre, int nombrePetales, double rayonCentre, double taillePetales, Color couleur) {
            super(centre, couleur, true, 1.0);
            this.nombrePetales = nombrePetales;
            this.rayonCentre = rayonCentre;
            this.taillePetales = taillePetales;
        }
        
        @Override
        public void dessiner(Graphics2D g2) {
            // Dessiner les pétales
            for (int i = 0; i < nombrePetales; i++) {
                double angle = 2 * Math.PI * i / nombrePetales;
                Vecteur2 centrePetales = new Vecteur2(
                    position.x + (rayonCentre + taillePetales / 2) * Math.cos(angle),
                    position.y + (rayonCentre + taillePetales / 2) * Math.sin(angle)
                );
                
                g2.setColor(couleur);
                Cercle petale = new Cercle(centrePetales, taillePetales / 2);
                petale.setCouleur(couleur);
                petale.setEstRemplie(true);
                petale.dessiner(g2);
            }
            
            // Dessiner le centre
            g2.setColor(couleur.darker());
            Cercle centre = new Cercle(position, rayonCentre);
            centre.setCouleur(couleur.darker());
            centre.setEstRemplie(true);
            centre.dessiner(g2);
        }
        
        @Override
        public boolean contientPoint(Vecteur2 point) {
            double distance = position.distance(point);
            return distance <= rayonCentre + taillePetales;
        }
        
        @Override
        public double calculerAire() {
            return Math.PI * (rayonCentre + taillePetales) * (rayonCentre + taillePetales);
        }
        
        @Override
        public double calculerPerimetre() {
            return 2 * Math.PI * (rayonCentre + taillePetales);
        }
    }
}