/*
 * ShopSphere - NotificationDao
 * Structure DAO securisee avec gestion des sessions, transactions et verifications d'erreurs
 */
package dao;

import java.util.Collections;
import java.util.List;
import model.Notification;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * DAO pour l'entite Notification.
 * @author ShopSphere
 */
public class NotificationDao {

    public Notification createNotification(Notification notifObj) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.save(notifObj);
            tr.commit();
            return notifObj;
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    public Notification updateNotification(Notification notifObj) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.update(notifObj);
            tr.commit();
            return notifObj;
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    public Notification deleteNotification(Notification notifObj) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.delete(notifObj);
            tr.commit();
            return notifObj;
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    public Notification findNotificationById(int id) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            return (Notification) ss.get(Notification.class, id);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    public List<Notification> findAll() {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<Notification> notifs = ss.createQuery("SELECT n FROM Notification n ORDER BY n.timestamp DESC").list();
            return notifs;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }

    public List<Notification> findUnreadByUser(int userId) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<Notification> notifs = ss.createQuery(
                    "SELECT n FROM Notification n WHERE n.user.id = :uid AND n.isRead = false ORDER BY n.timestamp DESC")
                    .setParameter("uid", userId)
                    .list();
            return notifs;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }

    public List<Notification> findByUser(int userId) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<Notification> notifs = ss.createQuery(
                    "SELECT n FROM Notification n WHERE n.user.id = :uid ORDER BY n.timestamp DESC")
                    .setParameter("uid", userId)
                    .list();
            return notifs;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }

    public void markAllRead(int userId) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.createQuery("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :uid")
              .setParameter("uid", userId)
              .executeUpdate();
            tr.commit();
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }
}
