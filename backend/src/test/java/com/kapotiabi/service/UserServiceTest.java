package com.kapotiabi.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.kapotiabi.dto.request.UpdateUserRequest;
import com.kapotiabi.dto.response.UserResponse;
import com.kapotiabi.entity.UserEntity;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock UserRepository userRepository;
  @InjectMocks UserService userService;

  @Test
  void getById_success() {
    UserEntity user = new UserEntity();
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
    UserEntity user = new UserEntity();
    user.setId(1L);
    user.setEmail("a@b.com");
    user.setRole("car_owner");
    user.setCreatedAt(LocalDateTime.now());
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    var req = new UpdateUserRequest("Jane", "Smith", "999");
    UserResponse result = userService.update(1L, req);

    assertThat(result.firstName()).isEqualTo("Jane");
    assertThat(result.phone()).isEqualTo("999");
  }
}
