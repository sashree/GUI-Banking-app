package com.nexabank.ui.components;

import com.nexabank.ui.theme.AppTheme;
import com.nexabank.ui.theme.IconFactory;

import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class NavButton extends JButton {

    private boolean active;

    public NavButton(String text, String iconKind) {
        super(text);
        setIcon(IconFactory.nav(iconKind, 22, AppTheme.MUTED));
        setHorizontalAlignment(SwingConstants.LEFT);
        setIconTextGap(12);
        setFont(AppTheme.font(14));
        setForeground(AppTheme.MUTED);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(220, 44));
    }

    public void setActive(boolean active) {
        this.active = active;
        Color color = active ? AppTheme.TEXT : AppTheme.MUTED;
        setForeground(color);
        setIcon(IconFactory.nav(getActionCommand() == null ? "dashboard" : getName(), 22, color));
        if (getClientProperty("iconKind") instanceof String kind) {
            setIcon(IconFactory.nav(kind, 22, color));
        }
        repaint();
    }

    public void bindKind(String kind) {
        putClientProperty("iconKind", kind);
        setIcon(IconFactory.nav(kind, 22, active ? AppTheme.TEXT : AppTheme.MUTED));
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (active) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(AppTheme.ACCENT_SOFT);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.setColor(AppTheme.ACCENT);
            g2.fillRoundRect(0, 8, 4, getHeight() - 16, 4, 4);
            g2.dispose();
        }
        super.paintComponent(g);
    }
}
