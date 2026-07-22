/*
 * ShopSphere - ProductDao
 * Structure DAO securisee avec gestion des sessions, transactions et verifications d'erreurs
 */
package dao;

import java.util.Collections;
import java.util.List;
import model.Product;
import model.Product.Category;
import model.Product.ProductStatus;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * DAO pour l'entite Product.
 * @author ShopSphere
 */
public class ProductDao {

    // ── Creer ─────────────────────────────────────────────────────────────
    public Product createProduct(Product productObj) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.save(productObj);
            tr.commit();
            return productObj;
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    // ── Modifier ──────────────────────────────────────────────────────────
    public Product updateProduct(Product productObj) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.update(productObj);
            tr.commit();
            return productObj;
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    // ── Supprimer (soft delete -> ARCHIVE) ────────────────────────────────
    public Product deleteProduct(Product productObj) {
        try {
            productObj.setStatus(ProductStatus.ARCHIVE);
            return updateProduct(productObj);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // ── Supprimer physiquement (Admin uniquement) ──────────────────────────
    public Product hardDelete(Product productObj) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.delete(productObj);
            tr.commit();
            return productObj;
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    // ── Trouver par ID ────────────────────────────────────────────────────
    public Product findProductById(int id) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            return (Product) ss.get(Product.class, id);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    // ── Lister tous ───────────────────────────────────────────────────────
    public List<Product> findAll() {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<Product> products = ss.createQuery("SELECT p FROM Product p ORDER BY p.id DESC").list();
            return products;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }

    // ── Produits actifs ───────────────────────────────────────────────────
    public List<Product> findActiveProducts() {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<Product> products = ss.createQuery(
                    "SELECT p FROM Product p WHERE p.status = :status AND p.stockQty > 0 ORDER BY p.id DESC")
                    .setParameter("status", ProductStatus.ACTIF)
                    .list();
            return products;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }

    // ── Par categorie ─────────────────────────────────────────────────────
    public List<Product> findByCategory(Category category) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<Product> products = ss.createQuery(
                    "SELECT p FROM Product p WHERE p.category = :cat AND p.status = :status ORDER BY p.id DESC")
                    .setParameter("cat", category)
                    .setParameter("status", ProductStatus.ACTIF)
                    .list();
            return products;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }

    // ── Par vendeur ───────────────────────────────────────────────────────
    public List<Product> findBySeller(int sellerId) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<Product> products = ss.createQuery(
                    "SELECT p FROM Product p WHERE p.seller.id = :sid ORDER BY p.id DESC")
                    .setParameter("sid", sellerId)
                    .list();
            return products;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }

    public List<Product> findByFarmer(int farmerId) {
        return findBySeller(farmerId);
    }

    // ── Recherche par nom + description + marque ───────────────────────────
    public List<Product> searchByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findActiveProducts();
        }
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            String kw = "%" + keyword.trim().toLowerCase() + "%";
            List<Product> products = ss.createQuery(
                    "SELECT p FROM Product p WHERE p.status = :status AND " +
                    "(LOWER(p.title) LIKE :kw OR LOWER(p.description) LIKE :kw OR LOWER(p.brand) LIKE :kw) ORDER BY p.id DESC")
                    .setParameter("status", ProductStatus.ACTIF)
                    .setParameter("kw", kw)
                    .list();
            return products;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }

    // ── Par plage de prix ─────────────────────────────────────────────────
    public List<Product> findByPriceRange(double min, double max) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<Product> products = ss.createQuery(
                    "SELECT p FROM Product p WHERE p.status = :status " +
                    "AND p.basePrice BETWEEN :min AND :max ORDER BY p.id DESC")
                    .setParameter("status", ProductStatus.ACTIF)
                    .setParameter("min", min)
                    .setParameter("max", max)
                    .list();
            return products;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }
}
