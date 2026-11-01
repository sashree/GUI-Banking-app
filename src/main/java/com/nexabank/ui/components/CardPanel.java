package com.nexabank.ui.components;

import com.nexabank.ui.theme.AppTheme;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

public class CardPanel extends JPanel {

    private final int arc;
    private final Color fill;
    private final Color border;

    public CardPanel() {
        this(20, AppTheme.CARD, AppTheme.LINE);
    }

    public CardPanel(int arc, Color fill, Color border) {
        this.arc = arc;
        this.fill = fill;
        this.border = border;
        setOpaque(false);
    }

    public CardPanel(LayoutManager layout) {
        this();
        setLayout(layout);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        if (border != null) {
            g2.setColor(border);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
