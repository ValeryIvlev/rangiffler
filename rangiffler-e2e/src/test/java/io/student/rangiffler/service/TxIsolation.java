package io.student.rangiffler.service;

import java.sql.Connection;

public enum TxIsolation {
    READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
    READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
    REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
    SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

    private final int jdbcLevel;

    TxIsolation(int jdbcLevel) {
        this.jdbcLevel = jdbcLevel;
    }

    public int jdbcLevel() {
        return jdbcLevel;
    }
}
