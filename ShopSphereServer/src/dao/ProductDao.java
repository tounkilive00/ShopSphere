/*
 * ShopSphere - ProductDao
 * Meme structure qu'AgriConnect dao/ProductDao.java
 * Memes methodes : createProduct, updateProduct, deleteProduct,
 *                  findProductById, findAll, findAvailableProducts,
 *                  findByCategory, findByFarmer (-> findBySeller), searchByName
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
 * Inspire de AgriConnect dao/ProductDao.java.
 * @author ShopSphere
 */
public class ProductDao {

    // ── Creer ─────────────────────────────────────────────────────────────
    public Product createProduct(Product productObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.save(productObj);
            tr.commit();
            ss.close();
            return productObj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // ── Modifier ──────────────────────────────────────────────────────────
    public Product updateProduct(Product productObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.update(productObj);
            tr.commit();
            ss.close();
            return productObj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // ── Supprimer (soft delete -> ARCHIVE) ────────────────────────────────
    public Product deleteProduct(Product productObj) {
        try {
            // Soft delete : archive au lieu de supprimer
            productObj.setStatus(ProductStatus.ARCHIVE);
            return updateProduct(productObj);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // ── Supprimer physiquement (Admin uniquement) ──────────────────────────
    public Product hardDelete(Product productObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.delete(productObj);
            tr.commit();
            ss.close();
            return productObj;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // ── Trouver par ID ────────────────────────────────────────────────────
    public Product findProductById(int id) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Product p = (Product) ss.get(Product.class, id);
            ss.close();
            return p;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // ── Lister tous ───────────────────────────────────────────────────────
    public List<Product> findAll() {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            List<Product> products = ss.createQuery("SELECT p FROM Product p").list();
            ss.close();
            return products;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    // ── Produits actifs (etait findAvailableProducts dans AgriConnect) ─────
    public List<Product> findActiveProducts() {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            List<Product> products = ss.createQuery(
                    "SELECT p FROM Product p WHERE p.status = 'ACTIF' AND p.stockQty > 0")
                    .list();
            ss.close();
            return products;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    // ── Par categorie (etait findByCategory dans AgriConnect) ─────────────
    public List<Product> findByCategory(Category category) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            List<Product> products = ss.createQuery(
                    "SELECT p FROM Product p WHERE p.category = :cat AND p.status = 'ACTIF'")
                    .setParameter("cat", category)
                    .list();
            ss.close();
            return products;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    // ── Par vendeur (etait findByFarmer dans AgriConnect) ─────────────────
    public List<Product> findBySeller(int sellerId) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            List<Product> products = ss.createQuery(
                    "SELECT p FROM Product p WHERE p.seller.id = :sid")
                    .setParameter("sid", sellerId)
                    .list();
            ss.close();
            return products;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Alias pour compat AgriConnect
    public List<Product> findByFarmer(int farmerId) {
        return findBySeller(farmerId);
    }

    // ── Recherche par nom + description + marque ───────────────────────────
    public List<Product> searchByName(String keyword) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            String kw = "%" + keyword.toLowerCase() + "%";
            List<Product> products = ss.createQuery(
                    "SELECT p FROM Product p WHERE p.status = 'ACTIF' AND " +
                    "(LOWER(p.title) LIKE :kw OR LOWER(p.description) LIKE :kw OR LOWER(p.brand) LIKE :kw)")
                    .setParameter("kw", kw)
                    .list();
            ss.close();
            return products;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    // ── Par plage de prix ─────────────────────────────────────────────────
    public List<Product> findByPriceRange(double min, double max) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            List<Product> products = ss.createQuery(
                    "SELECT p FROM Product p WHERE p.status = 'ACTIF' " +
                    "AND p.basePrice BETWEEN :min AND :max")
                    .setParameter("min", min)
                    .setParameter("max", max)
                    .list();
            ss.close();
            return products;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
}
