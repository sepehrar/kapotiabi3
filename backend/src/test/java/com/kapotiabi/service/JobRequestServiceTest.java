package com.kapotiabi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.kapotiabi.dto.request.JobRequestRequest;
import com.kapotiabi.dto.response.JobRequestResponse;
import com.kapotiabi.entity.CarEntity;
import com.kapotiabi.entity.JobRequestEntity;
import com.kapotiabi.entity.UserEntity;
import com.kapotiabi.exception.ForbiddenException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    List<JobRequestResponse> result = jobRequestService.getByUser(5L);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().userId()).isEqualTo(5L);
  }

  @Test
  void getAvailable_byCity() {
    Pageable pageable = PageRequest.of(0, 20);
    when(jobRequestRepository.findByStatusAndCity("pending", "Tallinn", pageable))
        .thenReturn(new PageImpl<>(List.of(buildRequest())));

    Page<JobRequestResponse> result = jobRequestService.getAvailable("Tallinn", null, pageable);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().getFirst().city()).isEqualTo("Tallinn");
  }

  @Test
  void getAvailable_byCounty() {
    Pageable pageable = PageRequest.of(0, 20);
    when(jobRequestRepository.findByStatusAndCounty("pending", "Harju", pageable))
        .thenReturn(new PageImpl<>(List.of(buildRequest())));

    Page<JobRequestResponse> result = jobRequestService.getAvailable(null, "Harju", pageable);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().getFirst().county()).isEqualTo("Harju");
  }

  @Test
  void getById() {
    when(jobRequestRepository.findById(1L)).thenReturn(Optional.of(buildRequest()));

    JobRequestResponse response = jobRequestService.getById(1L);

    assertThat(response.id()).isEqualTo(1L);
  }

  @Test
  void create() {
    JobRequestRequest req =
        new JobRequestRequest(3L, "Oil change", false, "OEM", "city", "Tallinn", null, null);
    JobRequestEntity saved = buildRequest();
    when(jobRequestRepository.save(any(JobRequestEntity.class))).thenReturn(saved);

    JobRequestResponse response = jobRequestService.create(req, 5L);

    assertThat(response.status()).isEqualTo("pending");
    assertThat(response.userId()).isEqualTo(5L);
  }

  @Test
  void cancel() {
    JobRequestEntity entity = buildRequest();
    when(jobRequestRepository.findById(1L)).thenReturn(Optional.of(entity));
    when(jobRequestRepository.save(any(JobRequestEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    JobRequestResponse response = jobRequestService.cancel(1L, 5L);

    assertThat(response.status()).isEqualTo("cancelled");
  }

  @Test
  void cancel_wrongUser_throwsForbidden() {
    when(jobRequestRepository.findById(1L)).thenReturn(Optional.of(buildRequest()));

    assertThatThrownBy(() -> jobRequestService.cancel(1L, 999L))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void update_wrongUser_throwsForbidden() {
    when(jobRequestRepository.findById(1L)).thenReturn(Optional.of(buildRequest()));

    JobRequestRequest req =
        new JobRequestRequest(3L, "Changed", false, "OEM", "city", "Tallinn", null, null);
    assertThatThrownBy(() -> jobRequestService.update(1L, req, 999L))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void getById_notFound() {
    when(jobRequestRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> jobRequestService.getById(99L)).isInstanceOf(NotFoundException.class);
  }
}
