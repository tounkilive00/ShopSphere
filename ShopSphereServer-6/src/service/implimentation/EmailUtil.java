/*
 * ShopSphere - EmailUtil
 * Envoi reel de codes OTP par email via JavaMail (SMTP Gmail / TLS).
 *
 * Configuration : modifier les constantes SMTP_USER et SMTP_PASS
 * ou passer par des variables d'environnement SMTP_USER / SMTP_PASS.
 */
package service.implimentation;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Utilitaire d'envoi d'emails transactionnels (OTP, notifications).
 * Utilise Gmail SMTP avec STARTTLS.
 *
 * Pour utiliser un autre fournisseur, modifiez SMTP_HOST et SMTP_PORT.
 */
public class EmailUtil {

    private static final Logger LOG = Logger.getLogger(EmailUtil.class.getName());

    // ── Configuration SMTP ───────────────────────────────────────────────
    // Remplacez par votre adresse Gmail et un Mot de passe d'application Google
    // (Compte Google → Sécurité → Mots de passe des applications)
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int    SMTP_PORT = 587;   // TLS
    private static final String SMTP_USER = System.getenv("SMTP_USER") != null
            ? System.getenv("SMTP_USER") : "votre.email@gmail.com"; // ← A CHANGER
    private static final String SMTP_PASS = System.getenv("SMTP_PASS") != null
            ? System.getenv("SMTP_PASS") : "votre_mot_de_passe_app";  // ← A CHANGER

    /** Envoie un email simple (texte brut). Bloquant — appeler depuis un thread séparé. */
    public static void envoyerEmail(String destinataire, String sujet, String corps) {
        if (SMTP_USER.contains("votre.email") || SMTP_PASS.contains("votre_mot_de_passe")) {
            LOG.warning("[EMAIL simulé — SMTP non configuré] Destinataire: "
                    + destinataire + " | Sujet: " + sujet);
            LOG.warning("Configurez SMTP_USER et SMTP_PASS (variables d'environnement ou constantes dans EmailUtil).");
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host",             SMTP_HOST);
        props.put("mail.smtp.port",             SMTP_PORT);
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout",           "5000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USER, SMTP_PASS);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USER, "ShopSphere"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            message.setSubject(sujet);
            message.setText(corps);
            Transport.send(message);
            LOG.info("[EMAIL envoyé] Vers: " + destinataire + " | Sujet: " + sujet);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "[EMAIL ERREUR] Impossible d'envoyer à " + destinataire + " : " + e.getMessage(), e);
        }
    }

    /** Envoie un email OTP formaté. */
    public static void envoyerOtp(String destinataire, String code, String langue) {
        String sujet, corps;
        if ("fr".equals(langue)) {
            sujet = "Votre code de vérification ShopSphere";
            corps = "Bonjour,\n\n"
                  + "Votre code de vérification ShopSphere est :\n\n"
                  + "    " + code + "\n\n"
                  + "Ce code est valable pendant 10 minutes.\n"
                  + "Ne le partagez avec personne.\n\n"
                  + "Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.\n\n"
                  + "— L'équipe ShopSphere";
        } else {
            sujet = "Your ShopSphere verification code";
            corps = "Hello,\n\n"
                  + "Your ShopSphere verification code is:\n\n"
                  + "    " + code + "\n\n"
                  + "This code is valid for 10 minutes.\n"
                  + "Do not share it with anyone.\n\n"
                  + "If you did not request this, please ignore this email.\n\n"
                  + "— The ShopSphere Team";
        }
        envoyerEmail(destinataire, sujet, corps);
    }
}
