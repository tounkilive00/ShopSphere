/*
 * ShopSphere - OrderDao
 * Meme structure qu'AgriConnect dao/OrderDao.java
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
 * Inspire de AgriConnect dao/OrderDao.java.
 * @author ShopSphere
 */
public class OrderDao {

    public Order createOrder(Order orderObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.save(orderObj);
            tr.commit();
            ss.close();
            return orderObj;
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    public Order updateOrder(Order orderObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.update(orderObj);
            tr.commit();
            ss.close();
            return orderObj;
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    public Order deleteOrder(Order orderObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.delete(orderObj);
            tr.commit();
            ss.close();
            return orderObj;
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    public Order findOrderById(int id) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Order o = (Order) ss.createQuery(
                    "SELECT DISTINCT o FROM Order o " +
                    "LEFT JOIN FETCH o.orderItems oi " +
                    "LEFT JOIN FETCH oi.product p " +
                    "LEFT JOIN FETCH p.seller s " +
                    "LEFT JOIN FETCH o.buyer b " +
                    "WHERE o.id = :id")
                    .setParameter("id", id)
                    .uniqueResult();
            ss.close();
            return o;
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    // meme methode qu'AgriConnect avec JOIN FETCH pour eviter lazy loading
    public List<Order> findAll() {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            List<Order> orders = ss.createQuery(
                    "SELECT DISTINCT o FROM Order o " +
                    "LEFT JOIN FETCH o.orderItems oi " +
                    "LEFT JOIN FETCH oi.product p " +
                    "LEFT JOIN FETCH p.seller s " +
                    "LEFT JOIN FETCH o.buyer b")
                    .list();
            ss.close();
            return orders;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Commandes d'un acheteur (etait findByBuyer dans AgriConnect)
    public List<Order> findByBuyer(int buyerId) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            List<Order> orders = ss.createQuery(
                    "SELECT DISTINCT o FROM Order o " +
                    "LEFT JOIN FETCH o.orderItems oi " +
                    "LEFT JOIN FETCH oi.product " +
                    "WHERE o.buyer.id = :bid")
                    .setParameter("bid", buyerId)
                    .list();
            ss.close();
            return orders;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Order> findByStatus(OrderStatus status) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            List<Order> orders = ss.createQuery(
                    "SELECT o FROM Order o WHERE o.status = :st")
                    .setParameter("st", status)
                    .list();
            ss.close();
            return orders;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Revenus d'un vendeur (nouveau pour analytics)
    public double sumRevenueBySeller(int sellerId) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Double total = (Double) ss.createQuery(
                    "SELECT COALESCE(SUM(i.quantity * i.unitPrice), 0) " +
                    "FROM OrderItem i WHERE i.seller.id = :sid " +
                    "AND i.order.status != 'ANNULEE'")
                    .setParameter("sid", sellerId)
                    .uniqueResult();
            ss.close();
            return total != null ? total : 0.0;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0.0;
        }
    }

    // Depenses totales d'un client (pour historique)
    public double sumExpenseByBuyer(int buyerId) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Double total = (Double) ss.createQuery(
                    "SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
                    "WHERE o.buyer.id = :bid AND o.status != 'ANNULEE'")
                    .setParameter("bid", buyerId)
                    .uniqueResult();
            ss.close();
            return total != null ? total : 0.0;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0.0;
        }
    }
}
