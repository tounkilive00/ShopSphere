package service0;
import java.rmi.Remote;
import java.rmi.RemoteException;
import model.Notification;
import model.Notification.TypeNotif;

public interface NotificationService extends Remote {
    Notification createNotificationRecord(Notification n) throws RemoteException;
    Notification updateNotificationRecord(Notification n) throws RemoteException;
    Notification deleteNotificationRecord(Notification n) throws RemoteException;
    Notification findNotificationRecordById(int id) throws RemoteException;
    java.util.List<Notification> findAllNotificationRecords() throws RemoteException;
    java.util.List<Notification> findUnreadNotifications(int userId) throws RemoteException;
    java.util.List<Notification> findNotificationsByUser(int userId) throws RemoteException;
    void marquerToutesLues(int userId) throws RemoteException;
    void envoyerNotification(int userId, String titre, String msg, TypeNotif type) throws RemoteException;
    long compterNonLues(int userId) throws RemoteException;
}
