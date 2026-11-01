package com.nexabank.service;

import com.nexabank.exception.AuthenticationException;
import com.nexabank.exception.BankingException;
import com.nexabank.exception.ValidationException;
import com.nexabank.model.SavingsAccount;
import com.nexabank.repository.AccountRepository;
import com.nexabank.util.IdGenerator;
import com.nexabank.util.PinHasher;
import com.nexabank.util.Validation;

import java.util.Arrays;

public class AuthService {

    private final AccountRepository accounts;

    public AuthService(AccountRepository accounts) {
        this.accounts = accounts;
    }

    public SavingsAccount login(String accountNumber, char[] pin) throws BankingException {
        String number = Validation.requireAccountNumber(accountNumber);
        Validation.requirePin(pin);
        SavingsAccount account = accounts.findByNumber(number)
                .orElseThrow(() -> new AuthenticationException("Invalid account number or PIN."));
        boolean matches = PinHasher.verify(pin, account.getPinSalt(), account.getPinHash());
        Arrays.fill(pin, '\0');
        if (!matches) {
            throw new AuthenticationException("Invalid account number or PIN.");
        }
        return account;
    }

    public SavingsAccount register(String holderName, String email, String phone, char[] pin) throws BankingException {
        String name = Validation.requireName(holderName);
        String mail = Validation.requireEmail(email);
        String mobile = Validation.requirePhone(phone);
        Validation.requirePin(pin);

        String nextNumber = IdGenerator.nextAccountNumber(accounts.findLastAccountNumber().orElse(null));
        String salt = PinHasher.newSalt();
        String hash = PinHasher.hash(pin, salt);
        Arrays.fill(pin, '\0');

        SavingsAccount account = new SavingsAccount(nextNumber, name, mail, mobile);
        account.replacePinCredentials(hash, salt);
        accounts.save(account);
        return account;
    }

    public void changePin(String accountNumber, char[] currentPin, char[] newPin) throws BankingException {
        Validation.requirePin(newPin);
        char[] currentCopy = Arrays.copyOf(currentPin, currentPin.length);
        SavingsAccount account = login(accountNumber, currentPin);
        boolean same = Arrays.equals(newPin, currentCopy);
        Arrays.fill(currentCopy, '\0');
        if (same) {
            throw new ValidationException("Choose a PIN that is different from the current one.");
        }
        String salt = PinHasher.newSalt();
        String hash = PinHasher.hash(newPin, salt);
        Arrays.fill(newPin, '\0');
        account.replacePinCredentials(hash, salt);
        accounts.save(account);
    }
}
