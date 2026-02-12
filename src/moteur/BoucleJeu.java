package moteur;

/**
 * Boucle de jeu principale pour gérer la logique et le rendu.
 */
public class BoucleJeu {
    private final int ipsCible;
    private boolean enCours;
    private long dernierTemps;
    private double tempsAccumule;
    private final double tempsParImage;
    
    private MiseAJour miseAJourCallback;
    private RenduCallback renduCallback;
    
    public BoucleJeu(int ipsCible) {
        this.ipsCible = ipsCible;
        this.enCours = false;
        this.tempsParImage = 1000000000.0 / ipsCible; // en nanosecondes
        this.tempsAccumule = 0;
    }
    
    /**
     * Démarre la boucle de jeu.
     */
    public void demarrer() {
        if (!enCours) {
            enCours = true;
            dernierTemps = System.nanoTime();
            Thread thread = new Thread(this::bouclePrincipale);
            thread.setName("BoucleJeu");
            thread.start();
        }
    }
    
    /**
     * Arrête la boucle de jeu.
     */
    public void arreter() {
        enCours = false;
    }
    
    /**
     * Boucle principale du jeu.
     */
    private void bouclePrincipale() {
        while (enCours) {
            long tempsActuel = System.nanoTime();
            long tempsEcoule = tempsActuel - dernierTemps;
            dernierTemps = tempsActuel;
            
            tempsAccumule += tempsEcoule;
            
            // Mettre à jour la logique du jeu à un rythme fixe
            while (tempsAccumule >= tempsParImage) {
                if (miseAJourCallback != null) {
                    miseAJourCallback.mettreAJour(tempsParImage / 1000000000.0); // Convertir en secondes
                }
                tempsAccumule -= tempsParImage;
            }
            
            // Rendre le jeu aussi souvent que possible
            if (renduCallback != null) {
                renduCallback.rendre();
            }
            
            // Éviter d'utiliser 100% du CPU
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * Définit le callback de mise à jour.
     */
    public void setMiseAJour(MiseAJour miseAJour) {
        this.miseAJourCallback = miseAJour;
    }
    
    /**
     * Définit le callback de rendu.
     */
    public void setRendu(RenduCallback rendu) {
        this.renduCallback = rendu;
    }
    
    // Getters
    public int getIpsCible() { return ipsCible; }
    public boolean estEnCours() { return enCours; }
    
    /**
     * Interface pour les callbacks de mise à jour.
     */
    @FunctionalInterface
    public interface MiseAJour {
        void mettreAJour(double deltaTemps);
    }
    
    /**
     * Interface pour les callbacks de rendu.
     */
    @FunctionalInterface
    public interface RenduCallback {
        void rendre();
    }
    
    /**
     * Classe abstraite pour les mises à jour (compatibilité avec le code existant).
     */
    public static abstract class MiseAJourAbstraite implements MiseAJour {
        @Override
        public abstract void mettreAJour(double deltaTemps);
    }
    
    /**
     * Classe abstraite pour le rendu (compatibilité avec le code existant).
     */
    public static abstract class RenduAbstrait implements RenduCallback {
        @Override
        public abstract void rendre();
    }
}