/*
 * ShopSphere - CartView
 * Fenetre du panier — liste des articles, total, et bouton passer commande.
 * Design amélioré : état vide illustré, contrôles quantité, Toast de confirmation.
 */
package view;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.OrderItem;
import model.User;
import service0.OrderService;
import view.components.*;
import view.theme.Theme;

public class CartView extends JFrame {

    private OrderService orderService;

    private final User currentUser;
    private JPanel itemsPanel;
    private JLabel totalLabel;


    public CartView(User user) {
        this.currentUser = user;
        initComponents();
        connectToServer();
        buildUI();
    }

    private void connectToServer() {
        try {
            Registry reg = LocateRegistry.getRegistry("127.0.0.1", 4999);
            this.orderService = (OrderService) reg.lookup("OrderService");
        } catch (Exception e) {
            System.err.println("Server connection failed: " + e.getMessage());
        }
    }

    private void buildUI() {
        setTitle("ShopSphere — Mon panier");
        setSize(640, 560);
        setMinimumSize(new Dimension(560, 480));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.NEUTRAL);
        setLayout(new BorderLayout());

        // ── Header ──────────────────────────────────────────────────────────
        PageHeader header = new PageHeader("Mon panier",
            Session.getCartCount() == 0 ? "Votre panier est vide"
                : Session.getCartCount() + " article(s) dans votre panier");
        SecondaryButton backBtn = new SecondaryButton("← Catalogue");
        backBtn.addActionListener(e -> AppNavigator.show(new MarketPlace(currentUser)));
        header.addAction(backBtn);
        add(header, BorderLayout.NORTH);

        // ── Liste articles ──────────────────────────────────────────────────
        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Theme.NEUTRAL);
        itemsPanel.setBorder(new EmptyBorder(12, 20, 12, 20));
        refreshItems();

        JScrollPane scroll = new JScrollPane(itemsPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(Theme.NEUTRAL);
        scroll.getViewport().setBackground(Theme.NEUTRAL);
        add(scroll, BorderLayout.CENTER);

        // ── Pied de page — total + actions ─────────────────────────────────
        JPanel footer = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Theme.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Ligne de séparation en haut
                g2.setColor(Theme.LIGHT_GREY);
                g2.drawLine(0, 0, getWidth(), 0);
                g2.dispose();
            }
        };
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(14, 20, 14, 20));

        // Total
        JPanel totalPanel = new JPanel();
        totalPanel.setOpaque(false);
        totalPanel.setLayout(new BoxLayout(totalPanel, BoxLayout.Y_AXIS));
        JLabel totalTitle = new JLabel("Total à payer");
        totalTitle.setFont(Theme.FONT_SMALL);
        totalTitle.setForeground(Theme.GREY_TEXT);
        totalLabel = new JLabel(String.format("%.0f FCFA", Session.getCartTotal()));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalLabel.setForeground(Theme.PRIMARY);
        totalPanel.add(totalTitle);
        totalPanel.add(totalLabel);
        footer.add(totalPanel, BorderLayout.WEST);

        // Boutons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        SecondaryButton clearBtn = new SecondaryButton("🗑  Vider le panier");
        clearBtn.addActionListener(e -> {
            Session.clearCart();
            refreshItems();
            updateTotal();
            Toast.info(CartView.this, "Panier vidé.");
        });
        AccentButton orderBtn = new AccentButton("Passer la commande →", 210, Theme.BTN_H);
        orderBtn.addActionListener(e -> passCommande());
        btnPanel.add(clearBtn);
        btnPanel.add(orderBtn);
        footer.add(btnPanel, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void refreshItems() {
        itemsPanel.removeAll();
        List<OrderItem> cart = Session.getCart();

        if (cart.isEmpty()) {
            // ── État vide illustré ──────────────────────────────────────────
            JPanel emptyState = new JPanel(new GridBagLayout());
            emptyState.setOpaque(false);
            JPanel inner = new JPanel();
            inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
            inner.setOpaque(false);

            JLabel emojiLbl = new JLabel("🛒");
            emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
            emojiLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel titleLbl = new JLabel("Votre panier est vide");
            titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titleLbl.setForeground(Theme.DARK_TEXT);
            titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel subLbl = new JLabel("Explorez le catalogue et ajoutez vos coups de cœur !");
            subLbl.setFont(Theme.FONT_BODY);
            subLbl.setForeground(Theme.GREY_TEXT);
            subLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

            PrimaryButton goShopBtn = new PrimaryButton("Découvrir les produits", 220, Theme.BTN_H);
            goShopBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            goShopBtn.addActionListener(e -> AppNavigator.show(new MarketPlace(currentUser)));

            inner.add(emojiLbl);
            inner.add(Box.createVerticalStrut(16));
            inner.add(titleLbl);
            inner.add(Box.createVerticalStrut(6));
            inner.add(subLbl);
            inner.add(Box.createVerticalStrut(20));
            inner.add(goShopBtn);
            emptyState.add(inner);
            emptyState.setPreferredSize(new Dimension(560, 320));
            itemsPanel.add(emptyState);
        } else {
            // ── Liste des articles ─────────────────────────────────────────
            for (OrderItem item : cart) {
                JPanel row = new JPanel(new BorderLayout(14, 0)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(Theme.WHITE);
                        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                        g2.dispose();
                    }
                };
                row.setOpaque(false);
                row.setBorder(new EmptyBorder(12, 14, 12, 14));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

                // Icône catégorie
                JLabel iconLbl = new JLabel("📦");
                if (item.getProduct().getCategory() != null) {
                    switch (item.getProduct().getCategory()) {
                        case ELECTRONIQUE: iconLbl.setText("📱"); break;
                        case MODE:         iconLbl.setText("👗"); break;
                        case MAISON:       iconLbl.setText("🏠"); break;
                        case SPORT:        iconLbl.setText("⚽"); break;
                        case BEAUTE:       iconLbl.setText("💄"); break;
                        case ALIMENTATION: iconLbl.setText("🥗"); break;
                        case LIVRES:       iconLbl.setText("📚"); break;
                        case JOUETS:       iconLbl.setText("🎮"); break;
                        case AUTOMOBILES:  iconLbl.setText("🚗"); break;
                        case SANTE:        iconLbl.setText("💊"); break;
                        default:           break;
                    }
                }
                iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
                row.add(iconLbl, BorderLayout.WEST);

                // Nom + prix
                JPanel namePanel = new JPanel();
                namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
                namePanel.setOpaque(false);

                JLabel nameLbl = new JLabel(item.getProduct().getTitle());
                nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                nameLbl.setForeground(Theme.DARK_TEXT);

                JLabel priceLbl = new JLabel(String.format("%.0f FCFA × %.0f = %.0f FCFA",
                    item.getUnitPrice(), item.getQuantity(), item.getSubtotal()));
                priceLbl.setFont(Theme.FONT_BODY);
                priceLbl.setForeground(Theme.GREY_TEXT);

                namePanel.add(nameLbl);
                namePanel.add(Box.createVerticalStrut(2));
                namePanel.add(priceLbl);
                row.add(namePanel, BorderLayout.CENTER);

                // Bouton retirer
                SecondaryButton removeBtn = new SecondaryButton("Retirer");
                removeBtn.setPreferredSize(new Dimension(80, 32));
                removeBtn.addActionListener(e -> {
                    Session.removeFromCart(item.getProduct().getId());
                    refreshItems();
                    updateTotal();
                    Toast.warning(CartView.this, "<b>" + item.getProduct().getTitle() + "</b> retiré du panier.");
                });
                row.add(removeBtn, BorderLayout.EAST);
                itemsPanel.add(row);
                itemsPanel.add(Box.createVerticalStrut(8));
            }
        }
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    private void updateTotal() {
        if (totalLabel != null)
            totalLabel.setText(String.format("%.0f FCFA", Session.getCartTotal()));
    }

    private void passCommande() {
        if (Session.getCart().isEmpty()) {
            Toast.warning(this, "Votre panier est vide.");
            return;
        }

        // ── Dialogue stylisé : adresse + paiement ──────────────────────────
        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
        dialogPanel.setBorder(new EmptyBorder(8, 4, 8, 4));
        dialogPanel.setBackground(Theme.WHITE);

        JLabel addrLabel = new JLabel("Adresse de livraison :");
        addrLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addrLabel.setForeground(Theme.DARK_TEXT);
        addrLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField addrField = new JTextField(30);
        addrField.setFont(Theme.FONT_BODY);
        addrField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.MID_GREY, 1, true),
            new EmptyBorder(6, 10, 6, 10)));
        addrField.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        addrField.setAlignmentX(Component.LEFT_ALIGNMENT);

        dialogPanel.add(addrLabel);
        dialogPanel.add(Box.createVerticalStrut(6));
        dialogPanel.add(addrField);
        dialogPanel.add(Box.createVerticalStrut(14));

        JLabel payLabel = new JLabel("Mode de paiement :");
        payLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        payLabel.setForeground(Theme.DARK_TEXT);
        payLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] paiements = {"Mobile Money (Airtel / Moov)", "Carte bancaire", "Paiement à la livraison"};
        JComboBox<String> payBox = new JComboBox<>(paiements);
        payBox.setFont(Theme.FONT_BODY);
        payBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        payBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        dialogPanel.add(payLabel);
        dialogPanel.add(Box.createVerticalStrut(6));
        dialogPanel.add(payBox);

        int result = JOptionPane.showConfirmDialog(this, dialogPanel,
            "Finaliser la commande", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return;

        String adresse = addrField.getText().trim();
        String paiement = (String) payBox.getSelectedItem();

        if (adresse.isEmpty()) {
            Toast.error(this, "Veuillez saisir une adresse de livraison.");
            return;
        }

        SwingWorker<model.Order, Void> worker = new SwingWorker<model.Order, Void>() {
            @Override protected model.Order doInBackground() throws Exception {
                if (orderService == null) { connectToServer(); }
                if (orderService == null) throw new Exception("Cannot connect to server.");
                return orderService.passerCommande(
                    currentUser.getId(), Session.getCart(), adresse, paiement);
            }
            @Override protected void done() {
                try {
                    model.Order order = get();
                    Session.clearCart();
                    Toast.success(CartView.this,
                        "Commande #" + order.getId() + " confirmée ! Total : "
                        + String.format("%.0f FCFA", order.getTotalAmount()));
                    AppNavigator.show(new MarketPlace(currentUser));
                } catch (Exception ex) {
                    Toast.error(CartView.this, "Erreur : " + ErrorUtil.rootMessage(ex));
                }
            }
        };
        worker.execute();
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
