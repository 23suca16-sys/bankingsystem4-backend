package com.bank.service;

import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.entity.User;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void getAccountsByUserId_whenUserExists_returnsAccounts() {
        Long userId = 1L;
        User user = new User();
        Account account = new Account();
        account.setAccountNumber("ACC-001");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepository.findByUserId(userId)).thenReturn(List.of(account));

        List<Account> result = accountService.getAccountsByUserId(userId);

        assertEquals(1, result.size());
        assertEquals("ACC-001", result.get(0).getAccountNumber());
    }

    @Test
    void getAccountsByUserId_whenUserMissing_returnsEmptyList() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        List<Account> result = accountService.getAccountsByUserId(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(accountRepository, never()).findByUserId(any());
    }

    @Test
    void getAccountsByUserId_whenRepositoryReturnsNull_returnsEmptyList() {
        Long userId = 2L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepository.findByUserId(userId)).thenReturn(null);

        List<Account> result = accountService.getAccountsByUserId(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void createAccount_whenUserExists_setsUserAndSaves() {
        Long userId = 1L;
        User user = new User();
        Account input = new Account();
        input.setAccountNumber("ACC-100");
        input.setBalance(200.0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepository.save(input)).thenReturn(input);

        Account result = accountService.createAccount(userId, input);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        verify(accountRepository).save(input);
    }

    @Test
    void createAccount_whenUserMissing_returnsNull() {
        Long userId = 404L;
        Account input = new Account();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Account result = accountService.createAccount(userId, input);

        assertNull(result);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void deposit_whenAccountExists_updatesBalanceAndCreatesTransaction() {
        Long accountId = 1L;
        Account account = new Account();
        account.setBalance(100.0);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        Account result = accountService.deposit(accountId, 50.0);

        assertNotNull(result);
        assertEquals(150.0, result.getBalance());
        verify(accountRepository).save(account);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        Transaction savedTransaction = transactionCaptor.getValue();
        assertEquals("DEPOSIT", savedTransaction.getType());
        assertEquals(50.0, savedTransaction.getAmount());
        assertEquals(account, savedTransaction.getAccount());
    }

    @Test
    void deposit_whenAccountMissing_returnsNull() {
        Long accountId = 123L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        Account result = accountService.deposit(accountId, 25.0);

        assertNull(result);
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void deposit_withZeroAmount_keepsBalanceAndCreatesTransaction() {
        Long accountId = 5L;
        Account account = new Account();
        account.setBalance(80.0);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        Account result = accountService.deposit(accountId, 0.0);

        assertNotNull(result);
        assertEquals(80.0, result.getBalance());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void withdraw_whenAccountExistsAndSufficientBalance_updatesBalanceAndCreatesTransaction() {
        Long accountId = 9L;
        Account account = new Account();
        account.setBalance(200.0);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        Account result = accountService.withdraw(accountId, 50.0);

        assertNotNull(result);
        assertEquals(150.0, result.getBalance());
        verify(accountRepository).save(account);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        Transaction savedTransaction = transactionCaptor.getValue();
        assertEquals("WITHDRAW", savedTransaction.getType());
        assertEquals(50.0, savedTransaction.getAmount());
    }

    @Test
    void withdraw_whenAccountMissing_returnsNull() {
        Long accountId = 8L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        Account result = accountService.withdraw(accountId, 10.0);

        assertNull(result);
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void withdraw_whenInsufficientBalance_throwsExceptionAndDoesNotSave() {
        Long accountId = 10L;
        Account account = new Account();
        account.setBalance(30.0);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.withdraw(accountId, 60.0));

        assertEquals("Insufficient balance", exception.getMessage());
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void withdraw_withExactBalance_reducesToZero() {
        Long accountId = 12L;
        Account account = new Account();
        account.setBalance(75.0);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        Account result = accountService.withdraw(accountId, 75.0);

        assertNotNull(result);
        assertEquals(0.0, result.getBalance());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void getTransactions_whenAccountExists_returnsTransactions() {
        Long accountId = 3L;
        Account account = new Account();
        Transaction transaction = new Transaction();
        transaction.setType("DEPOSIT");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountId(accountId)).thenReturn(List.of(transaction));

        List<Transaction> result = accountService.getTransactions(accountId);

        assertEquals(1, result.size());
        assertEquals("DEPOSIT", result.get(0).getType());
    }

    @Test
    void getTransactions_whenAccountMissing_returnsEmptyList() {
        Long accountId = 300L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        List<Transaction> result = accountService.getTransactions(accountId);

        assertNotNull(result);
        assertEquals(Collections.emptyList(), result);
        verify(transactionRepository, never()).findByAccountId(any());
    }

    @Test
    void getTransactions_whenRepositoryReturnsNull_returnsEmptyList() {
        Long accountId = 11L;
        Account account = new Account();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.findByAccountId(accountId)).thenReturn(null);

        List<Transaction> result = accountService.getTransactions(accountId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}