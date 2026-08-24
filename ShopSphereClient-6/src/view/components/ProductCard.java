/*
 * ShopSphere - ProductCard v3.0
 * Design premium : grande image cover, gradient overlay, badge promo stylé,
 * section info épurée, animation hover fluide.
 */
package view.components;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Product;
import view.theme.Theme;

/**
 * Carte produit e-commerce premium v3 — grande image, design épuré.
 * @author ShopSphere
 */
public class ProductCard extends JPanel {

    private static final int IMG_H   = 200;  // hauteur image — bien plus grande
    private static final int CARD_W  = 220;
    private static final int RADIUS  = 16;

    private final Product product;
    private float hoverProgress = 0f;
    private Timer hoverTimer;

    // Cache image pour éviter de recharger à chaque repaint
    private BufferedImage cachedImage = null;
    private boolean imageLoaded = false;

    public ProductCard(Product product, Consumer<Product> onAddToCart,
                       Consumer<Product> onDetails) {
        this.product = product;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(CARD_W, CARD_W + 160));
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Charger l'image en arrière-plan pour ne pas bloquer l'EDT
        loadImageAsync();

        // ── Zone image avec overlay gradient ──────────────────────────────
        JPanel imgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,       RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,  RenderingHints.VALUE_INTERPOLATION_BICUBIC);

                int w = getWidth(), h = getHeight();

                // ── Fond couleur catégorie ───────────────────────────────
                Color base = getCategoryColor(product.getCategory());
                GradientPaint bg = new GradientPaint(0, 0, base.brighter(),
                        w, h, base.darker());
                g2.setPaint(bg);
                g2.fillRoundRect(0, 0, w, h, RADIUS, RADIUS);

                // ── Décoration géométrique subtile (seulement si pas d'image) ──
                if (cachedImage == null) {
                    g2.setColor(new Color(255, 255, 255, 25));
                    g2.fillOval(w - 80, -30, 130, 130);
                    g2.fillOval(-30, h - 60, 100, 100);

                    // Icône centrale grande
                    String icon = getCategoryIcon(product.getCategory());
                    g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
                    FontMetrics fm = g2.getFontMetrics();
                    int iw = fm.stringWidth(icon);
                    int ih = fm.getAscent();
                    // Légère ombre portée de l'emoji
                    g2.setColor(new Color(0, 0, 0, 40));
                    g2.drawString(icon, (w - iw) / 2 + 2, (h + ih) / 2 - ih / 4 + 2);
                    g2.setColor(new Color(0, 0, 0, 180));
                    g2.drawString(icon, (w - iw) / 2, (h + ih) / 2 - ih / 4);
                }

                // ── Image produit (cover) ────────────────────────────────
                if (cachedImage != null) {
                    // Calcul cover : redimensionner pour remplir sans déformer
                    double scaleX = (double) w / cachedImage.getWidth();
                    double scaleY = (double) h / cachedImage.getHeight();
                    double scale  = Math.max(scaleX, scaleY);
                    int dw = (int)(cachedImage.getWidth()  * scale);
                    int dh = (int)(cachedImage.getHeight() * scale);
                    int dx = (w - dw) / 2;
                    int dy = (h - dh) / 2;

                    Shape clip = new RoundRectangle2D.Float(0, 0, w, h, RADIUS, RADIUS);
                    g2.setClip(clip);
                    g2.drawImage(cachedImage, dx, dy, dw, dh, null);
                    g2.setClip(null);
                }

                // ── Gradient overlay bas (scrim) pour lisibilité ─────────
                GradientPaint scrim = new GradientPaint(
                        0, h - 60, new Color(0, 0, 0, 0),
                        0, h,      new Color(0, 0, 0, 120));
                g2.setPaint(scrim);
                g2.fillRoundRect(0, 0, w, h, RADIUS, RADIUS);

                // ── Badge PROMO ──────────────────────────────────────────
                if (product.isOnSale() && product.getBasePrice() > 0) {
                    int pct = (int)((1 - product.getSalePrice() / product.getBasePrice()) * 100);
                    String badge = "-" + pct + "%";
                    int bw = 52, bh = 26;
                    g2.setColor(new Color(0xDC, 0x26, 0x26));
                    g2.fillRoundRect(10, 10, bw, bh, 10, 10);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    g2.setColor(Color.WHITE);
                    FontMetrics bfm = g2.getFontMetrics();
                    g2.drawString(badge,
                            10 + (bw - bfm.stringWidth(badge)) / 2,
                            10 + bh / 2 + bfm.getAscent() / 2 - 2);
                }

                // ── Overlay rupture de stock ─────────────────────────────
                if (!product.isInStock()) {
                    g2.setColor(new Color(0, 0, 0, 130));
                    g2.fillRoundRect(0, 0, w, h, RADIUS, RADIUS);
                    String oos = "Rupture de stock";
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    g2.setColor(Color.WHITE);
                    FontMetrics fm2 = g2.getFontMetrics();
                    g2.drawString(oos, (w - fm2.stringWidth(oos)) / 2, h / 2 + 6);
                }

                // ── Hover highlight ──────────────────────────────────────
                if (hoverProgress > 0) {
                    g2.setColor(new Color(255, 255, 255, (int)(hoverProgress * 25)));
                    g2.fillRoundRect(0, 0, w, h, RADIUS, RADIUS);
                }

                g2.dispose();
            }
        };
        imgPanel.setPreferredSize(new Dimension(CARD_W, IMG_H));
        imgPanel.setOpaque(false);
        imgPanel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (onDetails != null) onDetails.accept(product);
            }
        });

        // ── Zone info ─────────────────────────────────────────────────────
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.setBorder(new EmptyBorder(12, 14, 14, 14));

        // Catégorie pill
        if (product.getCategory() != null) {
            JLabel catLbl = new JLabel(product.getCategory().name());
            catLbl.setFont(new Font("Segoe UI", Font.BOLD, 9));
            catLbl.setForeground(Theme.PRIMARY);
            catLbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0x1A, 0x3C, 0x5E, 60), 1, true),
                    BorderFactory.createEmptyBorder(2, 8, 2, 8)));
            catLbl.setOpaque(true);
            catLbl.setBackground(new Color(0x1A, 0x3C, 0x5E, 15));
            catLbl.setAlignmentX(LEFT_ALIGNMENT);
            info.add(catLbl);
            info.add(Box.createVerticalStrut(7));
        }

        // Titre produit
        String titleStr = product.getTitle();
        if (titleStr != null && titleStr.length() > 30) titleStr = titleStr.substring(0, 27) + "…";
        JLabel titleLbl = new JLabel("<html><b>" + titleStr + "</b></html>");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(Theme.DARK_TEXT);
        titleLbl.setAlignmentX(LEFT_ALIGNMENT);
        info.add(titleLbl);
        info.add(Box.createVerticalStrut(3));

        // Vendeur
        if (product.getSeller() != null) {
            JLabel sellerLbl = new JLabel("par " + product.getSeller().getFullName());
            sellerLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            sellerLbl.setForeground(Theme.GREY_TEXT);
            sellerLbl.setAlignmentX(LEFT_ALIGNMENT);
            info.add(sellerLbl);
            info.add(Box.createVerticalStrut(8));
        }

        // Prix — plus grand et plus visible
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pricePanel.setOpaque(false);
        pricePanel.setAlignmentX(LEFT_ALIGNMENT);
        if (product.isOnSale()) {
            JLabel saleLbl = new JLabel(String.format("%.0f FCFA", product.getSalePrice()));
            saleLbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
            saleLbl.setForeground(new Color(0xDC, 0x26, 0x26));
            JLabel baseLbl = new JLabel("<html><strike>" +
                    String.format("%.0f", product.getBasePrice()) + "</strike></html>");
            baseLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            baseLbl.setForeground(Theme.GREY_TEXT);
            pricePanel.add(saleLbl);
            pricePanel.add(baseLbl);
        } else {
            JLabel priceLbl = new JLabel(String.format("%.0f FCFA", product.getBasePrice()));
            priceLbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
            priceLbl.setForeground(Theme.PRIMARY);
            pricePanel.add(priceLbl);
        }
        info.add(pricePanel);
        info.add(Box.createVerticalStrut(10));

        // Bouton panier full-width
        AccentButton cartBtn = new AccentButton(
                product.isInStock() ? "🛒  Ajouter au panier" : "Indisponible",
                CARD_W - 28, 36);
        cartBtn.setEnabled(product.isInStock());
        cartBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cartBtn.setAlignmentX(LEFT_ALIGNMENT);
        cartBtn.addActionListener(e -> { if (onAddToCart != null) onAddToCart.accept(product); });
        info.add(cartBtn);

        add(imgPanel, BorderLayout.NORTH);
        add(info,     BorderLayout.CENTER);

        // ── Hover animation ───────────────────────────────────────────────
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { animateHover(true);  }
            @Override public void mouseExited (MouseEvent e) { animateHover(false); }
            @Override public void mouseClicked(MouseEvent e) {
                if (onDetails != null) onDetails.accept(product);
            }
        });
    }

    /** Charge l'image du produit en arrière-plan (non-bloquant). */
    private void loadImageAsync() {
        if (product.getImageUrl() == null || product.getImageUrl().trim().isEmpty()) return;
        new Thread(() -> {
            try {
                String url = product.getImageUrl().trim();
                BufferedImage img;
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    img = javax.imageio.ImageIO.read(new java.net.URL(url));
                } else {
                    java.io.File f = new java.io.File(url);
                    img = f.exists() ? javax.imageio.ImageIO.read(f) : null;
                }
                if (img != null) {
                    cachedImage = img;
                    SwingUtilities.invokeLater(this::repaint);
                }
            } catch (Exception ignored) {}
        }).start();
    }

    private void animateHover(boolean enter) {
        if (hoverTimer != null) hoverTimer.stop();
        hoverTimer = new Timer(16, null);
        hoverTimer.addActionListener(e -> {
            hoverProgress = enter
                    ? Math.min(1f, hoverProgress + 0.10f)
                    : Math.max(0f, hoverProgress - 0.10f);
            repaint();
            if ((enter && hoverProgress >= 1f) || (!enter && hoverProgress <= 0f))
                ((Timer) e.getSource()).stop();
        });
        hoverTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        float elevate = hoverProgress;

        // Ombre portée dynamique multi-couches
        int shadowLayers = (int)(3 + elevate * 6);
        for (int i = shadowLayers; i > 0; i--) {
            int alpha = (int)((15 + elevate * 25) / i);
            g2.setColor(new Color(0x1A, 0x3C, 0x5E, Math.max(1, alpha)));
            g2.fillRoundRect(i, i + 3, w - i * 2, h - i, RADIUS + 2, RADIUS + 2);
        }

        // Fond blanc card
        g2.setColor(Theme.BG_CARD);
        g2.fill(new RoundRectangle2D.Float(0, 0, w - shadowLayers - 1,
                h - shadowLayers - 1, RADIUS, RADIUS));

        // Bordure hover ambre
        if (hoverProgress > 0.05f) {
            float alpha = Math.min(1f, hoverProgress * 2f);
            g2.setColor(new Color(Theme.ACCENT.getRed(), Theme.ACCENT.getGreen(),
                    Theme.ACCENT.getBlue(), (int)(alpha * 220)));
            g2.setStroke(new BasicStroke(2f));
            g2.draw(new RoundRectangle2D.Float(1f, 1f,
                    w - shadowLayers - 3f, h - shadowLayers - 3f,
                    RADIUS - 1, RADIUS - 1));
        }

        g2.dispose();
        super.paintComponent(g);
    }

    // ── Helpers couleur / icône ───────────────────────────────────────────
    private Color getCategoryColor(Product.Category cat) {
        if (cat == null) return new Color(0xC8, 0xD8, 0xE8);
        switch (cat) {
            case ELECTRONIQUE: return new Color(0x5B, 0xA3, 0xD9);
            case MODE:         return new Color(0xE8, 0x7A, 0xA8);
            case MAISON:       return new Color(0x60, 0xB8, 0x78);
            case SPORT:        return new Color(0xF0, 0xA0, 0x30);
            case BEAUTE:       return new Color(0xC8, 0x70, 0xD0);
            case ALIMENTATION: return new Color(0x78, 0xC8, 0x50);
            case LIVRES:       return new Color(0xE0, 0xB0, 0x40);
            case JOUETS:       return new Color(0xA0, 0x70, 0xE0);
            case AUTOMOBILES:  return new Color(0x50, 0x98, 0xD8);
            case SANTE:        return new Color(0x40, 0xC0, 0x90);
            default:           return new Color(0x90, 0xA8, 0xC0);
        }
    }

    private String getCategoryIcon(Product.Category cat) {
        if (cat == null) return "📦";
        switch (cat) {
            case ELECTRONIQUE: return "📱";
            case MODE:         return "👗";
            case MAISON:       return "🏠";
            case SPORT:        return "⚽";
            case BEAUTE:       return "💄";
            case ALIMENTATION: return "🥗";
            case LIVRES:       return "📚";
            case JOUETS:       return "🎮";
            case AUTOMOBILES:  return "🚗";
            case SANTE:        return "💊";
            default:           return "📦";
        }
    }
}
