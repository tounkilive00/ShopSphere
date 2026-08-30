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
    private List<Product> currentProducts = java.util.Collections.emptyList();

    public SellerDashboard(User user) {
        initComponents();
        connectToServer();
        this.currentUser = user;
        setTitle("ShopSphere — Tableau de bord vendeur");
        setSize(960, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.NEUTRAL);
        setLayout(new BorderLayout());

        // Header — design fourni par view.components.PageHeader (reutilisable)
        PageHeader header = new PageHeader("Tableau de bord — " + user.getFullName(),
                "Gerez votre catalogue et suivez vos ventes");
        AccentButton newProductBtn = new AccentButton("+ Nouveau produit", 170, 36);
        newProductBtn.addActionListener(e -> AppNavigator.show(
                new AddEditProduct(user, null,
                        () -> AppNavigator.show(new SellerDashboard(user)),
                        () -> AppNavigator.show(new SellerDashboard(user)))));
        SecondaryButton backBtn = new SecondaryButton("← Catalogue");
        backBtn.addActionListener(e -> AppNavigator.show(new MarketPlace(user)));
        header.addAction(backBtn);
        header.addAction(newProductBtn);
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
    private JTable productsTable;

    private JPanel buildProductsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.NEUTRAL);
        String[] cols = {"#", "Titre", "Categorie", "Prix (FCFA)", "Stock", "Statut", "Actions"};
        productsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 6; }
        };
        productsTable = new JTable(productsModel);
        productsTable.setFont(Theme.FONT_BODY);
        productsTable.setRowHeight(38);
        productsTable.getTableHeader().setFont(Theme.FONT_HEADING);
        productsTable.getTableHeader().setBackground(Theme.PRIMARY);
        productsTable.getTableHeader().setForeground(Theme.WHITE);
        Theme.styleTable(productsTable);

        // Colonne Actions cliquable — design fourni par view.components.ProductActionsCell,
        // logique metier (appels RMI) fournie ici via les callbacks.
        ProductActionsCell actionsCell = new ProductActionsCell(
                this::isProductVisible,
                this::openEditProduct,
                this::toggleProductVisibility,
                this::confirmAndDeleteProduct);
        productsTable.getColumnModel().getColumn(6).setCellRenderer(actionsCell);
        productsTable.getColumnModel().getColumn(6).setCellEditor(actionsCell);
        productsTable.getColumnModel().getColumn(6).setPreferredWidth(240);

        p.add(new JScrollPane(productsTable), BorderLayout.CENTER);
        return p;
    }

    private boolean isProductVisible(int productId) {
        for (Product prod : currentProducts) if (prod.getId() == productId) return prod.isVisible();
        return true;
    }

    private void openEditProduct(int productId) {
        for (Product prod : currentProducts) {
            if (prod.getId() == productId) {
                AppNavigator.show(new AddEditProduct(currentUser, prod,
                        () -> AppNavigator.show(new SellerDashboard(currentUser)),
                        () -> AppNavigator.show(new SellerDashboard(currentUser))));
                return;
            }
        }
    }

    private void toggleProductVisibility(int productId) {
        boolean newVisible = !isProductVisible(productId);
        SwingWorker<Void, Void> w = new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                if (productService == null) { connectToServer(); }
                productService.setProductVisibility(productId, newVisible);
                return null;
            }
            @Override protected void done() {
                try { get(); loadData(); } catch (Exception ex) {
                    JOptionPane.showMessageDialog(SellerDashboard.this, ErrorUtil.rootMessage(ex));
                }
            }
        };
        w.execute();
    }

    private void confirmAndDeleteProduct(int productId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer ce produit ? Il sera archive et n'apparaitra plus dans le catalogue.",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        SwingWorker<Void, Void> w = new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                if (productService == null) { connectToServer(); }
                Product prod = productService.findProductRecordById(productId);
                if (prod != null) { productService.deleteProductRecord(prod); }
                return null;
            }
            @Override protected void done() {
                try { get(); loadData(); } catch (Exception ex) {
                    JOptionPane.showMessageDialog(SellerDashboard.this, ErrorUtil.rootMessage(ex));
                }
            }
        };
        w.execute();
    }

    private JPanel buildSalesPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.NEUTRAL);
        String[] cols = {"#Commande", "Date", "Client", "Articles", "Montant (FCFA)", "Statut"};
        salesModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(salesModel);
        table.setFont(Theme.FONT_BODY);
        table.setRowHeight(28);
        table.getTableHeader().setFont(Theme.FONT_HEADING);
        table.getTableHeader().setBackground(Theme.PRIMARY);
        table.getTableHeader().setForeground(Theme.WHITE);
        Theme.styleTable(table);
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
                currentProducts = products != null ? products : java.util.Collections.emptyList();
                productsModel.setRowCount(0);
                for (Product p : currentProducts) {
                    productsModel.addRow(new Object[]{
                        p.getId(), p.getTitle(),
                        p.getCategory() != null ? p.getCategory().name() : "—",
                        String.format("%.2f", p.getPricePerUnit()),
                        p.getStockQty(),
                        p.getStatus().name() + (p.isVisible() ? "" : " (masque)"),
                        "Actions"
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
                    revenueLabel.setText(String.format("Revenu total : %.2f FCFA", revenue));
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
