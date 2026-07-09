/*
 * ShopSphere - Theme UI
 * Meme structure qu'AgriConnect view/theme/Theme.java
 * Palette de couleurs officielle (3 couleurs maximum selon le SRS)
 *   1. Bleu Marine Profond #1A3C5E  (primaire)
 *   2. Or Ambre            #E8A020  (accent)
 *   3. Gris-Bleu Doux      #F0F4F8  (neutre)
 */
package view.theme;

import java.awt.Color;
import java.awt.Font;

/**
 * Constantes visuelles de ShopSphere.
 * Meme role que AgriConnect Theme.java — centralise tous les styles UI.
 * @author ShopSphere
 */
public class Theme {

    // ── Palette 3 couleurs (maximum autorise par le SRS) ─────────────────
    public static final Color PRIMARY   = new Color(0x1A, 0x3C, 0x5E); // Bleu marine
    public static final Color ACCENT    = new Color(0xE8, 0xA0, 0x20); // Or ambre
    public static final Color NEUTRAL   = new Color(0xF0, 0xF4, 0xF8); // Gris-bleu doux

    // ── Couleurs systeme (hors quota 3 couleurs) ─────────────────────────
    public static final Color WHITE      = Color.WHITE;
    public static final Color DARK_TEXT  = new Color(0x0F, 0x17, 0x2A);
    public static final Color GREY_TEXT  = new Color(0x64, 0x74, 0x8B);
    public static final Color LIGHT_GREY = new Color(0xE2, 0xE8, 0xF0);
    public static final Color SUCCESS    = new Color(0x16, 0xA3, 0x4A); // Vert succes
    public static final Color ERROR      = new Color(0xDC, 0x26, 0x26); // Rouge erreur
    public static final Color WARNING    = new Color(0xF5, 0x9E, 0x0B); // Ambre avertissement
    public static final Color PRICE_RED  = new Color(0xDC, 0x26, 0x26); // Prix barre
    public static final Color BG_CARD    = new Color(0xFF, 0xFF, 0xFF); // Fond carte
    public static final Color BG_INPUT   = new Color(0xF8, 0xFA, 0xFC); // Fond champ

    // ── Couleurs statuts commande ─────────────────────────────────────────
    public static final Color STATUS_ATTENTE  = new Color(0xF5, 0x9E, 0x0B); // Ambre
    public static final Color STATUS_PAYEE    = new Color(0x3B, 0x82, 0xF6); // Bleu
    public static final Color STATUS_EXPEDIEE = new Color(0x8B, 0x5C, 0xF6); // Violet
    public static final Color STATUS_LIVREE   = new Color(0x10, 0xB9, 0x81); // Vert
    public static final Color STATUS_ANNULEE  = new Color(0xEF, 0x44, 0x44); // Rouge

    // ── Typographie (meme convention qu'AgriConnect) ──────────────────────
    public static final Font FONT_TITLE    = new Font("Arial", Font.BOLD,  24);
    public static final Font FONT_SUBTITLE = new Font("Arial", Font.BOLD,  18);
    public static final Font FONT_HEADING  = new Font("Arial", Font.BOLD,  14);
    public static final Font FONT_BODY     = new Font("Arial", Font.PLAIN, 13);
    public static final Font FONT_SMALL    = new Font("Arial", Font.PLAIN, 11);
    public static final Font FONT_BUTTON   = new Font("Arial", Font.BOLD,  13);
    public static final Font FONT_PRICE    = new Font("Arial", Font.BOLD,  15);
    public static final Font FONT_BADGE    = new Font("Arial", Font.BOLD,  10);
    public static final Font FONT_MONO     = new Font("Courier New", Font.BOLD, 28);

    // ── Dimensions ────────────────────────────────────────────────────────
    public static final int BORDER_RADIUS = 8;
    public static final int PADDING_XS    = 4;
    public static final int PADDING_SM    = 8;
    public static final int PADDING_MD    = 14;
    public static final int PADDING_LG    = 24;
    public static final int PADDING_XL    = 40;
    public static final int NAVBAR_H      = 60;
    public static final int SIDEBAR_W     = 220;
    public static final int CARD_W        = 210;
    public static final int CARD_H        = 290;
    public static final int FIELD_H       = 40;
    public static final int BTN_H         = 38;

    private Theme() {}
}
