package com.bank.service;

import com.bank.entity.User;
import com.bank.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_returnsSavedUser() {
        User user = buildUser("Alex", "alex@test.com");
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);

        assertNotNull(result);
        assertEquals("Alex", result.getName());
    }

    @Test
    void getAllUsers_returnsRepositoryList() {
        when(userRepository.findAll()).thenReturn(List.of(buildUser("A", "a@test.com")));

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
    }

    @Test
    void getUserById_whenFound_returnsUser() {
        User user = buildUser("Ben", "ben@test.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("Ben", result.getName());
    }

    @Test
    void getUserById_whenMissing_returnsNull() {
        when(userRepository.findById(44L)).thenReturn(Optional.empty());

        User result = userService.getUserById(44L);

        assertNull(result);
    }

    @Test
    void updateUser_whenFound_updatesAndSaves() {
        User existing = buildUser("Old", "old@test.com");
        User updates = buildUser("New", "new@test.com");
        when(userRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        User result = userService.updateUser(2L, updates);

        assertNotNull(result);
        assertEquals("New", result.getName());
        assertEquals("new@test.com", result.getEmail());
        verify(userRepository).save(existing);
    }

    @Test
    void updateUser_whenMissing_returnsNullAndDoesNotSave() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        User result = userService.updateUser(100L, buildUser("X", "x@test.com"));

        assertNull(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_callsRepositoryDelete() {
        userService.deleteUser(9L);
        verify(userRepository).deleteById(9L);
    }

    private User buildUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("secret");
        user.setRole("USER");
        return user;
    }
}