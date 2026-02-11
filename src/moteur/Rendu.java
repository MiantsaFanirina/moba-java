package moteur;

import moteur.formes.Forme;
import moteur.formes.Rectangle;
import moteur.formes.Cercle;
import moteur.formes.LigneCourbe;
import moteur.formes.Triangle;
import moteur.formes.Ligne;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Système de rendu pour dessiner les formes et les entités.
 */
public class Rendu {
    
    private Graphics2D graphiques;
    private List<moteur.formes.Forme> formes;
    private List<Entite> entites;
    
    public Rendu() {
        this.formes = new ArrayList<>();
        this.entites = new ArrayList<>();
    }
    
    // Définir le contexte graphique
    public void definirGraphiques(Graphics2D g2) {
        this.graphiques = g2;
    }
    
    // Dessiner toutes les formes
    public void dessinerFormes() {
        if (graphiques == null) return;
        
        for (moteur.formes.Forme forme : formes) {
            forme.dessiner(graphiques);
        }
    }
    
    // Dessiner toutes les entités
    public void dessinerEntites(Camera2D camera) {
        if (graphiques == null) return;
        
        for (moteur.Entite entite : entites) {
            if (entite.estActive()) {
                entite.rendre(this, camera);
            }
        }
    }
    
    // Ajouter une forme à dessiner
    public void ajouterForme(moteur.formes.Forme forme) {
        formes.add(forme);
    }
    
    // Ajouter une entité à dessiner
    public void ajouterEntite(Entite entite) {
        entites.add(entite);
    }
    
    // Nettoyer les formes
    public void nettoyerFormes() {
        formes.clear();
    }
    
    // Nettoyer les entités
    public void nettoyerEntites() {
        entites.clear();
    }
    
    // Méthodes de dessin utilitaires
    public void dessinerRectangle(double x, double y, double largeur, double hauteur) {
        if (graphiques == null) return;
        
        Rectangle rect = new Rectangle(new Vecteur2(x, y), largeur, hauteur);
        rect.dessiner(graphiques);
    }
    
    public void dessinerContourRectangle(double x, double y, double largeur, double hauteur) {
        if (graphiques == null) return;
        
        Rectangle rect = new Rectangle(new Vecteur2(x, y), largeur, hauteur);
        rect.setEstRemplie(false);
        rect.dessiner(graphiques);
    }
    
    public void dessinerCercle(double x, double y, double rayon) {
        if (graphiques == null) return;
        
        Cercle cercle = new Cercle(new Vecteur2(x, y), rayon);
        cercle.dessiner(graphiques);
    }
    
    public void dessinerLigne(double x1, double y1, double x2, double y2) {
        if (graphiques == null) return;
        
        Ligne ligne = new Ligne(new Vecteur2(x1, y1), new Vecteur2(x2, y2));
        ligne.dessiner(graphiques);
    }
    
    public void dessinerCheminCourbe(List<Vecteur2> points) {
        if (graphiques == null || points.size() < 2) return;
        
        // Créer une ligne courbe simple entre les points
        for (int i = 0; i < points.size() - 1; i++) {
            Vecteur2 debut = points.get(i);
            Vecteur2 fin = points.get(i + 1);
            
            // Point de contrôle pour une courbe douce
            Vecteur2 controle = new Vecteur2(
                (debut.x + fin.x) / 2,
                (debut.y + fin.y) / 2 - 20
            );
            
            LigneCourbe courbe = new LigneCourbe(debut, controle, controle, fin, 20);
            courbe.dessiner(graphiques);
        }
    }
    
    // Définir la couleur de dessin
    public void definirCouleur(java.awt.Color couleur) {
        if (graphiques != null) {
            graphiques.setColor(couleur);
        }
    }
    
    // Getters
    public List<moteur.formes.Forme> getFormes() { return new ArrayList<>(formes); }
    public List<Entite> getEntites() { return new ArrayList<>(entites); }
}