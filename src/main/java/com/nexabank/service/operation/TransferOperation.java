package com.nexabank.service.operation;

import com.nexabank.exception.AccountNotFoundException;
import com.nexabank.exception.BankingException;
import com.nexabank.exception.InsufficientFundsException;
import com.nexabank.exception.InvalidAmountException;
import com.nexabank.exception.ValidationException;
import com.nexabank.model.SavingsAccount;
import com.nexabank.model.Transaction;
import com.nexabank.model.TransactionType;
import com.nexabank.repository.AccountRepository;
import com.nexabank.repository.TransactionRepository;
import com.nexabank.util.IdGenerator;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;

public class TransferOperation extends BankOperation {

    private final AccountRepository accounts;
    private final TransactionRepository transactions;
    private final String fromAccount;
    private final String toAccount;
    private final BigDecimal amount;
    private final String note;

    public TransferOperation(AccountRepository accounts,
                             TransactionRepository transactions,
                             String fromAccount,
                             String toAccount,
                             BigDecimal amount,
                             String note) {
        super("Fund Transfer");
        this.accounts = accounts;
        this.transactions = transactions;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.note = note;
    }

    @Override
    public Transaction perform(Connection connection) throws BankingException, Exception {
        if (fromAccount.equals(toAccount)) {
            throw new ValidationException("You cannot transfer funds to the same account.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Transfer amount must be greater than zero.");
        }
        SavingsAccount sender = accounts.findByNumber(connection, fromAccount)
                .orElseThrow(() -> new AccountNotFoundException(fromAccount));
        SavingsAccount recipient = accounts.findByNumber(connection, toAccount)
                .orElseThrow(() -> new AccountNotFoundException(toAccount));
        if (!sender.canDebit(amount)) {
            throw new InsufficientFundsException(sender.getBalance(), amount);
        }

        sender.debit(amount);
        recipient.credit(amount);
        accounts.persist(connection, sender);
        accounts.persist(connection, recipient);

        LocalDateTime now = LocalDateTime.now();
        String description = note == null || note.isBlank()
                ? "Transfer to " + recipient.getHolderName()
                : note.trim();

        Transaction outgoing = new Transaction(
                IdGenerator.nextTransactionId(),
                sender.getAccountNumber(),
                TransactionType.TRANSFER_OUT,
                amount,
                sender.getBalance(),
                description + " → " + recipient.getAccountNumber(),
                recipient.getAccountNumber(),
                now
        );
        Transaction incoming = new Transaction(
                IdGenerator.nextTransactionId(),
                recipient.getAccountNumber(),
                TransactionType.TRANSFER_IN,
                amount,
                recipient.getBalance(),
                "Received from " + sender.getHolderName() + " ← " + sender.getAccountNumber(),
                sender.getAccountNumber(),
                now
        );
        transactions.insert(connection, outgoing);
        transactions.insert(connection, incoming);
        return outgoing;
    }
}
