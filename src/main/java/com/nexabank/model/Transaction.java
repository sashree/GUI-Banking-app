package com.nexabank.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction implements Auditable {

    private final String transactionId;
    private final String accountNumber;
    private final TransactionType type;
    private final BigDecimal amount;
    private final BigDecimal balanceAfter;
    private final String description;
    private final String counterpartyAccount;
    private final LocalDateTime occurredAt;

    public Transaction(String transactionId,
                       String accountNumber,
                       TransactionType type,
                       BigDecimal amount,
                       BigDecimal balanceAfter,
                       String description,
                       String counterpartyAccount,
                       LocalDateTime occurredAt) {
        this.transactionId = Objects.requireNonNull(transactionId);
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.balanceAfter = balanceAfter.setScale(2, RoundingMode.HALF_UP);
        this.description = description;
        this.counterpartyAccount = counterpartyAccount;
        this.occurredAt = occurredAt;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public String getDescription() {
        return description;
    }

    public String getCounterpartyAccount() {
        return counterpartyAccount;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String auditSummary() {
        return type.getDisplayName() + " " + amount + " on " + accountNumber;
    }
}
