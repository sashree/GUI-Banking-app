package com.nexabank.ui.components;

import com.nexabank.ui.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

public final class RoundedField {

    private RoundedField() {
    }

    public static JTextField text() {
        return style(new InnerTextField());
    }

    public static JPasswordField password() {
        JPasswordField field = new InnerPasswordField();
        field.setEchoChar('•');
        return style(field);
    }

    public static JTextField lightText() {
        JTextField field = text();
        field.setBackground(Color.WHITE);
        field.setForeground(new Color(15, 23, 42));
        field.setCaretColor(new Color(15, 23, 42));
        return field;
    }

    public static JPasswordField lightPassword() {
        JPasswordField field = password();
        field.setBackground(Color.WHITE);
        field.setForeground(new Color(15, 23, 42));
        field.setCaretColor(new Color(15, 23, 42));
        return field;
    }

    private static <T extends JTextField> T style(T field) {
        field.setOpaque(false);
        field.setForeground(AppTheme.TEXT);
        field.setCaretColor(AppTheme.TEXT);
        field.setFont(AppTheme.font(15));
        field.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        field.setBackground(AppTheme.FIELD);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        field.setPreferredSize(new Dimension(320, 46));
        return field;
    }

    private static void paintField(JTextField field, Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(field.getBackground());
        g2.fillRoundRect(0, 0, field.getWidth(), field.getHeight(), 14, 14);
        Color border = field.hasFocus() ? AppTheme.ACCENT : AppTheme.FIELD_BORDER;
        if (Color.WHITE.equals(field.getBackground())) {
            border = field.hasFocus() ? AppTheme.ACCENT : new Color(203, 213, 225);
        }
        g2.setColor(border);
        g2.drawRoundRect(0, 0, field.getWidth() - 1, field.getHeight() - 1, 14, 14);
        g2.dispose();
    }

    private static final class InnerTextField extends JTextField {
        @Override
        protected void paintComponent(Graphics g) {
            paintField(this, g);
            super.paintComponent(g);
        }

        @Override
        public Insets getInsets() {
            return new Insets(12, 14, 12, 14);
        }
    }

    private static final class InnerPasswordField extends JPasswordField {
        @Override
        protected void paintComponent(Graphics g) {
            paintField(this, g);
            super.paintComponent(g);
        }

        @Override
        public Insets getInsets() {
            return new Insets(12, 14, 12, 14);
        }
    }
}
