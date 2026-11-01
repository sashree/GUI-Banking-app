package com.nexabank.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class MoneyFormatter {

    private static final DecimalFormat FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        FORMAT = new DecimalFormat("'₹'#,##0.00", symbols);
        FORMAT.setParseBigDecimal(true);
    }

    private MoneyFormatter() {
    }

    public static synchronized String format(BigDecimal amount) {
        if (amount == null) {
            return "₹0.00";
        }
        return FORMAT.format(amount);
    }
}
