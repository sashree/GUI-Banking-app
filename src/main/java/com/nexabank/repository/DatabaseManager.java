package com.nexabank.repository;

import com.nexabank.exception.BankingException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Owns the SQLite file location and schema. One instance is shared by repositories.
 */
public class DatabaseManager {

    @FunctionalInterface
    public interface SqlWork<T> {
        T run(Connection connection) throws Exception;
    }

    private final String jdbcUrl;

    public DatabaseManager(Path databaseFile) {
        try {
            Files.createDirectories(databaseFile.getParent());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create data directory", e);
        }
        this.jdbcUrl = "jdbc:sqlite:" + databaseFile.toAbsolutePath();
        initialize();
    }

    public static DatabaseManager localDefault() {
        Path home = Path.of(System.getProperty("user.home"), ".nexabank", "nexabank.db");
        return new DatabaseManager(home);
    }

    public Connection open() throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcUrl);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            statement.execute("PRAGMA busy_timeout = 5000");
        }
        return connection;
    }

    public <T> T inTransaction(SqlWork<T> work) throws BankingException {
        try (Connection connection = open()) {
            connection.setAutoCommit(false);
            try {
                T result = work.run(connection);
                connection.commit();
                return result;
            } catch (BankingException e) {
                connection.rollback();
                throw e;
            } catch (Exception e) {
                connection.rollback();
                throw new BankingException("The operation could not be completed.", e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (BankingException e) {
            throw e;
        } catch (SQLException e) {
            throw new BankingException("Unable to reach the local database.", e);
        }
    }

    private void initialize() {
        try (Connection connection = open(); Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS accounts (
                        account_number TEXT PRIMARY KEY,
                        holder_name TEXT NOT NULL,
                        email TEXT NOT NULL,
                        phone TEXT NOT NULL,
                        pin_hash TEXT NOT NULL,
                        pin_salt TEXT NOT NULL,
                        balance TEXT NOT NULL,
                        total_deposited TEXT NOT NULL,
                        total_withdrawn TEXT NOT NULL,
                        opened_at TEXT NOT NULL
                    )
                    """);
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS transactions (
                        transaction_id TEXT PRIMARY KEY,
                        account_number TEXT NOT NULL,
                        type TEXT NOT NULL,
                        amount TEXT NOT NULL,
                        balance_after TEXT NOT NULL,
                        description TEXT NOT NULL,
                        counterparty_account TEXT,
                        occurred_at TEXT NOT NULL,
                        FOREIGN KEY (account_number) REFERENCES accounts(account_number)
                    )
                    """);
            statement.execute("""
                    CREATE INDEX IF NOT EXISTS idx_tx_account_time
                    ON transactions(account_number, occurred_at DESC)
                    """);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to initialize database schema", e);
        }
    }
}
