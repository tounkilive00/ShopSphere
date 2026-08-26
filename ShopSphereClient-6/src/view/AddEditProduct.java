/*
 * ShopSphere - AddEditProduct
 * Formulaire création/modification produit pour SELLER et ADMIN
 */
package view;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Product;
import model.Product.Category;
import model.User;
import service0.ProductService;
import view.components.*;
import view.theme.Theme;

public class AddEditProduct extends JFrame {

    private ProductService productService;

    private final User     currentUser;
    private final Product  editProduct; // null = nouveau produit
    private final Runnable onSave;
    private final Runnable onCancel;

    private AppTextField    titleField;
    private AppTextField    brandField;
    private AppTextField    skuField;
    private AppTextField    priceField;
    private AppTextField    salePriceField;
    private AppTextField    stockField;
    private AppTextField    imageUrlField;
    private AppComboBox<Category> categoryBox;
    private JTextArea       descArea;
    private PrimaryButton   saveBtn;
    private JLabel          statusLabel;

    public AddEditProduct(User user, Product product, Runnable onSave) {
        this(user, product, onSave, null);
    }

    public AddEditProduct(User user, Product product, Runnable onSave, Runnable onCancel) {
        this.currentUser = user != null ? user : Session.getCurrentUser();
        this.editProduct = product;
        this.onSave      = onSave;
        this.onCancel    = onCancel;
        initComponents();
        connectToServer();
        buildUI();
        if (product != null) prefillForm(product);
    }

    private void connectToServer() {
        try {
            Registry reg = LocateRegistry.getRegistry("127.0.0.1", 4999);
            this.productService = (ProductService) reg.lookup("ProductService");
        } catch (Exception e) {
            System.err.println("Server connection failed: " + e.getMessage());
        }
    }

    private void buildUI() {
        setTitle(editProduct == null ? "ShopSphere — Nouveau produit" : "ShopSphere — Modifier produit");
        setSize(540, 720);
        setMinimumSize(new Dimension(480, 600));
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.NEUTRAL);
        setLayout(new BorderLayout());

        // Header
        PageHeader header = new PageHeader(
                editProduct == null ? "Nouveau produit" : "Modifier le produit");
        SecondaryButton cancelBtn = new SecondaryButton("Annuler");
        cancelBtn.addActionListener(e -> {
            if (onCancel != null) onCancel.run(); else dispose();
        });
        header.addAction(cancelBtn);
        add(header, BorderLayout.NORTH);

        // Formulaire
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Theme.NEUTRAL);
        form.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Titre
        form.add(lbl("Titre du produit *")); titleField = field("Ex: Robe d'été fleurie");
        form.add(titleField); sp(form);

        // Marque
        form.add(lbl("Marque")); brandField = field("Ex: MarieStyle");
        form.add(brandField); sp(form);

        // SKU
        form.add(lbl("Référence (SKU)")); skuField = field("Ex: MS-ROBE-001");
        form.add(skuField); sp(form);

        // Catégorie
        form.add(lbl("Catégorie *"));
        categoryBox = new AppComboBox<>(Category.values());
        categoryBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        form.add(categoryBox); sp(form);

        // Prix
        JPanel priceRow = new JPanel(new GridLayout(1, 2, 10, 0));
        priceRow.setBackground(Theme.NEUTRAL);
        JPanel p1 = new JPanel(); p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS)); p1.setBackground(Theme.NEUTRAL);
        p1.add(lbl("Prix de base (FCFA) *")); priceField = field("5000"); p1.add(priceField);
        JPanel p2 = new JPanel(); p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS)); p2.setBackground(Theme.NEUTRAL);
        p2.add(lbl("Prix promo (FCFA)")); salePriceField = field("0 = pas de promo"); p2.add(salePriceField);
        priceRow.add(p1); priceRow.add(p2);
        priceRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H + 24));
        form.add(priceRow); sp(form);

        // Stock
        form.add(lbl("Quantité en stock *")); stockField = field("25");
        form.add(stockField); sp(form);

        // Image URL / Photo du produit
        form.add(lbl("Image du produit (URL web ou photo locale)"));
        JPanel imgRow = new JPanel(new BorderLayout(8, 0));
        imgRow.setBackground(Theme.NEUTRAL);
        imageUrlField = field("Ex: https://... ou C:/images/photo.jpg");
        SecondaryButton browseBtn = new SecondaryButton("Parcourir...");
        browseBtn.setPreferredSize(new Dimension(110, Theme.FIELD_H));
        browseBtn.addActionListener(e -> {
            System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Sélectionner une photo de produit");
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "webp", "gif"));
            if (chooser.showOpenDialog(AddEditProduct.this) == JFileChooser.APPROVE_OPTION) {
                imageUrlField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        imgRow.add(imageUrlField, BorderLayout.CENTER);
        imgRow.add(browseBtn, BorderLayout.EAST);
        imgRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.FIELD_H));
        form.add(imgRow); sp(form);

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
        if (p.getImageUrl()    != null) imageUrlField.setText(p.getImageUrl());
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

        String brandRaw = brandField.getText().trim();
        final String brand = brandRaw.isEmpty() ? null : brandRaw;

        String skuRaw = skuField.getText().trim();
        final String sku = skuRaw.isEmpty() ? null : skuRaw;

        String imgUrlRaw = imageUrlField.getText().trim();
        final String imgUrl = imgUrlRaw.isEmpty() ? null : imgUrlRaw;

        String descRaw = descArea.getText().trim();
        final String desc = descRaw.isEmpty() ? null : descRaw;

        User sellerToUse = currentUser != null ? currentUser : Session.getCurrentUser();
        if (sellerToUse == null) {
            showError("Aucun utilisateur connecté pour la publication."); return;
        }

        saveBtn.setEnabled(false); saveBtn.setText("Enregistrement...");

        SwingWorker<Product, Void> worker = new SwingWorker<Product, Void>() {
            @Override protected Product doInBackground() throws Exception {
                if (productService == null) { connectToServer(); }
                if (productService == null) {
                    throw new Exception("Impossible de se connecter au serveur RMI. Vérifiez qu'il est en cours d'exécution.");
                }
                Product p = editProduct != null ? editProduct : new Product();
                p.setTitle(title);
                p.setBrand(brand);
                p.setSku(sku);
                p.setCategory((Category) categoryBox.getSelectedItem());
                p.setBasePrice(fp); p.setSalePrice(fsp); p.setStockQty(fstock);
                p.setImageUrl(imgUrl);
                p.setDescription(desc);
                p.setSeller(sellerToUse);
                p.setStatus(Product.ProductStatus.ACTIF);
                
                Product result = editProduct == null ? productService.createProductRecord(p) : productService.updateProductRecord(p);
                if (result == null) {
                    throw new Exception("La sauvegarde du produit a échoué dans la base de données.");
                }
                return result;
            }
            @Override protected void done() {
                saveBtn.setEnabled(true);
                saveBtn.setText(editProduct == null ? "Publier le produit" : "Enregistrer les modifications");
                try {
                    Product result = get();
                    if (result == null) {
                        showError("Erreur : Le produit n'a pas pu être enregistré.");
                        return;
                    }
                    JOptionPane.showMessageDialog(AddEditProduct.this,
                        editProduct == null ? "Produit publié avec succès !" : "Produit mis à jour !",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                    if (onSave != null) onSave.run();
                    dispose();
                } catch (Exception ex) {
                    showError("Erreur : " + ErrorUtil.rootMessage(ex));
                }
            }
        };
        worker.execute();
    }

    private void showError(String msg) { statusLabel.setText(msg); statusLabel.setVisible(true); }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
