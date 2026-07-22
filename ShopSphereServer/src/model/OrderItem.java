/*
 * ShopSphere - Ligne de commande
 * Inspire de AgriConnect model/OrderItem.java — structure identique
 */
package model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Une ligne de commande (produit + quantite + prix).
 * Meme structure qu'AgriConnect model/OrderItem.java
 * @author ShopSphere
 */
@Entity
@Table(name = "order_items")
public class OrderItem implements Serializable {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "quantity", nullable = false) // meme champ qu'AgriConnect
    private double quantity;

    @Column(name = "unit_price", nullable = false) // meme champ qu'AgriConnect
    private double unitPrice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false) // meme association
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false) // meme association
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id") // nouveau : vendeur de l'article
    private User seller;

    // Constructeurs
    public OrderItem() {}

    public OrderItem(Order order, Product product, double quantity, double unitPrice) {
        this.order    = order;
        this.product  = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Getters & Setters — meme convention qu'AgriConnect
    public int getId()               { return id; }
    public void setId(int id)        { this.id = id; }

    public double getQuantity()      { return quantity; }
    public void setQuantity(double v){ this.quantity = v; }

    public double getUnitPrice()     { return unitPrice; }
    public void setUnitPrice(double v){ this.unitPrice = v; }

    public Order getOrder()          { return order; }
    public void setOrder(Order v)    { this.order = v; }

    public Product getProduct()      { return product; }
    public void setProduct(Product v){ this.product = v; }

    public User getSeller()          { return seller; }
    public void setSeller(User v)    { this.seller = v; }

    // Meme methode qu'AgriConnect
    public double getSubtotal()      { return quantity * unitPrice; }
}
