package com.nexabank.model;

/**
 * Abstraction used by domain objects that can produce a human-readable audit line.
 */
public interface Auditable {

    String auditSummary();
}
