package com.nexabank.service.operation;

import com.nexabank.exception.AccountNotFoundException;
import com.nexabank.exception.BankingException;
import com.nexabank.exception.InsufficientFundsException;
import com.nexabank.exception.InvalidAmountException;
import com.nexabank.model.SavingsAccount;
import com.nexabank.model.Transaction;
import com.nexabank.model.TransactionType;
import com.nexabank.repository.AccountRepository;
import com.nexabank.repository.TransactionRepository;
import com.nexabank.util.IdGenerator;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;

public class WithdrawOperation extends BankOperation {

    private final AccountRepository accounts;
    private final TransactionRepository transactions;
    private final String accountNumber;
    private final BigDecimal amount;
    private final String description;

    public WithdrawOperation(AccountRepository accounts,
                             TransactionRepository transactions,
                             String accountNumber,
                             BigDecimal amount,
                             String description) {
        super("Withdrawal");
        this.accounts = accounts;
        this.transactions = transactions;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.description = description;
    }

    @Override
    public Transaction perform(Connection connection) throws BankingException, Exception {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be greater than zero.");
        }
        SavingsAccount account = accounts.findByNumber(connection, accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        if (!account.canDebit(amount)) {
            throw new InsufficientFundsException(account.getBalance(), amount);
        }
        account.debit(amount);
        accounts.persist(connection, account);
        Transaction transaction = new Transaction(
                IdGenerator.nextTransactionId(),
                account.getAccountNumber(),
                TransactionType.WITHDRAWAL,
                amount,
                account.getBalance(),
                description,
                null,
                LocalDateTime.now()
        );
        transactions.insert(connection, transaction);
        return transaction;
    }
}
