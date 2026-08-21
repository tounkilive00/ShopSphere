/*
 * ShopSphere - Toast (système de notifications non-bloquantes)
 * Remplace les JOptionPane par des toasts animés style Material Design.
 * Glisse depuis le bas-droit de la fenêtre parente.
 */
package view.components;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import view.theme.Theme;

/**
 * Notification "toast" non-bloquante — slide-in/out depuis le bas-droit.
 * Usage : Toast.show(parentFrame, "Message", Toast.Type.SUCCESS);
 * @author ShopSphere
 */
public class Toast extends JWindow {

    public enum Type {
        SUCCESS(Theme.TOAST_SUCCESS_BG, "✔  "),
        ERROR  (Theme.TOAST_ERROR_BG,   "✖  "),
        WARNING(Theme.TOAST_WARNING_BG, "⚠  "),
        INFO   (Theme.TOAST_INFO_BG,    "ℹ  ");

        final Color bg;
        final String icon;
        Type(Color bg, String icon) { this.bg = bg; this.icon = icon; }
    }

    private float opacity = 0f;

    private Toast(Window parent, String message, Type type) {
        super(parent);

        JPanel panel = new JPanel(new BorderLayout(10, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                // Ombre
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRoundRect(3, 5, getWidth() - 3, getHeight() - 3, 16, 16);
                // Fond coloré
                g2.setColor(type.bg);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 16, 16);
                // Barre latérale blanche
                g2.setColor(new Color(255, 255, 255, 80));
                g2.fillRoundRect(0, 0, 5, getHeight() - 4, 4, 4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 20));
        panel.setPreferredSize(new Dimension(340, 58));

        JLabel iconLbl = new JLabel(type.icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        iconLbl.setForeground(Color.WHITE);
        panel.add(iconLbl, BorderLayout.WEST);

        JLabel msgLbl = new JLabel("<html>" + message + "</html>");
        msgLbl.setFont(Theme.FONT_BODY);
        msgLbl.setForeground(Color.WHITE);
        panel.add(msgLbl, BorderLayout.CENTER);

        // Close button
        JButton closeBtn = new JButton("×");
        closeBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        closeBtn.setForeground(new Color(255, 255, 255, 180));
        closeBtn.setOpaque(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dismiss());
        panel.add(closeBtn, BorderLayout.EAST);

        setContentPane(panel);
        pack();
    }

    /** Affiche un toast depuis le bas-droit de la fenêtre parente. */
    public static void show(Window parent, String message, Type type) {
        Toast toast = new Toast(parent, message, type);

        // Positionner en bas-droite du parent
        Rectangle parentBounds = (parent != null)
                ? parent.getBounds()
                : GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        int x = parentBounds.x + parentBounds.width  - toast.getWidth()  - 24;
        int y = parentBounds.y + parentBounds.height - toast.getHeight() - 60;
        toast.setLocation(x, y);
        toast.setVisible(true);

        // Animation : fade + slide in
        final int[] step = {0};
        final int targetY = y;
        final int startY  = y + 30;
        toast.setLocation(x, startY);

        Timer fadeIn = new Timer(12, null);
        fadeIn.addActionListener(e -> {
            step[0]++;
            float progress = Math.min(1f, step[0] / 15f);
            toast.opacity = progress;
            int curY = startY + (int) ((targetY - startY) * easeOut(progress));
            toast.setLocation(x, curY);
            toast.repaint();
            if (step[0] >= 15) ((Timer) e.getSource()).stop();
        });
        fadeIn.start();

        // Auto-dismiss after TOAST_DISPLAY_MS
        Timer autoHide = new Timer(Theme.TOAST_DISPLAY_MS, e -> toast.dismiss());
        autoHide.setRepeats(false);
        autoHide.start();
    }

    private void dismiss() {
        final int[] step = {0};
        int startX = getX(), startY = getY();
        Timer fadeOut = new Timer(12, null);
        fadeOut.addActionListener(e -> {
            step[0]++;
            float progress = Math.min(1f, step[0] / 12f);
            opacity = 1f - progress;
            setLocation(startX, startY + (int)(20 * easeIn(progress)));
            repaint();
            if (step[0] >= 12) {
                ((Timer) e.getSource()).stop();
                dispose();
            }
        });
        fadeOut.start();
    }

    // Easing functions
    private static double easeOut(double t) { return 1 - Math.pow(1 - t, 3); }
    private static double easeIn(double t)  { return t * t; }

    /** Raccourcis utilitaires. */
    public static void success(Window parent, String msg) { show(parent, msg, Type.SUCCESS); }
    public static void error  (Window parent, String msg) { show(parent, msg, Type.ERROR);   }
    public static void warning(Window parent, String msg) { show(parent, msg, Type.WARNING); }
    public static void info   (Window parent, String msg) { show(parent, msg, Type.INFO);    }
}
