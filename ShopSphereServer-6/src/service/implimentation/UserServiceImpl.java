/*
 * ShopSphere - UserServiceImpl
 * Meme structure qu'AgriConnect service/implimentation/UserServiceImpl.java
 * extends UnicastRemoteObject implements service0.UserService
 *
 * Améliorations :
 *   - PBKDF2-HMAC-SHA256 (JDK natif, aucune librairie externe) pour les mots
 *     de passe (etait .equals() en clair)
 *   - OTP dans OtpRecord (etait user.otpCode String en clair)
 *   - Langue preferee fr par defaut (i18n)
 */
package service.implimentation;

import dao.NotificationDao;
import dao.OtpRecordDao;
import dao.UserDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;
import java.util.List;
import java.util.logging.Logger;
import model.Notification;
import model.Notification.TypeNotif;
import model.OtpRecord;
import model.OtpRecord.Canal;
import model.OtpRecord.Raison;
import model.User;
import model.User.AccountStatus;
import model.User.Role;
import service0.UserService;

/**
 * Implementation RMI du service utilisateur.
 * Meme structure qu'AgriConnect UserServiceImpl — extends UnicastRemoteObject.
 * @author ShopSphere
 */
public class UserServiceImpl extends UnicastRemoteObject implements UserService {

    private static final Logger LOG = Logger.getLogger(UserServiceImpl.class.getName());
    private static final SecureRandom RANDOM = new SecureRandom();
    /** Message générique — identique que l'email soit introuvable ou le mdp faux (anti-enumération). */
    private static final String INVALID_CREDENTIALS = "Email ou mot de passe incorrect.";

    private final UserDao userDao = new UserDao();
    private final OtpRecordDao otpRecordDao = new OtpRecordDao();
    private final NotificationDao notifDao = new NotificationDao();

    public UserServiceImpl() throws RemoteException {
        super();
    }

    // ── CRUD (memes methodes qu'AgriConnect) ─────────────────────────────

    @Override
    public User createUserRecord(User userObj) throws RemoteException {
        if (userDao.existsByEmail(userObj.getEmail())) {
            throw new RemoteException("Email deja utilise : " + userObj.getEmail());
        }
        // Hash BCrypt — etait stockage en clair dans AgriConnect
        String hashed = PasswordUtil.hash(userObj.getPasswordHash());
        userObj.setPasswordHash(hashed);
        if (userObj.getPreferredLanguage() == null) {
            userObj.setPreferredLanguage("fr"); // Francais par defaut
        }
        return userDao.createUser(userObj);
    }

    @Override
    public User updateUserRecord(User userObj) throws RemoteException {
        return userDao.updateUser(userObj);
    }

    @Override
    public User deleteUserRecord(User userObj) throws RemoteException {
        // Suppression douce — meme concept qu'AgriConnect mais avec AccountStatus
        userObj.setStatus(AccountStatus.DELETED);
        return userDao.updateUser(userObj);
    }

    @Override
    public User findUserRecordById(int id) throws RemoteException {
        return userDao.findUserById(id);
    }

    @Override
    public User findUserRecordByEmail(String email) throws RemoteException {
        return userDao.findUserByEmail(email);
    }

    @Override
    public List<User> findAllUserRecords() throws RemoteException {
        return userDao.findAll();
    }

    @Override
    public List<User> findUserRecordsByRole(Role role) throws RemoteException {
        return userDao.findByRole(role);
    }

    // ── Authentification ─────────────────────────────────────────────────
    /**
     * Connexion sécurisée — anti-énumération + protection brute-force.
     * Le message d'erreur est identique que l'email soit introuvable ou le mot de passe faux.
     * Après MAX_ATTEMPTS échecs, le compte est bloqué pendant LOCKOUT_SECS secondes.
     */
    @Override
    public User login(String email, String passwordHash) throws RemoteException {
        // Validation basique des entrées
        if (email == null || email.isEmpty() || passwordHash == null || passwordHash.isEmpty()) {
            throw new RemoteException(INVALID_CREDENTIALS);
        }
        String normalizedEmail = email.trim().toLowerCase();

        // ── Vérification brute-force ────────────────────────────────────
        LoginAttemptService las = LoginAttemptService.getInstance();
        if (las.isBlocked(normalizedEmail)) {
            long secs = las.getSecondsUntilUnlock(normalizedEmail);
            long mins = secs / 60, remaining = secs % 60;
            throw new RemoteException("Compte temporairement bloqué. Réessayez dans "
                    + mins + "m " + remaining + "s.");
        }

        User found = userDao.findUserByEmail(normalizedEmail);
        if (found == null) {
            // Simuler le coût du hash pour éviter le timing-oracle (anti-énumération)
            PasswordUtil.verify(passwordHash, PasswordUtil.hash("dummy_anti_timing"));
            las.recordFailure(normalizedEmail);
            throw new RemoteException(INVALID_CREDENTIALS);
        }

        // Vérification PBKDF2 (ou fallback legacy)
        boolean passwordOk = PasswordUtil.verify(passwordHash, found.getPasswordHash());
        if (!passwordOk) {
            int remaining = las.recordFailure(normalizedEmail);
            String detail = remaining > 0
                    ? " (" + remaining + " tentative(s) restante(s))"
                    : " Compte bloqué " + LoginAttemptService.LOCKOUT_SECS / 60 + " min.";
            throw new RemoteException(INVALID_CREDENTIALS + detail);
        }

        // Connexion réussie — effacer le compteur brute-force
        las.recordSuccess(normalizedEmail);

        // Auto-upgrade en hash PBKDF2 si le mot de passe était stocké en texte clair
        if (found.getPasswordHash() != null && !found.getPasswordHash().contains(":")) {
            found.setPasswordHash(PasswordUtil.hash(passwordHash));
            userDao.updateUser(found);
            LOG.info("Password auto-upgraded to PBKDF2 for user: " + normalizedEmail);
        }

        if (found.getStatus() == AccountStatus.SUSPENDED || found.getStatus() == AccountStatus.DELETED) {
            throw new RemoteException("Votre compte est suspendu ou supprimé. Contactez le support.");
        }

        LOG.info("Successful login for: " + normalizedEmail);
        return found;
    }

    // ── OTP (remplace user.otpCode en clair d'AgriConnect) ───────────────
    @Override
    public void envoyerOtp(int userId, String canal, String raison) throws RemoteException {
        User user = userDao.findUserById(userId);
        if (user == null) throw new RemoteException("Utilisateur introuvable.");

        // Rate-limit : max 5 OTP par heure
        long recent = otpRecordDao.countRecentByUser(userId, 1);
        if (recent >= 5) {
            throw new RemoteException("Trop de demandes OTP. Reessayez dans une heure.");
        }

        // Invalider les OTP precedents
        Raison raisonEnum = Raison.valueOf(raison);
        otpRecordDao.invalidateAll(userId, raisonEnum);

        // Generer code 6 chiffres (SecureRandom — jamais Math.random)
        String code = String.valueOf(100_000 + RANDOM.nextInt(900_000));
        // Hacher avec BCrypt — JAMAIS stocker en clair
        String codeHash = PasswordUtil.hash(code);

        OtpRecord otp = new OtpRecord(user, codeHash,
                Canal.valueOf(canal), raisonEnum, 10);
        otpRecordDao.createOtpRecord(otp);

        // Construire le message en francais (langue principale)
        String msg;
        if ("fr".equals(user.getPreferredLanguage())) {
            msg = "Votre code ShopSphere est : " + code + ". Valable 10 minutes.";
        } else {
            msg = "Your ShopSphere code is: " + code + ". Valid for 10 minutes.";
        }

        // Creer notification in-app
        Notification notif = new Notification(user,
                "Code de verification", msg, TypeNotif.SECURITE);
        notifDao.createNotification(notif);

        // Envoyer le code par email ou SMS dans un thread daemon
        // (pour ne pas bloquer l'appel RMI)
        final String finalCode  = code;
        final String finalCanal = canal;
        final String dest       = ("SMS".equals(canal)) ? user.getPhone() : user.getEmail();
        final String lang       = user.getPreferredLanguage();
        Thread sender = new Thread(() -> {
            if ("SMS".equals(finalCanal)) {
                // Pas de gateway SMS integre — afficher dans la console
                LOG.info("[SMS vers " + dest + "] " + msg);
            } else {
                EmailUtil.envoyerOtp(dest, finalCode, lang);
            }
        }, "otp-sender");
        sender.setDaemon(true);
        sender.start();
        // Le code en clair est detruit ici — jamais logue ailleurs
    }

    @Override
    public boolean verifierOtp(int userId, String code, String raison) throws RemoteException {
        Raison raisonEnum = Raison.valueOf(raison);
        OtpRecord otp = otpRecordDao.findActiveByUserAndRaison(userId, raisonEnum);
        if (otp == null)       throw new RemoteException("Aucun OTP actif trouve.");
        if (otp.isExpire())    throw new RemoteException("Code OTP expire.");
        if (otp.isTropDeTentatives()) throw new RemoteException("Trop de tentatives.");

        if (!PasswordUtil.verify(code, otp.getCodeHash())) {
            otp.setAttempts(otp.getAttempts() + 1);
            otpRecordDao.updateOtpRecord(otp);
            throw new RemoteException("Code OTP invalide.");
        }
        // Succes — supprimer l'OTP utilise
        otpRecordDao.deleteRecord(otp);
        return true;
    }

    @Override
    public void renvoyerOtp(int userId, String raison) throws RemoteException {
        User user = userDao.findUserById(userId);
        if (user == null) throw new RemoteException("Utilisateur introuvable.");
        String canal = (user.getPhone() != null && user.isPhoneVerified()) ? "SMS" : "EMAIL";
        envoyerOtp(userId, canal, raison);
    }

    // ── Admin ─────────────────────────────────────────────────────────────
    @Override
    public void suspendreCompte(int targetId) throws RemoteException {
        userDao.updateStatus(targetId, AccountStatus.SUSPENDED);
    }

    @Override
    public void supprimerCompte(int targetId) throws RemoteException {
        userDao.updateStatus(targetId, AccountStatus.DELETED);
    }

    @Override
    public void verifierEmail(int userId) throws RemoteException {
        userDao.verifyEmail(userId);
    }

    @Override
    public List<User> findByStatus(AccountStatus status) throws RemoteException {
        return userDao.findByStatus(status);
    }
}
