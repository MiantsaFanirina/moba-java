package domaine.partie;

import domaine.equipe.Equipe;
import domaine.carte.Arene;
import domaine.unite.Heros;
import domaine.unite.Unite;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente une session de jeu.
 * Gère le cycle de vie d'une partie.
 */
public class Partie {
    
    private int idPartie;
    private double tempsEcouleSecondes;
    private boolean estTerminee;
    private Integer equipeGagnanteId;
    
    // Composants de la partie
    private Equipe equipe1;
    private Equipe equipe2;
    private Arene arene;
    
    // État du jeu
    private boolean estEnCours;
    private double tempsDebut;
    private static final double DUREE_MAXIMALE_PARTIE = 1800; // 30 minutes
    
    public Partie(int idPartie, Equipe equipe1, Equipe equipe2) {
        this.idPartie = idPartie;
        this.equipe1 = equipe1;
        this.equipe2 = equipe2;
        this.arene = new Arene(3, 60.0, 120.0); // 3 voies, 60s creeps, 120s monstres neutres
        
        this.tempsEcouleSecondes = 0;
        this.estTerminee = false;
        this.equipeGagnanteId = null;
        this.estEnCours = false;
        this.tempsDebut = 0;
    }
    
    // Démarrer la partie
    public void demarrer() {
        estEnCours = true;
        tempsDebut = System.currentTimeMillis() / 1000.0;
        tempsEcouleSecondes = 0;
        estTerminee = false;
        equipeGagnanteId = null;
        
        // Initialiser les composants
        arene.initialiser();
        equipe1.setPartie(this);
        equipe2.setPartie(this);
        
        System.out.println("*** Partie " + idPartie + ": " + equipe1.getNom() + " vs " + equipe2.getNom() + " ***");
    }
    
    // Mettre à jour la partie
    public void mettreAJour(double deltaSecondes) {
        if (!estEnCours || estTerminee) return;
        
        tempsEcouleSecondes += deltaSecondes;
        
        // Mettre à jour l'arène
        List<domaine.unite.Unite> toutesUnites = new ArrayList<>();
        toutesUnites.addAll(equipe1.getHeros());
        toutesUnites.addAll(equipe2.getHeros());
        toutesUnites.addAll(equipe1.getMinionsActifs());
        toutesUnites.addAll(equipe2.getMinionsActifs());
        
        arene.mettreAJour(deltaSecondes, List.of(equipe1, equipe2), toutesUnites);
        
        // Mettre à jour les équipes
        equipe1.mettreAJour(deltaSecondes);
        equipe2.mettreAJour(deltaSecondes);
        
        // Vérifier les conditions de victoire
        verifierFinDePartie();
    }
    
    // Vérifier si la partie est terminée
    private void verifierFinDePartie() {
        // Vérifier si une base est détruite
        if (equipe1.getBase().estDetruite()) {
            terminer(2);
        } else if (equipe2.getBase().estDetruite()) {
            terminer(1);
        }
        
        // Verifier la durée maximale
        if (tempsEcouleSecondes >= DUREE_MAXIMALE_PARTIE) {
            // Décider le vainqueur basé sur les performances
            Integer vainqueur = determinerVainqueurParPerformances();
            if (vainqueur != null) {
                terminer(vainqueur);
            } else {
                terminer(null); // Match nul
            }
        }
        
        // Vérifier la condition de surrendement (tous les joueurs déconnectés)
        if (verifierSurrendement()) {
            terminer(null); // Match nul
        }
    }
    
    // Déterminer le vainqueur basé sur les performances
    private Integer determinerVainqueurParPerformances() {
        double scoreEquipe1 = calculerScoreEquipe(equipe1);
        double scoreEquipe2 = calculerScoreEquipe(equipe2);
        
        if (Math.abs(scoreEquipe1 - scoreEquipe2) < 0.1) {
            return null; // Trop proche, match nul
        }
        
        return scoreEquipe1 > scoreEquipe2 ? 1 : 2;
    }
    
    // Calculer le score d'une équipe
    private double calculerScoreEquipe(Equipe equipe) {
        double score = 0;
        
        // Points pour les héros vivants
        for (Heros hero : equipe.getHeros()) {
            if (hero.estVivant()) {
                score += 10 * hero.getNiveau();
            }
        }
        
        // Points pour les tours détruites ennemies
        score += equipe.getToursEnnemiesDetruites() * 100;
        
        // Points pour les monstres neutres tués
        score += equipe.getMonstresNeutresTues() * 15;
        
        // Points pour les tours alliées encore debout
        score += equipe.getToursAllieesRestantes() * 50;
        
        // Points pour la base alliée
        if (!equipe.getBase().estDetruite()) {
            score += 500;
        }
        
        return score;
    }
    
    // Vérifier si toutes les équipes ont surrendu
    private boolean verifierSurrendement() {
        return equipe1.aSurrendu() && equipe2.aSurrendu();
    }
    
    // Terminer la partie
    public void terminer(Integer equipeGagnanteId) {
        estEnCours = false;
        estTerminee = true;
        this.equipeGagnanteId = equipeGagnanteId;
        
        // Notifier les équipes
        equipe1.notifierFinPartie(equipeGagnanteId);
        equipe2.notifierFinPartie(equipeGagnanteId);
        
        // Afficher le résultat
        afficherResultat();
    }
    
    // Afficher le résultat de la partie
    private void afficherResultat() {
        System.out.println("\n*** FIN DE PARTIE ***");
        
        if (equipeGagnanteId != null) {
            Equipe equipeGagnante = equipeGagnanteId == 1 ? equipe1 : equipe2;
            System.out.println("*** Victoire de " + equipeGagnante.getNom() + "! ***");
            System.out.println("*** Durée: " + (int)tempsEcouleSecondes + " secondes ***");
            
            // Afficher les statistiques
            afficherStatistiques(equipeGagnante, equipeGagnanteId == 1 ? equipe2 : equipe1);
        } else {
            System.out.println("*** Match nul! ***");
        }
        
        System.out.println("*** ***");
    }
        
        System.out.println("*** FIN ***");
    }
    
    // Afficher les statistiques détaillées
    private void afficherStatistiques(Equipe equipeGagnante, Equipe equipePerdante) {
        System.out.println("\n📊 Statistiques détaillées:");
        
        afficherStatsEquipe(equipeGagnante, "Vainqueur");
        afficherStatsEquipe(equipePerdante, "Perdant");
    }
    
    private void afficherStatsEquipe(Equipe equipe, String role) {
        System.out.println("\n" + role + " - " + equipe.getNom() + ":");
        
        int totalKills = 0, totalDeaths = 0, totalAssists = 0;
        double totalOr = 0;
        
        for (Heros hero : equipe.getHeros()) {
            totalKills += hero.getKills();
            totalDeaths += hero.getDeaths();
            totalAssists += hero.getAssists();
            totalOr += hero.getOr();
            
            System.out.printf("  *** %s: K:%d D:%d A:%d Or:%.0f Lvl:%d ***\n",
                hero.getNom(),
                hero.getKills(),
                hero.getDeaths(),
                hero.getAssists(),
                hero.getOr(),
                hero.getNiveau()
            );
        }
        
        System.out.printf("  *** Équipe: K:%d D:%d A:%d Or:%.0f ***\n",
            totalKills, totalDeaths, totalAssists, totalOr);
    }
    
    // Obtenir l'état actuel de la partie
    public String obtenirEtatPartie() {
        if (!estEnCours) return "En attente";
        if (estTerminee) {
            return equipeGagnanteId != null ? 
                "Terminée - Victoire Équipe " + equipeGagnanteId : 
                "Terminée - Match nul";
        }
        return "En cours";
    }
    
    // Getters et setters
    public int getIdPartie() { return idPartie; }
    public double getTempsEcouleSecondes() { return tempsEcouleSecondes; }
    public boolean estTerminee() { return estTerminee; }
    public Integer getEquipeGagnanteId() { return equipeGagnanteId; }
    public Equipe getEquipe1() { return equipe1; }
    public Equipe getEquipe2() { return equipe2; }
    public Arene getArene() { return arene; }
    public boolean estEnCours() { return estEnCours; }
    
    public void setArene(Arene arene) { this.arene = arene; }
}