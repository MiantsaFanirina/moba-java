package moteur;

import domaine.unite.Heros;
import java.util.ArrayList;
import java.util.List;

/**
 * Joueur contrôlé par l'utilisateur.
 * Hérite de Entite avec comportement de déplacement.
 */
public class Joueur extends Entite {
    
    private SuiveurDeChemin suiveurChemin;
    private List<Vecteur2> cheminActuel;
    private Vecteur2 objectifDestination;
    private RecalculateurChemin recalculateurChemin;
    
    public interface RecalculateurChemin {
        List<Vecteur2> recalculer(Vecteur2 depuis, Vecteur2 vers);
    }
    
    public Joueur(double x, double y) {
        super(x, y, 32, 32);
        this.cheminActuel = new ArrayList<>();
        this.objectifDestination = null;
        this.recalculateurChemin = null;
    }
    
    // Définir le recalculateur de chemin
    public void definirRecalculateurChemin(RecalculateurChemin recalculateur) {
        this.recalculateurChemin = recalculateur;
    }
    
    // Déplacer vers une destination
    public void deplacerVers(List<Vecteur2> chemin, double vitesse) {
        this.cheminActuel = chemin != null ? new ArrayList<>(chemin) : new ArrayList<>();
        this.objectifDestination = chemin != null && !chemin.isEmpty() ?
            chemin.get(chemin.size() - 1).copier() : null;
        
        this.suiveurChemin = chemin != null && !chemin.isEmpty() ?
            new SuiveurDeChemin(cheminActuel, vitesse) : null;
        
        if (suiveurChemin != null && recalculateurChemin != null && objectifDestination != null) {
            suiveurChemin.setOnStuckCallback((pos, dest) -> 
                recalculateurChemin.recalculer(pos, dest));
            suiveurChemin.setOnPathUpdated(nouveauChemin -> 
                cheminActuel = nouveauChemin);
        }
    }
    
    // Obtenir le chemin actuel
    public List<Vecteur2> getCheminActuel() {
        return new ArrayList<>(cheminActuel);
    }
    
    @Override
    protected void onUpdate(double tempsDelta, Monde monde) {
        if (suiveurChemin != null && !suiveurChemin.estTermine()) {
            Vecteur2 positionDesire = suiveurChemin.mettreAJour(position.copier(), tempsDelta);
            Vecteur2 deplacement = positionDesire.soustraire(position);
            
            if (tempsDelta > 0) {
                vitesse.x = deplacement.x / tempsDelta;
                vitesse.y = deplacement.y / tempsDelta;
            } else {
                vitesse.x = 0;
                vitesse.y = 0;
            }
        } else {
            vitesse.x = 0;
            vitesse.y = 0;
        }
    }
    
    @Override
    public void rendre(Rendu rendu, Camera2D camera) {
        Vecteur2 ecran = camera.mondeVersEcran(position);
        double zoom = camera.getZoom();
        double l = largeur * zoom;
        double h = hauteur * zoom;
        
        rendu.dessinerRectangle(ecran.x, ecran.y, l, h);
        rendu.definirCouleur(new java.awt.Color(220, 50, 50));
        rendu.dessinerContourRectangle(ecran.x, ecran.y, l, h);
    }
}