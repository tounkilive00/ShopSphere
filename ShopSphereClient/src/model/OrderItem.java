package model;
import java.io.Serializable;

public class OrderItem implements Serializable {
    public static final long serialVersionUID = 1L;
    private int    id;
    private double quantity;
    private double unitPrice;
    private Order  order;
    private Product product;
    private User   seller;

    public OrderItem() {}
    public OrderItem(Product product, double quantity, double unitPrice) {
        this.product = product; this.quantity = quantity; this.unitPrice = unitPrice;
    }
    public int    getId()               { return id; }
    public void   setId(int v)          { this.id = v; }
    public double getQuantity()         { return quantity; }
    public void   setQuantity(double v) { this.quantity = v; }
    public double getUnitPrice()        { return unitPrice; }
    public void   setUnitPrice(double v){ this.unitPrice = v; }
    public Order  getOrder()            { return order; }
    public void   setOrder(Order v)     { this.order = v; }
    public Product getProduct()         { return product; }
    public void   setProduct(Product v) { this.product = v; }
    public User   getSeller()           { return seller; }
    public void   setSeller(User v)     { this.seller = v; }
    public double getSubtotal()         { return quantity * unitPrice; }
}
