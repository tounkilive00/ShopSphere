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
        connectAndLoadProducts(); // Connexion + chargement asynchrones
    }

    // ── Connexion + chargement des produits en une seule passe ───────────────
    private void connectAndLoadProducts() {
        statusLabel.setText("Connexion au serveur...");
        productsGrid.removeAll();
        JLabel loadingLbl = new JLabel("Chargement des produits...");
        loadingLbl.setFont(Theme.FONT_BODY);
        loadingLbl.setForeground(Theme.GREY_TEXT);
        loadingLbl.setHorizontalAlignment(SwingConstants.CENTER);
        productsGrid.add(loadingLbl);
        productsGrid.revalidate();

        SwingWorker<List<Product>, Void> worker = new SwingWorker<List<Product>, Void>() {
            @Override
            protected List<Product> doInBackground() throws Exception {
                if (productService == null) {
                    int retries = 3;
                    for (int i = 0; i < retries; i++) {
                        try {
                            Registry reg = LocateRegistry.getRegistry("127.0.0.1", 4999);
                            productService = (ProductService) reg.lookup("ProductService");
                            break;
                        } catch (Exception e) {
                            if (i < retries - 1) Thread.sleep(800);
                        }
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
                        JLabel emptyLbl = new JLabel("Aucun produit disponible pour le moment.");
                        emptyLbl.setFont(Theme.FONT_BODY);
                        emptyLbl.setForeground(Theme.GREY_TEXT);
                        productsGrid.add(emptyLbl);
                        statusLabel.setText("0 produit(s)");
                    } else {
                        for (Product p : products) {
                            ProductCard card = new ProductCard(p,
                                product -> {
                                    Session.addToCart(product, 1);
                                    navBar.updateCartBadge(Session.getCartCount());
                                    JOptionPane.showMessageDialog(MarketPlace.this,
                                        "<html><b>" + product.getTitle() + "</b><br>ajoute au panier !</html>",
                                        "Panier", JOptionPane.INFORMATION_MESSAGE);
                                },
                                product -> new ProductDetail(currentUser, product).setVisible(true)
                            );
                            productsGrid.add(card);
                        }
                        statusLabel.setText(products.size() + " produit(s) disponible(s)");
                    }
                } catch (Exception ex) {
                    JLabel errLbl = new JLabel("Erreur : " + ErrorUtil.rootMessage(ex));
                    errLbl.setFont(Theme.FONT_BODY);
                    errLbl.setForeground(Theme.ERROR);
                    productsGrid.add(errLbl);
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
        JLabel loadingLbl = new JLabel("Recherche en cours...");
        loadingLbl.setFont(Theme.FONT_BODY);
        loadingLbl.setForeground(Theme.GREY_TEXT);
        productsGrid.add(loadingLbl);
        productsGrid.revalidate();

        SwingWorker<List<Product>, Void> worker = new SwingWorker<List<Product>, Void>() {
            @Override
            protected List<Product> doInBackground() throws Exception {
                if (productService == null) throw new Exception("Non connecte au serveur.");
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
                        JLabel empty = new JLabel("Aucun produit trouve.");
                        empty.setFont(Theme.FONT_BODY);
                        empty.setForeground(Theme.GREY_TEXT);
                        productsGrid.add(empty);
                    } else {
                        for (Product p : products) {
                            ProductCard card = new ProductCard(p,
                                product -> {
                                    Session.addToCart(product, 1);
                                    navBar.updateCartBadge(Session.getCartCount());
                                },
                                product -> new ProductDetail(currentUser, product).setVisible(true)
                            );
                            productsGrid.add(card);
                        }
                        statusLabel.setText(products.size() + " produit(s) trouve(s)");
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
        setSize(1140, 740);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.NEUTRAL);

        // ── NavBar persistante ────────────────────────────────────────────
        navBar = new NavBar(
            currentUser.getFullName(),
            query -> loadProducts(query),
            () -> new CartView(currentUser).setVisible(true),
            () -> new UserProfile(currentUser).setVisible(true)
        );
        add(navBar, BorderLayout.NORTH);

        // ── Corps principal ───────────────────────────────────────────────
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(Theme.NEUTRAL);

        // Sidebar
        body.add(buildSidebar(), BorderLayout.WEST);

        // Zone centrale
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Theme.NEUTRAL);

        // Bandeau titre + statut
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(Theme.WHITE);
        titleBar.setBorder(new EmptyBorder(12, 20, 12, 20));
        JLabel catLbl = new JLabel("Tous les produits");
        catLbl.setFont(new Font("SansSerif", Font.BOLD, 17));
        catLbl.setForeground(Theme.DARK_TEXT);
        titleBar.add(catLbl, BorderLayout.WEST);

        statusLabel = new JLabel("");
        statusLabel.setFont(Theme.FONT_SMALL);
        statusLabel.setForeground(Theme.GREY_TEXT);
        titleBar.add(statusLabel, BorderLayout.EAST);
        center.add(titleBar, BorderLayout.NORTH);

        // Grille produits scrollable
        productsGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 16, 16));
        productsGrid.setBackground(Theme.NEUTRAL);
        JScrollPane scroll = new JScrollPane(productsGrid);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.setBackground(Theme.NEUTRAL);
        scroll.getViewport().setBackground(Theme.NEUTRAL);
        center.add(scroll, BorderLayout.CENTER);

        // Barre vendeur (si SELLER ou ADMIN)
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
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(new EmptyBorder(16, 12, 16, 8));

        JLabel catTitle = new JLabel("  Categories");
        catTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        catTitle.setForeground(Theme.PRIMARY);
        catTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        sidebar.add(catTitle);
        sidebar.add(Box.createVerticalStrut(8));

        // Separateur
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
            {"Alimentation",  "🛒"},
            {"Livres",        "📚"},
            {"Jouets",        "🎮"},
            {"Automobiles",   "🚗"},
            {"Sante",         "💊"}
        };

        for (String[] cat : categories) {
            JLabel btn = new JLabel(cat[1] + "  " + cat[0]);
            btn.setFont(Theme.FONT_BODY);
            btn.setForeground(Theme.DARK_TEXT);
            btn.setOpaque(true);
            btn.setBackground(Theme.WHITE);
            btn.setBorder(new EmptyBorder(6, 10, 6, 8));
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                    btn.setBackground(Theme.NEUTRAL);
                    btn.setForeground(Theme.PRIMARY);
                }
                @Override public void mouseExited(java.awt.event.MouseEvent e) {
                    btn.setBackground(Theme.WHITE);
                    btn.setForeground(Theme.DARK_TEXT);
                }
                @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                    filterByCategory(cat[0]);
                }
            });
            sidebar.add(btn);
        }
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
        addProductBtn.addActionListener(e -> new AddEditProduct(currentUser, null, () -> connectAndLoadProducts()).setVisible(true));
        JLabel sellerLbl = new JLabel("Tableau de bord vendeur >");
        sellerLbl.setFont(Theme.FONT_BODY);
        sellerLbl.setForeground(Theme.WHITE);
        sellerLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sellerLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { new SellerDashboard(currentUser).setVisible(true); }
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
                            JLabel empty = new JLabel("Aucun produit dans la categorie : " + cat);
                            empty.setFont(Theme.FONT_BODY);
                            empty.setForeground(Theme.GREY_TEXT);
                            productsGrid.add(empty);
                        } else {
                            for (Product p : products) {
                                productsGrid.add(new ProductCard(p,
                                    product -> { Session.addToCart(product, 1); navBar.updateCartBadge(Session.getCartCount()); },
                                    product -> new ProductDetail(currentUser, product).setVisible(true)
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

    /**
     * WrapLayout — FlowLayout qui gere le retour a la ligne automatiquement.
     * Evite que les ProductCards debordent hors de la zone visible.
     */
    private static class WrapLayout extends FlowLayout {
        public WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            Dimension minimum = layoutSize(target, false);
            minimum.width -= (getHgap() + 1);
            return minimum;
        }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getSize().width;
                if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;
                int hgap = getHgap(), vgap = getVgap();
                Insets insets = target.getInsets();
                int maxWidth = targetWidth - (insets.left + insets.right + hgap * 2);
                int nmembers = target.getComponentCount();
                int x = 0, y = insets.top + vgap, rowHeight = 0;
                for (int i = 0; i < nmembers; i++) {
                    Component m = target.getComponent(i);
                    if (m.isVisible()) {
                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                        if (x == 0 || (x + d.width) <= maxWidth) {
                            if (x > 0) x += hgap;
                            x += d.width;
                            rowHeight = Math.max(rowHeight, d.height);
                        } else {
                            x = d.width;
                            y += vgap + rowHeight;
                            rowHeight = d.height;
                        }
                    }
                }
                y += rowHeight + insets.bottom + vgap;
                return new Dimension(targetWidth, y);
            }
        }
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
