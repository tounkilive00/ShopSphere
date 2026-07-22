package service0;
import java.rmi.Remote;
import java.rmi.RemoteException;
import model.Order;
import model.Order.OrderStatus;
import model.OrderItem;

public interface OrderService extends Remote {
    Order createOrderRecord(Order o) throws RemoteException;
    Order updateOrderRecord(Order o) throws RemoteException;
    Order deleteOrderRecord(Order o) throws RemoteException;
    Order findOrderRecordById(int id) throws RemoteException;
    java.util.List<Order> findAllOrderRecords() throws RemoteException;
    java.util.List<Order> findOrderRecordsByBuyer(int buyerId) throws RemoteException;
    java.util.List<Order> findOrderRecordsByStatus(OrderStatus status) throws RemoteException;
    Order passerCommande(int buyerId, java.util.List<OrderItem> items, String adresse, String paiement) throws RemoteException;
    void annulerCommande(int orderId, int buyerId) throws RemoteException;
    void mettreAJourStatut(int orderId, OrderStatus statut) throws RemoteException;
    double calculerRevenuVendeur(int sellerId) throws RemoteException;
    double calculerDepensesAcheteur(int buyerId) throws RemoteException;
}
