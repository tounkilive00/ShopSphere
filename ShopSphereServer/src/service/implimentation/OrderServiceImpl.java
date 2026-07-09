/*
 * ShopSphere - OrderServiceImpl
 * Meme structure qu'AgriConnect service/implimentation/OrderServiceImpl.java
 */
package service.implimentation;

import dao.NotificationDao;
import dao.OrderDao;
import dao.OrderItemDao;
import dao.ProductDao;
import dao.UserDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.List;
import model.Notification;
import model.Notification.TypeNotif;
import model.Order;
import model.Order.OrderStatus;
import model.OrderItem;
import model.Product;
import model.User;
import service0.OrderService;

/**
 * Implementation RMI du service commande.
 * Meme structure qu'AgriConnect OrderServiceImpl.
 * @author ShopSphere
 */
public class OrderServiceImpl extends UnicastRemoteObject implements OrderService {

    private final OrderDao orderDao       = new OrderDao();
    private final OrderItemDao itemDao    = new OrderItemDao();
    private final UserDao userDao         = new UserDao();
    private final ProductDao productDao   = new ProductDao();
    private final NotificationDao notifDao = new NotificationDao();

    public OrderServiceImpl() throws RemoteException {
        super();
    }

    /** Etait createOrderRecord() dans AgriConnect */
    @Override
    public Order createOrderRecord(Order orderObj) throws RemoteException {
        return orderDao.createOrder(orderObj);
    }

    /** Etait updateOrderRecord() dans AgriConnect */
    @Override
    public Order updateOrderRecord(Order orderObj) throws RemoteException {
        return orderDao.updateOrder(orderObj);
    }

    /** Etait deleteOrderRecord() dans AgriConnect */
    @Override
    public Order deleteOrderRecord(Order orderObj) throws RemoteException {
        return orderDao.deleteOrder(orderObj);
    }

    /** Etait findOrderRecordById() dans AgriConnect */
    @Override
    public Order findOrderRecordById(int id) throws RemoteException {
        return orderDao.findOrderById(id);
    }

    /** Etait findAllOrderRecords() dans AgriConnect */
    @Override
    public List<Order> findAllOrderRecords() throws RemoteException {
        return orderDao.findAll();
    }

    /** Etait findOrderRecordsByBuyer() dans AgriConnect */
    @Override
    public List<Order> findOrderRecordsByBuyer(int buyerId) throws RemoteException {
        return orderDao.findByBuyer(buyerId);
    }

    @Override
    public List<Order> findOrderRecordsByStatus(OrderStatus status) throws RemoteException {
        return orderDao.findByStatus(status);
    }

    /** Creer une commande complete depuis un panier */
    @Override
    public Order passerCommande(int buyerId, List<OrderItem> items,
                                String adresseLivraison, String modePaiement)
            throws RemoteException {
        User buyer = userDao.findUserById(buyerId);
        if (buyer == null) throw new RemoteException("Acheteur introuvable.");
        if (items == null || items.isEmpty()) throw new RemoteException("Panier vide.");

        // Calculer le total
        double subtotal = 0;
        for (OrderItem item : items) {
            subtotal += item.getQuantity() * item.getUnitPrice();
        }
        double total = subtotal + 2.99; // frais de livraison

        // Creer la commande
        Order order = new Order();
        order.setBuyer(buyer);
        order.setOrderDate(LocalDate.now());
        order.setStatus(OrderStatus.EN_ATTENTE_PAIEMENT);
        order.setSubtotal(subtotal);
        order.setShippingCost(2.99);
        order.setTotalAmount(total);
        order.setShippingAddress(adresseLivraison);
        order.setPaymentMethod(modePaiement);

        Order savedOrder = orderDao.createOrder(order);
        if (savedOrder == null) throw new RemoteException("Erreur creation commande.");

        // Sauvegarder les lignes
        for (OrderItem item : items) {
            item.setOrder(savedOrder);
            // Definir le vendeur de chaque article
            if (item.getProduct() != null) {
                item.setSeller(item.getProduct().getSeller());
                // Decrementer le stock
                Product p = productDao.findProductById(item.getProduct().getId());
                if (p != null) {
                    p.setStockQty(p.getStockQty() - (int) item.getQuantity());
                    productDao.updateProduct(p);
                }
            }
            itemDao.createOrderItem(item);
        }

        // Notification en francais (langue principale)
        String titre   = "fr".equals(buyer.getPreferredLanguage())
                ? "Commande confirmee" : "Order confirmed";
        String message = "fr".equals(buyer.getPreferredLanguage())
                ? "Votre commande #" + savedOrder.getId() + " a ete recue. Total : " + String.format("%.2f", total) + " EUR"
                : "Your order #" + savedOrder.getId() + " has been received. Total: " + String.format("%.2f", total) + " EUR";
        notifDao.createNotification(new Notification(buyer, titre, message, TypeNotif.MISE_A_JOUR_COMMANDE));

        return savedOrder;
    }

    @Override
    public void annulerCommande(int orderId, int buyerId) throws RemoteException {
        Order order = orderDao.findOrderById(orderId);
        if (order == null) throw new RemoteException("Commande introuvable.");
        if (order.getBuyer().getId() != buyerId) throw new RemoteException("Non autorise.");
        if (!order.canCancel()) throw new RemoteException("Cette commande ne peut plus etre annulee.");
        order.setStatus(OrderStatus.ANNULEE);
        orderDao.updateOrder(order);
        String msg = "fr".equals(order.getBuyer().getPreferredLanguage())
                ? "Votre commande #" + orderId + " a ete annulee."
                : "Your order #" + orderId + " has been cancelled.";
        notifDao.createNotification(new Notification(order.getBuyer(),
                "Commande annulee", msg, TypeNotif.MISE_A_JOUR_COMMANDE));
    }

    @Override
    public void mettreAJourStatut(int orderId, OrderStatus statut) throws RemoteException {
        Order order = orderDao.findOrderById(orderId);
        if (order == null) throw new RemoteException("Commande introuvable.");
        order.setStatus(statut);
        orderDao.updateOrder(order);
        String msg = "fr".equals(order.getBuyer().getPreferredLanguage())
                ? "Votre commande #" + orderId + " est maintenant : " + statut.name()
                : "Your order #" + orderId + " is now: " + statut.name();
        notifDao.createNotification(new Notification(order.getBuyer(),
                "Statut commande", msg, TypeNotif.MISE_A_JOUR_COMMANDE));
    }

    @Override
    public double calculerRevenuVendeur(int sellerId) throws RemoteException {
        return orderDao.sumRevenueBySeller(sellerId);
    }

    @Override
    public double calculerDepensesAcheteur(int buyerId) throws RemoteException {
        return orderDao.sumExpenseByBuyer(buyerId);
    }
}
