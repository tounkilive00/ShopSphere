/*
 * ShopSphere - OrderDao
 * Structure DAO securisee avec gestion des sessions, transactions et verifications d'erreurs
 */
package dao;

import java.util.Collections;
import java.util.List;
import model.Order;
import model.Order.OrderStatus;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * DAO pour l'entite Order.
 * @author ShopSphere
 */
public class OrderDao {

    public Order createOrder(Order orderObj) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.save(orderObj);
            tr.commit();
            return orderObj;
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    public Order updateOrder(Order orderObj) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.update(orderObj);
            tr.commit();
            return orderObj;
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    public Order deleteOrder(Order orderObj) {
        Session ss = null;
        Transaction tr = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            tr = ss.beginTransaction();
            ss.delete(orderObj);
            tr.commit();
            return orderObj;
        } catch (Exception ex) {
            if (tr != null && tr.isActive()) tr.rollback();
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    public Order findOrderById(int id) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Order o = (Order) ss.createQuery(
                    "SELECT DISTINCT o FROM Order o " +
                    "LEFT JOIN FETCH o.orderItems oi " +
                    "LEFT JOIN FETCH oi.product p " +
                    "LEFT JOIN FETCH p.seller s " +
                    "LEFT JOIN FETCH o.buyer b " +
                    "WHERE o.id = :id")
                    .setParameter("id", id)
                    .uniqueResult();
            return o;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return null;
    }

    public List<Order> findAll() {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<Order> orders = ss.createQuery(
                    "SELECT DISTINCT o FROM Order o " +
                    "LEFT JOIN FETCH o.orderItems oi " +
                    "LEFT JOIN FETCH oi.product p " +
                    "LEFT JOIN FETCH p.seller s " +
                    "LEFT JOIN FETCH o.buyer b " +
                    "ORDER BY o.id DESC")
                    .list();
            return orders;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }

    public List<Order> findByBuyer(int buyerId) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<Order> orders = ss.createQuery(
                    "SELECT DISTINCT o FROM Order o " +
                    "LEFT JOIN FETCH o.orderItems oi " +
                    "LEFT JOIN FETCH oi.product p " +
                    "LEFT JOIN FETCH p.seller s " +
                    "LEFT JOIN FETCH o.buyer b " +
                    "WHERE o.buyer.id = :bid " +
                    "ORDER BY o.id DESC")
                    .setParameter("bid", buyerId)
                    .list();
            return orders;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }

    public List<Order> findByStatus(OrderStatus status) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            List<Order> orders = ss.createQuery(
                    "SELECT DISTINCT o FROM Order o " +
                    "LEFT JOIN FETCH o.orderItems oi " +
                    "LEFT JOIN FETCH oi.product p " +
                    "LEFT JOIN FETCH p.seller s " +
                    "LEFT JOIN FETCH o.buyer b " +
                    "WHERE o.status = :st " +
                    "ORDER BY o.id DESC")
                    .setParameter("st", status)
                    .list();
            return orders;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
    }

    public double sumRevenueBySeller(int sellerId) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Object res = ss.createQuery(
                    "SELECT SUM(i.quantity * i.unitPrice) " +
                    "FROM OrderItem i WHERE i.seller.id = :sid " +
                    "AND i.order.status != :status")
                    .setParameter("sid", sellerId)
                    .setParameter("status", OrderStatus.ANNULEE)
                    .uniqueResult();
            if (res instanceof Number) {
                return ((Number) res).doubleValue();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return 0.0;
    }

    public double sumExpenseByBuyer(int buyerId) {
        Session ss = null;
        try {
            ss = HibernateUtil.getSessionFactory().openSession();
            Object res = ss.createQuery(
                    "SELECT SUM(o.totalAmount) FROM Order o " +
                    "WHERE o.buyer.id = :bid AND o.status != :status")
                    .setParameter("bid", buyerId)
                    .setParameter("status", OrderStatus.ANNULEE)
                    .uniqueResult();
            if (res instanceof Number) {
                return ((Number) res).doubleValue();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (ss != null && ss.isOpen()) ss.close();
        }
        return 0.0;
    }
}
