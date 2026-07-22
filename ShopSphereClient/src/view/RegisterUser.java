/*
 * ShopSphere - RegisterUser
 * Meme structure qu'AgriConnect view/RegisterUser.java (JFrame inscription)
 * Envoi OTP email apres creation du compte
 */
package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.User;
import model.User.Role;
import service0.UserService;
import view.components.*;
import view.theme.Theme;

public class RegisterUser extends JFrame {

    private AppTextField     nomField;
    private AppTextField     emailField;
    private AppPasswordField passwordField;
    private AppPasswordField confirmField;
    private AppTextField     phoneField;
    private AppComboBox<String> roleBox;
    private PrimaryButton    registerBtn;
    private JLabel           statusLabel;

    public RegisterUser() {
        initComponents();
        setTitle("ShopSphere — Creer un compte");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(440, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Theme.NEUTRAL);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(Theme.PRIMARY);
        header.setPreferredSize(new Dimension(0, 90));
        JLabel logoLbl = new JLabel("ShopSphere — Inscription");
        logoLbl.setFont(new Font("Arial", Font.BOLD, 20));
        logoLbl.setForeground(Theme.WHITE);
        header.add(logoLbl);
        add(header, BorderLayout.NORTH);

        // Formulaire
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Theme.NEUTRAL);
        form.setBorder(new EmptyBorder(24, 40, 20, 40));

        // Nom complet
        form.add(label("Nom complet"));
        nomField = new AppTextField("Jean Dupont");
        nomField.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        form.add(nomField);
        form.add(Box.createVerticalStrut(10));

        // Email
        form.add(label("Adresse email"));
        emailField = new AppTextField("votre@email.com");
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        form.add(emailField);
        form.add(Box.createVerticalStrut(10));

        // Telephone
        form.add(label("Telephone (pour OTP SMS)"));
        phoneField = new AppTextField("+33 6 12 34 56 78");
        phoneField.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        form.add(phoneField);
        form.add(Box.createVerticalStrut(10));

        // Mot de passe
        form.add(label("Mot de passe (8+ car., majuscule, chiffre)"));
        passwordField = new AppPasswordField("Votre mot de passe");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        form.add(passwordField);
        form.add(Box.createVerticalStrut(10));

        // Confirmation
        form.add(label("Confirmer le mot de passe"));
        confirmField = new AppPasswordField("Confirmer le mot de passe");
        confirmField.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        form.add(confirmField);
        form.add(Box.createVerticalStrut(10));

        // Role
        form.add(label("Vous etes :"));
        roleBox = new AppComboBox<>(new String[]{"Client", "Vendeur"});
        roleBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        form.add(roleBox);
        form.add(Box.createVerticalStrut(16));

        // Statut
        statusLabel = new JLabel("");
        statusLabel.setFont(Theme.FONT_BODY);
        statusLabel.setForeground(Theme.ERROR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setVisible(false);
        form.add(statusLabel);
        form.add(Box.createVerticalStrut(6));

        // Bouton
        registerBtn = new PrimaryButton("Creer mon compte");
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.BTN_H));
        registerBtn.addActionListener(e -> performRegister());
        form.add(registerBtn);
        form.add(Box.createVerticalStrut(10));

        // Retour connexion
        SecondaryButton backBtn = new SecondaryButton("Deja un compte ? Se connecter");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.BTN_H));
        backBtn.addActionListener(e -> { dispose(); new UserLogin(); });
        form.add(backBtn);

        add(new JScrollPane(form), BorderLayout.CENTER);
        setVisible(true);
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_HEADING);
        l.setForeground(Theme.DARK_TEXT);
        return l;
    }

    private void performRegister() {
        String nom     = nomField.getText().trim();
        String email   = emailField.getText().trim();
        String phone   = phoneField.getText().trim();
        String pass    = passwordField.getText();
        String confirm = confirmField.getText();
        String roleStr = (String) roleBox.getSelectedItem();

        if (nom.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            showError("Veuillez remplir tous les champs obligatoires."); return;
        }
        if (!pass.equals(confirm)) {
            showError("Les mots de passe ne correspondent pas."); return;
        }
        if (pass.length() < 8) {
            showError("Le mot de passe doit avoir au moins 8 caracteres."); return;
        }

        registerBtn.setEnabled(false);
        registerBtn.setText("Creation...");

        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override protected User doInBackground() throws Exception {
                UserService us = RMIClient.getUserService();
                User newUser = new User();
                newUser.setFullName(nom);
                newUser.setEmail(email);
                newUser.setPhone(phone.isEmpty() ? null : phone);
                newUser.setPasswordHash(pass); // hash fait cote serveur par BCryptUtil
                newUser.setRole("Vendeur".equals(roleStr) ? Role.SELLER : Role.CLIENT);
                newUser.setPreferredLanguage("fr");
                return us.createUserRecord(newUser);
            }
            @Override protected void done() {
                registerBtn.setEnabled(true);
                registerBtn.setText("Creer mon compte");
                try {
                    User created = get();
                    if (created != null) {
                        // Envoyer OTP de verification email
                        try {
                            RMIClient.getUserService().envoyerOtp(
                                created.getId(), "EMAIL", "INSCRIPTION");
                        } catch (Exception ignored) {}
                        JOptionPane.showMessageDialog(RegisterUser.this,
                            "Compte cree ! Un code de verification a ete envoye a :\n" + email,
                            "Succes", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        new OtpVerification(created.getId(), "INSCRIPTION", email).setVisible(true);
                    }
                } catch (Exception ex) {
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    showError(msg != null ? msg : "Erreur lors de la creation du compte.");
                }
            }
        };
        worker.execute();
    }

    private void showError(String msg) {
        statusLabel.setText("<html><center>" + msg + "</center></html>");
        statusLabel.setVisible(true);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
