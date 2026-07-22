/*
 * ShopSphere Client - Category (stub serialisable)
 * Copie exacte de la classe serveur pour la serialisation RMI.
 */
package model;

import java.io.Serializable;
import java.util.List;

public class Category implements Serializable {
    public static final long serialVersionUID = 1L;

    private int id;
    private String nom;
    private String slug;
    private String iconUrl;
    private Category parent;
    private List<Category> sousCategories;

    public Category() {}
    public Category(String nom, String slug) {
        this.nom = nom;
        this.slug = slug;
    }

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

    public List<Category> getSousCategories()       { return sousCategories; }
    public void setSousCategories(List<Category> v) { this.sousCategories = v; }

    @Override
    public String toString() { return nom; }
}
