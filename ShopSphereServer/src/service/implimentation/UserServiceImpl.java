/*
 * ShopSphere - UserServiceImpl
 * Meme structure qu'AgriConnect service/implimentation/UserServiceImpl.java
 * extends UnicastRemoteObject implements service0.UserService
 *
 * Améliorations :
 *   - BCrypt pour les mots de passe (etait .equals() en clair)
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
        String hashed = BCryptUtil.hash(userObj.getPasswordHash());
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
     * Etait login(email, passwordHash) dans AgriConnect.
     * AgriConnect : found.getPasswordHash().equals(passwordHash)
     * ShopSphere  : BCrypt.checkpw(plainPassword, storedHash)
     */
    @Override
    public User login(String email, String passwordHash) throws RemoteException {
        User found = userDao.findUserByEmail(email);
        if (found == null) {
            throw new RemoteException("Utilisateur introuvable : " + email);
        }
        if (!found.isActive()) {
            throw new RemoteException("Compte suspendu ou desactive.");
        }
        // Verification BCrypt
        if (!BCryptUtil.verify(passwordHash, found.getPasswordHash())) {
            throw new RemoteException("Email ou mot de passe incorrect.");
        }
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
        String codeHash = BCryptUtil.hash(code);

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

        // Creer notification in-app + simuler SMS/email
        Notification notif = new Notification(user,
                "Code de verification", msg, TypeNotif.SECURITE);
        notifDao.createNotification(notif);

        if ("SMS".equals(canal)) {
            LOG.info("[SMS vers " + user.getPhone() + "] " + msg);
        } else {
            LOG.info("[EMAIL vers " + user.getEmail() + "] " + msg);
        }
        // Le code en clair est detruit ici — jamais logue ailleurs
    }

    @Override
    public boolean verifierOtp(int userId, String code, String raison) throws RemoteException {
        Raison raisonEnum = Raison.valueOf(raison);
        OtpRecord otp = otpRecordDao.findActiveByUserAndRaison(userId, raisonEnum);
        if (otp == null)       throw new RemoteException("Aucun OTP actif trouve.");
        if (otp.isExpire())    throw new RemoteException("Code OTP expire.");
        if (otp.isTropDeTentatives()) throw new RemoteException("Trop de tentatives.");

        if (!BCryptUtil.verify(code, otp.getCodeHash())) {
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
