package com.nexabank.exception;

public class AccountNotFoundException extends BankingException {

    public AccountNotFoundException(String accountNumber) {
        super("No account found for number " + accountNumber + ".");
    }
}
