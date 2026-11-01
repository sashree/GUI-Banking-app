package com.nexabank.model;

public enum TransactionType {
    DEPOSIT("Deposit", true),
    WITHDRAWAL("Withdrawal", false),
    TRANSFER_IN("Transfer In", true),
    TRANSFER_OUT("Transfer Out", false);

    private final String displayName;
    private final boolean credit;

    TransactionType(String displayName, boolean credit) {
        this.displayName = displayName;
        this.credit = credit;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isCredit() {
        return credit;
    }
}
