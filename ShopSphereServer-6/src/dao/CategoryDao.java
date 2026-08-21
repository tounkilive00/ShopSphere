/*
 * ShopSphere - CategoryDao
 * Structure DAO securisee avec gestion des sessions, transactions et verifications d'erreurs
 * Meme convention que les autres DAO (UserDao, ProductDao, ...).
 * Corrige : model.Category etait mappee dans hibernate.cfg.xml et referencee
 * par Product.categoryRef, mais n'avait aucun DAO — entite totalement orpheline.
 */
package dao;

import java.util.Collections;
import java.util.List;
import model.Category;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * DAO pour l'entite Category.
 * @author ShopSphere
 */
public class CategoryDao {

    // ── Creer ─────────────────────────────────────────────────────────────
    public Category createCategory(Category categoryObj) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.save(categoryObj);
            tr.commit();
            return categoryObj;
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    // ── Modifier ──────────────────────────────────────────────────────────
    public Category updateCategory(Category categoryObj) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.update(categoryObj);
            tr.commit();
            return categoryObj;
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    // ── Supprimer ─────────────────────────────────────────────────────────
    public Category deleteCategory(Category categoryObj) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.delete(categoryObj);
            tr.commit();
            return categoryObj;
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    // ── Trouver par ID ────────────────────────────────────────────────────
    public Category findCategoryById(int id) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            return (Category) ss.get(Category.class, id);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    // ── Trouver par slug ──────────────────────────────────────────────────
    public Category findBySlug(String slug) {
        if (slug == null) return null;
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<Category> list = ss.createQuery(
                    "SELECT c FROM Category c WHERE c.slug = :slug")
                    .setParameter("slug", slug.trim())
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

    // ── Lister toutes ─────────────────────────────────────────────────────
    public List<Category> findAll() {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<Category> categories = ss.createQuery(
                    "SELECT c FROM Category c ORDER BY c.nom ASC").list();
            return categories;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }

    // ── Categories racines (sans parent) ────────────────────────────────
    public List<Category> findRootCategories() {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<Category> categories = ss.createQuery(
                    "SELECT c FROM Category c WHERE c.parent IS NULL ORDER BY c.nom ASC").list();
            return categories;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }

    // ── Sous-categories d'un parent ───────────────────────────────────────
    public List<Category> findByParent(int parentId) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<Category> categories = ss.createQuery(
                    "SELECT c FROM Category c WHERE c.parent.id = :pid ORDER BY c.nom ASC")
                    .setParameter("pid", parentId)
                    .list();
            return categories;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }
}
