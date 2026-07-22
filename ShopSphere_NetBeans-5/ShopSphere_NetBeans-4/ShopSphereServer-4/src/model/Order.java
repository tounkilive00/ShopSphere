/*
 * ShopSphere - Commande client
 * Inspire de AgriConnect model/Order.java
 * buyer -> buyer (garde), statuts etendus, montants en double (compat)
 */
package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.*;

/**
 * Commande passee par un CLIENT.
 * Meme structure qu'AgriConnect, statuts enrichis.
 * @author ShopSphere
 */
@Entity
@Table(name = "orders")
public class Order implements Serializable {

    public static final long serialVersionUID = 1L;

    // Statuts etendus — etait { PENDING, CONFIRMED, DISPATCHED, COMPLETED, CANCELLED }
    public enum OrderStatus {
        EN_ATTENTE_PAIEMENT, PAYEE, EN_TRAITEMENT,
        EXPEDIEE, LIVREE, TERMINEE, ANNULEE, REMBOURSEE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.EN_ATTENTE_PAIEMENT;

    @Column(name = "subtotal")
    private double subtotal;

    @Column(name = "shipping_cost")
    private double shippingCost = 2.99;

    @Column(name = "total_amount", nullable = false) // meme champ qu'AgriConnect
    private double totalAmount;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_ref")
    private String paymentRef;

    @Column(name = "shipping_address")
    private String shippingAddress;

    // meme association qu'AgriConnect (buyer)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems;

    // Constructeurs
    public Order() {}

    public Order(User buyer, double totalAmount, OrderStatus status) {
        this.buyer       = buyer;
        this.totalAmount = totalAmount;
        this.status      = status;
        this.orderDate   = LocalDate.now();
    }

    // Getters & Setters — meme convention qu'AgriConnect
    public int getId()                 { return id; }
    public void setId(int id)          { this.id = id; }

    public LocalDate getOrderDate()    { return orderDate; }
    public void setOrderDate(LocalDate v){ this.orderDate = v; }

    public OrderStatus getStatus()     { return status; }
    public void setStatus(OrderStatus v){ this.status = v; }

    public double getSubtotal()        { return subtotal; }
    public void setSubtotal(double v)  { this.subtotal = v; }

    public double getShippingCost()    { return shippingCost; }
    public void setShippingCost(double v){ this.shippingCost = v; }

    public double getTotalAmount()     { return totalAmount; }
    public void setTotalAmount(double v){ this.totalAmount = v; }

    public String getPaymentMethod()   { return paymentMethod; }
    public void setPaymentMethod(String v){ this.paymentMethod = v; }

    public String getPaymentRef()      { return paymentRef; }
    public void setPaymentRef(String v){ this.paymentRef = v; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String v){ this.shippingAddress = v; }

    public User getBuyer()             { return buyer; }
    public void setBuyer(User v)       { this.buyer = v; }

    public List<OrderItem> getOrderItems(){ return orderItems; }
    public void setOrderItems(List<OrderItem> v){ this.orderItems = v; }

    // Meme methode qu'AgriConnect
    public boolean canCancel() {
        return status == OrderStatus.EN_ATTENTE_PAIEMENT || status == OrderStatus.PAYEE;
    }

    @Override
    public String toString() {
        return "Commande #" + id + " [" + status + "] - " + totalAmount + " EUR";
    }
}
