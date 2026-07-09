/*
 * ShopSphere - AdminPanel
 * Panneau d'administration — gestion utilisateurs, produits, commandes
 * ADMIN uniquement
 */
package view;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import model.Order;
import model.Product;
import model.User;
import service0.OrderService;
import service0.ProductService;
import service0.UserService;
import view.components.*;
import view.theme.Theme;

public class AdminPanel extends JFrame {

    private final User adminUser;

    public AdminPanel(User user) {
        this.adminUser = user;
        setTitle("ShopSphere — Panneau d'administration");
        setSize(1050, 660);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Theme.NEUTRAL);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.PRIMARY);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));
        JLabel titre = new JLabel("Administration ShopSphere");
        titre.setFont(Theme.FONT_SUBTITLE);
        titre.setForeground(Theme.WHITE);
        header.add(titre, BorderLayout.WEST);
        SecondaryButton catalogBtn = new SecondaryButton("Voir le catalogue");
        catalogBtn.addActionListener(e -> new MarketPlace(adminUser).setVisible(true));
        header.add(catalogBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Onglets
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.FONT_HEADING);
        tabs.addTab("Utilisateurs", buildUsersTab());
        tabs.addTab("Produits",     buildProductsTab());
        tabs.addTab("Commandes",    buildOrdersTab());
        add(tabs, BorderLayout.CENTER);
        setVisible(true);
        loadAllData();
    }

    private DefaultTableModel usersModel, productsModel, ordersModel;

    private JPanel buildUsersTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.NEUTRAL);
        String[] cols = {"ID", "Nom", "Email", "Role", "Statut", "Email verifie"};
        usersModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable t = new JTable(usersModel);
        styleTable(t);
        t.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && t.getSelectedRow() >= 0) {
                    int id = (int) usersModel.getValueAt(t.getSelectedRow(), 0);
                    String[] actions = {"Suspendre", "Supprimer compte", "Annuler"};
                    int choice = JOptionPane.showOptionDialog(AdminPanel.this,
                        "Action pour l'utilisateur #" + id, "Action",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, actions, actions[2]);
                    try {
                        UserService us = RMIClient.getUserService();
                        if (choice == 0) { us.suspendreCompte(id); loadAllData(); }
                        else if (choice == 1) {
                            int confirm = JOptionPane.showConfirmDialog(AdminPanel.this,
                                "Supprimer definitivement ce compte ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                            if (confirm == JOptionPane.YES_OPTION) { us.supprimerCompte(id); loadAllData(); }
                        }
                    } catch (Exception ex) { JOptionPane.showMessageDialog(AdminPanel.this, ex.getMessage()); }
                }
            }
        });
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bar.setBackground(Theme.WHITE);
        bar.add(new JLabel("Double-cliquez sur un utilisateur pour le suspendre ou supprimer."));
        p.add(bar, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildProductsTab() {
        JPanel p = new JPanel(new BorderLayout());
        String[] cols = {"ID", "Titre", "Vendeur", "Categorie", "Prix", "Stock", "Statut"};
        productsModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable t = new JTable(productsModel);
        styleTable(t);
        t.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && t.getSelectedRow() >= 0) {
                    int pid = (int) productsModel.getValueAt(t.getSelectedRow(), 0);
                    int confirm = JOptionPane.showConfirmDialog(AdminPanel.this,
                        "Archiver ce produit (#" + pid + ") ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            ProductService ps = RMIClient.getProductService();
                            Product prod = ps.findProductRecordById(pid);
                            if (prod != null) { ps.deleteProductRecord(prod); loadAllData(); }
                        } catch (Exception ex) { JOptionPane.showMessageDialog(AdminPanel.this, ex.getMessage()); }
                    }
                }
            }
        });
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildOrdersTab() {
        JPanel p = new JPanel(new BorderLayout());
        String[] cols = {"#", "Date", "Client", "Total (EUR)", "Statut"};
        ordersModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable t = new JTable(ordersModel);
        styleTable(t);
        t.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && t.getSelectedRow() >= 0) {
                    String idStr = ordersModel.getValueAt(t.getSelectedRow(), 0).toString().replace("#","");
                    String[] statuts = {"PAYEE","EN_TRAITEMENT","EXPEDIEE","LIVREE","TERMINEE","REMBOURSEE"};
                    String choice = (String) JOptionPane.showInputDialog(AdminPanel.this,
                        "Changer le statut de la commande #" + idStr, "Statut",
                        JOptionPane.QUESTION_MESSAGE, null, statuts, statuts[0]);
                    if (choice != null) {
                        try {
                            RMIClient.getOrderService().mettreAJourStatut(
                                Integer.parseInt(idStr), Order.OrderStatus.valueOf(choice));
                            loadAllData();
                        } catch (Exception ex) { JOptionPane.showMessageDialog(AdminPanel.this, ex.getMessage()); }
                    }
                }
            }
        });
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        return p;
    }

    private void styleTable(JTable t) {
        t.setFont(Theme.FONT_BODY);
        t.setRowHeight(28);
        t.getTableHeader().setFont(Theme.FONT_HEADING);
        t.getTableHeader().setBackground(Theme.PRIMARY);
        t.getTableHeader().setForeground(Theme.WHITE);
        t.setGridColor(Theme.LIGHT_GREY);
    }

    private void loadAllData() {
        SwingWorker<Void, Void> w = new SwingWorker<>() {
            List<User> users; List<Product> products; List<Order> orders;
            @Override protected Void doInBackground() throws Exception {
                users    = RMIClient.getUserService().findAllUserRecords();
                products = RMIClient.getProductService().findAllProductRecords();
                orders   = RMIClient.getOrderService().findAllOrderRecords();
                return null;
            }
            @Override protected void done() {
                try { get(); } catch (Exception ignored) {}
                usersModel.setRowCount(0);
                if (users != null) for (User u : users)
                    usersModel.addRow(new Object[]{u.getId(), u.getFullName(), u.getEmail(),
                        u.getRole(), u.getStatus(), u.isEmailVerified() ? "Oui" : "Non"});
                productsModel.setRowCount(0);
                if (products != null) for (Product p : products)
                    productsModel.addRow(new Object[]{p.getId(), p.getTitle(),
                        p.getSeller() != null ? p.getSeller().getFullName() : "—",
                        p.getCategory(), String.format("%.2f", p.getPricePerUnit()),
                        p.getStockQty(), p.getStatus()});
                ordersModel.setRowCount(0);
                if (orders != null) for (Order o : orders)
                    ordersModel.addRow(new Object[]{"#" + o.getId(),
                        o.getOrderDate(), o.getBuyer() != null ? o.getBuyer().getFullName() : "—",
                        String.format("%.2f", o.getTotalAmount()), o.getStatus()});
            }
        };
        w.execute();
    }
}
