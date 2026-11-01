package com.nexabank.ui.panels;

import com.nexabank.exception.BankingException;
import com.nexabank.model.SavingsAccount;
import com.nexabank.ui.MainFrame;
import com.nexabank.ui.components.AppDialogs;
import com.nexabank.ui.components.CardPanel;
import com.nexabank.ui.components.RoundedButton;
import com.nexabank.ui.components.RoundedField;
import com.nexabank.ui.theme.AppTheme;
import com.nexabank.util.MoneyFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;

public class ProfilePanel extends JPanel implements MainFrame.Refreshable {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private final MainFrame frame;
    private final JLabel name = valueLabel();
    private final JLabel number = valueLabel();
    private final JLabel email = valueLabel();
    private final JLabel phone = valueLabel();
    private final JLabel opened = valueLabel();
    private final JLabel balance = valueLabel();
    private final JPasswordField currentPin = RoundedField.password();
    private final JPasswordField newPin = RoundedField.password();
    private final JPasswordField confirmPin = RoundedField.password();

    public ProfilePanel(MainFrame frame) {
        this.frame = frame;
        setOpaque(false);
        setLayout(new GridLayout(1, 2, 18, 0));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        add(profileCard());
        add(pinCard());
        refresh();
    }

    private CardPanel profileCard() {
        CardPanel card = new CardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        card.add(heading("Account details"));
        card.add(Box.createVerticalStrut(6));
        card.add(muted("Profile information stored in the local SQLite ledger."));
        card.add(Box.createVerticalStrut(20));
        card.add(row("Account holder", name));
        card.add(row("Account number", number));
        card.add(row("Email", email));
        card.add(row("Phone", phone));
        card.add(row("Opened on", opened));
        card.add(row("Available balance", balance));
        card.add(Box.createVerticalStrut(24));
        RoundedButton logout = RoundedButton.ghost("Sign out");
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.setMaximumSize(new Dimension(220, 42));
        logout.addActionListener(e -> frame.logout());
        card.add(logout);
        return card;
    }

    private CardPanel pinCard() {
        CardPanel card = new CardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        card.add(heading("Change PIN"));
        card.add(Box.createVerticalStrut(6));
        card.add(muted("The new PIN is salted and hashed. NexaBank never stores the PIN in plaintext."));
        card.add(Box.createVerticalStrut(18));
        card.add(caption("Current PIN"));
        card.add(currentPin);
        card.add(Box.createVerticalStrut(12));
        card.add(caption("New 4-digit PIN"));
        card.add(newPin);
        card.add(Box.createVerticalStrut(12));
        card.add(caption("Confirm new PIN"));
        card.add(confirmPin);
        card.add(Box.createVerticalStrut(22));
        RoundedButton save = RoundedButton.primary("Update PIN");
        save.setAlignmentX(Component.LEFT_ALIGNMENT);
        save.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        save.addActionListener(e -> changePin());
        card.add(save);
        return card;
    }

    private void changePin() {
        try {
            if (!String.valueOf(newPin.getPassword()).equals(String.valueOf(confirmPin.getPassword()))) {
                AppDialogs.error(this, "PIN mismatch", "The two new PIN entries do not match.");
                return;
            }
            frame.auth().changePin(frame.currentAccount().getAccountNumber(), currentPin.getPassword(), newPin.getPassword());
            currentPin.setText("");
            newPin.setText("");
            confirmPin.setText("");
            AppDialogs.info(this, "PIN updated", "Your PIN was changed. Use the new PIN the next time you sign in.");
        } catch (BankingException ex) {
            AppDialogs.error(this, "Could not change PIN", ex.getMessage());
        }
    }

    @Override
    public void refresh() {
        SavingsAccount account = frame.currentAccount();
        name.setText(account.getHolderName());
        number.setText(account.getAccountNumber());
        email.setText(account.getEmail());
        phone.setText(account.getPhone());
        opened.setText(DATE.format(account.getOpenedAt()));
        balance.setText(MoneyFormatter.format(account.getBalance()));
    }

    private JPanel row(String label, JLabel value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.LINE));
        JLabel key = new JLabel(label);
        key.setFont(AppTheme.font(12));
        key.setForeground(AppTheme.MUTED);
        row.add(key, BorderLayout.NORTH);
        row.add(value, BorderLayout.CENTER);
        return row;
    }

    private static JLabel heading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.fontBold(22));
        label.setForeground(AppTheme.TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private static JLabel muted(String text) {
        JLabel label = new JLabel("<html><div style='width:360px'>" + text + "</div></html>");
        label.setFont(AppTheme.font(13));
        label.setForeground(AppTheme.MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private static JLabel caption(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.fontBold(12));
        label.setForeground(AppTheme.MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        return label;
    }

    private static JLabel valueLabel() {
        JLabel label = new JLabel("-");
        label.setFont(AppTheme.fontBold(15));
        label.setForeground(AppTheme.TEXT);
        return label;
    }
}
