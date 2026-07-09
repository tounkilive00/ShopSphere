/*
 * ShopSphere - AddEditProduct
 * Meme structure qu'AgriConnect view/AddEditProduct.java
 * Formulaire creation/modification produit pour SELLER et ADMIN
 */
package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Product;
import model.Product.Category;
import model.User;
import service0.ProductService;
import view.components.*;
import view.theme.Theme;

public class AddEditProduct extends JFrame {

    private final User     currentUser;
    private final Product  editProduct; // null = nouveau produit
    private final Runnable onSave;

    private AppTextField    titleField;
    private AppTextField    brandField;
    private AppTextField    skuField;
    private AppTextField    priceField;
    private AppTextField    salePriceField;
    private AppTextField    stockField;
    private AppComboBox<Category> categoryBox;
    private JTextArea       descArea;
    private PrimaryButton   saveBtn;
    private JLabel          statusLabel;

    public AddEditProduct(User user, Product product, Runnable onSave) {
        this.currentUser = user;
        this.editProduct = product;
        this.onSave      = onSave;
        initComponents();
        if (product != null) prefillForm(product);
    }

    private void initComponents() {
        setTitle(editProduct == null ? "ShopSphere — Nouveau produit" : "ShopSphere — Modifier produit");
        setSize(500, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.NEUTRAL);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(Theme.PRIMARY);
        header.setPreferredSize(new Dimension(0, 70));
        JLabel titre = new JLabel(editProduct == null ? "Nouveau produit" : "Modifier le produit");
        titre.setFont(Theme.FONT_TITLE);
        titre.setForeground(Theme.WHITE);
        header.add(titre);
        add(header, BorderLayout.NORTH);

        // Formulaire
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Theme.NEUTRAL);
        form.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Titre
        form.add(lbl("Titre du produit *")); titleField = field("Ex: Robe d'ete fleurie");
        form.add(titleField); sp(form);

        // Marque
        form.add(lbl("Marque")); brandField = field("Ex: MarieStyle");
        form.add(brandField); sp(form);

        // SKU
        form.add(lbl("Reference (SKU)")); skuField = field("Ex: MS-ROBE-001");
        form.add(skuField); sp(form);

        // Categorie
        form.add(lbl("Categorie *"));
        categoryBox = new AppComboBox<>(Category.values());
        categoryBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        form.add(categoryBox); sp(form);

        // Prix
        JPanel priceRow = new JPanel(new GridLayout(1, 2, 10, 0));
        priceRow.setBackground(Theme.NEUTRAL);
        JPanel p1 = new JPanel(); p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS)); p1.setBackground(Theme.NEUTRAL);
        p1.add(lbl("Prix de base (EUR) *")); priceField = field("49.99"); p1.add(priceField);
        JPanel p2 = new JPanel(); p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS)); p2.setBackground(Theme.NEUTRAL);
        p2.add(lbl("Prix promo (EUR)")); salePriceField = field("0 = pas de promo"); p2.add(salePriceField);
        priceRow.add(p1); priceRow.add(p2);
        priceRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H + 24));
        form.add(priceRow); sp(form);

        // Stock
        form.add(lbl("Quantite en stock *")); stockField = field("25");
        form.add(stockField); sp(form);

        // Description
        form.add(lbl("Description"));
        descArea = new JTextArea(3, 20);
        descArea.setFont(Theme.FONT_BODY);
        descArea.setLineWrap(true); descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createLineBorder(Theme.LIGHT_GREY));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        form.add(descScroll); sp(form);

        // Statut
        statusLabel = new JLabel("");
        statusLabel.setFont(Theme.FONT_BODY);
        statusLabel.setForeground(Theme.ERROR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setVisible(false);
        form.add(statusLabel); sp(form);

        // Bouton
        saveBtn = new PrimaryButton(editProduct == null ? "Publier le produit" : "Enregistrer les modifications");
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.BTN_H));
        saveBtn.addActionListener(e -> saveProduct());
        form.add(saveBtn);

        add(new JScrollPane(form), BorderLayout.CENTER);
        setVisible(true);
    }

    private AppTextField field(String placeholder) {
        AppTextField f = new AppTextField(placeholder);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        return f;
    }
    private JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_HEADING);
        l.setForeground(Theme.DARK_TEXT);
        return l;
    }
    private void sp(JPanel p) { p.add(Box.createVerticalStrut(10)); }

    private void prefillForm(Product p) {
        titleField.setText(p.getTitle());
        if (p.getBrand()       != null) brandField.setText(p.getBrand());
        if (p.getSku()         != null) skuField.setText(p.getSku());
        if (p.getCategory()    != null) categoryBox.setSelectedItem(p.getCategory());
        priceField.setText(String.valueOf(p.getBasePrice()));
        salePriceField.setText(String.valueOf(p.getSalePrice()));
        stockField.setText(String.valueOf(p.getStockQty()));
        if (p.getDescription() != null) descArea.setText(p.getDescription());
    }

    private void saveProduct() {
        String title = titleField.getText().trim();
        String priceStr = priceField.getText().trim();
        String stockStr = stockField.getText().trim();

        if (title.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            showError("Veuillez remplir les champs obligatoires (*)."); return;
        }

        double price; int stock;
        try { price = Double.parseDouble(priceStr); } catch (NumberFormatException ex) { showError("Prix invalide."); return; }
        try { stock = Integer.parseInt(stockStr); }  catch (NumberFormatException ex) { showError("Stock invalide."); return; }

        double salePrice = 0;
        try { salePrice = Double.parseDouble(salePriceField.getText().trim()); } catch (NumberFormatException ignored) {}

        final double fp = price, fsp = salePrice;
        final int fstock = stock;
        saveBtn.setEnabled(false); saveBtn.setText("Enregistrement...");

        SwingWorker<Product, Void> worker = new SwingWorker<>() {
            @Override protected Product doInBackground() throws Exception {
                ProductService ps = RMIClient.getProductService();
                Product p = editProduct != null ? editProduct : new Product();
                p.setTitle(title);
                p.setBrand(brandField.getText().trim());
                p.setSku(skuField.getText().trim());
                p.setCategory((Category) categoryBox.getSelectedItem());
                p.setBasePrice(fp); p.setSalePrice(fsp); p.setStockQty(fstock);
                p.setDescription(descArea.getText().trim());
                p.setSeller(currentUser);
                p.setStatus(Product.ProductStatus.ACTIF);
                return editProduct == null ? ps.createProductRecord(p) : ps.updateProductRecord(p);
            }
            @Override protected void done() {
                saveBtn.setEnabled(true);
                saveBtn.setText(editProduct == null ? "Publier le produit" : "Enregistrer les modifications");
                try {
                    get();
                    JOptionPane.showMessageDialog(AddEditProduct.this,
                        editProduct == null ? "Produit publie avec succes !" : "Produit mis a jour !",
                        "Succes", JOptionPane.INFORMATION_MESSAGE);
                    if (onSave != null) onSave.run();
                    dispose();
                } catch (Exception ex) {
                    showError("Erreur : " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void showError(String msg) { statusLabel.setText(msg); statusLabel.setVisible(true); }
}
