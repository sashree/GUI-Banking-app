package com.nexabank.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Encapsulates shared account state. Concrete products such as {@link SavingsAccount}
 * override type-specific rules like minimum balance.
 */
public abstract class AbstractAccount implements Auditable {

    private final String accountNumber;
    private String holderName;
    private String email;
    private String phone;
    private String pinHash;
    private String pinSalt;
    private BigDecimal balance;
    private BigDecimal totalDeposited;
    private BigDecimal totalWithdrawn;
    private final LocalDateTime openedAt;

    protected AbstractAccount(String accountNumber,
                              String holderName,
                              String email,
                              String phone,
                              String pinHash,
                              String pinSalt,
                              BigDecimal balance,
                              BigDecimal totalDeposited,
                              BigDecimal totalWithdrawn,
                              LocalDateTime openedAt) {
        this.accountNumber = Objects.requireNonNull(accountNumber);
        this.holderName = holderName;
        this.email = email;
        this.phone = phone;
        this.pinHash = pinHash;
        this.pinSalt = pinSalt;
        this.balance = scale(balance);
        this.totalDeposited = scale(totalDeposited);
        this.totalWithdrawn = scale(totalWithdrawn);
        this.openedAt = openedAt == null ? LocalDateTime.now() : openedAt;
    }

    protected AbstractAccount(String accountNumber, String holderName, String email, String phone) {
        this(accountNumber, holderName, email, phone, "", "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                LocalDateTime.now());
    }

    public abstract AccountType getAccountType();

    public abstract BigDecimal getMinimumBalance();

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPinHash() {
        return pinHash;
    }

    public String getPinSalt() {
        return pinSalt;
    }

    public void replacePinCredentials(String pinHash, String pinSalt) {
        this.pinHash = pinHash;
        this.pinSalt = pinSalt;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getTotalDeposited() {
        return totalDeposited;
    }

    public BigDecimal getTotalWithdrawn() {
        return totalWithdrawn;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public void credit(BigDecimal amount) {
        BigDecimal value = scale(amount);
        this.balance = this.balance.add(value);
        this.totalDeposited = this.totalDeposited.add(value);
    }

    public void debit(BigDecimal amount) {
        BigDecimal value = scale(amount);
        this.balance = this.balance.subtract(value);
        this.totalWithdrawn = this.totalWithdrawn.add(value);
    }

    public boolean canDebit(BigDecimal amount) {
        BigDecimal projected = balance.subtract(scale(amount));
        return projected.compareTo(getMinimumBalance()) >= 0;
    }

    protected static BigDecimal scale(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String auditSummary() {
        return getAccountType().getDisplayName() + " " + accountNumber + " held by " + holderName;
    }
}
