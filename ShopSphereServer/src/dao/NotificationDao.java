/*
 * ShopSphere - NotificationDao
 * Meme structure qu'AgriConnect dao/NotificationDao.java
 */
package dao;

import java.util.Collections;
import java.util.List;
import model.Notification;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * DAO pour l'entite Notification — inspire de AgriConnect dao/NotificationDao.java.
 * @author ShopSphere
 */
public class NotificationDao {

    public Notification createNotification(Notification notifObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.save(notifObj);
            tr.commit();
            ss.close();
            return notifObj;
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    public Notification updateNotification(Notification notifObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.update(notifObj);
            tr.commit();
            ss.close();
            return notifObj;
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    public Notification deleteNotification(Notification notifObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.delete(notifObj);
            tr.commit();
            ss.close();
            return notifObj;
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    public Notification findNotificationById(int id) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Notification n = (Notification) ss.get(Notification.class, id);
            ss.close();
            return n;
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    public List<Notification> findAll() {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            List<Notification> notifs = ss.createQuery("SELECT n FROM Notification n").list();
            ss.close();
            return notifs;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    // etait findUnreadByUser dans AgriConnect
    public List<Notification> findUnreadByUser(int userId) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            List<Notification> notifs = ss.createQuery(
                    "SELECT n FROM Notification n WHERE n.user.id = :uid AND n.isRead = false")
                    .setParameter("uid", userId)
                    .list();
            ss.close();
            return notifs;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Notification> findByUser(int userId) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            List<Notification> notifs = ss.createQuery(
                    "SELECT n FROM Notification n WHERE n.user.id = :uid ORDER BY n.timestamp DESC")
                    .setParameter("uid", userId)
                    .list();
            ss.close();
            return notifs;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void markAllRead(int userId) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.createQuery("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :uid")
              .setParameter("uid", userId)
              .executeUpdate();
            tr.commit();
            ss.close();
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
