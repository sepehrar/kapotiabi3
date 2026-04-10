package com.kapotiabi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.kapotiabi.dto.request.UpdateUserRequest;
import com.kapotiabi.dto.response.UserResponse;
import com.kapotiabi.entity.UserEntity;
import com.kapotiabi.enums.UserRole;
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

  @Mock private UserRepository userRepository;

  @InjectMocks private UserService userService;

  private UserEntity buildUser() {
    return UserEntity.builder()
        .id(1L)
        .email("test@test.com")
        .passwordHash("hash")
        .role(UserRole.CAR_OWNER)
        .firstName("John")
        .lastName("Doe")
        .phone("555-1234")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  @Test
  void getById_found() {
    UserEntity user = buildUser();
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    UserResponse response = userService.getById(1L);

    assertThat(response.id()).isEqualTo(1L);
    assertThat(response.email()).isEqualTo("test@test.com");
    assertThat(response.role()).isEqualTo("car_owner");
  }

  @Test
  void getById_notFound() {
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.getById(99L))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("99");
  }

  @Test
  void update_updatesFields() {
    UserEntity user = buildUser();
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

    UpdateUserRequest req = new UpdateUserRequest("Jane", "Smith", "555-9999");
    UserResponse response = userService.update(1L, req);

    assertThat(response.firstName()).isEqualTo("Jane");
    assertThat(response.lastName()).isEqualTo("Smith");
    assertThat(response.phone()).isEqualTo("555-9999");
  }

  @Test
  void update_notFound() {
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    UpdateUserRequest req = new UpdateUserRequest("Jane", "Smith", "555-9999");
    assertThatThrownBy(() -> userService.update(99L, req))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("99");
  }
}
