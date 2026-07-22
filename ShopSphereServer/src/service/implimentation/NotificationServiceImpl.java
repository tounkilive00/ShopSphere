/*
 * ShopSphere - NotificationServiceImpl
 * Meme structure qu'AgriConnect service/implimentation/NotificationServiceImpl.java
 */
package service.implimentation;

import dao.NotificationDao;
import dao.UserDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.logging.Logger;
import model.Notification;
import model.Notification.TypeNotif;
import model.User;
import service0.NotificationService;

/**
 * Implementation RMI du service notification.
 * Meme structure qu'AgriConnect NotificationServiceImpl.
 * @author ShopSphere
 */
public class NotificationServiceImpl extends UnicastRemoteObject implements NotificationService {

    private static final Logger LOG = Logger.getLogger(NotificationServiceImpl.class.getName());
    private final NotificationDao notifDao = new NotificationDao();
    private final UserDao userDao          = new UserDao();

    public NotificationServiceImpl() throws RemoteException {
        super();
    }

    /** Etait createNotificationRecord() dans AgriConnect */
    @Override
    public Notification createNotificationRecord(Notification notifObj) throws RemoteException {
        return notifDao.createNotification(notifObj);
    }

    /** Etait updateNotificationRecord() dans AgriConnect */
    @Override
    public Notification updateNotificationRecord(Notification notifObj) throws RemoteException {
        return notifDao.updateNotification(notifObj);
    }

    /** Etait deleteNotificationRecord() dans AgriConnect */
    @Override
    public Notification deleteNotificationRecord(Notification notifObj) throws RemoteException {
        return notifDao.deleteNotification(notifObj);
    }

    /** Etait findNotificationRecordById() dans AgriConnect */
    @Override
    public Notification findNotificationRecordById(int id) throws RemoteException {
        return notifDao.findNotificationById(id);
    }

    /** Etait findAllNotificationRecords() dans AgriConnect */
    @Override
    public List<Notification> findAllNotificationRecords() throws RemoteException {
        return notifDao.findAll();
    }

    /** Etait findUnreadNotifications() dans AgriConnect */
    @Override
    public List<Notification> findUnreadNotifications(int userId) throws RemoteException {
        return notifDao.findUnreadByUser(userId);
    }

    @Override
    public List<Notification> findNotificationsByUser(int userId) throws RemoteException {
        return notifDao.findByUser(userId);
    }

    @Override
    public void marquerToutesLues(int userId) throws RemoteException {
        notifDao.markAllRead(userId);
    }

    @Override
    public void envoyerNotification(int userId, String titre,
                                     String message, TypeNotif type) throws RemoteException {
        User user = userDao.findUserById(userId);
        if (user == null) throw new RemoteException("Utilisateur introuvable : " + userId);
        Notification notif = new Notification(user, titre, message, type);
        notifDao.createNotification(notif);
        LOG.info("[NOTIF -> " + user.getEmail() + "] " + titre + " : " + message);
    }

    @Override
    public long compterNonLues(int userId) throws RemoteException {
        List<Notification> unread = notifDao.findUnreadByUser(userId);
        return unread != null ? unread.size() : 0L;
    }
}
