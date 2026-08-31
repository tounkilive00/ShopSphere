/*
 * ShopSphere - CustomHeaderRenderer
 * Custom table header renderer to ensure column titles (ID, Nom, Email, Téléphone, etc.)
 * are always 100% visible with high contrast across all system Look & Feels.
 */
package view.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import view.theme.Theme;

public class CustomHeaderRenderer extends JLabel implements TableCellRenderer {

    public CustomHeaderRenderer() {
        setOpaque(true);
        setHorizontalAlignment(SwingConstants.LEFT);
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setBackground(Theme.PRIMARY); // Dark navy background (#1A3C5E)
        setForeground(Theme.WHITE);   // Solid crisp white text (#FFFFFF)
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 1, new Color(0x0F, 0x29, 0x42)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        setText(value != null ? value.toString() : "");
        return this;
    }
}
