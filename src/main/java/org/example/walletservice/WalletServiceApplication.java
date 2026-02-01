package org.example.walletservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.example.walletservice.domain.Wallet;
import org.example.walletservice.repository.WalletRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@SpringBootApplication
public class WalletServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WalletServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner initTestWallet(WalletRepository walletRepository) {
        return args -> {
            // Зададим фиксированный UUID, чтобы ты мог им пользоваться в запросах
            UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");

            if (!walletRepository.findById(id).isPresent()) {
                Wallet wallet = new Wallet(id, 10_000L); // 100.00 в копейках
                walletRepository.save(wallet);
                System.out.println("Test wallet created with id " + id + " and balance 10000");
            }
        };
    }
}
