package com.nexabank.ui.components;

import com.nexabank.ui.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

public class StatCard extends CardPanel {

    private final JLabel valueLabel;

    public StatCard(String title, String value, Color accent) {
        super(18, AppTheme.CARD_ALT, AppTheme.LINE);
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        setPreferredSize(new Dimension(220, 110));

        JLabel titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(AppTheme.font(11));
        titleLabel.setForeground(AppTheme.MUTED);

        valueLabel = new JLabel(value, SwingConstants.LEFT);
        valueLabel.setFont(AppTheme.fontBold(22));
        valueLabel.setForeground(AppTheme.TEXT);

        JLabel bar = new JLabel(" ");
        bar.setOpaque(true);
        bar.setBackground(accent);
        bar.setPreferredSize(new Dimension(36, 4));

        CardPanel barWrap = new CardPanel(4, accent, null);
        barWrap.setPreferredSize(new Dimension(36, 4));

        add(titleLabel, BorderLayout.NORTH);
        add(valueLabel, BorderLayout.CENTER);
        add(barWrap, BorderLayout.SOUTH);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}
