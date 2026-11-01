package com.nexabank.ui.components;

import com.nexabank.ui.theme.AppTheme;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

public class GradientPanel extends JPanel {

    private final Color start;
    private final Color end;

    public GradientPanel(LayoutManager layout) {
        this(layout, AppTheme.NAVY_DEEP, new Color(15, 40, 90));
    }

    public GradientPanel(LayoutManager layout, Color start, Color end) {
        super(layout);
        this.start = start;
        this.end = end;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0, 0, start, getWidth(), getHeight(), end));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(new Color(56, 189, 248, 28));
        g2.fillOval(getWidth() - 220, -80, 320, 320);
        g2.setColor(new Color(37, 99, 235, 40));
        g2.fillOval(-120, getHeight() - 220, 300, 300);
        g2.dispose();
        super.paintComponent(g);
    }
}
