package moteur.systemes;

import java.util.ArrayList;
import java.util.List;

/**
 * Système de gestion de l'audio du jeu.
 * Utilise la bibliothèque audio Java.
 */
public class SonAudioManager {
    
    private static SonAudioManager instance;
    private List<Son> sons;
    
    // Paramètres audio
    private static final float VOLUME_DEFAUT = 1.0f;
    private static final float VOLUME_MIN = 0.0f;
    private static final float VOLUME_MAX = 1.0f;
    private static final int MAX_SONS = 50;
    
    // États audio
    private boolean musiqueActive = false;
    private boolean effetsActives = true;
    
    // Private pour singleton
    private SonAudioManager() {
        if (instance == null) {
            instance = new SonAudioManager();
        }
    }
    
    // Obtenir l'instance singleton
    public static SonAudioManager getInstance() {
        return instance;
    }
    
    // Initialiser le système
    private SonAudioManager() {
        this.sons = new ArrayList<>();
        this.volumeGlobal = VOLUME_DEFAUT;
        this.musiqueActive = false;
        this.effetsActives = true;
    }
    
    // Ajouter un son à la liste
    public void ajouterSon(String nom, String cheminFichier, float volume) {
        Son son = new Son(nom, cheminFichier, volume);
        sons.add(son);
    }
    
    // Supprimer un son de la liste
    public void supprimerSon(Son son) {
        sons.remove(son);
    }
    
    // Jouer un son spécifique
    public void jouerSon(String nom) {
        Son son = trouverSon(nom);
        if (son != null && son.estActif()) {
            son.jouer();
        }
    }
    
    // Arrêter tous les sons
    public void arreterTousLesSons() {
        for (Son son : sons) {
            son.arreter();
        }
    }
    
    // Activer/désactiver la musique
    public void setMusiqueActive(boolean active) {
        this.musiqueActive = active;
        if (!active) {
            arreterTousLesSons();
        }
    }
    
    // Activer/désactiver les effets
    public void setEffetsActifs(boolean actifs) {
        this.effetsActives = actifs;
    }
    
    // Gérer le volume global
    public void setVolumeGlobal(float volume) {
        this.volumeGlobal = Math.max(VOLUME_MIN, Math.min(VOLUME_MAX, volume));
        mettreAJourVolumeGlobal();
    }
    
    // Mettre à jour le volume de tous les sons
    public void mettreAJourVolumeGlobal() {
        for (Son son : sons) {
            son.setVolume(this.volumeGlobal);
        }
    }
    
    // Obtenir le volume actuel
    public float getVolumeGlobal() {
        return volumeGlobal;
    }
    
    // Obtenir la liste des sons
    public List<Son> getSons() {
        return new ArrayList<>(sons);
    }
    
    // Obtenir des états
    public boolean estMusiqueActive() {
        return musiqueActive;
    }
    public boolean estEffetsActifs() { return effetsActives; }
    
    // Obtenir une instance de Son
    private Son trouverSon(String nom) {
        for (Son son : sons) {
            if (son.getNom().equals(nom)) {
                return son;
            }
        }
        return null;
    }
}