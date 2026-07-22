/*
 * ShopSphere - CartView
 * Fenetre du panier — liste les articles, total, et bouton passer commande
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

    /**
     * Connexion RMI — identique a AgriConnect : LocateRegistry.getRegistry(host, port)
     * + reg.lookup("OrderService").
     */
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
        setSize(600, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.NEUTRAL);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(Theme.PRIMARY);
        header.setPreferredSize(new Dimension(0, 60));
        JLabel titre = new JLabel("Mon panier (" + Session.getCartCount() + " article(s))");
        titre.setFont(Theme.FONT_SUBTITLE);
        titre.setForeground(Theme.WHITE);
        header.add(titre);
        add(header, BorderLayout.NORTH);

        // Liste articles
        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Theme.NEUTRAL);
        itemsPanel.setBorder(new EmptyBorder(12, 20, 12, 20));
        refreshItems();

        JScrollPane scroll = new JScrollPane(itemsPanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // Pied de page — total + bouton
        JPanel footer = new JPanel(new BorderLayout(16, 0));
        footer.setBackground(Theme.WHITE);
        footer.setBorder(new EmptyBorder(14, 20, 14, 20));

        totalLabel = new JLabel(String.format("Total : %.2f EUR", Session.getCartTotal()));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(Theme.PRIMARY);
        footer.add(totalLabel, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Theme.WHITE);
        SecondaryButton clearBtn = new SecondaryButton("Vider le panier");
        clearBtn.addActionListener(e -> { Session.clearCart(); refreshItems(); updateTotal(); });
        AccentButton orderBtn = new AccentButton("Passer la commande", 200, Theme.BTN_H);
        orderBtn.addActionListener(e -> passCommande());
        btnPanel.add(clearBtn); btnPanel.add(orderBtn);
        footer.add(btnPanel, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void refreshItems() {
        itemsPanel.removeAll();
        List<OrderItem> cart = Session.getCart();
        if (cart.isEmpty()) {
            JLabel emptyLbl = new JLabel("Votre panier est vide.");
            emptyLbl.setFont(Theme.FONT_BODY);
            emptyLbl.setForeground(Theme.GREY_TEXT);
            emptyLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            itemsPanel.add(Box.createVerticalStrut(40));
            itemsPanel.add(emptyLbl);
        } else {
            for (OrderItem item : cart) {
                JPanel row = new JPanel(new BorderLayout(12, 0));
                row.setBackground(Theme.WHITE);
                row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.LIGHT_GREY),
                    new EmptyBorder(10, 10, 10, 10)
                ));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

                JLabel nameLbl = new JLabel(item.getProduct().getTitle());
                nameLbl.setFont(Theme.FONT_HEADING);
                nameLbl.setForeground(Theme.DARK_TEXT);

                JLabel priceLbl = new JLabel(String.format("%.2f EUR x %.0f = %.2f EUR",
                        item.getUnitPrice(), item.getQuantity(), item.getSubtotal()));
                priceLbl.setFont(Theme.FONT_BODY);
                priceLbl.setForeground(Theme.GREY_TEXT);

                JPanel namePanel = new JPanel();
                namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
                namePanel.setBackground(Theme.WHITE);
                namePanel.add(nameLbl);
                namePanel.add(priceLbl);
                row.add(namePanel, BorderLayout.CENTER);

                SecondaryButton removeBtn = new SecondaryButton("Retirer");
                removeBtn.setPreferredSize(new Dimension(80, 30));
                removeBtn.addActionListener(e -> {
                    Session.removeFromCart(item.getProduct().getId());
                    refreshItems(); updateTotal();
                });
                row.add(removeBtn, BorderLayout.EAST);
                itemsPanel.add(row);
                itemsPanel.add(Box.createVerticalStrut(6));
            }
        }
        itemsPanel.revalidate(); itemsPanel.repaint();
    }

    private void updateTotal() {
        if (totalLabel != null)
            totalLabel.setText(String.format("Total : %.2f EUR", Session.getCartTotal()));
    }

    private void passCommande() {
        if (Session.getCart().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Votre panier est vide.", "Panier", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String adresse = JOptionPane.showInputDialog(this,
                "Adresse de livraison :", "Livraison", JOptionPane.QUESTION_MESSAGE);
        if (adresse == null || adresse.isEmpty()) return;

        String[] paiements = {"Carte bancaire", "PayPal", "Virement"};
        String paiement = (String) JOptionPane.showInputDialog(this,
                "Mode de paiement :", "Paiement",
                JOptionPane.QUESTION_MESSAGE, null, paiements, paiements[0]);
        if (paiement == null) return;

        SwingWorker<model.Order, Void> worker = new SwingWorker<model.Order, Void>() {
            @Override protected model.Order doInBackground() throws Exception {
                if (orderService == null) { connectToServer(); }
                if (orderService == null) {
                    throw new Exception("Cannot connect to server.");
                }
                return orderService.passerCommande(
                        currentUser.getId(), Session.getCart(), adresse, paiement);
            }
            @Override protected void done() {
                try {
                    model.Order order = get();
                    Session.clearCart();
                    JOptionPane.showMessageDialog(CartView.this,
                        "Commande #" + order.getId() + " passee avec succes !\nTotal : "
                        + String.format("%.2f EUR", order.getTotalAmount()),
                        "Commande confirmee", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(CartView.this,
                        "Erreur : " + ErrorUtil.rootMessage(ex), "Erreur", JOptionPane.ERROR_MESSAGE);
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
