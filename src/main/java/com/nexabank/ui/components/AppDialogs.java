package com.nexabank.ui.components;

import com.nexabank.ui.theme.AppTheme;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Component;

public final class AppDialogs {

    private AppDialogs() {
    }

    public static void info(Component parent, String title, String message) {
        show(parent, title, message, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component parent, String title, String message) {
        show(parent, title, message, JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirm(Component parent, String title, String message) {
        JPanel panel = new JPanel();
        panel.setBackground(AppTheme.CARD);
        int result = JOptionPane.showConfirmDialog(parent, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }

    private static void show(Component parent, String title, String message, int type) {
        JOptionPane.showMessageDialog(parent, message, title, type);
    }
}
