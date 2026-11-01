package com.nexabank.ui.components;

import com.nexabank.ui.theme.AppTheme;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {

    private final Color base;
    private final Color hover;
    private final Color press;
    private boolean hovered;
    private boolean pressed;

    public RoundedButton(String text, Color base, Color hover, Color foreground) {
        super(text);
        this.base = base;
        this.hover = hover;
        this.press = hover.darker();
        setForeground(foreground);
        setFont(AppTheme.fontBold(14));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                pressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }

    public static RoundedButton primary(String text) {
        return new RoundedButton(text, AppTheme.ACCENT, AppTheme.ACCENT_HOVER, Color.WHITE);
    }

    public static RoundedButton ghost(String text) {
        return new RoundedButton(text, new Color(255, 255, 255, 18), new Color(255, 255, 255, 32), AppTheme.TEXT);
    }

    public static RoundedButton danger(String text) {
        return new RoundedButton(text, AppTheme.DANGER, AppTheme.DANGER.brighter(), Color.WHITE);
    }

    public static RoundedButton success(String text) {
        return new RoundedButton(text, AppTheme.SUCCESS, AppTheme.SUCCESS.brighter(), Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fill = pressed ? press : hovered ? hover : base;
        if (!isEnabled()) {
            fill = new Color(fill.getRed(), fill.getGreen(), fill.getBlue(), 90);
        }
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
        g2.dispose();
        super.paintComponent(g);
    }
}
