/*
 * ShopSphere - Categorie de produit
 * Nouveau par rapport a AgriConnect (qui utilisait un enum Product.Category)
 */
package model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

/**
 * Categorie de produit — remplace l'enum Product.Category d'AgriConnect
 * par une entite complete avec hierarchie parent/enfant.
 * @author ShopSphere
 */
@Entity
@Table(name = "categories")
public class Category implements Serializable {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "icon_url")
    private String iconUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Category> sousCategories;

    // Constructeurs
    public Category() {}

    public Category(String nom, String slug) {
        this.nom  = nom;
        this.slug = slug;
    }

    // Getters & Setters
    public int getId()                   { return id; }
    public void setId(int v)             { this.id = v; }

    public String getNom()               { return nom; }
    public void setNom(String v)         { this.nom = v; }

    public String getSlug()              { return slug; }
    public void setSlug(String v)        { this.slug = v; }

    public String getIconUrl()           { return iconUrl; }
    public void setIconUrl(String v)     { this.iconUrl = v; }

    public Category getParent()          { return parent; }
    public void setParent(Category v)    { this.parent = v; }

    public List<Category> getSousCategories(){ return sousCategories; }
    public void setSousCategories(List<Category> v){ this.sousCategories = v; }

    @Override
    public String toString()             { return nom; }
}
