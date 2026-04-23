package com.kapotiabi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.kapotiabi.dto.request.JobRequestRequest;
import com.kapotiabi.entity.CarEntity;
import com.kapotiabi.entity.JobRequestEntity;
import com.kapotiabi.entity.UserEntity;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.CarRepository;
import com.kapotiabi.repository.JobRequestRepository;
import com.kapotiabi.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JobRequestServiceTest {

  @Mock private JobRequestRepository jobRequestRepository;
  @Mock private UserRepository userRepository;
  @Mock private CarRepository carRepository;

  @InjectMocks private JobRequestService jobRequestService;

  private JobRequestEntity buildRequest() {
    return JobRequestEntity.builder()
        .id(1L)
        .user(UserEntity.builder().id(5L).build())
        .car(CarEntity.builder().id(3L).build())
        .description("Oil change")
        .status("pending")
        .city("Tallinn")
        .county("Harju")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  @Test
  void getByUser() {
    when(jobRequestRepository.findByUserId(5L)).thenReturn(List.of(buildRequest()));

    var result = jobRequestService.getByUser(5L);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().userId()).isEqualTo(5L);
  }

  @Test
  void getAvailable_byCity() {
    when(jobRequestRepository.findByStatusAndCity("pending", "Tallinn"))
        .thenReturn(List.of(buildRequest()));

    var result = jobRequestService.getAvailable("Tallinn", null);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().city()).isEqualTo("Tallinn");
  }

  @Test
  void getAvailable_byCounty() {
    when(jobRequestRepository.findByStatusAndCounty("pending", "Harju"))
        .thenReturn(List.of(buildRequest()));

    var result = jobRequestService.getAvailable(null, "Harju");

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().county()).isEqualTo("Harju");
  }

  @Test
  void getById() {
    when(jobRequestRepository.findById(1L)).thenReturn(Optional.of(buildRequest()));

    var response = jobRequestService.getById(1L);

    assertThat(response.id()).isEqualTo(1L);
  }

  @Test
  void create() {
    var req =
        new JobRequestRequest(5L, 3L, "Oil change", false, "OEM", "city", "Tallinn", null, null);
    var saved = buildRequest();
    when(userRepository.getReferenceById(5L)).thenReturn(UserEntity.builder().id(5L).build());
    when(carRepository.getReferenceById(3L)).thenReturn(CarEntity.builder().id(3L).build());
    when(jobRequestRepository.save(any(JobRequestEntity.class))).thenReturn(saved);

    var response = jobRequestService.create(req);

    assertThat(response.status()).isEqualTo("pending");
    assertThat(response.userId()).isEqualTo(5L);
  }

  @Test
  void cancel() {
    var entity = buildRequest();
    when(jobRequestRepository.findById(1L)).thenReturn(Optional.of(entity));
    when(jobRequestRepository.save(any(JobRequestEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    var response = jobRequestService.cancel(1L);

    assertThat(response.status()).isEqualTo("cancelled");
  }

  @Test
  void getById_notFound() {
    when(jobRequestRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> jobRequestService.getById(99L)).isInstanceOf(NotFoundException.class);
  }
}
