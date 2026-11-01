package com.nexabank.service;

import com.nexabank.exception.BankingException;
import com.nexabank.model.SavingsAccount;
import com.nexabank.repository.AccountRepository;
import com.nexabank.util.PinHasher;

import java.math.BigDecimal;

public class DemoDataSeeder {

    private final AccountRepository accounts;
    private final BankingService banking;

    public DemoDataSeeder(AccountRepository accounts, BankingService banking) {
        this.accounts = accounts;
        this.banking = banking;
    }

    public void seedIfEmpty() throws BankingException {
        if (!accounts.isEmpty()) {
            return;
        }

        SavingsAccount priya = create("1001001001", "Priya Sharma", "priya.sharma@nexabank.demo", "9876500001", "1234");
        SavingsAccount arjun = create("1001001002", "Arjun Mehta", "arjun.mehta@nexabank.demo", "9876500002", "5678");
        SavingsAccount ananya = create("1001001003", "Ananya Reddy", "ananya.reddy@nexabank.demo", "9876500003", "9012");

        accounts.save(priya);
        accounts.save(arjun);
        accounts.save(ananya);

        banking.deposit(priya.getAccountNumber(), new BigDecimal("85000.00"), "Initial account funding");
        banking.deposit(arjun.getAccountNumber(), new BigDecimal("42000.00"), "Initial account funding");
        banking.deposit(ananya.getAccountNumber(), new BigDecimal("125000.00"), "Initial account funding");

        banking.withdraw(priya.getAccountNumber(), new BigDecimal("2500.00"), "ATM withdrawal");
        banking.transfer(priya.getAccountNumber(), arjun.getAccountNumber(), new BigDecimal("4000.00"),
                "Rent share");
        banking.deposit(ananya.getAccountNumber(), new BigDecimal("7500.00"), "Salary credit");
        banking.withdraw(arjun.getAccountNumber(), new BigDecimal("1200.00"), "Grocery payment");
        banking.transfer(ananya.getAccountNumber(), priya.getAccountNumber(), new BigDecimal("3000.00"),
                "Gift transfer");
    }

    private SavingsAccount create(String number, String name, String email, String phone, String pin) {
        SavingsAccount account = new SavingsAccount(number, name, email, phone);
        String salt = PinHasher.newSalt();
        account.replacePinCredentials(PinHasher.hash(pin.toCharArray(), salt), salt);
        return account;
    }
}
