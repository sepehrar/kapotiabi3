package com.kapotiabi.service;

import com.kapotiabi.dto.request.RegisterRequest;
import com.kapotiabi.dto.response.UserResponse;
import com.kapotiabi.entity.User;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks UserService userService;

    @Test
    void register_success() {
        var req = new RegisterRequest("test@example.com", "pass", "car_owner", "John", "Doe", "123");
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserResponse result = userService.register(req);

        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.role()).isEqualTo("car_owner");
        verify(passwordEncoder).encode("pass");
    }

    @Test
    void register_duplicateEmail_throws() {
        var req = new RegisterRequest("dup@example.com", "pass", "car_owner", null, null, null);
        when(userRepository.existsByEmail("dup@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already in use");
    }

    @Test
    void getById_success() {
        User user = new User();
        user.setId(1L);
        user.setEmail("a@b.com");
        user.setRole("car_owner");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse result = userService.getById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("a@b.com");
    }

    @Test
    void getById_notFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_success() {
        User user = new User();
        user.setId(1L);
        user.setEmail("a@b.com");
        user.setRole("car_owner");
        user.setCreatedAt(LocalDateTime.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var req = new RegisterRequest("a@b.com", "pass", "car_owner", "Jane", "Smith", "999");
        UserResponse result = userService.update(1L, req);

        assertThat(result.firstName()).isEqualTo("Jane");
        assertThat(result.phone()).isEqualTo("999");
    }
}
