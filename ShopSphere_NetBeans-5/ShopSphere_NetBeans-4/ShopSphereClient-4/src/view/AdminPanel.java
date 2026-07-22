/*
 * ShopSphere - AdminPanel
 * Panneau d'administration — gestion utilisateurs, produits, commandes
 * ADMIN uniquement
 */
package view;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
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

    private UserService    userService;
    private ProductService productService;
    private OrderService   orderService;

    private final User adminUser;

    private DefaultTableModel usersModel, productsModel, ordersModel;
    private JTable usersTable, productsTable, ordersTable;
    private JLabel totalUsersLbl, activeUsersLbl, suspendedUsersLbl, sellersLbl, clientsLbl;

    private AppTextField userSearchTxt;
    private JComboBox<String> roleFilterCmb;
    private JComboBox<String> statusFilterCmb;

    private List<User> rawUsersList = new ArrayList<>();
    private List<Product> rawProductsList = new ArrayList<>();
    private List<Order> rawOrdersList = new ArrayList<>();

    public AdminPanel(User user) {
        initComponents();
        this.adminUser = user;
        setTitle("ShopSphere — Panneau d'administration");
        setSize(1180, 740);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Theme.NEUTRAL);
        setLayout(new BorderLayout());

        // Header degrade
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, Theme.PRIMARY, getWidth(), 0, new Color(0x23, 0x52, 0x7A));
                g2.setPaint(gp); g2.fillRect(0, 0, getWidth(), getHeight()); g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 64));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));
        
        JLabel titre = new JLabel("🛡 Administration ShopSphere");
        titre.setFont(new Font("SansSerif", Font.BOLD, 20));
        titre.setForeground(Theme.WHITE);
        header.add(titre, BorderLayout.WEST);
        
        JPanel topActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topActionPanel.setOpaque(false);
        
        SecondaryButton refreshBtn = new SecondaryButton("🔄 Actualiser");
        refreshBtn.addActionListener(e -> loadAllData());
        topActionPanel.add(refreshBtn);

        SecondaryButton catalogBtn = new SecondaryButton("🛍 Voir le catalogue");
        catalogBtn.addActionListener(e -> new MarketPlace(adminUser).setVisible(true));
        topActionPanel.add(catalogBtn);

        header.add(topActionPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Onglets
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.FONT_HEADING);
        tabs.addTab("👥 Utilisateurs", buildUsersTab());
        tabs.addTab("📦 Produits",     buildProductsTab());
        tabs.addTab("🛒 Commandes",    buildOrdersTab());
        add(tabs, BorderLayout.CENTER);
        
        setVisible(true);
        loadAllData(); // Connexion + chargement asynchrones
    }

    private void connectToServer() {
        try {
            Registry reg = LocateRegistry.getRegistry("127.0.0.1", 4999);
            this.userService    = (UserService)    reg.lookup("UserService");
            this.productService = (ProductService) reg.lookup("ProductService");
            this.orderService   = (OrderService)   reg.lookup("OrderService");
        } catch (Exception e) {
            System.err.println("Server connection failed: " + e.getMessage());
        }
    }

    private JPanel buildUsersTab() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Theme.NEUTRAL);
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // 1. Stats Bar
        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        statsPanel.setOpaque(false);
        
        totalUsersLbl     = createStatCard("Total Utilisateurs", "0", Theme.PRIMARY);
        activeUsersLbl    = createStatCard("Comptes Actifs", "0", Theme.SUCCESS);
        suspendedUsersLbl = createStatCard("Suspendus", "0", Theme.ERROR);
        sellersLbl        = createStatCard("Vendeurs", "0", Theme.ACCENT);
        clientsLbl        = createStatCard("Clients", "0", new Color(0x3B, 0x82, 0xF6));

        statsPanel.add(totalUsersLbl.getParent());
        statsPanel.add(activeUsersLbl.getParent());
        statsPanel.add(suspendedUsersLbl.getParent());
        statsPanel.add(sellersLbl.getParent());
        statsPanel.add(clientsLbl.getParent());

        // 2. Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filterBar.setBackground(Theme.WHITE);
        filterBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.LIGHT_GREY));

        filterBar.add(new JLabel("Rechercher :"));
        userSearchTxt = new AppTextField("Nom, email ou téléphone...");
        userSearchTxt.setPreferredSize(new Dimension(220, 34));
        userSearchTxt.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { applyUserFilters(); }
        });
        filterBar.add(userSearchTxt);

        filterBar.add(new JLabel("Rôle :"));
        roleFilterCmb = new JComboBox<>(new String[]{"TOUS", "ADMIN", "SELLER", "CLIENT"});
        roleFilterCmb.setPreferredSize(new Dimension(110, 34));
        roleFilterCmb.addActionListener(e -> applyUserFilters());
        filterBar.add(roleFilterCmb);

        filterBar.add(new JLabel("Statut :"));
        statusFilterCmb = new JComboBox<>(new String[]{"TOUS", "ACTIVE", "SUSPENDED", "PENDING_VERIFICATION", "DELETED"});
        statusFilterCmb.setPreferredSize(new Dimension(160, 34));
        statusFilterCmb.addActionListener(e -> applyUserFilters());
        filterBar.add(statusFilterCmb);

        PrimaryButton searchBtn = new PrimaryButton("Filtrer");
        searchBtn.addActionListener(e -> applyUserFilters());
        filterBar.add(searchBtn);

        JPanel topContainer = new JPanel(new BorderLayout(0, 10));
        topContainer.setOpaque(false);
        topContainer.add(statsPanel, BorderLayout.NORTH);
        topContainer.add(filterBar, BorderLayout.SOUTH);

        mainPanel.add(topContainer, BorderLayout.NORTH);

        // 3. User Table
        String[] cols = {
            "ID", "Nom complet", "Email", "Téléphone", "Rôle", "Statut", "Email vérifié", "Tél. vérifié", "Langue"
        };
        usersModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        usersTable = new JTable(usersModel);
        styleTable(usersTable);

        // Color cell rendering for status & roles
        usersTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF9, 0xFA, 0xFB));
                    if (column == 4) { // Role
                        String roleStr = String.valueOf(value);
                        if ("ADMIN".equals(roleStr)) setForeground(new Color(0xDC, 0x26, 0x26));
                        else if ("SELLER".equals(roleStr)) setForeground(new Color(0xD9, 0x77, 0x06));
                        else setForeground(new Color(0x25, 0x63, 0xEB));
                    } else if (column == 5) { // Status
                        String st = String.valueOf(value);
                        if ("ACTIVE".equals(st)) setForeground(Theme.SUCCESS);
                        else if ("SUSPENDED".equals(st)) setForeground(Theme.ERROR);
                        else if ("DELETED".equals(st)) setForeground(Color.GRAY);
                        else setForeground(Theme.ACCENT);
                    } else {
                        setForeground(Theme.DARK_TEXT);
                    }
                }
                return c;
            }
        });

        usersTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && usersTable.getSelectedRow() >= 0) {
                    showUserDetailsAndActions(usersTable.getSelectedRow());
                }
            }
        });

        mainPanel.add(new JScrollPane(usersTable), BorderLayout.CENTER);

        // 4. Bottom Info & Actions Bar
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(Theme.WHITE);
        bottomBar.setBorder(new EmptyBorder(8, 12, 8, 12));
        
        JLabel helpLbl = new JLabel("💡 Double-cliquez sur une ligne d'utilisateur pour afficher les détails et modifier son statut.");
        helpLbl.setFont(Theme.FONT_BODY);
        helpLbl.setForeground(Theme.DARK_TEXT);
        bottomBar.add(helpLbl, BorderLayout.WEST);

        JPanel actionsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionsRight.setOpaque(false);

        SecondaryButton detailsBtn = new SecondaryButton("Détails compte");
        detailsBtn.addActionListener(e -> {
            int row = usersTable.getSelectedRow();
            if (row >= 0) showUserDetailsAndActions(row);
            else JOptionPane.showMessageDialog(AdminPanel.this, "Veuillez sélectionner un utilisateur dans le tableau.");
        });
        actionsRight.add(detailsBtn);

        SecondaryButton toggleStatusBtn = new SecondaryButton("Modifier Statut");
        toggleStatusBtn.addActionListener(e -> {
            int row = usersTable.getSelectedRow();
            if (row >= 0) {
                int id = (int) usersModel.getValueAt(row, 0);
                String currentStatus = String.valueOf(usersModel.getValueAt(row, 5));
                String[] options = {"ACTIVE", "SUSPENDED", "PENDING_VERIFICATION", "DELETED", "Annuler"};
                String choice = (String) JOptionPane.showInputDialog(AdminPanel.this,
                        "Sélectionnez le nouveau statut pour l'utilisateur #" + id + ":",
                        "Modifier Statut", JOptionPane.QUESTION_MESSAGE, null, options, currentStatus);
                if (choice != null && !"Annuler".equals(choice)) {
                    try {
                        if (userService == null) connectToServer();
                        userService.suspendreCompte(id); // updates status
                        User u = userService.findUserRecordById(id);
                        if (u != null) {
                            u.setStatus(User.AccountStatus.valueOf(choice));
                            userService.updateUserRecord(u);
                        }
                        loadAllData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(AdminPanel.this, ErrorUtil.rootMessage(ex));
                    }
                }
            } else {
                JOptionPane.showMessageDialog(AdminPanel.this, "Veuillez sélectionner un utilisateur.");
            }
        });
        actionsRight.add(toggleStatusBtn);

        bottomBar.add(actionsRight, BorderLayout.EAST);
        mainPanel.add(bottomBar, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JLabel createStatCard(String title, String initialVal, Color accentColor) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Theme.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                new EmptyBorder(8, 12, 8, 12)));
        
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleLbl.setForeground(Color.GRAY);

        JLabel valLbl = new JLabel(initialVal);
        valLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        valLbl.setForeground(Theme.DARK_TEXT);

        p.add(titleLbl, BorderLayout.NORTH);
        p.add(valLbl, BorderLayout.CENTER);

        return valLbl;
    }

    private void applyUserFilters() {
        if (rawUsersList == null) return;
        String query = userSearchTxt != null ? userSearchTxt.getText().trim().toLowerCase() : "";
        String roleSel = roleFilterCmb != null ? String.valueOf(roleFilterCmb.getSelectedItem()) : "TOUS";
        String statusSel = statusFilterCmb != null ? String.valueOf(statusFilterCmb.getSelectedItem()) : "TOUS";

        usersModel.setRowCount(0);
        for (User u : rawUsersList) {
            boolean matchQuery = query.isEmpty() ||
                    String.valueOf(u.getId()).contains(query) ||
                    (u.getFullName() != null && u.getFullName().toLowerCase().contains(query)) ||
                    (u.getEmail() != null && u.getEmail().toLowerCase().contains(query)) ||
                    (u.getPhone() != null && u.getPhone().toLowerCase().contains(query));

            boolean matchRole = "TOUS".equals(roleSel) || (u.getRole() != null && u.getRole().name().equals(roleSel));
            boolean matchStatus = "TOUS".equals(statusSel) || (u.getStatus() != null && u.getStatus().name().equals(statusSel));

            if (matchQuery && matchRole && matchStatus) {
                usersModel.addRow(new Object[]{
                    u.getId(),
                    u.getFullName() != null ? u.getFullName() : "—",
                    u.getEmail() != null ? u.getEmail() : "—",
                    u.getPhone() != null && !u.getPhone().isEmpty() ? u.getPhone() : "—",
                    u.getRole() != null ? u.getRole().name() : "—",
                    u.getStatus() != null ? u.getStatus().name() : "—",
                    u.isEmailVerified() ? "Oui" : "Non",
                    u.isPhoneVerified() ? "Oui" : "Non",
                    u.getPreferredLanguage() != null ? u.getPreferredLanguage() : "fr"
                });
            }
        }
    }

    private void showUserDetailsAndActions(int row) {
        int id = (int) usersModel.getValueAt(row, 0);
        User u = null;
        for (User candidate : rawUsersList) {
            if (candidate.getId() == id) {
                u = candidate;
                break;
            }
        }
        if (u == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("📋 FICHE UTILISATEUR #").append(u.getId()).append("\n\n");
        sb.append("• Nom complet : ").append(u.getFullName()).append("\n");
        sb.append("• Email : ").append(u.getEmail()).append("\n");
        sb.append("• Téléphone : ").append(u.getPhone() != null ? u.getPhone() : "Non renseigné").append("\n");
        sb.append("• Rôle : ").append(u.getRole()).append("\n");
        sb.append("• Statut : ").append(u.getStatus()).append("\n");
        sb.append("• Email vérifié : ").append(u.isEmailVerified() ? "Oui" : "Non").append("\n");
        sb.append("• Téléphone vérifié : ").append(u.isPhoneVerified() ? "Oui" : "Non").append("\n");
        sb.append("• Langue préférée : ").append(u.getPreferredLanguage()).append("\n");

        String[] options = {"Suspendre", "Réactiver", "Supprimer compte", "Fermer"};
        int choice = JOptionPane.showOptionDialog(AdminPanel.this,
                sb.toString(), "Informations Utilisateur #" + u.getId(),
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[3]);

        try {
            if (userService == null) connectToServer();
            if (choice == 0) { // Suspendre
                userService.suspendreCompte(id);
                loadAllData();
            } else if (choice == 1) { // Reactiver
                User target = userService.findUserRecordById(id);
                if (target != null) {
                    target.setStatus(User.AccountStatus.ACTIVE);
                    userService.updateUserRecord(target);
                }
                loadAllData();
            } else if (choice == 2) { // Supprimer
                int confirm = JOptionPane.showConfirmDialog(AdminPanel.this,
                        "Voulez-vous vraiment désactiver/supprimer le compte #" + id + " ?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    userService.supprimerCompte(id);
                    loadAllData();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(AdminPanel.this, ErrorUtil.rootMessage(ex));
        }
    }

    private JPanel buildProductsTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        p.setBackground(Theme.NEUTRAL);

        String[] cols = {"ID", "Titre", "Vendeur", "Catégorie", "Prix (EUR)", "Stock", "Statut"};
        productsModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        productsTable = new JTable(productsModel);
        styleTable(productsTable);

        productsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && productsTable.getSelectedRow() >= 0) {
                    int pid = (int) productsModel.getValueAt(productsTable.getSelectedRow(), 0);
                    int confirm = JOptionPane.showConfirmDialog(AdminPanel.this,
                        "Archiver ce produit (#" + pid + ") ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            if (productService == null) { connectToServer(); }
                            Product prod = productService.findProductRecordById(pid);
                            if (prod != null) { productService.deleteProductRecord(prod); loadAllData(); }
                        } catch (Exception ex) { JOptionPane.showMessageDialog(AdminPanel.this, ErrorUtil.rootMessage(ex)); }
                    }
                }
            }
        });
        p.add(new JScrollPane(productsTable), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildOrdersTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        p.setBackground(Theme.NEUTRAL);

        String[] cols = {"#", "Date", "Client", "Total (EUR)", "Statut"};
        ordersModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        ordersTable = new JTable(ordersModel);
        styleTable(ordersTable);

        ordersTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && ordersTable.getSelectedRow() >= 0) {
                    String idStr = ordersModel.getValueAt(ordersTable.getSelectedRow(), 0).toString().replace("#","");
                    String[] statuts = {"PAYEE","EN_TRAITEMENT","EXPEDIEE","LIVREE","TERMINEE","REMBOURSEE","ANNULEE"};
                    String choice = (String) JOptionPane.showInputDialog(AdminPanel.this,
                        "Changer le statut de la commande #" + idStr, "Statut",
                        JOptionPane.QUESTION_MESSAGE, null, statuts, statuts[0]);
                    if (choice != null) {
                        try {
                            if (orderService == null) { connectToServer(); }
                            orderService.mettreAJourStatut(
                                Integer.parseInt(idStr), Order.OrderStatus.valueOf(choice));
                            loadAllData();
                        } catch (Exception ex) { JOptionPane.showMessageDialog(AdminPanel.this, ErrorUtil.rootMessage(ex)); }
                    }
                }
            }
        });
        p.add(new JScrollPane(ordersTable), BorderLayout.CENTER);
        return p;
    }

    private void styleTable(JTable t) {
        t.setFont(Theme.FONT_BODY);
        t.setRowHeight(32);
        t.getTableHeader().setFont(Theme.FONT_HEADING);
        t.getTableHeader().setBackground(Theme.PRIMARY);
        t.getTableHeader().setForeground(Theme.WHITE);
        t.setGridColor(Theme.LIGHT_GREY);
        t.setSelectionBackground(new Color(0xE0, 0xF2, 0xFE));
        t.setSelectionForeground(Theme.DARK_TEXT);
    }

    private void loadAllData() {
        SwingWorker<Void, Void> w = new SwingWorker<Void, Void>() {
            List<User> users; List<Product> products; List<Order> orders;
            @Override protected Void doInBackground() throws Exception {
                if (userService == null || productService == null || orderService == null) {
                    connectToServer();
                }
                if (userService != null)    users    = userService.findAllUserRecords();
                if (productService != null) products = productService.findAllProductRecords();
                if (orderService != null)   orders   = orderService.findAllOrderRecords();
                return null;
            }
            @Override protected void done() {
                try { get(); } catch (Exception ignored) {}
                
                rawUsersList = users != null ? users : new ArrayList<>();
                rawProductsList = products != null ? products : new ArrayList<>();
                rawOrdersList = orders != null ? orders : new ArrayList<>();

                // Update Stats
                int total = rawUsersList.size();
                int active = 0, suspended = 0, sellers = 0, clients = 0;
                for (User u : rawUsersList) {
                    if (u.getStatus() == User.AccountStatus.ACTIVE) active++;
                    else if (u.getStatus() == User.AccountStatus.SUSPENDED) suspended++;

                    if (u.getRole() == User.Role.SELLER) sellers++;
                    else if (u.getRole() == User.Role.CLIENT) clients++;
                }

                if (totalUsersLbl != null) totalUsersLbl.setText(String.valueOf(total));
                if (activeUsersLbl != null) activeUsersLbl.setText(String.valueOf(active));
                if (suspendedUsersLbl != null) suspendedUsersLbl.setText(String.valueOf(suspended));
                if (sellersLbl != null) sellersLbl.setText(String.valueOf(sellers));
                if (clientsLbl != null) clientsLbl.setText(String.valueOf(clients));

                // Render User Table with Filters
                applyUserFilters();

                // Render Products Table
                productsModel.setRowCount(0);
                for (Product p : rawProductsList) {
                    productsModel.addRow(new Object[]{
                        p.getId(), p.getTitle(),
                        p.getSeller() != null ? p.getSeller().getFullName() : "—",
                        p.getCategory(), String.format("%.2f", p.getPricePerUnit()),
                        p.getStockQty(), p.getStatus()
                    });
                }

                // Render Orders Table
                ordersModel.setRowCount(0);
                for (Order o : rawOrdersList) {
                    ordersModel.addRow(new Object[]{
                        "#" + o.getId(),
                        o.getOrderDate(), o.getBuyer() != null ? o.getBuyer().getFullName() : "—",
                        String.format("%.2f", o.getTotalAmount()), o.getStatus()
                    });
                }
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
