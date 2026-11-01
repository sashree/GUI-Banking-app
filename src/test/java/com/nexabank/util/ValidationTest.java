package com.nexabank.util;

import com.nexabank.exception.InvalidAmountException;
import com.nexabank.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationTest {

    @Test
    void rejectsZeroAndNegativeAmounts() {
        assertThrows(InvalidAmountException.class, () -> Validation.requirePositiveAmount("0"));
        assertThrows(InvalidAmountException.class, () -> Validation.requirePositiveAmount("-10"));
        assertThrows(InvalidAmountException.class, () -> Validation.requirePositiveAmount("abc"));
    }

    @Test
    void acceptsScaledPositiveAmount() throws Exception {
        assertEquals(new BigDecimal("12.50"), Validation.requirePositiveAmount("12.5"));
    }

    @Test
    void rejectsInvalidAccountAndPin() {
        assertThrows(ValidationException.class, () -> Validation.requireAccountNumber("123"));
        assertThrows(ValidationException.class, () -> Validation.requirePin("12".toCharArray()));
        assertThrows(ValidationException.class, () -> Validation.requireEmail("not-an-email"));
    }
}
