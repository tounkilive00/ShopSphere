/*
 * ShopSphere - UserService (interface RMI)
 * Meme structure qu'AgriConnect service0/UserService.java
 * extends Remote, chaque methode throws RemoteException
 */
package service0;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.User;
import model.User.AccountStatus;
import model.User.Role;

/**
 * Interface RMI du service utilisateur.
 * Identique a AgriConnect service0/UserService.java — memes signatures.
 * @author ShopSphere
 */
public interface UserService extends Remote {

    // CRUD de base (meme qu'AgriConnect)
    User createUserRecord(User userObj)           throws RemoteException;
    User updateUserRecord(User userObj)           throws RemoteException;
    User deleteUserRecord(User userObj)           throws RemoteException;
    User findUserRecordById(int id)               throws RemoteException;
    User findUserRecordByEmail(String email)      throws RemoteException;
    List<User> findAllUserRecords()               throws RemoteException;
    List<User> findUserRecordsByRole(Role role)   throws RemoteException;

    // Auth (etait login dans AgriConnect)
    User login(String email, String passwordHash) throws RemoteException;

    // OTP (nouveau — etait otpCode String en clair dans AgriConnect)
    void envoyerOtp(int userId, String canal, String raison) throws RemoteException;
    boolean verifierOtp(int userId, String code, String raison) throws RemoteException;
    void renvoyerOtp(int userId, String raison)  throws RemoteException;

    // Admin
    void suspendreCompte(int targetId)            throws RemoteException;
    void supprimerCompte(int targetId)            throws RemoteException;
    void verifierEmail(int userId)                throws RemoteException;
    List<User> findByStatus(AccountStatus status) throws RemoteException;
}
