package com.nexabank.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends BankingException {

    public InsufficientFundsException(BigDecimal available, BigDecimal requested) {
        super("Insufficient funds. Available: " + available + ", requested: " + requested + ".");
    }
}
