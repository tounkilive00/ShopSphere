/*
 * ShopSphere - MarketPlace
 * Fenetre principale client — meme role qu'AgriConnect view/MarketPlace.java
 * Catalogue produits en grille style Amazon/Temu
 * NavBar persistante + recherche globale + panier + profil
 */
package view;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Product;
import model.User;
import service0.ProductService;
import view.components.*;
import view.theme.Theme;

public class MarketPlace extends JFrame {

    private final User currentUser;
    private NavBar     navBar;
    private JPanel     productsGrid;
    private JLabel     statusLabel;

    public MarketPlace(User user) {
        this.currentUser = user;
        initComponents();
        loadProducts(null);
    }

    private void initComponents() {
        setTitle("ShopSphere — Catalogue");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.NEUTRAL);

        // ── NavBar persistante ────────────────────────────────────────────
        navBar = new NavBar(
            currentUser.getFullName(),
            query -> loadProducts(query),       // Recherche globale
            () -> new CartView(currentUser).setVisible(true),
            () -> new UserProfile(currentUser).setVisible(true)
        );
        add(navBar, BorderLayout.NORTH);

        // ── Panneau principal ─────────────────────────────────────────────
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Theme.NEUTRAL);

        // Sidebar categories
        JPanel sidebar = buildSidebar();
        main.add(sidebar, BorderLayout.WEST);

        // Grille produits
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Theme.NEUTRAL);

        // Bandeau titre
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(Theme.NEUTRAL);
        titleBar.setBorder(new EmptyBorder(14, 20, 6, 20));

        JLabel catLbl = new JLabel("Tous les produits");
        catLbl.setFont(Theme.FONT_SUBTITLE);
        catLbl.setForeground(Theme.DARK_TEXT);
        titleBar.add(catLbl, BorderLayout.WEST);

        statusLabel = new JLabel("");
        statusLabel.setFont(Theme.FONT_SMALL);
        statusLabel.setForeground(Theme.GREY_TEXT);
        titleBar.add(statusLabel, BorderLayout.EAST);
        center.add(titleBar, BorderLayout.NORTH);

        // Grille scrollable
        productsGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 14));
        productsGrid.setBackground(Theme.NEUTRAL);
        JScrollPane scroll = new JScrollPane(productsGrid);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(Theme.NEUTRAL);
        center.add(scroll, BorderLayout.CENTER);
        main.add(center, BorderLayout.CENTER);

        // Barre vendeur (si SELLER ou ADMIN)
        if (currentUser.isSeller()) {
            JPanel sellerBar = buildSellerBar();
            main.add(sellerBar, BorderLayout.SOUTH);
        }

        add(main, BorderLayout.CENTER);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Theme.WHITE);
        sidebar.setPreferredSize(new Dimension(Theme.SIDEBAR_W, 0));
        sidebar.setBorder(new EmptyBorder(16, 12, 16, 12));

        JLabel catTitle = new JLabel("Categories");
        catTitle.setFont(Theme.FONT_HEADING);
        catTitle.setForeground(Theme.PRIMARY);
        sidebar.add(catTitle);
        sidebar.add(Box.createVerticalStrut(10));

        String[] categories = {"Tous", "Electronique", "Mode", "Maison",
                               "Sport", "Beaute", "Alimentation", "Livres", "Jouets"};
        for (String cat : categories) {
            JButton btn = new JButton(getCatIcon(cat) + "  " + cat);
            btn.setFont(Theme.FONT_BODY);
            btn.setForeground(Theme.DARK_TEXT);
            btn.setBackground(Theme.WHITE);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
            btn.addActionListener(e -> filterByCategory(cat));
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(Theme.NEUTRAL); }
                @Override public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(Theme.WHITE); }
            });
            sidebar.add(btn);
        }
        return sidebar;
    }

    private JPanel buildSellerBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        bar.setBackground(Theme.PRIMARY);
        AccentButton addProductBtn = new AccentButton("+ Ajouter un produit", 180, Theme.BTN_H);
        addProductBtn.addActionListener(e -> new AddEditProduct(currentUser, null,
                () -> loadProducts(null)).setVisible(true));
        JLabel sellerLbl = new JLabel("Tableau de bord vendeur >");
        sellerLbl.setFont(Theme.FONT_BODY);
        sellerLbl.setForeground(Theme.WHITE);
        sellerLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sellerLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                new SellerDashboard(currentUser).setVisible(true);
            }
        });
        bar.add(sellerLbl);
        bar.add(addProductBtn);
        return bar;
    }

    private void loadProducts(String keyword) {
        statusLabel.setText("Chargement...");
        productsGrid.removeAll();
        productsGrid.add(new JLabel("Chargement des produits..."));
        productsGrid.revalidate();

        SwingWorker<List<Product>, Void> worker = new SwingWorker<>() {
            @Override protected List<Product> doInBackground() throws Exception {
                ProductService ps = RMIClient.getProductService();
                if (keyword != null && !keyword.isEmpty()) {
                    return ps.searchProductRecordsByName(keyword);
                }
                return ps.findAvailableProductRecords();
            }
            @Override protected void done() {
                try {
                    List<Product> products = get();
                    productsGrid.removeAll();
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
                                    JOptionPane.showMessageDialog(MarketPlace.this,
                                        product.getTitle() + " ajoute au panier !",
                                        "Panier", JOptionPane.INFORMATION_MESSAGE);
                                },
                                product -> new ProductDetail(currentUser, product).setVisible(true)
                            );
                            productsGrid.add(card);
                        }
                        statusLabel.setText(products.size() + " produit(s) trouve(s)");
                    }
                    productsGrid.revalidate();
                    productsGrid.repaint();
                } catch (Exception ex) {
                    statusLabel.setText("Erreur chargement : " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void filterByCategory(String cat) {
        if ("Tous".equals(cat)) { loadProducts(null); return; }
        try {
            Product.Category c = Product.Category.valueOf(cat.toUpperCase()
                    .replace(" ", "_").replace("É","E").replace("Â","A").replace("Î","I"));
            SwingWorker<List<Product>, Void> w = new SwingWorker<>() {
                @Override protected List<Product> doInBackground() throws Exception {
                    return RMIClient.getProductService().findProductRecordsByCategory(c);
                }
                @Override protected void done() {
                    try {
                        List<Product> products = get();
                        productsGrid.removeAll();
                        for (Product p : products) {
                            productsGrid.add(new ProductCard(p,
                                product -> { Session.addToCart(product, 1); navBar.updateCartBadge(Session.getCartCount()); },
                                product -> new ProductDetail(currentUser, product).setVisible(true)
                            ));
                        }
                        statusLabel.setText(products.size() + " produit(s) — " + cat);
                        productsGrid.revalidate(); productsGrid.repaint();
                    } catch (Exception ex) { statusLabel.setText("Erreur : " + ex.getMessage()); }
                }
            };
            w.execute();
        } catch (IllegalArgumentException e) { loadProducts(null); }
    }

    private String getCatIcon(String cat) {
        switch (cat) {
            case "Electronique": return "📱"; case "Mode":       return "👗";
            case "Maison":       return "🏠"; case "Sport":      return "⚽";
            case "Beaute":       return "💄"; case "Alimentation":return "🛒";
            case "Livres":       return "📚"; case "Jouets":     return "🎮";
            default:             return "🏪";
        }
    }
}
