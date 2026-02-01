package org.example.walletservice.service;

import org.example.walletservice.domain.Wallet;
import org.example.walletservice.domain.WalletNotFoundException;
import org.example.walletservice.domain.InsufficientFundsException;
import org.example.walletservice.repository.WalletRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional(readOnly = true)
    public long getBalance(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));
        return wallet.getBalance();
    }

    @Transactional
    public long deposit(UUID walletId, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be > 0");
        }

        Wallet wallet = walletRepository.findByIdForUpdate(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));

        long newBalance = wallet.getBalance() + amount;
        wallet.setBalance(newBalance);
        wallet.setUpdatedAt(Instant.now());

        // Можно не вызывать save(): Hibernate сам обновит при commit
        return newBalance;
    }

    @Transactional
    public long withdraw(UUID walletId, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be > 0");
        }

        Wallet wallet = walletRepository.findByIdForUpdate(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));

        long currentBalance = wallet.getBalance();
        if (currentBalance < amount) {
            throw new InsufficientFundsException(walletId, amount);
        }

        long newBalance = currentBalance - amount;
        wallet.setBalance(newBalance);
        wallet.setUpdatedAt(Instant.now());

        return newBalance;
    }
}
