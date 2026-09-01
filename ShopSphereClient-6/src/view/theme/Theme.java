/*
 * ShopSphere - Theme UI  (v2.0 — Segoe UI premium + dark mode ready)
 * Palette 3 couleurs + systeme complet :
 *   1. Bleu Marine Profond  #1A3C5E  (primaire)
 *   2. Or Ambre             #E8A020  (accent)
 *   3. Gris-Bleu Doux       #F0F4F8  (neutre)
 */
package view.theme;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Constantes visuelles centralisées de ShopSphere v2.
 * Toute modification ici se répercute sur l'ensemble de l'UI.
 * @author ShopSphere
 */
public class Theme {

    private static final Logger LOG = Logger.getLogger(Theme.class.getName());

    // ── Palette primaire (3 couleurs SRS) ────────────────────────────────
    public static final Color PRIMARY    = new Color(0x1A, 0x3C, 0x5E);  // Bleu marine
    public static final Color ACCENT     = new Color(0xE8, 0xA0, 0x20);  // Or ambre
    public static final Color NEUTRAL    = new Color(0xF0, 0xF4, 0xF8);  // Gris-bleu doux

    // ── Dérivées primaire ─────────────────────────────────────────────────
    public static final Color PRIMARY_LIGHT   = new Color(0x2A, 0x5A, 0x8C);
    public static final Color PRIMARY_DARK    = new Color(0x0D, 0x24, 0x3C);
    public static final Color ACCENT_LIGHT    = new Color(0xFF, 0xBF, 0x47);
    public static final Color ACCENT_DARK     = new Color(0xB3, 0x7A, 0x0D);

    // ── Couleurs système ─────────────────────────────────────────────────
    public static final Color WHITE      = Color.WHITE;
    public static final Color DARK_TEXT  = new Color(0x0F, 0x17, 0x2A);
    public static final Color GREY_TEXT  = new Color(0x64, 0x74, 0x8B);
    public static final Color LIGHT_GREY = new Color(0xE2, 0xE8, 0xF0);
    public static final Color MID_GREY   = new Color(0xCB, 0xD5, 0xE1);
    public static final Color SUCCESS    = new Color(0x16, 0xA3, 0x4A);
    public static final Color SUCCESS_BG = new Color(0xDC, 0xFC, 0xE7);
    public static final Color ERROR      = new Color(0xDC, 0x26, 0x26);
    public static final Color ERROR_BG   = new Color(0xFE, 0xE2, 0xE2);
    public static final Color WARNING    = new Color(0xF5, 0x9E, 0x0B);
    public static final Color WARNING_BG = new Color(0xFF, 0xF7, 0xCD);
    public static final Color INFO       = new Color(0x38, 0xBD, 0xF8);
    public static final Color INFO_BG    = new Color(0xE0, 0xF7, 0xFF);
    public static final Color PRICE_RED  = new Color(0xDC, 0x26, 0x26);
    public static final Color BG_CARD    = new Color(0xFF, 0xFF, 0xFF);
    public static final Color BG_INPUT   = new Color(0xF8, 0xFA, 0xFC);
    public static final Color SURFACE    = new Color(0xF1, 0xF5, 0xF9);
    public static final Color SHADOW     = new Color(0x0F, 0x17, 0x2A, 30);
    public static final Color SHADOW_HOVER = new Color(0x1A, 0x3C, 0x5E, 50);

    // ── Statuts commande ──────────────────────────────────────────────────
    public static final Color STATUS_ATTENTE  = new Color(0xF5, 0x9E, 0x0B);
    public static final Color STATUS_PAYEE    = new Color(0x3B, 0x82, 0xF6);
    public static final Color STATUS_EXPEDIEE = new Color(0x8B, 0x5C, 0xF6);
    public static final Color STATUS_LIVREE   = new Color(0x10, 0xB9, 0x81);
    public static final Color STATUS_ANNULEE  = new Color(0xEF, 0x44, 0x44);

    // ── Toast / Notification ──────────────────────────────────────────────
    public static final Color TOAST_SUCCESS_BG  = new Color(0x16, 0xA3, 0x4A);
    public static final Color TOAST_ERROR_BG    = new Color(0xDC, 0x26, 0x26);
    public static final Color TOAST_WARNING_BG  = new Color(0xF5, 0x9E, 0x0B);
    public static final Color TOAST_INFO_BG     = new Color(0x1A, 0x3C, 0x5E);

    // ── Typographie — Segoe UI avec fallback SansSerif ────────────────────
    private static final String FONT_FAMILY = resolveFontFamily();

    public static final Font FONT_TITLE    = new Font(FONT_FAMILY, Font.BOLD,  24);
    public static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, Font.BOLD,  18);
    public static final Font FONT_HEADING  = new Font(FONT_FAMILY, Font.BOLD,  14);
    public static final Font FONT_BODY     = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font FONT_SMALL    = new Font(FONT_FAMILY, Font.PLAIN, 11);
    public static final Font FONT_BUTTON   = new Font(FONT_FAMILY, Font.BOLD,  13);
    public static final Font FONT_PRICE    = new Font(FONT_FAMILY, Font.BOLD,  15);
    public static final Font FONT_BADGE    = new Font(FONT_FAMILY, Font.BOLD,  10);
    public static final Font FONT_MONO     = new Font("Consolas",  Font.BOLD,  26);
    public static final Font FONT_LABEL    = new Font(FONT_FAMILY, Font.BOLD,  12);
    public static final Font FONT_CAPTION  = new Font(FONT_FAMILY, Font.PLAIN, 10);

    // ── Dimensions ────────────────────────────────────────────────────────
    public static final int BORDER_RADIUS = 12;
    public static final int BORDER_RADIUS_SM = 6;
    public static final int BORDER_RADIUS_LG = 20;
    public static final int PADDING_XS    = 4;
    public static final int PADDING_SM    = 8;
    public static final int PADDING_MD    = 14;
    public static final int PADDING_LG    = 24;
    public static final int PADDING_XL    = 40;
    public static final int NAVBAR_H      = 64;
    public static final int SIDEBAR_W     = 220;
    public static final int CARD_W        = 220;
    public static final int CARD_H        = 300;
    public static final int FIELD_H       = 44;
    public static final int BTN_H         = 42;
    public static final int BTN_H_SM      = 32;
    public static final int ICON_SIZE     = 20;

    // ── Animation ─────────────────────────────────────────────────────────
    /** Durée standard d'animation de transition en ms. */
    public static final int ANIM_DURATION_FAST = 150;
    public static final int ANIM_DURATION_NORMAL = 250;
    public static final int ANIM_DURATION_SLOW = 400;
    public static final int TOAST_DISPLAY_MS = 3500;

    /** Détermine la famille de police optimale disponible. */
    private static String resolveFontFamily() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        java.util.Set<String> fonts = new java.util.HashSet<>(
                java.util.Arrays.asList(ge.getAvailableFontFamilyNames()));
        if (fonts.contains("Segoe UI"))   return "Segoe UI";
        if (fonts.contains("Inter"))      return "Inter";
        if (fonts.contains("Helvetica Neue")) return "Helvetica Neue";
        if (fonts.contains("SF Pro Text")) return "SF Pro Text";
        return "SansSerif";
    }

    /** Applique un style moderne et lisible avec en-tete sombre ultra-visible a n'importe quel JTable. */
    public static void styleTable(javax.swing.JTable t) {
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setRowHeight(38);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 38));
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setDefaultRenderer(new view.components.CustomHeaderRenderer());
        t.setGridColor(new Color(0xE2, 0xE8, 0xF0));
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.setSelectionBackground(new Color(0xE0, 0xF2, 0xFE));
        t.setSelectionForeground(Theme.DARK_TEXT);
    }

    private Theme() {}
}
