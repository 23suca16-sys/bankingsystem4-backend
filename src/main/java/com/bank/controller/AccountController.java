package com.bank.controller;

import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) { this.accountService = accountService; }

    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createAccount(@PathVariable Long userId, @RequestBody Account account) {
        Account createdAccount = accountService.createAccount(userId, account);
        if (createdAccount == null) {
            return ResponseEntity.ok(Map.of("message", "User not found"));
        }
        return ResponseEntity.ok(createdAccount);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Account>> getAccountsByUser(@PathVariable Long userId) {
        List<Account> accounts = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(accounts != null ? accounts : Collections.emptyList());
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<?> deposit(@PathVariable Long accountId, @RequestParam double amount) {
        Account updatedAccount = accountService.deposit(accountId, amount);
        if (updatedAccount == null) {
            return ResponseEntity.ok(Map.of("message", "Account not found"));
        }
        return ResponseEntity.ok(updatedAccount);
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable Long accountId, @RequestParam double amount) {
        Account updatedAccount = accountService.withdraw(accountId, amount);
        if (updatedAccount == null) {
            return ResponseEntity.ok(Map.of("message", "Account not found"));
        }
        return ResponseEntity.ok(updatedAccount);
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable Long accountId) {
        List<Transaction> transactions = accountService.getTransactions(accountId);
        return ResponseEntity.ok(transactions != null ? transactions : Collections.emptyList());
    }
}
