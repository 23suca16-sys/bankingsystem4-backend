package com.bank.controller;

import com.bank.entity.User;
import com.bank.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void createUser_returnsSavedUser() throws Exception {
        User user = user("Ravi", "ravi@test.com");
        when(userService.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ravi"))
                .andExpect(jsonPath("$.email").value("ravi@test.com"));
    }

    @Test
    void getAllUsers_whenServiceReturnsList_returnsList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(user("A", "a@test.com")));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getAllUsers_whenServiceReturnsNull_returnsEmptyList() throws Exception {
        when(userService.getAllUsers()).thenReturn(null);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getUserById_whenFound_returnsUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(user("Nila", "nila@test.com"));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nila"));
    }

    @Test
    void getUserById_whenMissing_returnsEmptyObject() throws Exception {
        when(userService.getUserById(2L)).thenReturn(null);

        mockMvc.perform(get("/api/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap());
    }

    @Test
    void updateUser_whenFound_returnsUpdatedUser() throws Exception {
        User updated = user("Uma", "uma@test.com");
        when(userService.updateUser(any(Long.class), any(User.class))).thenReturn(updated);

        mockMvc.perform(put("/api/users/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Uma"));
    }

    @Test
    void updateUser_whenMissing_returnsSafeMessage() throws Exception {
        when(userService.updateUser(any(Long.class), any(User.class))).thenReturn(null);

        mockMvc.perform(put("/api/users/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user("X", "x@test.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void deleteUser_returnsConfirmationMessage() throws Exception {
        doNothing().when(userService).deleteUser(5L);

        mockMvc.perform(delete("/api/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User deleted with id: 5"));
    }

    private User user(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("pwd");
        user.setRole("USER");
        return user;
    }
}