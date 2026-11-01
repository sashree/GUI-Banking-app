package com.nexabank.service.operation;

import com.nexabank.exception.BankingException;
import com.nexabank.model.Transaction;

import java.sql.Connection;

/**
 * Abstract command for a money movement. Subclasses override {@link #perform(Connection)}
 * so {@code BankingService} can execute different operations polymorphically.
 */
public abstract class BankOperation {

    private final String operationName;

    protected BankOperation(String operationName) {
        this.operationName = operationName;
    }

    public String getOperationName() {
        return operationName;
    }

    public abstract Transaction perform(Connection connection) throws BankingException, Exception;
}
