package com.nexabank.exception;

public class InvalidAmountException extends BankingException {

    public InvalidAmountException(String message) {
        super(message);
    }
}
