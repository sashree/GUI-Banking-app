package com.nexabank.util;

import com.nexabank.exception.InvalidAmountException;
import com.nexabank.exception.ValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Validation {

    private Validation() {
    }

    public static String requireName(String name) throws ValidationException {
        if (name == null || name.trim().length() < 3) {
            throw new ValidationException("Please enter the account holder's full name.");
        }
        return name.trim();
    }

    public static String requireEmail(String email) throws ValidationException {
        if (email == null || !email.trim().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("Please enter a valid email address.");
        }
        return email.trim().toLowerCase();
    }

    public static String requirePhone(String phone) throws ValidationException {
        if (phone == null || !phone.trim().matches("^[0-9]{10}$")) {
            throw new ValidationException("Phone number must be exactly 10 digits.");
        }
        return phone.trim();
    }

    public static String requireAccountNumber(String accountNumber) throws ValidationException {
        if (accountNumber == null || !accountNumber.trim().matches("^[0-9]{10}$")) {
            throw new ValidationException("Account number must be exactly 10 digits.");
        }
        return accountNumber.trim();
    }

    public static char[] requirePin(char[] pin) throws ValidationException {
        if (pin == null || pin.length != 4 || !String.valueOf(pin).matches("^[0-9]{4}$")) {
            throw new ValidationException("PIN must be exactly 4 digits.");
        }
        return pin;
    }

    public static BigDecimal requirePositiveAmount(String raw) throws InvalidAmountException {
        if (raw == null || raw.isBlank()) {
            throw new InvalidAmountException("Please enter an amount.");
        }
        try {
            BigDecimal amount = new BigDecimal(raw.trim()).setScale(2, RoundingMode.HALF_UP);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidAmountException("Amount must be greater than zero.");
            }
            if (amount.compareTo(new BigDecimal("10000000.00")) > 0) {
                throw new InvalidAmountException("Amount exceeds the maximum allowed for this simulator.");
            }
            return amount;
        } catch (NumberFormatException ex) {
            throw new InvalidAmountException("Please enter a valid numeric amount.");
        }
    }
}
