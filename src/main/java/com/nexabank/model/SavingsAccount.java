package com.nexabank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Savings product used by NexaBank. Minimum balance is zero, which still
 * forbids overdrafts because {@link #canDebit(BigDecimal)} checks that floor.
 */
public class SavingsAccount extends AbstractAccount {

    public SavingsAccount(String accountNumber,
                          String holderName,
                          String email,
                          String phone,
                          String pinHash,
                          String pinSalt,
                          BigDecimal balance,
                          BigDecimal totalDeposited,
                          BigDecimal totalWithdrawn,
                          LocalDateTime openedAt) {
        super(accountNumber, holderName, email, phone, pinHash, pinSalt, balance, totalDeposited, totalWithdrawn,
                openedAt);
    }

    public SavingsAccount(String accountNumber, String holderName, String email, String phone) {
        super(accountNumber, holderName, email, phone);
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.SAVINGS;
    }

    @Override
    public BigDecimal getMinimumBalance() {
        return BigDecimal.ZERO.setScale(2);
    }
}
