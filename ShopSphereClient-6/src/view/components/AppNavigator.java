/*
 * ShopSphere - AppNavigator
 * Gestionnaire de navigation "une page a la fois" : chaque appel a show()
 * ferme automatiquement la fenetre precedemment affichee avant d'afficher
 * la nouvelle, exactement comme un site web classique (une seule page visible).
 */
package view.components;

import javax.swing.JFrame;

/**
 * Navigateur central de l'application cliente ShopSphere.
 * Remplace le pattern "new Frame().setVisible(true)" disperse dans chaque vue
 * par un point d'entree unique qui garantit qu'une seule fenetre de page est
 * visible a la fois (evite l'empilement Catalogue + Profil + Tableau de bord).
 * @author ShopSphere
 */
public final class AppNavigator {

    private static JFrame currentPage;

    private AppNavigator() {}

    /**
     * Affiche une nouvelle page et ferme la precedente (si elle existe encore).
     * @param nextPage la fenetre a afficher
     */
    public static synchronized void show(JFrame nextPage) {
        JFrame previous = currentPage;
        currentPage = nextPage;
        nextPage.setVisible(true);
        if (previous != null && previous != nextPage && previous.isDisplayable()) {
            previous.dispose();
        }
    }

    /**
     * Ferme la page courante sans en ouvrir de nouvelle (ex: deconnexion).
     */
    public static synchronized void closeCurrent() {
        if (currentPage != null && currentPage.isDisplayable()) {
            currentPage.dispose();
        }
        currentPage = null;
    }

    public static synchronized JFrame getCurrentPage() {
        return currentPage;
    }
}
