/*
 * ShopSphere - PasswordUtil
 * Utilitaire de hachage et vérification des mots de passe et codes OTP.
 * Supporte le hachage PBKDF2-HMAC-SHA256 et le fallback pour mots de passe en clair dans la DB.
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
 * @author ShopSphere
 */
public class PasswordUtil {

    private static final String ALGORITHM   = "PBKDF2WithHmacSHA256";
    private static final int    ITERATIONS  = 65_536;
    private static final int    KEY_LENGTH  = 256;      // bits
    private static final int    SALT_LENGTH = 16;       // octets
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
     * Vérifie un mot de passe brut contre un hash stocké.
     * Inclut un fallback de comparaison directe au cas où le mot de passe est en clair dans la base.
     */
    public static boolean verify(String plainValue, String storedHash) {
        if (plainValue == null || storedHash == null) return false;
        
        // 1. Fallback pour mot de passe stocké en clair dans la base de données
        if (plainValue.equals(storedHash.trim())) {
            return true;
        }

        // 2. Vérification PBKDF2-HMAC-SHA256 (iterations:sel:hash)
        try {
            String[] parts = storedHash.split(":");
            if (parts.length == 3) {
                int    iterations = Integer.parseInt(parts[0]);
                byte[] salt       = Base64.getDecoder().decode(parts[1]);
                byte[] expected   = Base64.getDecoder().decode(parts[2]);
                byte[] actual     = pbkdf2(plainValue.toCharArray(), salt, iterations);
                if (constantTimeEquals(expected, actual)) {
                    return true;
                }
            }
        } catch (Exception ignored) {}

        return false;
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
        if (a.length != b.length) return false;
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }
}
