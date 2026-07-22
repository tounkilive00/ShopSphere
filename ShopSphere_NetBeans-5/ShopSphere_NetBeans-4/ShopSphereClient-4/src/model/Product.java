/*
 * ShopSphere Client - Product (stub serialisable)
 * Copie exacte de la classe serveur pour la serialisation RMI.
 */
package model;

import java.io.Serializable;

public class Product implements Serializable {
    public static final long serialVersionUID = 1L;
    public enum Category {
        ELECTRONIQUE, MODE, MAISON, SPORT, BEAUTE, ALIMENTATION,
        LIVRES, JOUETS, AUTOMOBILES, SANTE, AUTRES
    }
    public enum ProductStatus { BROUILLON, ACTIF, RUPTURE_STOCK, ARCHIVE }

    private int    id;
    private String title;
    private String description;
    private String brand;
    private String sku;
    private Category category;
    private double basePrice;
    private double salePrice = 0;
    private int    stockQty = 0;
    private String unit = "piece";
    private String imageUrl;
    private ProductStatus status = ProductStatus.BROUILLON;
    private User   seller;
    private model.Category categoryRef; // FK optionnelle vers l'entite Category (hierarchie)

    public Product() {}
    public Product(String title, Category category, int stockQty, double basePrice, String description, User seller) {
        this.title = title; this.category = category; this.stockQty = stockQty;
        this.basePrice = basePrice; this.description = description; this.seller = seller;
        this.status = ProductStatus.ACTIF;
    }

    public int    getId()              { return id; }
    public void   setId(int v)         { this.id = v; }
    public String getTitle()           { return title; }
    public void   setTitle(String v)   { this.title = v; }
    public String getName()            { return title; }
    public void   setName(String v)    { this.title = v; }
    public String getDescription()     { return description; }
    public void   setDescription(String v){ this.description = v; }
    public String getBrand()           { return brand; }
    public void   setBrand(String v)   { this.brand = v; }
    public String getSku()             { return sku; }
    public void   setSku(String v)     { this.sku = v; }
    public Category getCategory()      { return category; }
    public void   setCategory(Category v){ this.category = v; }
    public model.Category getCategoryRef()      { return categoryRef; }
    public void   setCategoryRef(model.Category v){ this.categoryRef = v; }
    public double getBasePrice()       { return basePrice; }
    public void   setBasePrice(double v){ this.basePrice = v; }
    public double getPricePerUnit()    { return (salePrice > 0 && salePrice < basePrice) ? salePrice : basePrice; }
    public void   setPricePerUnit(double v){ this.basePrice = v; }
    public double getSalePrice()       { return salePrice; }
    public void   setSalePrice(double v){ this.salePrice = v; }
    public int    getStockQty()        { return stockQty; }
    public void   setStockQty(int v)   { this.stockQty = v; }
    public double getQuantity()        { return stockQty; }
    public void   setQuantity(double v){ this.stockQty = (int) v; }
    public String getUnit()            { return unit; }
    public void   setUnit(String v)    { this.unit = v; }
    public String getImageUrl()        { return imageUrl; }
    public void   setImageUrl(String v){ this.imageUrl = v; }
    public ProductStatus getStatus()   { return status; }
    public void   setStatus(ProductStatus v){ this.status = v; }
    public User   getSeller()          { return seller; }
    public void   setSeller(User v)    { this.seller = v; }
    public User   getFarmer()          { return seller; }
    public void   setFarmer(User v)    { this.seller = v; }
    public boolean isInStock()         { return stockQty > 0; }
    public boolean isOnSale()          { return salePrice > 0 && salePrice < basePrice; }
    @Override public String toString() { return title + " - " + String.format("%.2f", getPricePerUnit()) + " EUR"; }
}
