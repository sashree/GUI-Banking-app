package com.nexabank.repository;

import com.nexabank.exception.BankingException;
import com.nexabank.model.SavingsAccount;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountRepository {

    private final DatabaseManager database;

    public AccountRepository(DatabaseManager database) {
        this.database = database;
    }

    public void save(SavingsAccount account) throws BankingException {
        database.inTransaction(connection -> {
            persist(connection, account);
            return null;
        });
    }

    public void persist(Connection connection, SavingsAccount account) throws SQLException {
        String sql = """
                INSERT INTO accounts(account_number, holder_name, email, phone, pin_hash, pin_salt,
                    balance, total_deposited, total_withdrawn, opened_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(account_number) DO UPDATE SET
                    holder_name = excluded.holder_name,
                    email = excluded.email,
                    phone = excluded.phone,
                    pin_hash = excluded.pin_hash,
                    pin_salt = excluded.pin_salt,
                    balance = excluded.balance,
                    total_deposited = excluded.total_deposited,
                    total_withdrawn = excluded.total_withdrawn
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, account.getAccountNumber());
            statement.setString(2, account.getHolderName());
            statement.setString(3, account.getEmail());
            statement.setString(4, account.getPhone());
            statement.setString(5, account.getPinHash());
            statement.setString(6, account.getPinSalt());
            statement.setString(7, account.getBalance().toPlainString());
            statement.setString(8, account.getTotalDeposited().toPlainString());
            statement.setString(9, account.getTotalWithdrawn().toPlainString());
            statement.setString(10, account.getOpenedAt().toString());
            statement.executeUpdate();
        }
    }

    public Optional<SavingsAccount> findByNumber(String accountNumber) throws BankingException {
        return database.inTransaction(connection -> findByNumber(connection, accountNumber));
    }

    public Optional<SavingsAccount> findByNumber(Connection connection, String accountNumber) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, accountNumber);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
                return Optional.empty();
            }
        }
    }

    public List<SavingsAccount> findAll() throws BankingException {
        return database.inTransaction(connection -> {
            List<SavingsAccount> accounts = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM accounts ORDER BY account_number");
                 ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    accounts.add(map(rs));
                }
            }
            return accounts;
        });
    }

    public Optional<String> findLastAccountNumber() throws BankingException {
        return database.inTransaction(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT account_number FROM accounts ORDER BY account_number DESC LIMIT 1");
                 ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getString(1));
                }
                return Optional.empty();
            }
        });
    }

    public boolean isEmpty() throws BankingException {
        return database.inTransaction(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM accounts");
                 ResultSet rs = statement.executeQuery()) {
                return rs.next() && rs.getInt(1) == 0;
            }
        });
    }

    private SavingsAccount map(ResultSet rs) throws SQLException {
        return new SavingsAccount(
                rs.getString("account_number"),
                rs.getString("holder_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("pin_hash"),
                rs.getString("pin_salt"),
                new BigDecimal(rs.getString("balance")),
                new BigDecimal(rs.getString("total_deposited")),
                new BigDecimal(rs.getString("total_withdrawn")),
                LocalDateTime.parse(rs.getString("opened_at"))
        );
    }
}
