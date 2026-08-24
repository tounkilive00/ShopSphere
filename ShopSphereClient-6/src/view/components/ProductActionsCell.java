/*
 * ShopSphere - ProductActionsCell
 * Colonne "Actions" réutilisable pour les tableaux produits (SellerDashboard,
 * AdminPanel) : boutons Modifier / Masquer-Afficher / Supprimer dans la ligne.
 * Design moderne avec rendu Java2D anti-aliasé pour éviter les bordures PLAF Swing.
 */
package view.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import view.theme.Theme;

/**
 * Rendu + édition d'une colonne "Actions" avec 3 boutons modernes par ligne.
 * @author ShopSphere
 */
public class ProductActionsCell extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {

    private final JPanel renderPanel;
    private final JPanel editPanel;
    private final ActionButton editBtn;
    private final ActionButton visBtn;
    private final ActionButton delBtn;
    private final ActionButton renderEditBtn;
    private final ActionButton renderVisBtn;
    private final ActionButton renderDelBtn;

    private final IntPredicate isVisible;   // recoit l'ID produit de la ligne -> visible ?
    private int currentRowId = -1;

    public ProductActionsCell(IntPredicate isVisible,
                               IntConsumer onEdit,
                               IntConsumer onToggleVisibility,
                               IntConsumer onDelete) {
        this.isVisible = isVisible;

        renderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        editPanel   = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        renderPanel.setOpaque(true);
        editPanel.setOpaque(true);

        editBtn = new ActionButton("✏️ Modifier", Theme.PRIMARY);
        visBtn  = new ActionButton("👁️ Masquer", Theme.WARNING);
        delBtn  = new ActionButton("🗑️ Suppr.", Theme.ERROR);

        renderEditBtn = new ActionButton("✏️ Modifier", Theme.PRIMARY);
        renderVisBtn  = new ActionButton("👁️ Masquer", Theme.WARNING);
        renderDelBtn  = new ActionButton("🗑️ Suppr.", Theme.ERROR);

        editBtn.addActionListener(e -> { onEdit.accept(currentRowId); fireEditingStopped(); });
        visBtn.addActionListener(e -> { onToggleVisibility.accept(currentRowId); fireEditingStopped(); });
        delBtn.addActionListener(e -> { onDelete.accept(currentRowId); fireEditingStopped(); });

        renderPanel.add(renderEditBtn);
        renderPanel.add(renderVisBtn);
        renderPanel.add(renderDelBtn);

        editPanel.add(editBtn);
        editPanel.add(visBtn);
        editPanel.add(delBtn);
    }

    private void refreshLabels(int productId) {
        boolean vis = isVisible.test(productId);
        String label = vis ? "👁️ Masquer" : "👁️ Afficher";
        Color color = vis ? new Color(0xD9, 0x77, 0x06) : Theme.SUCCESS;

        visBtn.setText(label);
        visBtn.setCustomBackground(color);

        renderVisBtn.setText(label);
        renderVisBtn.setCustomBackground(color);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        int id = (int) table.getModel().getValueAt(row, 0);
        Color bg = isSelected ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : new Color(0xF8, 0xFA, 0xFC));
        renderPanel.setBackground(bg);
        refreshLabels(id);
        return renderPanel;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        currentRowId = (int) table.getModel().getValueAt(row, 0);
        editPanel.setBackground(table.getSelectionBackground());
        refreshLabels(currentRowId);
        return editPanel;
    }

    @Override
    public Object getCellEditorValue() { return "Actions"; }

    /**
     * Bouton personnalisé avec rendu Java2D anti-aliasé, coins arrondis, effet survol,
     * et centrage parfait du texte. Contourne les limitations PLAF Windows Swing.
     */
    private static class ActionButton extends JButton {
        private Color normalColor;

        public ActionButton(String text, Color bg) {
            super(text);
            this.normalColor = bg;
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setForeground(Theme.WHITE);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(74, 28));
        }

        public void setCustomBackground(Color bg) {
            this.normalColor = bg;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            Color fill = normalColor;
            if (getModel().isPressed()) {
                fill = normalColor.darker();
            } else if (getModel().isRollover()) {
                fill = normalColor.brighter();
            }

            // Fond arrondi style pastille
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, w - 1, h - 1, 10, 10);

            // Légère brillance supérieure
            g2.setColor(new Color(255, 255, 255, 35));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);

            // Centrage texte + icône
            g2.setFont(getFont());
            g2.setColor(Theme.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            String txt = getText();
            int x = (w - fm.stringWidth(txt)) / 2;
            int y = (h + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(txt, x, y);

            g2.dispose();
        }
    }
}

