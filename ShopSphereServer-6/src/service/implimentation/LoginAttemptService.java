/*
 * ShopSphere Server - LoginAttemptService
 * Protection anti-brute-force pour la connexion.
 * In-memory thread-safe : max 5 tentatives / 15 min par IP ou email.
 * Délai exponentiel : 0s → 2s → 4s → 8s → 16s avant blocage.
 */
package service.implimentation;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Service anti-brute-force pour les tentatives de connexion.
 * Combine IP + email comme clé pour contrer les attaques distribuées.
 * @author ShopSphere
 */
public class LoginAttemptService {

    private static final Logger LOG = Logger.getLogger(LoginAttemptService.class.getName());

    /** Nombre maximum de tentatives avant blocage. */
    public static final int MAX_ATTEMPTS  = 5;
    /** Durée de blocage en secondes (15 minutes). */
    public static final int LOCKOUT_SECS  = 15 * 60;
    /** Délai d'expiration des tentatives hors-blocage (15 minutes). */
    public static final int WINDOW_SECS   = 15 * 60;

    private static final LoginAttemptService INSTANCE = new LoginAttemptService();

    private final Map<String, AttemptRecord> store = new ConcurrentHashMap<>();

    private LoginAttemptService() {
        // Nettoyage périodique toutes les 5 minutes
        java.util.concurrent.Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "LoginAttemptCleaner");
            t.setDaemon(true);
            return t;
        }).scheduleAtFixedRate(this::evictExpired, 5, 5, java.util.concurrent.TimeUnit.MINUTES);
    }

    public static LoginAttemptService getInstance() { return INSTANCE; }

    /**
     * Enregistre une tentative échouée.
     * @param key email ou "email:ip"
     * @return nombre de tentatives restantes (0 = compte bloqué)
     */
    public int recordFailure(String key) {
        if (key == null) return MAX_ATTEMPTS;
        String k = key.toLowerCase().trim();
        AttemptRecord rec = store.compute(k, (unused, old) -> {
            if (old == null) return new AttemptRecord();
            if (old.isExpired()) return new AttemptRecord();
            old.increment();
            return old;
        });
        int remaining = Math.max(0, MAX_ATTEMPTS - rec.count);
        LOG.warning("Login attempt failed for [" + k + "] — attempts: " + rec.count + ", remaining: " + remaining);
        return remaining;
    }

    /**
     * Vérifie si un compte est actuellement bloqué.
     * @return true si bloqué
     */
    public boolean isBlocked(String key) {
        if (key == null) return false;
        AttemptRecord rec = store.get(key.toLowerCase().trim());
        if (rec == null) return false;
        if (rec.isExpired()) { store.remove(key.toLowerCase().trim()); return false; }
        return rec.count >= MAX_ATTEMPTS;
    }

    /**
     * Retourne le délai d'attente recommandé en secondes selon le nombre de tentatives.
     * Délai exponentiel : 2^(n-1) secondes.
     */
    public int getBackoffSeconds(String key) {
        if (key == null) return 0;
        AttemptRecord rec = store.get(key.toLowerCase().trim());
        if (rec == null || rec.count == 0) return 0;
        return (int) Math.min(30, Math.pow(2, rec.count - 1));
    }

    /**
     * Retourne le temps restant avant déblocage en secondes.
     */
    public long getSecondsUntilUnlock(String key) {
        if (key == null) return 0;
        AttemptRecord rec = store.get(key.toLowerCase().trim());
        if (rec == null || !isBlocked(key)) return 0;
        long elapsed = Instant.now().getEpochSecond() - rec.firstAttempt;
        return Math.max(0, LOCKOUT_SECS - elapsed);
    }

    /**
     * Réinitialise le compteur après une connexion réussie.
     */
    public void recordSuccess(String key) {
        if (key == null) return;
        store.remove(key.toLowerCase().trim());
    }

    /** Supprime les entrées expirées de la map. */
    private void evictExpired() {
        store.entrySet().removeIf(e -> e.getValue().isExpired());
        LOG.fine("LoginAttemptService: evicted expired entries, remaining: " + store.size());
    }

    // ── Classe interne ────────────────────────────────────────────────────
    private static class AttemptRecord {
        int  count        = 1;
        long firstAttempt = Instant.now().getEpochSecond();
        long lastAttempt  = firstAttempt;

        void increment() {
            count++;
            lastAttempt = Instant.now().getEpochSecond();
        }

        boolean isExpired() {
            long age = Instant.now().getEpochSecond() - firstAttempt;
            return age > LOCKOUT_SECS;
        }
    }
}
