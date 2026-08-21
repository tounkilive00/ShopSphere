/*
 * ShopSphere - AppComboBox
 * Meme structure qu'AgriConnect view/components/AppComboBox.java
 */
package view.components;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import view.theme.Theme;

/**
 * Liste deroulante personnalisee ShopSphere.
 * Meme pattern qu'AgriConnect AppComboBox.
 * @author ShopSphere
 */
public class AppComboBox<T> extends JComboBox<T> {

    public AppComboBox(T[] items) {
        super(items);
        setFont(Theme.FONT_BODY);
        setForeground(Theme.DARK_TEXT);
        setBackground(Theme.BG_INPUT);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.LIGHT_GREY, 1, true),
            new EmptyBorder(4, Theme.PADDING_SM, 4, Theme.PADDING_SM)
        ));
        setPreferredSize(new Dimension(200, Theme.FIELD_H));
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(Theme.FONT_BODY);
                if (isSelected) {
                    setBackground(Theme.PRIMARY);
                    setForeground(Theme.WHITE);
                } else {
                    setBackground(Theme.WHITE);
                    setForeground(Theme.DARK_TEXT);
                }
                setBorder(new EmptyBorder(4, Theme.PADDING_SM, 4, Theme.PADDING_SM));
                return this;
            }
        });
    }

    public AppComboBox() {
        this((T[]) new Object[0]);
    }
}
