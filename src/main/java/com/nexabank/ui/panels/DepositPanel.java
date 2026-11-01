package com.nexabank.ui.panels;

import com.nexabank.exception.BankingException;
import com.nexabank.model.Transaction;
import com.nexabank.ui.MainFrame;
import com.nexabank.ui.components.AppDialogs;
import com.nexabank.ui.components.CardPanel;
import com.nexabank.ui.components.RoundedButton;
import com.nexabank.ui.components.RoundedField;
import com.nexabank.ui.theme.AppTheme;
import com.nexabank.util.MoneyFormatter;
import com.nexabank.util.Validation;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.math.BigDecimal;

public class DepositPanel extends JPanel implements MainFrame.Refreshable {

    private final MainFrame frame;
    private final JLabel balance = new JLabel();
    private final JTextField amount = RoundedField.text();
    private final JTextField note = RoundedField.text();

    public DepositPanel(MainFrame frame) {
        this.frame = frame;
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        add(buildCard(), BorderLayout.WEST);
        refresh();
    }

    private CardPanel buildCard() {
        CardPanel card = new CardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        card.setPreferredSize(new Dimension(480, 460));

        JLabel title = heading("Deposit money");
        JLabel copy = muted("Credits this savings account. The amount is stored with two-decimal precision.");
        balance.setFont(AppTheme.fontBold(22));
        balance.setForeground(AppTheme.CYAN);
        balance.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton submit = RoundedButton.success("Confirm deposit");
        submit.setAlignmentX(Component.LEFT_ALIGNMENT);
        submit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        submit.addActionListener(e -> deposit());

        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(copy);
        card.add(Box.createVerticalStrut(20));
        card.add(muted("Current available balance"));
        card.add(balance);
        card.add(Box.createVerticalStrut(18));
        card.add(label("Amount (₹)"));
        card.add(amount);
        card.add(Box.createVerticalStrut(12));
        card.add(label("Description (optional)"));
        card.add(note);
        card.add(Box.createVerticalStrut(22));
        card.add(submit);
        return card;
    }

    private void deposit() {
        try {
            BigDecimal value = Validation.requirePositiveAmount(amount.getText());
            String description = note.getText().isBlank() ? "Cash deposit" : note.getText().trim();
            if (!AppDialogs.confirm(this, "Confirm deposit",
                    "Deposit " + MoneyFormatter.format(value) + " into this account?")) {
                return;
            }
            Transaction tx = frame.banking().deposit(frame.currentAccount().getAccountNumber(), value, description);
            amount.setText("");
            note.setText("");
            frame.reloadAccount();
            AppDialogs.info(this, "Deposit successful",
                    "Reference " + tx.getTransactionId() + "\nNew balance: "
                            + MoneyFormatter.format(frame.currentAccount().getBalance()));
        } catch (BankingException ex) {
            AppDialogs.error(this, "Deposit declined", ex.getMessage());
        }
    }

    @Override
    public void refresh() {
        balance.setText(MoneyFormatter.format(frame.currentAccount().getBalance()));
    }

    private static JLabel heading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.fontBold(24));
        label.setForeground(AppTheme.TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private static JLabel muted(String text) {
        JLabel label = new JLabel("<html><div style='width:380px'>" + text + "</div></html>");
        label.setFont(AppTheme.font(13));
        label.setForeground(AppTheme.MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private static JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.fontBold(12));
        label.setForeground(AppTheme.MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        return label;
    }
}
