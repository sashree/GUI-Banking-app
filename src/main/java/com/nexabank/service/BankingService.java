package com.nexabank.service;

import com.nexabank.exception.AccountNotFoundException;
import com.nexabank.exception.BankingException;
import com.nexabank.model.SavingsAccount;
import com.nexabank.model.Transaction;
import com.nexabank.repository.AccountRepository;
import com.nexabank.repository.DatabaseManager;
import com.nexabank.repository.TransactionRepository;
import com.nexabank.service.operation.BankOperation;
import com.nexabank.service.operation.DepositOperation;
import com.nexabank.service.operation.TransferOperation;
import com.nexabank.service.operation.WithdrawOperation;

import java.math.BigDecimal;
import java.util.List;

public class BankingService {

    private final DatabaseManager database;
    private final AccountRepository accounts;
    private final TransactionRepository transactions;

    public BankingService(DatabaseManager database, AccountRepository accounts, TransactionRepository transactions) {
        this.database = database;
        this.accounts = accounts;
        this.transactions = transactions;
    }

    public SavingsAccount getAccount(String accountNumber) throws BankingException {
        return accounts.findByNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    public List<SavingsAccount> listAccounts() throws BankingException {
        return accounts.findAll();
    }

    public List<Transaction> history(String accountNumber) throws BankingException {
        return transactions.findByAccount(accountNumber);
    }

    public Transaction deposit(String accountNumber, BigDecimal amount, String description) throws BankingException {
        return execute(new DepositOperation(accounts, transactions, accountNumber, amount, description));
    }

    public Transaction withdraw(String accountNumber, BigDecimal amount, String description) throws BankingException {
        return execute(new WithdrawOperation(accounts, transactions, accountNumber, amount, description));
    }

    public Transaction transfer(String fromAccount, String toAccount, BigDecimal amount, String note)
            throws BankingException {
        return execute(new TransferOperation(accounts, transactions, fromAccount, toAccount, amount, note));
    }

    /**
     * Polymorphic entry point: any {@link BankOperation} subclass can be executed here.
     */
    public Transaction execute(BankOperation operation) throws BankingException {
        return database.inTransaction(connection -> {
            try {
                return operation.perform(connection);
            } catch (BankingException e) {
                throw e;
            } catch (Exception e) {
                throw new BankingException(operation.getOperationName() + " failed.", e);
            }
        });
    }
}
