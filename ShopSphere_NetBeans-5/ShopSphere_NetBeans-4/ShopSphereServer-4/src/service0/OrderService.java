/*
 * ShopSphere - OrderService (interface RMI)
 * Meme structure qu'AgriConnect service0/OrderService.java
 */
package service0;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Order;
import model.Order.OrderStatus;
import model.OrderItem;

/**
 * Interface RMI du service commande.
 * Identique a AgriConnect service0/OrderService.java — memes signatures.
 * @author ShopSphere
 */
public interface OrderService extends Remote {

    // CRUD de base (meme qu'AgriConnect)
    Order createOrderRecord(Order orderObj)                throws RemoteException;
    Order updateOrderRecord(Order orderObj)                throws RemoteException;
    Order deleteOrderRecord(Order orderObj)                throws RemoteException;
    Order findOrderRecordById(int id)                      throws RemoteException;
    List<Order> findAllOrderRecords()                      throws RemoteException;

    // Filtres (meme qu'AgriConnect)
    List<Order> findOrderRecordsByBuyer(int buyerId)       throws RemoteException;
    List<Order> findOrderRecordsByStatus(OrderStatus status) throws RemoteException;

    // Cycle de vie commande
    Order passerCommande(int buyerId, List<OrderItem> items,
                         String adresseLivraison, String modePaiement) throws RemoteException;
    void annulerCommande(int orderId, int buyerId)         throws RemoteException;
    void mettreAJourStatut(int orderId, OrderStatus statut) throws RemoteException;

    // Analytique (nouveau pour ShopSphere)
    double calculerRevenuVendeur(int sellerId)             throws RemoteException;
    double calculerDepensesAcheteur(int buyerId)           throws RemoteException;
}
