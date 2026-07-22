/*
 * ShopSphere Client - User (stub serialisable)
 * Copie exacte de la classe serveur pour la serialisation RMI.
 * Meme structure qu'AgriConnect client/model/User.java
 */
package model;

import java.io.Serializable;

public class User implements Serializable {
    public static final long serialVersionUID = 1L;
    public enum Role         { ADMIN, SELLER, CLIENT }
    public enum AccountStatus{ ACTIVE, SUSPENDED, PENDING_VERIFICATION, DELETED }

    private int    id;
    private String fullName;
    private String email;
    private String phone;
    private String passwordHash;
    private Role   role;
    private AccountStatus status = AccountStatus.PENDING_VERIFICATION;
    private boolean emailVerified = false;
    private boolean phoneVerified = false;
    private String  profilePictureUrl;
    private String  preferredLanguage = "fr";

    public User() {}
    public User(String fullName, String email, String passwordHash, Role role) {
        this.fullName = fullName; this.email = email;
        this.passwordHash = passwordHash; this.role = role;
        this.status = AccountStatus.ACTIVE;
    }

    public int    getId()                    { return id; }
    public void   setId(int v)               { this.id = v; }
    public String getFullName()              { return fullName; }
    public void   setFullName(String v)      { this.fullName = v; }
    public String getEmail()                 { return email; }
    public void   setEmail(String v)         { this.email = v; }
    public String getPhone()                 { return phone; }
    public void   setPhone(String v)         { this.phone = v; }
    public String getPasswordHash()          { return passwordHash; }
    public void   setPasswordHash(String v)  { this.passwordHash = v; }
    public Role   getRole()                  { return role; }
    public void   setRole(Role v)            { this.role = v; }
    public AccountStatus getStatus()         { return status; }
    public void   setStatus(AccountStatus v) { this.status = v; }
    public boolean isEmailVerified()         { return emailVerified; }
    public void   setEmailVerified(boolean v){ this.emailVerified = v; }
    public boolean isPhoneVerified()         { return phoneVerified; }
    public void   setPhoneVerified(boolean v){ this.phoneVerified = v; }
    public String getProfilePictureUrl()     { return profilePictureUrl; }
    public void   setProfilePictureUrl(String v){ this.profilePictureUrl = v; }
    public String getPreferredLanguage()     { return preferredLanguage; }
    public void   setPreferredLanguage(String v){ this.preferredLanguage = v; }
    public boolean isAdmin()                 { return role == Role.ADMIN; }
    public boolean isSeller()                { return role == Role.SELLER || role == Role.ADMIN; }
    public boolean isActive()               { return status == AccountStatus.ACTIVE; }
    @Override public String toString()       { return fullName + " [" + role + "]"; }
}
