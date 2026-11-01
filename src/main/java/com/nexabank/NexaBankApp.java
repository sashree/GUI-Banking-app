package com.nexabank;

import com.nexabank.exception.BankingException;
import com.nexabank.repository.AccountRepository;
import com.nexabank.repository.DatabaseManager;
import com.nexabank.repository.TransactionRepository;
import com.nexabank.service.AuthService;
import com.nexabank.service.BankingService;
import com.nexabank.service.DemoDataSeeder;
import com.nexabank.ui.LoginFrame;
import com.nexabank.ui.theme.AppTheme;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.EventQueue;

/**
 * Application entry point. GUI work is scheduled on the Event Dispatch Thread.
 */
public final class NexaBankApp {

    private NexaBankApp() {
    }

    public static void main(String[] args) {
        System.setProperty("apple.awt.application.name", "NexaBank");
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
                // Custom painting does not depend on the platform look and feel.
            }
            AppTheme.install();

            DatabaseManager database = DatabaseManager.localDefault();
            AccountRepository accounts = new AccountRepository(database);
            TransactionRepository transactions = new TransactionRepository(database);
            BankingService banking = new BankingService(database, accounts, transactions);
            AuthService auth = new AuthService(accounts);

            try {
                new DemoDataSeeder(accounts, banking).seedIfEmpty();
            } catch (BankingException e) {
                System.err.println("Unable to prepare demo data: " + e.getMessage());
            }

            LoginFrame login = new LoginFrame(auth, banking);
            login.setVisible(true);
            EventQueue.invokeLater(login::toFront);
        });
    }
}
