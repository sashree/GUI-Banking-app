package com.nexabank.service;

import com.nexabank.exception.AccountNotFoundException;
import com.nexabank.exception.AuthenticationException;
import com.nexabank.exception.InsufficientFundsException;
import com.nexabank.exception.ValidationException;
import com.nexabank.model.SavingsAccount;
import com.nexabank.model.TransactionType;
import com.nexabank.repository.AccountRepository;
import com.nexabank.repository.DatabaseManager;
import com.nexabank.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BankingServiceTest {

    @TempDir
    Path tempDir;

    private AuthService auth;
    private BankingService banking;

    @BeforeEach
    void setUp() throws Exception {
        DatabaseManager database = new DatabaseManager(tempDir.resolve("test.db"));
        AccountRepository accounts = new AccountRepository(database);
        TransactionRepository transactions = new TransactionRepository(database);
        auth = new AuthService(accounts);
        banking = new BankingService(database, accounts, transactions);
        new DemoDataSeeder(accounts, banking).seedIfEmpty();
    }

    @Test
    void demoLoginSucceedsAndRejectsBadPin() throws Exception {
        SavingsAccount priya = auth.login("1001001001", "1234".toCharArray());
        assertEquals("Priya Sharma", priya.getHolderName());
        assertThrows(AuthenticationException.class, () -> auth.login("1001001001", "0000".toCharArray()));
    }

    @Test
    void depositIncreasesBalanceAndHistory() throws Exception {
        BigDecimal before = banking.getAccount("1001001001").getBalance();
        banking.deposit("1001001001", new BigDecimal("1000.00"), "Test deposit");
        SavingsAccount after = banking.getAccount("1001001001");
        assertEquals(0, after.getBalance().compareTo(before.add(new BigDecimal("1000.00"))));
        assertTrue(banking.history("1001001001").stream().anyMatch(tx -> tx.getType() == TransactionType.DEPOSIT));
    }

    @Test
    void withdrawRejectsOverdraft() {
        assertThrows(InsufficientFundsException.class,
                () -> banking.withdraw("1001001002", new BigDecimal("999999.00"), "Too much"));
    }

    @Test
    void transferMovesFundsAtomically() throws Exception {
        BigDecimal fromBefore = banking.getAccount("1001001001").getBalance();
        BigDecimal toBefore = banking.getAccount("1001001002").getBalance();
        banking.transfer("1001001001", "1001001002", new BigDecimal("500.00"), "Test transfer");
        assertEquals(0, banking.getAccount("1001001001").getBalance()
                .compareTo(fromBefore.subtract(new BigDecimal("500.00"))));
        assertEquals(0, banking.getAccount("1001001002").getBalance()
                .compareTo(toBefore.add(new BigDecimal("500.00"))));
        assertTrue(banking.history("1001001001").stream().anyMatch(tx -> tx.getType() == TransactionType.TRANSFER_OUT));
        assertTrue(banking.history("1001001002").stream().anyMatch(tx -> tx.getType() == TransactionType.TRANSFER_IN));
    }

    @Test
    void transferToSameAccountIsRejected() {
        assertThrows(ValidationException.class,
                () -> banking.transfer("1001001001", "1001001001", new BigDecimal("10.00"), "self"));
    }

    @Test
    void unknownRecipientIsRejected() {
        assertThrows(AccountNotFoundException.class,
                () -> banking.transfer("1001001001", "1999999999", new BigDecimal("10.00"), "ghost"));
    }

    @Test
    void registerCreatesPersistedAccount() throws Exception {
        SavingsAccount created = auth.register("Neha Kapoor", "neha@nexabank.demo", "9123456789", "4321".toCharArray());
        SavingsAccount reloaded = auth.login(created.getAccountNumber(), "4321".toCharArray());
        assertEquals("Neha Kapoor", reloaded.getHolderName());
        assertEquals(0, reloaded.getBalance().compareTo(BigDecimal.ZERO.setScale(2)));
    }
}
