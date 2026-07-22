/*
 * ShopSphere - BCryptUtil
 * Utilitaire de hashage BCrypt pour les mots de passe et codes OTP.
 * Remplace AgriConnect : password.equals(storedPassword) NON SECURISE
 *
 * IMPORTANT : Cette classe necessite la bibliotheque jBCrypt.
 * Telecharger : https://www.mindrot.org/projects/jBCrypt/
 * Ajouter jbcrypt.jar dans lib/ du projet.
 */
package service.implimentation;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilitaire BCrypt pour hachage securise des mots de passe et OTP.
 * @author ShopSphere
 */
public class BCryptUtil {

    // Facteur de cout BCrypt (12 = securite elevee, ~300ms par hash)
    private static final int COST_FACTOR = 12;

    private BCryptUtil() {}

    /**
     * Hache une valeur avec BCrypt.
     * Utiliser pour les mots de passe ET les codes OTP.
     */
    public static String hash(String plainValue) {
        return BCrypt.hashpw(plainValue, BCrypt.gensalt(COST_FACTOR));
    }

    /**
     * Verifie une valeur brute contre un hash BCrypt.
     * Remplace : storedHash.equals(plainValue) d'AgriConnect
     */
    public static boolean verify(String plainValue, String storedHash) {
        try {
            return BCrypt.checkpw(plainValue, storedHash);
        } catch (Exception e) {
            return false;
        }
    }
}
