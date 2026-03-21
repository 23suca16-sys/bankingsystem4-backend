package com.bank.service;

import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.entity.User;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository,
                          UserRepository userRepository,
                          TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    // Create account
    public Account createAccount(Long userId, Account account) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        account.setUser(user);
        return accountRepository.save(account);
    }

    // Get accounts by user
    public List<Account> getAccountsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Collections.emptyList();
        }
        List<Account> accounts = accountRepository.findByUserId(userId);
        return accounts != null ? accounts : Collections.emptyList();
    }

    // Deposit money
    public Account deposit(Long accountId, double amount) {
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null) {
            return null;
        }
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setType("DEPOSIT");
        transactionRepository.save(transaction);

        return account;
    }

    // Withdraw money
    public Account withdraw(Long accountId, double amount) {
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null) {
            return null;
        }
        if (account.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setType("WITHDRAW");
        transactionRepository.save(transaction);

        return account;
    }

    // Get transaction history
    public List<Transaction> getTransactions(Long accountId) {
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null) {
            return Collections.emptyList();
        }
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        return transactions != null ? transactions : Collections.emptyList();
    }
}
