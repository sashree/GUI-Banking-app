package com.nexabank.ui.components;

import com.nexabank.model.Transaction;
import com.nexabank.model.TransactionType;
import com.nexabank.ui.theme.AppTheme;
import com.nexabank.util.MoneyFormatter;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.Color;
import java.awt.Component;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionTable extends JTable {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
    private final Model model;
    private final TableRowSorter<Model> sorter;

    public TransactionTable() {
        this.model = new Model();
        setModel(model);
        sorter = new TableRowSorter<>(model);
        setRowSorter(sorter);
        setRowHeight(38);
        setShowGrid(false);
        setIntercellSpacing(new java.awt.Dimension(0, 0));
        setFillsViewportHeight(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setBackground(AppTheme.CARD);
        setForeground(AppTheme.TEXT);
        setSelectionBackground(new Color(37, 99, 235, 80));
        setSelectionForeground(AppTheme.TEXT);
        setFont(AppTheme.font(13));
        setFocusable(false);

        JTableHeader header = getTableHeader();
        header.setReorderingAllowed(false);
        header.setBackground(AppTheme.CARD_ALT);
        header.setForeground(AppTheme.MUTED);
        header.setFont(AppTheme.fontBold(12));
        header.setOpaque(true);

        getColumnModel().getColumn(0).setPreferredWidth(150);
        getColumnModel().getColumn(1).setPreferredWidth(170);
        getColumnModel().getColumn(2).setPreferredWidth(110);
        getColumnModel().getColumn(3).setPreferredWidth(110);
        getColumnModel().getColumn(4).setPreferredWidth(120);
        getColumnModel().getColumn(5).setPreferredWidth(240);

        DefaultTableCellRenderer base = new ZebraRenderer();
        setDefaultRenderer(Object.class, base);
        getColumnModel().getColumn(3).setCellRenderer(new AmountRenderer());
    }

    public void setTransactions(List<Transaction> rows) {
        model.setRows(rows);
    }

    public void applyFilter(String query, TransactionType type) {
        sorter.setRowFilter(new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends Model, ? extends Integer> entry) {
                Transaction tx = model.rows.get(entry.getIdentifier());
                boolean typeOk = type == null || tx.getType() == type;
                if (!typeOk) {
                    return false;
                }
                if (query == null || query.isBlank()) {
                    return true;
                }
                String q = query.toLowerCase();
                return tx.getTransactionId().toLowerCase().contains(q)
                        || tx.getDescription().toLowerCase().contains(q)
                        || tx.getType().getDisplayName().toLowerCase().contains(q)
                        || tx.getAmount().toPlainString().contains(q);
            }
        });
    }

    private static final class Model extends AbstractTableModel {
        private final String[] columns = {
                "Transaction ID", "Date and time", "Type", "Amount", "Balance after", "Description"
        };
        private List<Transaction> rows = new ArrayList<>();

        void setRows(List<Transaction> rows) {
            this.rows = new ArrayList<>(rows);
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Transaction tx = rows.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> tx.getTransactionId();
                case 1 -> TIME.format(tx.getOccurredAt());
                case 2 -> tx.getType().getDisplayName();
                case 3 -> tx;
                case 4 -> MoneyFormatter.format(tx.getBalanceAfter());
                case 5 -> tx.getDescription();
                default -> "";
            };
        }
    }

    private static class ZebraRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10));
            if (!isSelected) {
                label.setBackground(row % 2 == 0 ? AppTheme.CARD : AppTheme.CARD_ALT);
                label.setForeground(AppTheme.TEXT);
            }
            return label;
        }
    }

    private static final class AmountRenderer extends ZebraRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Transaction tx = (Transaction) value;
            String text = (tx.getType().isCredit() ? "+ " : "− ") + MoneyFormatter.format(tx.getAmount());
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
            if (!isSelected) {
                label.setForeground(tx.getType().isCredit() ? AppTheme.SUCCESS : AppTheme.DANGER);
            }
            return label;
        }
    }
}
