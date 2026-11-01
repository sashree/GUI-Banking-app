package com.nexabank.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PinHasherTest {

    @Test
    void hashesDifferFromPlaintextAndVerifySucceeds() {
        String salt = PinHasher.newSalt();
        String hash = PinHasher.hash("1234".toCharArray(), salt);
        assertNotEquals("1234", hash);
        assertTrue(PinHasher.verify("1234".toCharArray(), salt, hash));
        assertFalse(PinHasher.verify("0000".toCharArray(), salt, hash));
    }

    @Test
    void differentSaltsProduceDifferentHashes() {
        String first = PinHasher.hash("1234".toCharArray(), PinHasher.newSalt());
        String second = PinHasher.hash("1234".toCharArray(), PinHasher.newSalt());
        assertNotEquals(first, second);
    }
}
