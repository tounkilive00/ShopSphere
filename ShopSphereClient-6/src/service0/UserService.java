package service0;
import java.rmi.Remote;
import java.rmi.RemoteException;
import model.User;
import model.User.AccountStatus;
import model.User.Role;

public interface UserService extends Remote {
    User createUserRecord(User u) throws RemoteException;
    User updateUserRecord(User u) throws RemoteException;
    User deleteUserRecord(User u) throws RemoteException;
    User findUserRecordById(int id) throws RemoteException;
    User findUserRecordByEmail(String email) throws RemoteException;
    java.util.List<User> findAllUserRecords() throws RemoteException;
    java.util.List<User> findUserRecordsByRole(Role role) throws RemoteException;
    User login(String email, String passwordHash) throws RemoteException;
    void envoyerOtp(int userId, String canal, String raison) throws RemoteException;
    boolean verifierOtp(int userId, String code, String raison) throws RemoteException;
    void renvoyerOtp(int userId, String raison) throws RemoteException;
    void suspendreCompte(int targetId) throws RemoteException;
    void supprimerCompte(int targetId) throws RemoteException;
    void verifierEmail(int userId) throws RemoteException;
    java.util.List<User> findByStatus(AccountStatus status) throws RemoteException;
}
