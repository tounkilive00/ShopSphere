/*
 * ShopSphere - Produit du catalogue
 * Inspire de AgriConnect model/Product.java
 * farmer -> seller, double -> double (garde pour compat), statuts etendus
 */
package model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

/**
 * Produit liste par un SELLER sur la plateforme.
 * Etait product agricole d'un FARMER dans AgriConnect.
 * @author ShopSphere
 */
@Entity
@Table(name = "products")
public class Product implements Serializable {

    public static final long serialVersionUID = 1L;

    // Categories etendues depuis AgriConnect (VEGETABLES, FRUITS, GRAINS, DAIRY, LIVESTOCK, OTHER)
    // Uses fully-qualified model.Category for the JPA entity FK to avoid conflict
    public enum Category {
        ELECTRONIQUE, MODE, MAISON, SPORT, BEAUTE, ALIMENTATION,
        LIVRES, JOUETS, AUTOMOBILES, SANTE, AUTRES
    }

    // Statuts — etait { AVAILABLE, OUT_OF_STOCK, REMOVED } dans AgriConnect
    public enum ProductStatus { BROUILLON, ACTIF, RUPTURE_STOCK, ARCHIVE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title", nullable = false)   // etait "name"
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "brand")
    private String brand;

    @Column(name = "sku", unique = true)
    private String sku;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @Column(name = "base_price", nullable = false)  // etait price_per_unit
    private double basePrice;

    @Column(name = "sale_price")
    private double salePrice = 0;

    @Column(name = "stock_qty", nullable = false)   // etait quantity
    private int stockQty = 0;

    @Column(name = "unit")                          // garde comme AgriConnect
    private String unit = "piece";

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductStatus status = ProductStatus.BROUILLON;

    // etait farmer dans AgriConnect
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private model.Category categoryRef;  // FK vers table categories (optionnel)

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    // Constructeurs
    public Product() {}

    public Product(String title, Category category, int stockQty,
                   double basePrice, String description, User seller) {
        this.title       = title;
        this.category    = category;
        this.stockQty    = stockQty;
        this.basePrice   = basePrice;
        this.description = description;
        this.seller      = seller;
        this.status      = ProductStatus.ACTIF;
    }

    // Getters & Setters — meme convention qu'AgriConnect
    public int getId()              { return id; }
    public void setId(int id)       { this.id = id; }

    public String getTitle()        { return title; }
    public void setTitle(String v)  { this.title = v; }

    // Compat alias pour le client (getName() comme AgriConnect)
    public String getName()         { return title; }
    public void setName(String v)   { this.title = v; }

    public String getDescription()  { return description; }
    public void setDescription(String v){ this.description = v; }

    public String getBrand()        { return brand; }
    public void setBrand(String v)  { this.brand = v; }

    public String getSku()          { return sku; }
    public void setSku(String v)    { this.sku = v; }

    public Category getCategory()   { return category; }
    public void setCategory(Category v){ this.category = v; }

    // FK optionnelle vers l'entite Category (hierarchie parent/enfant) — etait mappee
    // sans accesseurs, donc totalement inaccessible depuis le reste de l'application.
    public model.Category getCategoryRef()      { return categoryRef; }
    public void setCategoryRef(model.Category v){ this.categoryRef = v; }

    public double getBasePrice()    { return basePrice; }
    public void setBasePrice(double v){ this.basePrice = v; }

    // Alias pour compat AgriConnect (getPricePerUnit)
    public double getPricePerUnit() { return (salePrice > 0 && salePrice < basePrice) ? salePrice : basePrice; }
    public void setPricePerUnit(double v){ this.basePrice = v; }

    public double getSalePrice()    { return salePrice; }
    public void setSalePrice(double v){ this.salePrice = v; }

    public int getStockQty()        { return stockQty; }
    public void setStockQty(int v)  { this.stockQty = v; }

    // Alias pour compat AgriConnect (getQuantity/setQuantity)
    public double getQuantity()     { return stockQty; }
    public void setQuantity(double v){ this.stockQty = (int) v; }

    public String getUnit()         { return unit; }
    public void setUnit(String v)   { this.unit = v; }

    public String getImageUrl()     { return imageUrl; }
    public void setImageUrl(String v){ this.imageUrl = v; }

    public ProductStatus getStatus(){ return status; }
    public void setStatus(ProductStatus v){ this.status = v; }

    public User getSeller()         { return seller; }
    public void setSeller(User v)   { this.seller = v; }

    // Alias pour compat AgriConnect (getFarmer/setFarmer)
    public User getFarmer()         { return seller; }
    public void setFarmer(User v)   { this.seller = v; }

    public List<OrderItem> getOrderItems(){ return orderItems; }
    public void setOrderItems(List<OrderItem> v){ this.orderItems = v; }

    public boolean isInStock()      { return stockQty > 0; }
    public boolean isOnSale()       { return salePrice > 0 && salePrice < basePrice; }

    @Override
    public String toString() {
        return title + " (" + category + ") - " + getPricePerUnit() + " EUR";
    }
}
