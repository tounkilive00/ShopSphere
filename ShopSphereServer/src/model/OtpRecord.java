/*
 * ShopSphere - Enregistrement OTP securise
 * REMPLACE AgriConnect : private String otpCode (stockage en clair NON SECURISE)
 * Ameliorations : hash BCrypt, expiration, canal (EMAIL/SMS), raison, max 3 tentatives
 */
package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.*;

/**
 * Remplace le champ user.otpCode de AgriConnect.
 * Le code OTP n'est JAMAIS stocke en clair — uniquement son hash.
 * @author ShopSphere
 */
@Entity
@Table(name = "otp_records")
public class OtpRecord implements Serializable {

    public static final long serialVersionUID = 1L;

    public enum Canal { EMAIL, SMS }

    public enum Raison {
        INSCRIPTION, CONNEXION_2FA, REINIT_MDP, VERIF_TELEPHONE, ACTION_SENSIBLE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "canal", nullable = false)
    private Canal canal;

    @Enumerated(EnumType.STRING)
    @Column(name = "raison", nullable = false)
    private Raison raison;

    // HASH BCrypt uniquement — jamais le code en clair
    @Column(name = "code_hash", nullable = false)
    private String codeHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "attempts")
    private int attempts = 0;

    @Column(name = "utilise")
    private boolean utilise = false;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructeurs
    public OtpRecord() {}

    public OtpRecord(User user, String codeHash, Canal canal, Raison raison, int minutesValide) {
        this.user      = user;
        this.codeHash  = codeHash;
        this.canal     = canal;
        this.raison    = raison;
        this.expiresAt = LocalDateTime.now().plusMinutes(minutesValide);
    }

    // Getters & Setters
    public int getId()                 { return id; }
    public void setId(int v)           { this.id = v; }

    public User getUser()              { return user; }
    public void setUser(User v)        { this.user = v; }

    public Canal getCanal()            { return canal; }
    public void setCanal(Canal v)      { this.canal = v; }

    public Raison getRaison()          { return raison; }
    public void setRaison(Raison v)    { this.raison = v; }

    public String getCodeHash()        { return codeHash; }
    public void setCodeHash(String v)  { this.codeHash = v; }

    public LocalDateTime getExpiresAt(){ return expiresAt; }
    public void setExpiresAt(LocalDateTime v){ this.expiresAt = v; }

    public int getAttempts()           { return attempts; }
    public void setAttempts(int v)     { this.attempts = v; }

    public boolean isUtilise()         { return utilise; }
    public void setUtilise(boolean v)  { this.utilise = v; }

    public String getIpAddress()       { return ipAddress; }
    public void setIpAddress(String v) { this.ipAddress = v; }

    public LocalDateTime getCreatedAt(){ return createdAt; }

    // Helpers
    public boolean isExpire()           { return LocalDateTime.now().isAfter(expiresAt); }
    public boolean isTropDeTentatives() { return attempts >= 3; }
}
