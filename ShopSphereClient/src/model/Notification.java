package model;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Notification implements Serializable {
    public static final long serialVersionUID = 1L;
    public enum TypeNotif { MISE_A_JOUR_COMMANDE, PROMO, MESSAGE, SECURITE, ANNONCE }
    private int           id;
    private String        title;
    private String        message;
    private TypeNotif     type;
    private LocalDateTime timestamp = LocalDateTime.now();
    private boolean       isRead = false;
    private User          user;

    public Notification() {}
    public Notification(User user, String title, String message, TypeNotif type) {
        this.user = user; this.title = title; this.message = message; this.type = type;
    }
    public int           getId()              { return id; }
    public void          setId(int v)         { this.id = v; }
    public String        getTitle()           { return title; }
    public void          setTitle(String v)   { this.title = v; }
    public String        getMessage()         { return message; }
    public void          setMessage(String v) { this.message = v; }
    public TypeNotif     getType()            { return type; }
    public void          setType(TypeNotif v) { this.type = v; }
    public LocalDateTime getTimestamp()       { return timestamp; }
    public boolean       isRead()             { return isRead; }
    public void          setRead(boolean v)   { this.isRead = v; }
    public User          getUser()            { return user; }
    public void          setUser(User v)      { this.user = v; }
}
