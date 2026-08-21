/*
 * ShopSphere - Plateforme E-Commerce Multi-Roles
 * Inspire de AgriConnect model/User.java
 * Ameliorations : roles CLIENT/SELLER/ADMIN, OTP dans entite separee,
 *                 langue preferee (fr par defaut)
 */
package model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

/**
 * Entite utilisateur — ADMIN, SELLER ou CLIENT.
 * Etait FARMER/BUYER/ADMIN dans AgriConnect.
 * @author ShopSphere
 */
@Entity
@Table(name = "users")
public class User implements Serializable {

    public static final long serialVersionUID = 1L;

    // Roles — etait { FARMER, BUYER, ADMIN } dans AgriConnect
    public enum Role { ADMIN, SELLER, CLIENT }

    // Statut du compte — etait boolean isActive dans AgriConnect
    public enum AccountStatus { ACTIVE, SUSPENDED, PENDING_VERIFICATION, DELETED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    // Hash BCrypt — etait mot de passe en clair dans AgriConnect
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status = AccountStatus.PENDING_VERIFICATION;

    @Column(name = "email_verified")
    private boolean emailVerified = false;

    @Column(name = "phone_verified")
    private boolean phoneVerified = false;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    // Langue preferee — fr par defaut (langue principale de ShopSphere)
    // AgriConnect n'avait pas de gestion de langue
    @Column(name = "preferred_language", length = 5)
    private String preferredLanguage = "fr";

    // NOTE : OTP n'est plus stocke ici en clair comme dans AgriConnect
    //        (etait : private String otpCode;)
    //        Il est maintenant dans l'entite OtpRecord (hash BCrypt)

    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY)
    private List<Product> products; // etait farmer dans AgriConnect

    @OneToMany(mappedBy = "buyer", fetch = FetchType.LAZY)
    private List<Order> orders;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Notification> notifications;

    // Constructeurs
    public User() {}

    public User(String fullName, String email, String phone,
                String passwordHash, Role role) {
        this.fullName     = fullName;
        this.email        = email;
        this.phone        = phone;
        this.passwordHash = passwordHash;
        this.role         = role;
        this.status       = AccountStatus.ACTIVE;
    }

    // Getters & Setters
    public int getId()                   { return id; }
    public void setId(int id)            { this.id = id; }

    public String getFullName()          { return fullName; }
    public void setFullName(String v)    { this.fullName = v; }

    public String getEmail()             { return email; }
    public void setEmail(String v)       { this.email = v; }

    public String getPhone()             { return phone; }
    public void setPhone(String v)       { this.phone = v; }

    public String getPasswordHash()      { return passwordHash; }
    public void setPasswordHash(String v){ this.passwordHash = v; }

    public Role getRole()                { return role; }
    public void setRole(Role v)          { this.role = v; }

    public AccountStatus getStatus()     { return status; }
    public void setStatus(AccountStatus v){ this.status = v; }

    public boolean isEmailVerified()     { return emailVerified; }
    public void setEmailVerified(boolean v){ this.emailVerified = v; }

    public boolean isPhoneVerified()     { return phoneVerified; }
    public void setPhoneVerified(boolean v){ this.phoneVerified = v; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String v){ this.profilePictureUrl = v; }

    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String v){ this.preferredLanguage = v; }

    public List<Product> getProducts()   { return products; }
    public void setProducts(List<Product> v){ this.products = v; }

    public List<Order> getOrders()       { return orders; }
    public void setOrders(List<Order> v) { this.orders = v; }

    public List<Notification> getNotifications(){ return notifications; }
    public void setNotifications(List<Notification> v){ this.notifications = v; }

    // Helpers
    public boolean isAdmin()  { return role == Role.ADMIN; }
    public boolean isSeller() { return role == Role.SELLER || role == Role.ADMIN; }
    public boolean isActive() { return status == AccountStatus.ACTIVE; }

    @Override
    public String toString() { return fullName + " [" + role + "]"; }
}
