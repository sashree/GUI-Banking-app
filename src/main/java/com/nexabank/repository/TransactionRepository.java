package com.nexabank.repository;

import com.nexabank.exception.BankingException;
import com.nexabank.model.Transaction;
import com.nexabank.model.TransactionType;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {

    private final DatabaseManager database;

    public TransactionRepository(DatabaseManager database) {
        this.database = database;
    }

    public void insert(Transaction transaction) throws BankingException {
        database.inTransaction(connection -> {
            insert(connection, transaction);
            return null;
        });
    }

    public void insert(Connection connection, Transaction transaction) throws SQLException {
        String sql = """
                INSERT INTO transactions(transaction_id, account_number, type, amount, balance_after,
                    description, counterparty_account, occurred_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, transaction.getTransactionId());
            statement.setString(2, transaction.getAccountNumber());
            statement.setString(3, transaction.getType().name());
            statement.setString(4, transaction.getAmount().toPlainString());
            statement.setString(5, transaction.getBalanceAfter().toPlainString());
            statement.setString(6, transaction.getDescription());
            statement.setString(7, transaction.getCounterpartyAccount());
            statement.setString(8, transaction.getOccurredAt().toString());
            statement.executeUpdate();
        }
    }

    public List<Transaction> findByAccount(String accountNumber) throws BankingException {
        return database.inTransaction(connection -> findByAccount(connection, accountNumber));
    }

    public List<Transaction> findByAccount(Connection connection, String accountNumber) throws SQLException {
        String sql = """
                SELECT * FROM transactions
                WHERE account_number = ?
                ORDER BY occurred_at DESC, transaction_id DESC
                """;
        List<Transaction> rows = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, accountNumber);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    rows.add(map(rs));
                }
            }
        }
        return rows;
    }

    private Transaction map(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getString("transaction_id"),
                rs.getString("account_number"),
                TransactionType.valueOf(rs.getString("type")),
                new BigDecimal(rs.getString("amount")),
                new BigDecimal(rs.getString("balance_after")),
                rs.getString("description"),
                rs.getString("counterparty_account"),
                LocalDateTime.parse(rs.getString("occurred_at"))
        );
    }
}
