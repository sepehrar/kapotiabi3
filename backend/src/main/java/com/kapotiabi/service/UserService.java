package com.kapotiabi.service;

import com.kapotiabi.dto.request.UpdateUserRequest;
import com.kapotiabi.dto.response.UserResponse;
import com.kapotiabi.entity.UserEntity;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public UserResponse getById(Long id) {
    return UserResponse.from(findOrThrow(id));
  }

  public UserResponse update(Long id, UpdateUserRequest req) {
    UserEntity user = findOrThrow(id);
    user.setFirstName(req.firstName());
    user.setLastName(req.lastName());
    user.setPhone(req.phone());
    user.setUpdatedAt(LocalDateTime.now());
    return UserResponse.from(userRepository.save(user));
  }

  private UserEntity findOrThrow(Long id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("User not found: " + id));
  }
}
