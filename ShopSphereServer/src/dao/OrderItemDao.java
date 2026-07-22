/*
 * ShopSphere - OrderItemDao
 * Meme structure qu'AgriConnect dao/OrderItemDao.java
 */
package dao;

import java.util.Collections;
import java.util.List;
import model.OrderItem;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * DAO pour l'entite OrderItem — identique a AgriConnect.
 * @author ShopSphere
 */
public class OrderItemDao {

    public OrderItem createOrderItem(OrderItem itemObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.save(itemObj);
            tr.commit();
            ss.close();
            return itemObj;
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    public OrderItem updateOrderItem(OrderItem itemObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.update(itemObj);
            tr.commit();
            ss.close();
            return itemObj;
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    public OrderItem deleteOrderItem(OrderItem itemObj) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.delete(itemObj);
            tr.commit();
            ss.close();
            return itemObj;
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    public OrderItem findOrderItemById(int id) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            OrderItem item = (OrderItem) ss.get(OrderItem.class, id);
            ss.close();
            return item;
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    public List<OrderItem> findAll() {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            List<OrderItem> items = ss.createQuery("SELECT i FROM OrderItem i").list();
            ss.close();
            return items;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<OrderItem> findByOrder(int orderId) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            List<OrderItem> items = ss.createQuery(
                    "SELECT i FROM OrderItem i WHERE i.order.id = :oid")
                    .setParameter("oid", orderId)
                    .list();
            ss.close();
            return items;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<OrderItem> findBySeller(int sellerId) {
        try {
            Session ss = HibernateUtil.getSessionFactory().openSession();
            List<OrderItem> items = ss.createQuery(
                    "SELECT i FROM OrderItem i WHERE i.seller.id = :sid")
                    .setParameter("sid", sellerId)
                    .list();
            ss.close();
            return items;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }
}
