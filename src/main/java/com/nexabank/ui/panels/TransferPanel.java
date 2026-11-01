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
import java.util.stream.Collectors;

public class TransferPanel extends JPanel implements MainFrame.Refreshable {

    private final MainFrame frame;
    private final JLabel balance = new JLabel();
    private final JLabel directory = new JLabel();
    private final JTextField recipient = RoundedField.text();
    private final JTextField amount = RoundedField.text();
    private final JTextField note = RoundedField.text();

    public TransferPanel(MainFrame frame) {
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
        card.setPreferredSize(new Dimension(520, 560));

        JLabel title = new JLabel("Fund transfer");
        title.setFont(AppTheme.fontBold(24));
        title.setForeground(AppTheme.TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel copy = new JLabel("<html><div style='width:430px'>Moves money between local demo accounts in a single database transaction so both ledgers stay consistent.</div></html>");
        copy.setFont(AppTheme.font(13));
        copy.setForeground(AppTheme.MUTED);
        copy.setAlignmentX(Component.LEFT_ALIGNMENT);

        balance.setFont(AppTheme.fontBold(22));
        balance.setForeground(AppTheme.CYAN);
        balance.setAlignmentX(Component.LEFT_ALIGNMENT);

        directory.setFont(AppTheme.font(12));
        directory.setForeground(AppTheme.MUTED);
        directory.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton submit = RoundedButton.primary("Send transfer");
        submit.setAlignmentX(Component.LEFT_ALIGNMENT);
        submit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        submit.addActionListener(e -> transfer());

        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(copy);
        card.add(Box.createVerticalStrut(16));
        card.add(caption("Your available balance"));
        card.add(balance);
        card.add(Box.createVerticalStrut(12));
        card.add(directory);
        card.add(Box.createVerticalStrut(16));
        card.add(caption("Recipient account number"));
        card.add(recipient);
        card.add(Box.createVerticalStrut(12));
        card.add(caption("Amount (₹)"));
        card.add(amount);
        card.add(Box.createVerticalStrut(12));
        card.add(caption("Note (optional)"));
        card.add(note);
        card.add(Box.createVerticalStrut(22));
        card.add(submit);
        return card;
    }

    private void transfer() {
        try {
            String to = Validation.requireAccountNumber(recipient.getText());
            BigDecimal value = Validation.requirePositiveAmount(amount.getText());
            if (!AppDialogs.confirm(this, "Confirm transfer",
                    "Send " + MoneyFormatter.format(value) + " to account " + to + "?")) {
                return;
            }
            Transaction tx = frame.banking().transfer(
                    frame.currentAccount().getAccountNumber(),
                    to,
                    value,
                    note.getText()
            );
            recipient.setText("");
            amount.setText("");
            note.setText("");
            frame.reloadAccount();
            AppDialogs.info(this, "Transfer successful",
                    "Reference " + tx.getTransactionId() + "\nNew balance: "
                            + MoneyFormatter.format(frame.currentAccount().getBalance()));
        } catch (BankingException ex) {
            AppDialogs.error(this, "Transfer declined", ex.getMessage());
        }
    }

    @Override
    public void refresh() {
        balance.setText(MoneyFormatter.format(frame.currentAccount().getBalance()));
        try {
            String others = frame.banking().listAccounts().stream()
                    .filter(a -> !a.getAccountNumber().equals(frame.currentAccount().getAccountNumber()))
                    .map(a -> a.getAccountNumber() + "  ·  " + a.getHolderName())
                    .collect(Collectors.joining("<br>"));
            directory.setText("<html><b>Other demo accounts</b><br>" + (others.isBlank() ? "None yet." : others) + "</html>");
        } catch (BankingException e) {
            directory.setText("Unable to load account directory.");
        }
    }

    private static JLabel caption(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.fontBold(12));
        label.setForeground(AppTheme.MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        return label;
    }
}
