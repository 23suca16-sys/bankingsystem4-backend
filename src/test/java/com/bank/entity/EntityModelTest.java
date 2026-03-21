package com.bank.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EntityModelTest {

    @Test
    void account_gettersAndSetters_workAsExpected() {
        User user = new User();
        user.setName("Dev");

        Account account = new Account();
        account.setAccountNumber("ACC-11");
        account.setAccountType("CURRENT");
        account.setBalance(500.0);
        account.setUser(user);

        assertEquals("ACC-11", account.getAccountNumber());
        assertEquals("CURRENT", account.getAccountType());
        assertEquals(500.0, account.getBalance());
        assertEquals(user, account.getUser());
        assertNotNull(account.getCreatedAt());
    }

    @Test
    void transaction_gettersAndSetters_workAsExpected() {
        Account account = new Account();
        LocalDateTime now = LocalDateTime.now();

        Transaction transaction = new Transaction();
        transaction.setAmount(120.5);
        transaction.setType("DEPOSIT");
        transaction.setAccount(account);
        transaction.setTimestamp(now);

        assertEquals(120.5, transaction.getAmount());
        assertEquals("DEPOSIT", transaction.getType());
        assertEquals(account, transaction.getAccount());
        assertEquals(now, transaction.getTimestamp());
    }

    @Test
    void user_gettersAndSetters_workAsExpected() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@test.com");
        user.setPassword("pass123");
        user.setRole("ADMIN");

        Account account = new Account();
        user.setAccounts(List.of(account));

        assertEquals("Alice", user.getName());
        assertEquals("alice@test.com", user.getEmail());
        assertEquals("pass123", user.getPassword());
        assertEquals("ADMIN", user.getRole());
        assertEquals(1, user.getAccounts().size());
    }
}