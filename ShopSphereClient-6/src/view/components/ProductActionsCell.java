/*
 * ShopSphere - ProductActionsCell
 * Colonne "Actions" reutilisable pour les tableaux produits (SellerDashboard,
 * AdminPanel) : boutons Modifier / Masquer-Afficher / Supprimer dans la ligne
 * elle-meme, au lieu d'un texte statique non cliquable.
 * Le design (rendu des boutons) vit ici ; la logique metier (appels RMI) reste
 * dans la vue appelante via les callbacks fournis au constructeur.
 */
package view.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import view.theme.Theme;

/**
 * Rendu + edition d'une colonne "Actions" avec 3 boutons compacts par ligne.
 * @author ShopSphere
 */
public class ProductActionsCell extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {

    private final JPanel renderPanel;
    private final JPanel editPanel;
    private final JButton editBtn;
    private final JButton visBtn;
    private final JButton delBtn;
    private final IntPredicate isVisible;   // recoit l'ID produit de la ligne -> visible ?
    private int currentRowId = -1;

    public ProductActionsCell(IntPredicate isVisible,
                               IntConsumer onEdit,
                               IntConsumer onToggleVisibility,
                               IntConsumer onDelete) {
        this.isVisible = isVisible;

        renderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 2));
        editPanel   = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 2));
        renderPanel.setOpaque(true);
        editPanel.setOpaque(true);

        editBtn = miniButton("Modifier", Theme.PRIMARY);
        visBtn  = miniButton("Masquer", Theme.WARNING);
        delBtn  = miniButton("Supprimer", Theme.ERROR);

        editBtn.addActionListener(e -> { onEdit.accept(currentRowId); fireEditingStopped(); });
        visBtn.addActionListener(e -> { onToggleVisibility.accept(currentRowId); fireEditingStopped(); });
        delBtn.addActionListener(e -> { onDelete.accept(currentRowId); fireEditingStopped(); });

        renderPanel.add(cloneLabelButton(editBtn));
        renderPanel.add(cloneLabelButton(visBtn));
        renderPanel.add(cloneLabelButton(delBtn));

        editPanel.add(editBtn);
        editPanel.add(visBtn);
        editPanel.add(delBtn);
    }

    private JButton miniButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 10));
        b.setForeground(Theme.WHITE);
        b.setBackground(color);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setMargin(new java.awt.Insets(2, 6, 2, 6));
        b.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        return b;
    }

    /** Version non-cliquable utilisee uniquement pour le rendu (pas d'ecouteurs actifs). */
    private JButton cloneLabelButton(JButton src) {
        JButton b = miniButton(src.getText(), src.getBackground());
        return b;
    }

    private void refreshLabels(int productId) {
        boolean vis = isVisible.test(productId);
        String label = vis ? "Masquer" : "Afficher";
        Color color = vis ? Theme.WARNING : Theme.SUCCESS;
        visBtn.setText(label);
        visBtn.setBackground(color);
        JButton renderVis = (JButton) renderPanel.getComponent(1);
        renderVis.setText(label);
        renderVis.setBackground(color);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        int id = (int) table.getModel().getValueAt(row, 0);
        renderPanel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
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
}
