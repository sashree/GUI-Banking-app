package com.nexabank.util;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public final class IdGenerator {

    private static final AtomicLong SEQUENCE = new AtomicLong(System.currentTimeMillis());

    private IdGenerator() {
    }

    public static String nextTransactionId() {
        long stamp = SEQUENCE.incrementAndGet();
        int noise = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "TXN" + stamp + noise;
    }

    public static String nextAccountNumber(String lastAccountNumber) {
        if (lastAccountNumber == null || lastAccountNumber.isBlank()) {
            return "1001001001";
        }
        long value = Long.parseLong(lastAccountNumber);
        return String.valueOf(value + 1);
    }
}
