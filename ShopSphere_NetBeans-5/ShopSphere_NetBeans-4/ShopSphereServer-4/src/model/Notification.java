/*
 * ShopSphere - Notification
 * Inspire de AgriConnect model/Notification.java — structure identique
 * Ajout : title, deepLinkUrl
 */
package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.*;

/**
 * Notification systeme envoyee a un utilisateur.
 * Meme structure de base qu'AgriConnect model/Notification.java
 * @author ShopSphere
 */
@Entity
@Table(name = "notifications")
public class Notification implements Serializable {

    public static final long serialVersionUID = 1L;

    // Types de notification (etait String type dans AgriConnect)
    public enum TypeNotif {
        MISE_A_JOUR_COMMANDE, PROMO, MESSAGE, SECURITE, ANNONCE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title")
    private String title;   // nouveau par rapport a AgriConnect

    @Column(name = "message", nullable = false) // meme champ qu'AgriConnect
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TypeNotif type; // etait String type dans AgriConnect

    @Column(name = "timestamp", nullable = false) // meme champ qu'AgriConnect
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "is_read") // meme champ qu'AgriConnect
    private boolean isRead = false;

    @Column(name = "deep_link_url")
    private String deepLinkUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false) // meme association qu'AgriConnect
    private User user;

    // Constructeurs
    public Notification() {}

    public Notification(User user, String title, String message, TypeNotif type) {
        this.user      = user;
        this.title     = title;
        this.message   = message;
        this.type      = type;
        this.timestamp = LocalDateTime.now();
    }

    // Getters & Setters — meme convention qu'AgriConnect
    public int getId()                 { return id; }
    public void setId(int v)           { this.id = v; }

    public String getTitle()           { return title; }
    public void setTitle(String v)     { this.title = v; }

    public String getMessage()         { return message; }
    public void setMessage(String v)   { this.message = v; }

    public TypeNotif getType()         { return type; }
    public void setType(TypeNotif v)   { this.type = v; }

    public LocalDateTime getTimestamp(){ return timestamp; }
    public void setTimestamp(LocalDateTime v){ this.timestamp = v; }

    public boolean isRead()            { return isRead; }
    public void setRead(boolean v)     { this.isRead = v; }

    public String getDeepLinkUrl()     { return deepLinkUrl; }
    public void setDeepLinkUrl(String v){ this.deepLinkUrl = v; }

    public User getUser()              { return user; }
    public void setUser(User v)        { this.user = v; }
}
