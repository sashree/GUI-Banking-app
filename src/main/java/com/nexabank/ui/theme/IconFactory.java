package com.nexabank.ui.theme;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;

public final class IconFactory {

    private IconFactory() {
    }

    public static Icon logo(int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                paintLogo(g, x, y, size);
            }

            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }

    public static Icon nav(String kind, int size, Color color) {
        return new NavIcon(kind, size, color);
    }

    private static void paintLogo(Graphics g, int x, int y, int size) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(x, y);
        g2.setColor(AppTheme.ACCENT);
        g2.fillRoundRect(0, 0, size, size, size / 3, size / 3);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(Math.max(2f, size / 12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int m = size / 5;
        g2.drawLine(size / 2, m, m, size / 2);
        g2.drawLine(size / 2, m, size - m, size / 2);
        g2.drawLine(m + 2, size / 2, m + 2, size - m);
        g2.drawLine(size - m - 2, size / 2, size - m - 2, size - m);
        g2.drawLine(m + 2, size - m, size - m - 2, size - m);
        g2.drawLine(size / 2, size / 2 - 2, size / 2, size - m);
        g2.dispose();
    }

    private record NavIcon(String kind, int size, Color color) implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.translate(x, y);
            switch (kind) {
                case "dashboard" -> {
                    g2.draw(new RoundRectangle2D.Float(3, 3, 8, 8, 2, 2));
                    g2.draw(new RoundRectangle2D.Float(13, 3, 8, 5, 2, 2));
                    g2.draw(new RoundRectangle2D.Float(13, 11, 8, 8, 2, 2));
                    g2.draw(new RoundRectangle2D.Float(3, 14, 8, 5, 2, 2));
                }
                case "deposit" -> {
                    g2.draw(new RoundRectangle2D.Float(4, 8, 16, 12, 4, 4));
                    g2.drawLine(12, 3, 12, 14);
                    g2.drawLine(8, 8, 12, 4);
                    g2.drawLine(16, 8, 12, 4);
                }
                case "withdraw" -> {
                    g2.draw(new RoundRectangle2D.Float(4, 4, 16, 12, 4, 4));
                    g2.drawLine(12, 10, 12, 21);
                    g2.drawLine(8, 16, 12, 20);
                    g2.drawLine(16, 16, 12, 20);
                }
                case "transfer" -> {
                    g2.drawLine(3, 8, 17, 8);
                    g2.drawLine(13, 4, 17, 8);
                    g2.drawLine(13, 12, 17, 8);
                    g2.drawLine(21, 16, 7, 16);
                    g2.drawLine(11, 12, 7, 16);
                    g2.drawLine(11, 20, 7, 16);
                }
                case "history" -> {
                    g2.draw(new Arc2D.Float(4, 4, 16, 16, 40, 290, Arc2D.OPEN));
                    g2.drawLine(12, 8, 12, 13);
                    g2.drawLine(12, 13, 16, 13);
                }
                case "profile" -> {
                    g2.drawOval(8, 4, 8, 8);
                    g2.drawArc(5, 13, 14, 10, 0, 180);
                }
                case "logout" -> {
                    g2.draw(new RoundRectangle2D.Float(4, 4, 10, 16, 3, 3));
                    g2.drawLine(12, 12, 20, 12);
                    g2.drawLine(16, 8, 20, 12);
                    g2.drawLine(16, 16, 20, 12);
                }
                default -> g2.drawOval(4, 4, 16, 16);
            }
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }
}
