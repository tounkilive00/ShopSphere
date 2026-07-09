/*
 * ShopSphere - UserDao
 * Meme structure qu'AgriConnect dao/UserDao.java
 * Memes methodes : createUser, updateUser, deleteUser, findUserById,
 *                  findUserByEmail, findAll, findByRole
 * Nouvelles methodes : findByStatus, updateStatus, verifyEmail
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
 * Inspire de AgriConnect dao/UserDao.java — memes operations CRUD Hibernate.
 * @author ShopSphere
 */
public class UserDao {

    // ── Creer (etait createUser dans AgriConnect) ─────────────────────────
    public User createUser(User userObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.save(userObj);
            tr.commit();
            ss.close();
            return userObj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // ── Modifier (etait updateUser dans AgriConnect) ──────────────────────
    public User updateUser(User userObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.update(userObj);
            tr.commit();
            ss.close();
            return userObj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // ── Supprimer (etait deleteUser dans AgriConnect) ─────────────────────
    public User deleteUser(User userObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.delete(userObj);
            tr.commit();
            ss.close();
            return userObj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // ── Trouver par ID (etait findUserById dans AgriConnect) ──────────────
    public User findUserById(int id) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            User u = (User) ss.get(User.class, id);
            ss.close();
            return u;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // ── Trouver par email (etait findUserByEmail dans AgriConnect) ─────────
    public User findUserByEmail(String email) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            User u = (User) ss.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email")
                    .setParameter("email", email)
                    .uniqueResult();
            ss.close();
            return u;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // ── Lister tous (etait findAll dans AgriConnect) ──────────────────────
    public List<User> findAll() {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            List<User> users = ss.createQuery("SELECT u FROM User u").list();
            ss.close();
            return users;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    // ── Filtrer par role (etait findByRole dans AgriConnect) ──────────────
    public List<User> findByRole(Role role) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            List<User> users = ss.createQuery(
                    "SELECT u FROM User u WHERE u.role = :role")
                    .setParameter("role", role)
                    .list();
            ss.close();
            return users;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    // ── Filtrer par statut (nouveau pour ShopSphere) ──────────────────────
    public List<User> findByStatus(AccountStatus status) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            List<User> users = ss.createQuery(
                    "SELECT u FROM User u WHERE u.status = :status")
                    .setParameter("status", status)
                    .list();
            ss.close();
            return users;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    // ── Changer statut (suspend / suppression douce) ──────────────────────
    public void updateStatus(int userId, AccountStatus status) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.createQuery("UPDATE User u SET u.status = :status WHERE u.id = :id")
              .setParameter("status", status)
              .setParameter("id", userId)
              .executeUpdate();
            tr.commit();
            ss.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ── Verifier email ────────────────────────────────────────────────────
    public void verifyEmail(int userId) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.createQuery("UPDATE User u SET u.emailVerified = true WHERE u.id = :id")
              .setParameter("id", userId)
              .executeUpdate();
            tr.commit();
            ss.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ── Authentification (etait login dans AgriConnect UserServiceImpl) ───
    public User authenticate(String email, String passwordHash) {
        User found = findUserByEmail(email);
        if (found != null
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
