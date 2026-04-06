package com.kapotiabi.service;

import com.kapotiabi.dto.request.JobRequestRequest;
import com.kapotiabi.dto.response.JobRequestResponse;
import com.kapotiabi.entity.JobRequest;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.JobRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobRequestServiceTest {

    @Mock JobRequestRepository jobRequestRepository;
    @InjectMocks JobRequestService jobRequestService;

    private JobRequest sampleRequest() {
        JobRequest jr = new JobRequest();
        jr.setId(1L);
        jr.setUserId(2L);
        jr.setCarId(3L);
        jr.setDescription("Engine noise");
        jr.setStatus("pending");
        jr.setSearchScope("city");
        jr.setCity("Tallinn");
        return jr;
    }

    @Test
    void create_setsStatusToPending() {
        var req = new JobRequestRequest(2L, 3L, "Engine noise", false, null, "city", "Tallinn", null, null);
        when(jobRequestRepository.save(any())).thenAnswer(inv -> {
            JobRequest jr = inv.getArgument(0);
            jr.setId(1L);
            return jr;
        });

        JobRequestResponse result = jobRequestService.create(req);

        assertThat(result.status()).isEqualTo("pending");
        assertThat(result.description()).isEqualTo("Engine noise");
    }

    @Test
    void cancel_setsStatusToCancelled() {
        when(jobRequestRepository.findById(1L)).thenReturn(Optional.of(sampleRequest()));
        when(jobRequestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        JobRequestResponse result = jobRequestService.cancel(1L);

        assertThat(result.status()).isEqualTo("cancelled");
    }

    @Test
    void getById_notFound_throws() {
        when(jobRequestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jobRequestService.getById(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAvailable_filterByCity() {
        when(jobRequestRepository.findByStatusAndCity("pending", "Tallinn"))
                .thenReturn(List.of(sampleRequest()));

        List<JobRequestResponse> result = jobRequestService.getAvailable("Tallinn", null);

        assertThat(result).hasSize(1);
        verify(jobRequestRepository).findByStatusAndCity("pending", "Tallinn");
    }

    @Test
    void getAvailable_filterByCounty() {
        when(jobRequestRepository.findByStatusAndCounty("pending", "Harju"))
                .thenReturn(List.of(sampleRequest()));

        List<JobRequestResponse> result = jobRequestService.getAvailable(null, "Harju");

        assertThat(result).hasSize(1);
        verify(jobRequestRepository).findByStatusAndCounty("pending", "Harju");
    }

    @Test
    void getByUser_success() {
        when(jobRequestRepository.findByUserId(2L)).thenReturn(List.of(sampleRequest()));

        List<JobRequestResponse> result = jobRequestService.getByUser(2L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).userId()).isEqualTo(2L);
    }
}
