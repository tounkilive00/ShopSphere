/*
 * ShopSphere - MarketPlace
 * Catalogue produits — connexion RMI asynchrone + design professionnel.
 * NavBar persistante + sidebar categories + grille produits
 */
package view;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Product;
import model.User;
import service0.ProductService;
import view.components.*;
import view.theme.Theme;

public class MarketPlace extends JFrame {

    private ProductService productService;
    private final User currentUser;

    private NavBar  navBar;
    private JPanel  productsGrid;
    private JLabel  statusLabel;

    public MarketPlace(User user) {
        this.currentUser = user;
        initComponents();
        buildUI();
        connectAndLoadProducts();
    }

    // ── Connexion + chargement des produits ──────────────────────────────────
    private void connectAndLoadProducts() {
        statusLabel.setText("Connexion au serveur...");
        productsGrid.removeAll();
        productsGrid.add(buildLoadingPanel("Chargement des produits..."));
        productsGrid.revalidate();

        SwingWorker<List<Product>, Void> worker = new SwingWorker<List<Product>, Void>() {
            @Override
            protected List<Product> doInBackground() throws Exception {
                if (productService == null) {
                    try {
                        Registry reg = LocateRegistry.getRegistry("127.0.0.1", 4999);
                        productService = (ProductService) reg.lookup("ProductService");
                    } catch (Exception e) {
                        Thread.sleep(400);
                        Registry reg = LocateRegistry.getRegistry("127.0.0.1", 4999);
                        productService = (ProductService) reg.lookup("ProductService");
                    }
                }
                if (productService == null) throw new Exception("Impossible de se connecter au serveur.");
                return productService.findAvailableProductRecords();
            }

            @Override
            protected void done() {
                productsGrid.removeAll();
                try {
                    List<Product> products = get();
                    if (products == null || products.isEmpty()) {
                        productsGrid.add(buildEmptyPanel(
                            "Aucun produit disponible pour le moment.",
                            "Les vendeurs ajouteront bientôt des articles.", "🛍"));
                        statusLabel.setText("0 produit(s)");
                    } else {
                        for (Product p : products) {
                            ProductCard card = new ProductCard(p,
                                product -> {
                                    Session.addToCart(product, 1);
                                    navBar.updateCartBadge(Session.getCartCount());
                                    Toast.success(MarketPlace.this,
                                        "<b>" + product.getTitle() + "</b> ajouté au panier !");
                                },
                                product -> AppNavigator.show(new ProductDetail(currentUser, product))
                            );
                            productsGrid.add(card);
                        }
                        statusLabel.setText(products.size() + " produit(s) disponible(s)");
                    }
                } catch (Exception ex) {
                    productsGrid.add(buildEmptyPanel(
                        "Impossible de charger les produits.",
                        "Vérifiez que le serveur est démarré, puis réessayez.", "⚠"));
                    statusLabel.setText("Erreur de chargement");
                }
                productsGrid.revalidate();
                productsGrid.repaint();
            }
        };
        worker.execute();
    }

    private void loadProducts(String keyword) {
        statusLabel.setText("Recherche...");
        productsGrid.removeAll();
        productsGrid.add(buildLoadingPanel("Recherche en cours..."));
        productsGrid.revalidate();

        SwingWorker<List<Product>, Void> worker = new SwingWorker<List<Product>, Void>() {
            @Override
            protected List<Product> doInBackground() throws Exception {
                if (productService == null) throw new Exception("Non connecté au serveur.");
                if (keyword != null && !keyword.isEmpty()) {
                    return productService.searchProductRecordsByName(keyword);
                }
                return productService.findAvailableProductRecords();
            }
            @Override
            protected void done() {
                productsGrid.removeAll();
                try {
                    List<Product> products = get();
                    if (products == null || products.isEmpty()) {
                        productsGrid.add(buildEmptyPanel(
                            "Aucun résultat pour « " + keyword + " »",
                            "Essayez un autre mot-clé ou parcourez les catégories.", "🔍"));
                    } else {
                        for (Product p : products) {
                            ProductCard card = new ProductCard(p,
                                product -> {
                                    Session.addToCart(product, 1);
                                    navBar.updateCartBadge(Session.getCartCount());
                                    Toast.success(MarketPlace.this,
                                        "<b>" + product.getTitle() + "</b> ajouté au panier !");
                                },
                                product -> AppNavigator.show(new ProductDetail(currentUser, product))
                            );
                            productsGrid.add(card);
                        }
                        statusLabel.setText(products.size() + " produit(s) trouvé(s)");
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Erreur : " + ErrorUtil.rootMessage(ex));
                }
                productsGrid.revalidate();
                productsGrid.repaint();
            }
        };
        worker.execute();
    }

    private void buildUI() {
        setTitle("ShopSphere — Catalogue");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(900, 600));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.NEUTRAL);

        // ── NavBar persistante ────────────────────────────────────────────
        navBar = new NavBar(
            currentUser.getFullName(),
            currentUser,
            query -> loadProducts(query),
            () -> AppNavigator.show(new CartView(currentUser)),
            () -> AppNavigator.show(new UserProfile(currentUser))
        );
        add(navBar, BorderLayout.NORTH);

        // ── Corps principal ───────────────────────────────────────────────
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(Theme.NEUTRAL);

        body.add(buildSidebar(), BorderLayout.WEST);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Theme.NEUTRAL);

        // Bandeau titre + statut
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(Theme.WHITE);
        titleBar.setBorder(new EmptyBorder(14, 20, 14, 20));

        JPanel titleLeft = new JPanel();
        titleLeft.setOpaque(false);
        titleLeft.setLayout(new BoxLayout(titleLeft, BoxLayout.Y_AXIS));

        JLabel catLbl = new JLabel("Tous les produits");
        catLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        catLbl.setForeground(Theme.DARK_TEXT);

        String firstName = currentUser.getFullName() != null && currentUser.getFullName().contains(" ")
            ? currentUser.getFullName().split(" ")[0]
            : currentUser.getFullName();
        JLabel welcomeLbl = new JLabel("Bonjour, " + firstName + " ! Découvrez nos nouveautés.");
        welcomeLbl.setFont(Theme.FONT_SMALL);
        welcomeLbl.setForeground(Theme.GREY_TEXT);

        titleLeft.add(catLbl);
        titleLeft.add(welcomeLbl);
        titleBar.add(titleLeft, BorderLayout.WEST);

        statusLabel = new JLabel("");
        statusLabel.setFont(Theme.FONT_SMALL);
        statusLabel.setForeground(Theme.GREY_TEXT);
        titleBar.add(statusLabel, BorderLayout.EAST);
        center.add(titleBar, BorderLayout.NORTH);

        // Grille produits 6 par ligne
        productsGrid = new JPanel(new GridLayout(0, 6, 12, 12));
        productsGrid.setBackground(Theme.NEUTRAL);

        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setBackground(Theme.NEUTRAL);
        gridWrapper.setBorder(new EmptyBorder(16, 16, 16, 16));
        gridWrapper.add(productsGrid, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(gridWrapper);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.setBackground(Theme.NEUTRAL);
        scroll.getViewport().setBackground(Theme.NEUTRAL);
        center.add(scroll, BorderLayout.CENTER);

        if (currentUser.isSeller()) {
            center.add(buildSellerBar(), BorderLayout.SOUTH);
        }

        body.add(center, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Theme.WHITE);
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBorder(new EmptyBorder(16, 12, 16, 8));

        JLabel catTitle = new JLabel("  Catégories");
        catTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        catTitle.setForeground(Theme.PRIMARY);
        catTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        sidebar.add(catTitle);
        sidebar.add(Box.createVerticalStrut(8));

        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.LIGHT_GREY);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(8));

        String[][] categories = {
            {"Tous",          "🏪"},
            {"Electronique",  "📱"},
            {"Mode",          "👗"},
            {"Maison",        "🏠"},
            {"Sport",         "⚽"},
            {"Beaute",        "💄"},
            {"Alimentation",  "🥗"},
            {"Livres",        "📚"},
            {"Jouets",        "🎮"},
            {"Automobiles",   "🚗"},
            {"Sante",         "💊"}
        };

        final JLabel[] selected = {null};

        for (String[] cat : categories) {
            JLabel btn = new JLabel(cat[1] + "  " + cat[0]);
            btn.setFont(Theme.FONT_BODY);
            btn.setForeground(Theme.DARK_TEXT);
            btn.setOpaque(true);
            btn.setBackground(Theme.WHITE);
            btn.setBorder(new EmptyBorder(8, 12, 8, 8));
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (btn != selected[0]) {
                        btn.setBackground(Theme.NEUTRAL);
                        btn.setForeground(Theme.PRIMARY);
                    }
                }
                @Override public void mouseExited(java.awt.event.MouseEvent e) {
                    if (btn != selected[0]) {
                        btn.setBackground(Theme.WHITE);
                        btn.setForeground(Theme.DARK_TEXT);
                    }
                }
                @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (selected[0] != null) {
                        selected[0].setBackground(Theme.WHITE);
                        selected[0].setForeground(Theme.DARK_TEXT);
                        selected[0].setFont(Theme.FONT_BODY);
                    }
                    btn.setBackground(new Color(0xE8, 0xF0, 0xFB));
                    btn.setForeground(Theme.PRIMARY);
                    btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    selected[0] = btn;
                    filterByCategory(cat[0]);
                }
            });
            sidebar.add(btn);
        }
        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JPanel buildSellerBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, Theme.PRIMARY, getWidth(), 0, new Color(0x23, 0x52, 0x7A));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        AccentButton addProductBtn = new AccentButton("+ Ajouter un produit", 190, Theme.BTN_H);
        addProductBtn.addActionListener(e -> AppNavigator.show(
                new AddEditProduct(currentUser, null,
                        () -> AppNavigator.show(new MarketPlace(currentUser)),
                        () -> AppNavigator.show(new MarketPlace(currentUser)))));
        JLabel sellerLbl = new JLabel("Tableau de bord vendeur >");
        sellerLbl.setFont(Theme.FONT_BODY);
        sellerLbl.setForeground(Theme.WHITE);
        sellerLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sellerLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { AppNavigator.show(new SellerDashboard(currentUser)); }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { sellerLbl.setForeground(Theme.ACCENT); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { sellerLbl.setForeground(Theme.WHITE); }
        });
        bar.add(sellerLbl);
        bar.add(addProductBtn);
        return bar;
    }

    private void filterByCategory(String cat) {
        if ("Tous".equals(cat)) { connectAndLoadProducts(); return; }
        try {
            String enumName = cat.toUpperCase()
                .replace("É", "E").replace("È", "E").replace("Ê", "E")
                .replace("À", "A").replace("Â", "A")
                .replace("Î", "I").replace("Ô", "O").replace("Û", "U")
                .replace(" ", "_");
            Product.Category c = Product.Category.valueOf(enumName);

            statusLabel.setText("Filtrage : " + cat + "...");
            productsGrid.removeAll();
            productsGrid.add(buildLoadingPanel("Filtrage : " + cat + "..."));
            productsGrid.revalidate();

            SwingWorker<List<Product>, Void> w = new SwingWorker<List<Product>, Void>() {
                @Override
                protected List<Product> doInBackground() throws Exception {
                    if (productService == null) throw new Exception("Non connecte.");
                    return productService.findProductRecordsByCategory(c);
                }
                @Override
                protected void done() {
                    productsGrid.removeAll();
                    try {
                        List<Product> products = get();
                        if (products == null || products.isEmpty()) {
                            productsGrid.add(buildEmptyPanel(
                                "Aucun produit dans « " + cat + " »",
                                "Revenez bientôt, cette catégorie s'enrichit !", "📦"));
                        } else {
                            for (Product p : products) {
                                productsGrid.add(new ProductCard(p,
                                    product -> {
                                        Session.addToCart(product, 1);
                                        navBar.updateCartBadge(Session.getCartCount());
                                        Toast.success(MarketPlace.this,
                                            "<b>" + product.getTitle() + "</b> ajouté au panier !");
                                    },
                                    product -> AppNavigator.show(new ProductDetail(currentUser, product))
                                ));
                            }
                        }
                        statusLabel.setText(products != null ? products.size() + " produit(s) — " + cat : "0 produit(s)");
                    } catch (Exception ex) {
                        statusLabel.setText("Erreur : " + ErrorUtil.rootMessage(ex));
                    }
                    productsGrid.revalidate();
                    productsGrid.repaint();
                }
            };
            w.execute();
        } catch (IllegalArgumentException e) {
            connectAndLoadProducts();
        }
    }

    // ── Panneau de chargement ─────────────────────────────────────────────────
    private JPanel buildLoadingPanel(String message) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Theme.NEUTRAL);
        p.setPreferredSize(new Dimension(600, 300));
        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        JLabel iconLbl = new JLabel("⏳");
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel msgLbl = new JLabel(message);
        msgLbl.setFont(Theme.FONT_BODY);
        msgLbl.setForeground(Theme.GREY_TEXT);
        msgLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(iconLbl);
        inner.add(Box.createVerticalStrut(10));
        inner.add(msgLbl);
        p.add(inner);
        return p;
    }

    // ── Panneau vide / erreur ─────────────────────────────────────────────────
    private JPanel buildEmptyPanel(String title, String subtitle, String icon) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Theme.NEUTRAL);
        p.setPreferredSize(new Dimension(600, 300));
        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLbl.setForeground(Theme.DARK_TEXT);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(Theme.FONT_BODY);
        subLbl.setForeground(Theme.GREY_TEXT);
        subLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(iconLbl);
        inner.add(Box.createVerticalStrut(12));
        inner.add(titleLbl);
        inner.add(Box.createVerticalStrut(4));
        inner.add(subLbl);
        p.add(inner);
        return p;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
