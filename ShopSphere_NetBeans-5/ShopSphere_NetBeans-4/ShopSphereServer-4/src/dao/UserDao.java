/*
 * ShopSphere - UserDao
 * Structure DAO securisee avec gestion des sessions, transactions et verifications d'erreurs
 */
package dao;

import java.util.Collections;
import java.util.List;
import model.User;
import model.User.AccountStatus;
import model.User.Role;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * DAO pour l'entite User.
 * Operations CRUD Hibernate avec gestion propre des transactions et fermeture des sessions.
 * @author ShopSphere
 */
public class UserDao {

    // ── Creer ─────────────────────────────────────────────────────────────
    public User createUser(User userObj) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.save(userObj);
            tr.commit();
            return userObj;
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    // ── Modifier ──────────────────────────────────────────────────────────
    public User updateUser(User userObj) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.update(userObj);
            tr.commit();
            return userObj;
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    // ── Supprimer ─────────────────────────────────────────────────────────
    public User deleteUser(User userObj) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.delete(userObj);
            tr.commit();
            return userObj;
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    // ── Trouver par ID ────────────────────────────────────────────────────
    public User findUserById(int id) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            return (User) ss.get(User.class, id);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    // ── Trouver par email ─────────────────────────────────────────────────
    public User findUserByEmail(String email) {
        if (email == null) return null;
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<User> list = ss.createQuery(
                    "SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
                    .setParameter("email", email.trim())
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

    // ── Lister tous ───────────────────────────────────────────────────────
    public List<User> findAll() {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<User> users = ss.createQuery("SELECT u FROM User u ORDER BY u.id ASC").list();
            return users;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }

    // ── Filtrer par role ──────────────────────────────────────────────────
    public List<User> findByRole(Role role) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<User> users = ss.createQuery(
                    "SELECT u FROM User u WHERE u.role = :role ORDER BY u.id ASC")
                    .setParameter("role", role)
                    .list();
            return users;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }

    // ── Filtrer par statut ────────────────────────────────────────────────
    public List<User> findByStatus(AccountStatus status) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<User> users = ss.createQuery(
                    "SELECT u FROM User u WHERE u.status = :status ORDER BY u.id ASC")
                    .setParameter("status", status)
                    .list();
            return users;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }

    // ── Changer statut ────────────────────────────────────────────────────
    public void updateStatus(int userId, AccountStatus status) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            User u = (User) ss.get(User.class, userId);
            if (u != null) {
                u.setStatus(status);
                ss.update(u);
            }
            tr.commit();
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }

    // ── Verifier email ────────────────────────────────────────────────────
    public void verifyEmail(int userId) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            User u = (User) ss.get(User.class, userId);
            if (u != null) {
                u.setEmailVerified(true);
                ss.update(u);
            }
            tr.commit();
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }

    // ── Authentification ─────────────────────────────────────────────────
    public User authenticate(String email, String passwordHash) {
        User found = findUserByEmail(email);
        if (found != null
                && found.getPasswordHash() != null
                && found.getPasswordHash().equals(passwordHash)
                && found.isActive()) {
            return found;
        }
        return null;
    }

    // ── Email deja utilise ────────────────────────────────────────────────
    public boolean existsByEmail(String email) {
        return findUserByEmail(email) != null;
    }
}
