/*
 * ShopSphere - OtpRecordDao
 * REMPLACE AgriConnect : acces direct a user.otpCode (champ String en clair)
 * Desormais OTP dans table separee avec hash BCrypt, expiration, canal
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
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.save(otpObj);
            tr.commit();
            ss.close();
            return otpObj;
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    public OtpRecord updateOtpRecord(OtpRecord otpObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.update(otpObj);
            tr.commit();
            ss.close();
            return otpObj;
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    // Trouver OTP actif pour un utilisateur et une raison
    public OtpRecord findActiveByUserAndRaison(int userId, Raison raison) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            OtpRecord otp = (OtpRecord) ss.createQuery(
                    "SELECT o FROM OtpRecord o WHERE o.user.id = :uid " +
                    "AND o.raison = :raison AND o.utilise = false " +
                    "AND o.expiresAt > :now")
                    .setParameter("uid", userId)
                    .setParameter("raison", raison)
                    .setParameter("now", LocalDateTime.now())
                    .uniqueResult();
            ss.close();
            return otp;
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    // Invalider tous les OTP precedents pour un utilisateur et une raison
    public void invalidateAll(int userId, Raison raison) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.createQuery(
                    "UPDATE OtpRecord o SET o.utilise = true " +
                    "WHERE o.user.id = :uid AND o.raison = :raison")
                    .setParameter("uid", userId)
                    .setParameter("raison", raison)
                    .executeUpdate();
            tr.commit();
            ss.close();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // Compter les OTP envoyes dans la derniere heure (rate-limiting)
    public long countRecentByUser(int userId, int heures) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Long count = (Long) ss.createQuery(
                    "SELECT COUNT(o) FROM OtpRecord o WHERE o.user.id = :uid " +
                    "AND o.createdAt > :since")
                    .setParameter("uid", userId)
                    .setParameter("since", LocalDateTime.now().minusHours(heures))
                    .uniqueResult();
            ss.close();
            return count != null ? count : 0L;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0L;
        }
    }

    // Nettoyer les OTP expires (appele par le scheduler)
    public void deleteExpired() {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.createQuery("DELETE FROM OtpRecord o WHERE o.expiresAt < :now")
              .setParameter("now", LocalDateTime.now())
              .executeUpdate();
            tr.commit();
            ss.close();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void deleteRecord(OtpRecord otp) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.delete(otp);
            tr.commit();
            ss.close();
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
