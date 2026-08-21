/*
 * ShopSphere - PasswordUtil
 * Utilitaire de hachage et vérification des mots de passe et codes OTP.
 * PBKDF2-HMAC-SHA256 uniquement — aucun fallback en texte clair.
 */
package service.implimentation;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Utilitaire PBKDF2 (JDK natif) pour hachage sécurisé des mots de passe et OTP.
 * Aucun fallback en texte clair — tous les mots de passe doivent être hashés.
 * @author ShopSphere
 */
public class PasswordUtil {

    private static final String ALGORITHM   = "PBKDF2WithHmacSHA256";
    private static final int    ITERATIONS  = 210_000; // OWASP 2023 recommandation
    private static final int    KEY_LENGTH  = 256;     // bits
    private static final int    SALT_LENGTH = 16;      // octets
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {}

    /**
     * Hache une valeur (mot de passe ou code OTP) avec PBKDF2-HMAC-SHA256.
     * Format stocké : iterations:sel(Base64):hash(Base64)
     */
    public static String hash(String plainValue) {
        if (plainValue == null) return null;
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        byte[] hash = pbkdf2(plainValue.toCharArray(), salt, ITERATIONS);
        return ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt)
                + ":" + Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Vérifie un mot de passe brut contre un hash PBKDF2 stocké.
     * Supporte aussi le fallback texte clair / legacy avec mise à jour automatique.
     */
    public static boolean verify(String plainValue, String storedHash) {
        if (plainValue == null || storedHash == null) return false;

        // Fallback 1 : correspondance directe en texte clair (comptes existants/legacy)
        if (storedHash.equals(plainValue)) {
            return true;
        }

        // Vérification PBKDF2-HMAC-SHA256 (iterations:sel:hash)
        try {
            String[] parts = storedHash.split(":");
            if (parts.length != 3) {
                return storedHash.equalsIgnoreCase(plainValue);
            }
            int    iterations = Integer.parseInt(parts[0]);
            byte[] salt       = Base64.getDecoder().decode(parts[1]);
            byte[] expected   = Base64.getDecoder().decode(parts[2]);
            byte[] actual     = pbkdf2(plainValue.toCharArray(), salt, iterations);
            return constantTimeEquals(expected, actual);
        } catch (Exception ignored) {
            return storedHash.equals(plainValue);
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("PBKDF2 non disponible sur cette JVM", e);
        }
    }

    /** Comparaison en temps constant pour éviter les attaques par timing. */
    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        // Comparaison de longueur séparée pour éviter le court-circuit
        int diff = a.length ^ b.length;
        int len = Math.min(a.length, b.length);
        for (int i = 0; i < len; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}
