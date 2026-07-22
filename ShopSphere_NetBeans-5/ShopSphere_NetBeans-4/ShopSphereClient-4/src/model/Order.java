package model;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class Order implements Serializable {
    public static final long serialVersionUID = 1L;
    public enum OrderStatus {
        EN_ATTENTE_PAIEMENT, PAYEE, EN_TRAITEMENT,
        EXPEDIEE, LIVREE, TERMINEE, ANNULEE, REMBOURSEE
    }
    private int id;
    private LocalDate orderDate;
    private OrderStatus status = OrderStatus.EN_ATTENTE_PAIEMENT;
    private double subtotal;
    private double shippingCost = 2.99;
    private double totalAmount;
    private String paymentMethod;
    private String paymentRef;
    private String shippingAddress;
    private User   buyer;
    private List<OrderItem> orderItems;

    public Order() {}
    public Order(User buyer, double totalAmount, OrderStatus status) {
        this.buyer = buyer; this.totalAmount = totalAmount;
        this.status = status; this.orderDate = LocalDate.now();
    }
    public int    getId()                    { return id; }
    public void   setId(int v)               { this.id = v; }
    public LocalDate getOrderDate()          { return orderDate; }
    public void   setOrderDate(LocalDate v)  { this.orderDate = v; }
    public OrderStatus getStatus()           { return status; }
    public void   setStatus(OrderStatus v)   { this.status = v; }
    public double getSubtotal()              { return subtotal; }
    public void   setSubtotal(double v)      { this.subtotal = v; }
    public double getShippingCost()          { return shippingCost; }
    public void   setShippingCost(double v)  { this.shippingCost = v; }
    public double getTotalAmount()           { return totalAmount; }
    public void   setTotalAmount(double v)   { this.totalAmount = v; }
    public String getPaymentMethod()         { return paymentMethod; }
    public void   setPaymentMethod(String v) { this.paymentMethod = v; }
    public String getShippingAddress()       { return shippingAddress; }
    public void   setShippingAddress(String v){ this.shippingAddress = v; }
    public User   getBuyer()                 { return buyer; }
    public void   setBuyer(User v)           { this.buyer = v; }
    public List<OrderItem> getOrderItems()   { return orderItems; }
    public void   setOrderItems(List<OrderItem> v){ this.orderItems = v; }
    public boolean canCancel()               { return status == OrderStatus.EN_ATTENTE_PAIEMENT || status == OrderStatus.PAYEE; }
    @Override public String toString()       { return "Commande #" + id + " [" + status + "] - " + totalAmount + " EUR"; }
}
