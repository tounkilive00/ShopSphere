/*
 * ShopSphere - NotificationService (interface RMI)
 * Meme structure qu'AgriConnect service0/NotificationService.java
 */
package service0;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Notification;
import model.Notification.TypeNotif;

/**
 * Interface RMI du service notification.
 * Identique a AgriConnect service0/NotificationService.java — memes signatures.
 * @author ShopSphere
 */
public interface NotificationService extends Remote {

    // CRUD de base (meme qu'AgriConnect)
    Notification createNotificationRecord(Notification notifObj) throws RemoteException;
    Notification updateNotificationRecord(Notification notifObj) throws RemoteException;
    Notification deleteNotificationRecord(Notification notifObj) throws RemoteException;
    Notification findNotificationRecordById(int id)              throws RemoteException;
    List<Notification> findAllNotificationRecords()              throws RemoteException;

    // Filtres (meme qu'AgriConnect)
    List<Notification> findUnreadNotifications(int userId)       throws RemoteException;
    List<Notification> findNotificationsByUser(int userId)       throws RemoteException;

    // Actions
    void marquerToutesLues(int userId)                           throws RemoteException;
    void envoyerNotification(int userId, String titre,
                             String message, TypeNotif type)     throws RemoteException;
    long compterNonLues(int userId)                              throws RemoteException;
}
