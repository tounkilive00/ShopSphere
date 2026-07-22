/*
 * ShopSphere - OtpRecordDao
 * Structure DAO securisee avec gestion des sessions, transactions et verifications d'erreurs
 */
package dao;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import model.OtpRecord;
import model.OtpRecord.Raison;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * DAO pour l'entite OtpRecord.
 * @author ShopSphere
 */
public class OtpRecordDao {

    public OtpRecord createOtpRecord(OtpRecord otpObj) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.save(otpObj);
            tr.commit();
            return otpObj;
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    public OtpRecord updateOtpRecord(OtpRecord otpObj) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.update(otpObj);
            tr.commit();
            return otpObj;
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    // Trouver OTP actif pour un utilisateur et une raison
    public OtpRecord findActiveByUserAndRaison(int userId, Raison raison) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<OtpRecord> list = ss.createQuery(
                    "SELECT o FROM OtpRecord o WHERE o.user.id = :uid " +
                    "AND o.raison = :raison AND o.utilise = false " +
                    "AND o.expiresAt > :now ORDER BY o.createdAt DESC")
                    .setParameter("uid", userId)
                    .setParameter("raison", raison)
                    .setParameter("now", LocalDateTime.now())
                    .setMaxResults(1)
                    .list();
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    // Invalider tous les OTP precedents pour un utilisateur et une raison
    public void invalidateAll(int userId, Raison raison) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.createQuery(
                    "UPDATE OtpRecord o SET o.utilise = true " +
                    "WHERE o.user.id = :uid AND o.raison = :raison")
                    .setParameter("uid", userId)
                    .setParameter("raison", raison)
                    .executeUpdate();
            tr.commit();
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }

    // Compter les OTP envoyes dans la derniere heure (rate-limiting)
    public long countRecentByUser(int userId, int heures) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Object count = ss.createQuery(
                    "SELECT COUNT(o) FROM OtpRecord o WHERE o.user.id = :uid " +
                    "AND o.createdAt > :since")
                    .setParameter("uid", userId)
                    .setParameter("since", LocalDateTime.now().minusHours(heures))
                    .uniqueResult();
            if (count instanceof Number) {
                return ((Number) count).longValue();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return 0L;
    }

    // Nettoyer les OTP expires (appele par le scheduler)
    public void deleteExpired() {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.createQuery("DELETE FROM OtpRecord o WHERE o.expiresAt < :now")
              .setParameter("now", LocalDateTime.now())
              .executeUpdate();
            tr.commit();
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }

    public void deleteRecord(OtpRecord otp) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.delete(otp);
            tr.commit();
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }
}
