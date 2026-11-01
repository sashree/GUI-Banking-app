package com.nexabank.ui.panels;

import com.nexabank.exception.BankingException;
import com.nexabank.model.SavingsAccount;
import com.nexabank.model.Transaction;
import com.nexabank.ui.MainFrame;
import com.nexabank.ui.components.CardPanel;
import com.nexabank.ui.components.RoundedButton;
import com.nexabank.ui.components.StatCard;
import com.nexabank.ui.components.TransactionTable;
import com.nexabank.ui.theme.AppTheme;
import com.nexabank.util.MoneyFormatter;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

public class DashboardPanel extends JPanel implements MainFrame.Refreshable {

    private final MainFrame frame;
    private final StatCard balance = new StatCard("Available balance", "₹0.00", AppTheme.CYAN);
    private final StatCard deposited = new StatCard("Total deposited", "₹0.00", AppTheme.SUCCESS);
    private final StatCard withdrawn = new StatCard("Total withdrawn", "₹0.00", AppTheme.WARNING);
    private final StatCard typeCard = new StatCard("Account type", "Savings", AppTheme.ACCENT);
    private final TransactionTable table = new TransactionTable();
    private final JLabel greeting = new JLabel();

    public DashboardPanel(MainFrame frame) {
        this.frame = frame;
        setOpaque(false);
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        greeting.setFont(AppTheme.fontBold(24));
        greeting.setForeground(AppTheme.TEXT);

        JPanel stats = new JPanel(new GridLayout(1, 4, 14, 0));
        stats.setOpaque(false);
        stats.add(balance);
        stats.add(deposited);
        stats.add(withdrawn);
        stats.add(typeCard);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        actions.add(action("Deposit money", "deposit"));
        actions.add(action("Withdraw", "withdraw"));
        actions.add(action("Transfer funds", "transfer"));
        actions.add(action("Full history", "history"));

        JPanel north = new JPanel(new BorderLayout(0, 16));
        north.setOpaque(false);
        north.add(greeting, BorderLayout.NORTH);
        north.add(stats, BorderLayout.CENTER);
        north.add(actions, BorderLayout.SOUTH);

        CardPanel recent = new CardPanel(new BorderLayout());
        recent.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel title = new JLabel("Recent transactions");
        title.setFont(AppTheme.fontBold(16));
        title.setForeground(AppTheme.TEXT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JScrollPane scroll = wrap(table);
        recent.add(title, BorderLayout.NORTH);
        recent.add(scroll, BorderLayout.CENTER);

        add(north, BorderLayout.NORTH);
        add(recent, BorderLayout.CENTER);
        refresh();
    }

    private RoundedButton action(String label, String page) {
        RoundedButton button = RoundedButton.ghost(label);
        button.setPreferredSize(new Dimension(150, 40));
        button.addActionListener(e -> frame.showPage(page));
        return button;
    }

    private JScrollPane wrap(TransactionTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AppTheme.CARD);
        scroll.setOpaque(false);
        return scroll;
    }

    @Override
    public void refresh() {
        SavingsAccount account = frame.currentAccount();
        greeting.setText("Hello, " + account.getHolderName().split(" ")[0] + ".");
        balance.setValue(MoneyFormatter.format(account.getBalance()));
        deposited.setValue(MoneyFormatter.format(account.getTotalDeposited()));
        withdrawn.setValue(MoneyFormatter.format(account.getTotalWithdrawn()));
        typeCard.setValue(account.getAccountType().getDisplayName());
        try {
            List<Transaction> all = frame.banking().history(account.getAccountNumber());
            table.setTransactions(all.size() > 8 ? all.subList(0, 8) : all);
        } catch (BankingException e) {
            table.setTransactions(List.of());
        }
    }
}
