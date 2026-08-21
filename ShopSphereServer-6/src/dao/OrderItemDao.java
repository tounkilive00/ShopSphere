/*
 * ShopSphere - OrderItemDao
 * Structure DAO securisee avec gestion des sessions, transactions et verifications d'erreurs
 */
package dao;

import java.util.Collections;
import java.util.List;
import model.OrderItem;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * DAO pour l'entite OrderItem.
 * @author ShopSphere
 */
public class OrderItemDao {

    public OrderItem createOrderItem(OrderItem itemObj) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.save(itemObj);
            tr.commit();
            return itemObj;
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    public OrderItem updateOrderItem(OrderItem itemObj) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.update(itemObj);
            tr.commit();
            return itemObj;
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    public OrderItem deleteOrderItem(OrderItem itemObj) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.delete(itemObj);
            tr.commit();
            return itemObj;
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    public OrderItem findOrderItemById(int id) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            return (OrderItem) ss.get(OrderItem.class, id);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    public List<OrderItem> findAll() {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<OrderItem> items = ss.createQuery("SELECT i FROM OrderItem i ORDER BY i.id DESC").list();
            return items;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }

    public List<OrderItem> findByOrder(int orderId) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<OrderItem> items = ss.createQuery(
                    "SELECT i FROM OrderItem i WHERE i.order.id = :oid ORDER BY i.id ASC")
                    .setParameter("oid", orderId)
                    .list();
            return items;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }

    public List<OrderItem> findBySeller(int sellerId) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<OrderItem> items = ss.createQuery(
                    "SELECT i FROM OrderItem i WHERE i.seller.id = :sid ORDER BY i.id DESC")
                    .setParameter("sid", sellerId)
                    .list();
            return items;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }
}
