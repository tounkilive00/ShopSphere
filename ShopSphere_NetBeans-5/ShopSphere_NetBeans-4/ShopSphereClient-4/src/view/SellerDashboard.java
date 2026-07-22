/*
 * ShopSphere - SellerDashboard
 * Tableau de bord vendeur : produits, ventes, revenus
 */
package view;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import model.Order;
import model.Product;
import model.User;
import service0.OrderService;
import service0.ProductService;
import view.components.*;
import view.theme.Theme;

public class SellerDashboard extends JFrame {

    private ProductService productService;
    private OrderService   orderService;

    private final User currentUser;

    public SellerDashboard(User user) {
        initComponents();
        connectToServer();
        this.currentUser = user;
        setTitle("ShopSphere — Tableau de bord vendeur");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.NEUTRAL);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.PRIMARY);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));
        JLabel titre = new JLabel("Tableau de bord — " + user.getFullName());
        titre.setFont(Theme.FONT_SUBTITLE);
        titre.setForeground(Theme.WHITE);
        header.add(titre, BorderLayout.WEST);
        AccentButton newProductBtn = new AccentButton("+ Nouveau produit", 160, 36);
        newProductBtn.addActionListener(e -> new AddEditProduct(user, null,
                () -> dispose()).setVisible(true));
        header.add(newProductBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Onglets
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.FONT_HEADING);
        tabs.setBackground(Theme.NEUTRAL);
        tabs.addTab("Mes produits",   buildProductsPanel());
        tabs.addTab("Mes ventes",     buildSalesPanel());
        add(tabs, BorderLayout.CENTER);
        setVisible(true);
        loadData();
    }

    /**
     * Connexion RMI — identique a AgriConnect : LocateRegistry.getRegistry(host, port)
     * + reg.lookup(...) pour les services vendeur.
     */
    private void connectToServer() {
        try {
            Registry reg = LocateRegistry.getRegistry("127.0.0.1", 4999);
            this.productService = (ProductService) reg.lookup("ProductService");
            this.orderService   = (OrderService)   reg.lookup("OrderService");
        } catch (Exception e) {
            System.err.println("Server connection failed: " + e.getMessage());
        }
    }

    private DefaultTableModel productsModel;
    private DefaultTableModel salesModel;
    private JLabel revenueLabel;

    private JPanel buildProductsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.NEUTRAL);
        String[] cols = {"#", "Titre", "Categorie", "Prix (EUR)", "Stock", "Statut", "Actions"};
        productsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(productsModel);
        table.setFont(Theme.FONT_BODY);
        table.setRowHeight(28);
        table.getTableHeader().setFont(Theme.FONT_HEADING);
        table.getTableHeader().setBackground(Theme.PRIMARY);
        table.getTableHeader().setForeground(Theme.WHITE);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        // Double-clic : modifier le produit
                        JOptionPane.showMessageDialog(SellerDashboard.this,
                            "Editeur produit — fonctionnalite complete dans AddEditProduct.",
                            "Modifier", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildSalesPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.NEUTRAL);
        String[] cols = {"#Commande", "Date", "Client", "Articles", "Montant (EUR)", "Statut"};
        salesModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(salesModel);
        table.setFont(Theme.FONT_BODY);
        table.setRowHeight(28);
        table.getTableHeader().setFont(Theme.FONT_HEADING);
        table.getTableHeader().setBackground(Theme.PRIMARY);
        table.getTableHeader().setForeground(Theme.WHITE);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        // Pied de page revenus
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        footer.setBackground(Theme.WHITE);
        footer.setBorder(new EmptyBorder(8, 16, 8, 16));
        revenueLabel = new JLabel("Revenu total : Chargement...");
        revenueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        revenueLabel.setForeground(Theme.PRIMARY);
        footer.add(revenueLabel);
        p.add(footer, BorderLayout.SOUTH);
        return p;
    }

    private void loadData() {
        SwingWorker<Void, Void> w = new SwingWorker<Void, Void>() {
            List<Product> products;
            List<Order>   orders;
            double        revenue;
            @Override protected Void doInBackground() throws Exception {
                if (productService == null || orderService == null) { connectToServer(); }
                products = productService.findProductRecordsBySeller(currentUser.getId());
                orders   = orderService.findOrderRecordsByBuyer(currentUser.getId()); // simplifie
                revenue  = orderService.calculerRevenuVendeur(currentUser.getId());
                return null;
            }
            @Override protected void done() {
                try { get(); } catch (Exception ignored) {}
                // Produits
                productsModel.setRowCount(0);
                if (products != null) for (Product p : products) {
                    productsModel.addRow(new Object[]{
                        p.getId(), p.getTitle(),
                        p.getCategory() != null ? p.getCategory().name() : "—",
                        String.format("%.2f", p.getPricePerUnit()),
                        p.getStockQty(), p.getStatus().name(), "Modifier / Supprimer"
                    });
                }
                // Ventes
                salesModel.setRowCount(0);
                if (orders != null) for (Order o : orders) {
                    int nb = o.getOrderItems() != null ? o.getOrderItems().size() : 0;
                    salesModel.addRow(new Object[]{
                        "#" + o.getId(),
                        o.getOrderDate() != null ? o.getOrderDate().toString() : "—",
                        o.getBuyer() != null ? o.getBuyer().getFullName() : "—",
                        nb + " article(s)",
                        String.format("%.2f", o.getTotalAmount()),
                        o.getStatus().name()
                    });
                }
                if (revenueLabel != null)
                    revenueLabel.setText(String.format("Revenu total : %.2f EUR", revenue));
            }
        };
        w.execute();
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
