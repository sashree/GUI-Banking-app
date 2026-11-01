package com.nexabank.ui.panels;

import com.nexabank.exception.BankingException;
import com.nexabank.model.TransactionType;
import com.nexabank.ui.MainFrame;
import com.nexabank.ui.components.CardPanel;
import com.nexabank.ui.components.RoundedField;
import com.nexabank.ui.components.TransactionTable;
import com.nexabank.ui.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class HistoryPanel extends JPanel implements MainFrame.Refreshable {

    private final MainFrame frame;
    private final TransactionTable table = new TransactionTable();
    private final JTextField search = RoundedField.text();
    private final JComboBox<String> typeFilter = new JComboBox<>();
    private final JLabel count = new JLabel();

    public HistoryPanel(MainFrame frame) {
        this.frame = frame;
        setOpaque(false);
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        JLabel title = new JLabel("Transaction history");
        title.setFont(AppTheme.fontBold(24));
        title.setForeground(AppTheme.TEXT);

        search.setPreferredSize(new Dimension(260, 42));
        typeFilter.setFont(AppTheme.font(13));
        typeFilter.setBackground(AppTheme.FIELD);
        typeFilter.setForeground(AppTheme.TEXT);
        typeFilter.setPreferredSize(new Dimension(180, 42));
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("All types");
        for (TransactionType type : TransactionType.values()) {
            model.addElement(type.getDisplayName());
        }
        typeFilter.setModel(model);
        typeFilter.addActionListener(e -> applyFilter());
        search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter();
            }
        });

        count.setFont(AppTheme.font(13));
        count.setForeground(AppTheme.MUTED);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filters.setOpaque(false);
        filters.add(search);
        filters.add(typeFilter);
        filters.add(count);

        JPanel north = new JPanel(new BorderLayout(0, 12));
        north.setOpaque(false);
        north.add(title, BorderLayout.NORTH);
        north.add(filters, BorderLayout.SOUTH);

        CardPanel tableCard = new CardPanel(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AppTheme.CARD);
        tableCard.add(scroll, BorderLayout.CENTER);

        add(north, BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);
        refresh();
    }

    @Override
    public void refresh() {
        try {
            var rows = frame.banking().history(frame.currentAccount().getAccountNumber());
            table.setTransactions(rows);
            count.setText(rows.size() + " records  ·  newest first");
            applyFilter();
        } catch (BankingException e) {
            count.setText("Unable to load history.");
        }
    }

    private void applyFilter() {
        TransactionType type = null;
        int index = typeFilter.getSelectedIndex();
        if (index > 0) {
            type = TransactionType.values()[index - 1];
        }
        table.applyFilter(search.getText(), type);
    }
}
