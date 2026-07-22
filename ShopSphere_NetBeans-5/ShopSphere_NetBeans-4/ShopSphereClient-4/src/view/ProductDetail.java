/*
 * ShopSphere - ProductDetail
 * Page detail produit — image, description, prix, stock, ajouter au panier
 * Meme role qu'AgriConnect view/Product.java
 */
package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Product;
import model.User;
import view.components.*;
import view.theme.Theme;

public class ProductDetail extends JFrame {

    public ProductDetail(User user, Product product) {
        initComponents();
        setTitle("ShopSphere — " + product.getTitle());
        setSize(680, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.NEUTRAL);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.PRIMARY);
        header.setPreferredSize(new Dimension(0, 55));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));
        JLabel titleLbl = new JLabel(product.getTitle());
        titleLbl.setFont(Theme.FONT_SUBTITLE);
        titleLbl.setForeground(Theme.WHITE);
        header.add(titleLbl, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Corps
        JPanel body = new JPanel(new GridLayout(1, 2, 20, 0));
        body.setBackground(Theme.NEUTRAL);
        body.setBorder(new EmptyBorder(20, 20, 20, 20));

        // ── Colonne gauche : image ───────────────────────────────────────
        JPanel imgPanel = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.NEUTRAL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                String icon = "📦";
                if (product.getCategory() != null) switch (product.getCategory()) {
                    case ELECTRONIQUE: icon = "📱"; break; case MODE: icon = "👗"; break;
                    case MAISON: icon = "🏠"; break; case SPORT: icon = "⚽"; break;
                    default: icon = "📦";
                }
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(icon, (getWidth() - fm.stringWidth(icon)) / 2,
                        getHeight() / 2 + 25);
                if (product.isOnSale()) {
                    g2.setColor(Theme.ERROR);
                    g2.fillRoundRect(10, 10, 80, 26, 8, 8);
                    g2.setFont(Theme.FONT_HEADING);
                    g2.setColor(Theme.WHITE);
                    g2.drawString("PROMO", 18, 28);
                }
                g2.dispose();
            }
        };

        // ── Colonne droite : infos ───────────────────────────────────────
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Theme.WHITE);
        info.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Categorie
        JLabel catLbl = new JLabel(product.getCategory() != null ? product.getCategory().name() : "");
        catLbl.setFont(Theme.FONT_SMALL);
        catLbl.setForeground(Theme.GREY_TEXT);
        info.add(catLbl);
        info.add(Box.createVerticalStrut(6));

        // Marque
        if (product.getBrand() != null) {
            JLabel brandLbl = new JLabel("Marque : " + product.getBrand());
            brandLbl.setFont(Theme.FONT_BODY);
            brandLbl.setForeground(Theme.GREY_TEXT);
            info.add(brandLbl);
            info.add(Box.createVerticalStrut(4));
        }

        // Vendeur
        if (product.getSeller() != null) {
            JLabel sellerLbl = new JLabel("Vendu par : " + product.getSeller().getFullName());
            sellerLbl.setFont(Theme.FONT_BODY);
            sellerLbl.setForeground(Theme.GREY_TEXT);
            info.add(sellerLbl);
            info.add(Box.createVerticalStrut(10));
        }

        // Prix
        if (product.isOnSale()) {
            JLabel sale = new JLabel(String.format("%.2f EUR", product.getSalePrice()));
            sale.setFont(new Font("Arial", Font.BOLD, 26));
            sale.setForeground(Theme.ERROR);
            info.add(sale);
            JLabel orig = new JLabel(String.format("Prix normal : %.2f EUR", product.getBasePrice()));
            orig.setFont(Theme.FONT_SMALL);
            orig.setForeground(Theme.GREY_TEXT);
            info.add(orig);
        } else {
            JLabel price = new JLabel(String.format("%.2f EUR", product.getBasePrice()));
            price.setFont(new Font("Arial", Font.BOLD, 26));
            price.setForeground(Theme.PRIMARY);
            info.add(price);
        }
        info.add(Box.createVerticalStrut(10));

        // Stock
        JLabel stockLbl = new JLabel(product.isInStock()
                ? "✓ En stock (" + product.getStockQty() + " disponibles)"
                : "✗ Rupture de stock");
        stockLbl.setFont(Theme.FONT_BODY);
        stockLbl.setForeground(product.isInStock() ? Theme.SUCCESS : Theme.ERROR);
        info.add(stockLbl);
        info.add(Box.createVerticalStrut(12));

        // Description
        JTextArea desc = new JTextArea(product.getDescription() != null
                ? product.getDescription() : "Aucune description disponible.");
        desc.setFont(Theme.FONT_BODY);
        desc.setForeground(Theme.DARK_TEXT);
        desc.setBackground(Theme.WHITE);
        desc.setEditable(false);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setRows(4);
        info.add(new JScrollPane(desc));
        info.add(Box.createVerticalStrut(16));

        // Quantite
        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        qtyPanel.setBackground(Theme.WHITE);
        qtyPanel.add(new JLabel("Quantite :"));
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, product.getStockQty(), 1));
        qtySpinner.setFont(Theme.FONT_BODY);
        qtyPanel.add(qtySpinner);
        info.add(qtyPanel);
        info.add(Box.createVerticalStrut(10));

        // Boutons
        AccentButton cartBtn = new AccentButton("🛒 Ajouter au panier");
        cartBtn.setEnabled(product.isInStock());
        cartBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.BTN_H));
        cartBtn.addActionListener(e -> {
            int qty = (int) qtySpinner.getValue();
            Session.addToCart(product, qty);
            JOptionPane.showMessageDialog(this,
                    qty + " x " + product.getTitle() + " ajoute au panier !",
                    "Panier", JOptionPane.INFORMATION_MESSAGE);
        });
        info.add(cartBtn);

        body.add(imgPanel);
        body.add(info);
        add(body, BorderLayout.CENTER);
        setVisible(true);
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
