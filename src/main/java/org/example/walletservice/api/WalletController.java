package org.example.walletservice.api;

import org.example.walletservice.api.dto.OperationType;
import org.example.walletservice.api.dto.WalletBalanceResponse;
import org.example.walletservice.api.dto.WalletOperationRequest;
import org.example.walletservice.service.WalletService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/wallet")
    public ResponseEntity<WalletBalanceResponse> processOperation(
            @Valid @RequestBody WalletOperationRequest request) {

        UUID walletId = request.getWalletId();
        long amount = request.getAmount();
        OperationType type = request.getOperationType();

        long newBalance;
        switch (type) {
            case DEPOSIT:
                newBalance = walletService.deposit(walletId, amount);
                break;
            case WITHDRAW:
                newBalance = walletService.withdraw(walletId, amount);
                break;
            default:
                throw new IllegalArgumentException("Unsupported operation type: " + type);
        }

        WalletBalanceResponse response = new WalletBalanceResponse(walletId, newBalance);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<WalletBalanceResponse> getBalance(
            @PathVariable("walletId") UUID walletId) {

        long balance = walletService.getBalance(walletId);
        WalletBalanceResponse response = new WalletBalanceResponse(walletId, balance);
        return ResponseEntity.ok(response);
    }
}
