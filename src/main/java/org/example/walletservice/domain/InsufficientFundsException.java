package org.example.walletservice.domain;

import java.util.UUID;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(UUID walletId, long amount) {
        super("Insufficient funds on wallet " + walletId + " for amount " + amount);
    }
}
