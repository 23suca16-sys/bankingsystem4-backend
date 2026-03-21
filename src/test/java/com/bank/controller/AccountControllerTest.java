package com.bank.controller;

import com.bank.config.GlobalExceptionHandler;
import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@Import(GlobalExceptionHandler.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    @Test
    void getAccountsByUser_whenServiceReturnsAccounts_returnsAccounts() throws Exception {
        Account account = new Account();
        account.setAccountNumber("ACC-1");
        account.setBalance(120.0);

        when(accountService.getAccountsByUserId(1L)).thenReturn(List.of(account));

        mockMvc.perform(get("/api/accounts/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].accountNumber").value("ACC-1"))
                .andExpect(jsonPath("$[0].balance").value(120.0));
    }

    @Test
    void getAccountsByUser_whenServiceReturnsNull_returnsEmptyList() throws Exception {
        when(accountService.getAccountsByUserId(2L)).thenReturn(null);

        mockMvc.perform(get("/api/accounts/user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createAccount_whenUserExists_returnsCreatedAccount() throws Exception {
        Account request = new Account();
        request.setAccountNumber("ACC-NEW");
        request.setAccountType("SAVINGS");
        request.setBalance(1000.0);

        Account response = new Account();
        response.setAccountNumber("ACC-NEW");
        response.setAccountType("SAVINGS");
        response.setBalance(1000.0);

        when(accountService.createAccount(eq(7L), any(Account.class))).thenReturn(response);

        mockMvc.perform(post("/api/accounts/user/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("ACC-NEW"))
                .andExpect(jsonPath("$.accountType").value("SAVINGS"))
                .andExpect(jsonPath("$.balance").value(1000.0));
    }

    @Test
    void createAccount_whenUserMissing_returnsSafeMessage() throws Exception {
        Account request = new Account();
        request.setAccountNumber("ACC-X");

        when(accountService.createAccount(eq(999L), any(Account.class))).thenReturn(null);

        mockMvc.perform(post("/api/accounts/user/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void deposit_whenAccountExists_returnsUpdatedAccount() throws Exception {
        Account updated = new Account();
        updated.setAccountNumber("ACC-DEP");
        updated.setBalance(450.0);

        when(accountService.deposit(5L, 50.0)).thenReturn(updated);

        mockMvc.perform(post("/api/accounts/5/deposit").param("amount", "50.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("ACC-DEP"))
                .andExpect(jsonPath("$.balance").value(450.0));
    }

    @Test
    void deposit_whenAccountMissing_returnsSafeMessage() throws Exception {
        when(accountService.deposit(404L, 20.0)).thenReturn(null);

        mockMvc.perform(post("/api/accounts/404/deposit").param("amount", "20.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account not found"));
    }

    @Test
    void withdraw_whenAccountExists_returnsUpdatedAccount() throws Exception {
        Account updated = new Account();
        updated.setAccountNumber("ACC-WDR");
        updated.setBalance(150.0);

        when(accountService.withdraw(6L, 50.0)).thenReturn(updated);

        mockMvc.perform(post("/api/accounts/6/withdraw").param("amount", "50.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("ACC-WDR"))
                .andExpect(jsonPath("$.balance").value(150.0));
    }

    @Test
    void withdraw_whenAccountMissing_returnsSafeMessage() throws Exception {
        when(accountService.withdraw(405L, 5.0)).thenReturn(null);

        mockMvc.perform(post("/api/accounts/405/withdraw").param("amount", "5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account not found"));
    }

    @Test
    void withdraw_whenServiceThrowsException_isHandledByGlobalExceptionHandler() throws Exception {
        when(accountService.withdraw(10L, 500.0)).thenThrow(new RuntimeException("Insufficient balance"));

        mockMvc.perform(post("/api/accounts/10/withdraw").param("amount", "500.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Request processed with an error"))
                .andExpect(jsonPath("$.error").value("Insufficient balance"));
    }

    @Test
    void getTransactions_whenServiceReturnsTransactions_returnsTransactions() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setType("DEPOSIT");
        transaction.setAmount(100.0);

        when(accountService.getTransactions(3L)).thenReturn(List.of(transaction));

        mockMvc.perform(get("/api/accounts/3/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"))
                .andExpect(jsonPath("$[0].amount").value(100.0));
    }

    @Test
    void getTransactions_whenServiceReturnsNull_returnsEmptyList() throws Exception {
        when(accountService.getTransactions(4L)).thenReturn(null);

        mockMvc.perform(get("/api/accounts/4/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}