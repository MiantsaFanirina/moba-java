package moteur;

import moteur.formes.*;
import java.util.List;

/**
 * Système de collision avancé pour les entités du jeu.
 */
public class SystemeCollision {
    
    private List<Forme> obstacles;
    private List<Rectangle> zonesInterdites;
    
    public SystemeCollision(List<Forme> obstacles, List<Rectangle> zonesInterdites) {
        this.obstacles = obstacles;
        this.zonesInterdites = zonesInterdites;
    }
    
    /**
     * Vérifie si une position est valide pour une entité avec un rayon donné.
     */
    public boolean estPositionValide(Vecteur2 position, double rayon) {
        // Vérifier les zones interdites
        for (Rectangle zone : zonesInterdites) {
            if (rectangleCercleCollision(zone, position, rayon)) {
                return false;
            }
        }
        
        // Vérifier les obstacles
        for (Forme obstacle : obstacles) {
            if (obstacle instanceof Rectangle) {
                if (rectangleCercleCollision((Rectangle) obstacle, position, rayon)) {
                    return false;
                }
            } else if (obstacle instanceof Cercle) {
                if (cercleCercleCollision(((Cercle) obstacle).getCentre(), 
                                         ((Cercle) obstacle).getRayon(), 
                                         position, rayon)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Résout les collisions en déplaçant l'entité hors des obstacles.
     */
    public Vecteur2 resoudreCollision(Vecteur2 position, double rayon, Vecteur2 vitesse) {
        Vecteur2 nouvellePosition = position.ajouter(vitesse);
        
        if (!estPositionValide(nouvellePosition, rayon)) {
            // Trouver la direction de collision
            for (Rectangle zone : zonesInterdites) {
                if (rectangleCercleCollision(zone, nouvellePosition, rayon)) {
                    return eviterRectangle(position, zone, rayon, vitesse);
                }
            }
            
            for (Forme obstacle : obstacles) {
                if (obstacle instanceof Rectangle) {
                    Rectangle rect = (Rectangle) obstacle;
                    if (rectangleCercleCollision(rect, nouvellePosition, rayon)) {
                        return eviterRectangle(position, rect, rayon, vitesse);
                    }
                } else if (obstacle instanceof Cercle) {
                    Cercle cercle = (Cercle) obstacle;
                    if (cercleCercleCollision(cercle.getCentre(), cercle.getRayon(), 
                                           nouvellePosition, rayon)) {
                        return eviterCercle(position, cercle.getCentre(), 
                                         cercle.getRayon(), rayon, vitesse);
                    }
                }
            }
        }
        
        return nouvellePosition;
    }
    
    /**
     * Évite un rectangle en trouvant la meilleure direction.
     */
    private Vecteur2 eviterRectangle(Vecteur2 position, Rectangle rectangle, 
                                   double rayon, Vecteur2 vitesse) {
        // Calculer le point le plus proche sur le rectangle
        Vecteur2 centreRectangle = new Vecteur2(
            rectangle.x + rectangle.largeur / 2,
            rectangle.y + rectangle.hauteur / 2
        );
        
        Vecteur2 direction = position.soustraire(centreRectangle).normaliser();
        
        // Essayer de contourner le rectangle
        Vecteur2[] alternatives = {
            new Vecteur2(direction.x, 0),   // Contournement horizontal
            new Vecteur2(0, direction.y),   // Contournement vertical
            new Vecteur2(-direction.x, direction.y), // Diagonale 1
            new Vecteur2(direction.x, -direction.y)  // Diagonale 2
        };
        
        for (Vecteur2 alternative : alternatives) {
            Vecteur2 testPosition = position.ajouter(
                alternative.multiplier(vitesse.norme())
            );
            
            if (estPositionValide(testPosition, rayon)) {
                return testPosition;
            }
        }
        
        return position; // Pas de mouvement possible
    }
    
    /**
     * Évite un cercle en trouvant la meilleure direction.
     */
    private Vecteur2 eviterCercle(Vecteur2 position, Vecteur2 centreCercle, 
                                 double rayonCercle, double rayonEntite, 
                                 Vecteur2 vitesse) {
        Vecteur2 direction = position.soustraire(centreCercle).normaliser();
        
        // Distance minimale entre les centres
        double distanceMinimale = rayonCercle + rayonEntite + 5;
        
        // Positionner l'entité à la distance minimale
        Vecteur2 positionSecurite = centreCercle.ajouter(
            direction.multiplier(distanceMinimale)
        );
        
        return positionSecurite;
    }
    
    /**
     * Collision entre rectangle et cercle.
     */
    private boolean rectangleCercleCollision(Rectangle rectangle, 
                                            Vecteur2 centreCercle, double rayon) {
        // Trouver le point le plus proche du cercle sur le rectangle
        double xPlusProche = Math.max(rectangle.x, 
                            Math.min(centreCercle.x, rectangle.x + rectangle.largeur));
        double yPlusProche = Math.max(rectangle.y, 
                            Math.min(centreCercle.y, rectangle.y + rectangle.hauteur));
        
        // Distance du centre du cercle au point le plus proche
        double distanceX = centreCercle.x - xPlusProche;
        double distanceY = centreCercle.y - yPlusProche;
        
        return (distanceX * distanceX + distanceY * distanceY) <= (rayon * rayon);
    }
    
    /**
     * Collision entre deux cercles.
     */
    private boolean cercleCercleCollision(Vecteur2 centre1, double rayon1, 
                                         Vecteur2 centre2, double rayon2) {
        double distance = centre1.distance(centre2);
        return distance < (rayon1 + rayon2);
    }
    
    /**
     * Vérifie si un chemin est libre d'obstacles.
     */
    public boolean estCheminLibre(Vecteur2 debut, Vecteur2 fin, double rayon) {
        int steps = 20;
        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            Vecteur2 point = new Vecteur2(
                debut.x + t * (fin.x - debut.x),
                debut.y + t * (fin.y - debut.y)
            );
            
            if (!estPositionValide(point, rayon)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Trouve un chemin en contournant les obstacles.
     */
    public java.util.List<Vecteur2> trouverChemin(Vecteur2 debut, Vecteur2 fin, double rayon) {
        java.util.List<Vecteur2> chemin = new java.util.ArrayList<>();
        
        // Si le chemin direct est libre
        if (estCheminLibre(debut, fin, rayon)) {
            chemin.add(fin);
            return chemin;
        }
        
        // Sinon, essayer des points intermédiaires stratégiques
        Vecteur2[] pointsIntermediaires = {
            // Contournements latéraux
            new Vecteur2(debut.x, fin.y),
            new Vecteur2(fin.x, debut.y),
            // Passage par le centre
            new Vecteur2(1200, 900), // Centre de la carte
            // Contournements par les voies
            new Vecteur2(debut.x, 300),  // Voie sup
            new Vecteur2(debut.x, 1500), // Voie inf
            new Vecteur2(1200, debut.y),  // Voie mid horizontale
        };
        
        for (Vecteur2 intermediaire : pointsIntermediaires) {
            if (estPositionValide(intermediaire, rayon)) {
                if (estCheminLibre(debut, intermediaire, rayon) && 
                    estCheminLibre(intermediaire, fin, rayon)) {
                    chemin.add(intermediaire);
                    chemin.add(fin);
                    return chemin;
                }
            }
        }
        
        // Dernier recours: chemin direct (même si collision)
        chemin.add(fin);
        return chemin;
    }
}