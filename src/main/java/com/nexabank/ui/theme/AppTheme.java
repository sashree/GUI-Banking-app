package com.nexabank.ui.theme;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class AppTheme {

    public static final Color NAVY = new Color(8, 16, 32);
    public static final Color NAVY_DEEP = new Color(6, 12, 26);
    public static final Color SIDEBAR = new Color(11, 22, 44);
    public static final Color CARD = new Color(18, 32, 58);
    public static final Color CARD_ALT = new Color(22, 40, 72);
    public static final Color LINE = new Color(38, 58, 92);
    public static final Color ACCENT = new Color(37, 99, 235);
    public static final Color ACCENT_HOVER = new Color(59, 130, 246);
    public static final Color ACCENT_SOFT = new Color(37, 99, 235, 40);
    public static final Color CYAN = new Color(56, 189, 248);
    public static final Color TEXT = new Color(248, 250, 252);
    public static final Color MUTED = new Color(148, 163, 184);
    public static final Color SUCCESS = new Color(16, 185, 129);
    public static final Color DANGER = new Color(239, 68, 68);
    public static final Color WARNING = new Color(245, 158, 11);
    public static final Color WHITE = Color.WHITE;
    public static final Color LOGIN_PANEL = new Color(248, 250, 252);
    public static final Color FIELD = new Color(15, 27, 48);
    public static final Color FIELD_BORDER = new Color(51, 74, 112);

    private static final String FONT_FAMILY = resolveFamily();

    private AppTheme() {
    }

    public static void install() {
        UIManager.put("control", new ColorUIResource(NAVY));
        UIManager.put("text", new ColorUIResource(TEXT));
        UIManager.put("nimbusBase", new ColorUIResource(NAVY));
        UIManager.put("ToolTip.background", CARD);
        UIManager.put("ToolTip.foreground", TEXT);
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(LINE));
        UIManager.put("OptionPane.background", CARD);
        UIManager.put("Panel.background", NAVY);
        UIManager.put("Label.foreground", TEXT);
    }

    public static Font font(int size) {
        return new Font(FONT_FAMILY, Font.PLAIN, size);
    }

    public static Font fontBold(int size) {
        return new Font(FONT_FAMILY, Font.BOLD, size);
    }

    public static Font fontMedium(int size) {
        return new Font(FONT_FAMILY, Font.PLAIN, size);
    }

    private static String resolveFamily() {
        Set<String> available = Arrays.stream(
                        GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
                .collect(Collectors.toSet());
        for (String candidate : new String[]{"Avenir Next", "Helvetica Neue", "SF Pro Display", "Inter", "Segoe UI"}) {
            if (available.contains(candidate)) {
                return candidate;
            }
        }
        return "SansSerif";
    }
}
