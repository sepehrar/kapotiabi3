package com.kapotiabi.service;

import com.kapotiabi.dto.request.JobRequestRequest;
import com.kapotiabi.dto.response.JobRequestResponse;
import com.kapotiabi.entity.JobRequest;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.JobRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobRequestService {

    private final JobRequestRepository jobRequestRepository;

    public List<JobRequestResponse> getByUser(Long userId) {
        return jobRequestRepository.findByUserId(userId).stream().map(JobRequestResponse::from).toList();
    }

    // Returns open job requests visible to a shop (filtered by their area)
    public List<JobRequestResponse> getAvailable(String city, String county) {
        if (city != null) return jobRequestRepository.findByStatusAndCity("pending", city)
                .stream().map(JobRequestResponse::from).toList();
        if (county != null) return jobRequestRepository.findByStatusAndCounty("pending", county)
                .stream().map(JobRequestResponse::from).toList();
        return jobRequestRepository.findByStatus("pending").stream().map(JobRequestResponse::from).toList();
    }

    public JobRequestResponse getById(Long id) {
        return JobRequestResponse.from(findOrThrow(id));
    }

    public JobRequestResponse create(JobRequestRequest req) {
        JobRequest jr = new JobRequest();
        applyFields(jr, req);
        jr.setStatus("pending");
        jr.setCreatedAt(LocalDateTime.now());
        jr.setUpdatedAt(LocalDateTime.now());
        return JobRequestResponse.from(jobRequestRepository.save(jr));
    }

    public JobRequestResponse update(Long id, JobRequestRequest req) {
        JobRequest jr = findOrThrow(id);
        applyFields(jr, req);
        jr.setUpdatedAt(LocalDateTime.now());
        return JobRequestResponse.from(jobRequestRepository.save(jr));
    }

    public JobRequestResponse cancel(Long id) {
        JobRequest jr = findOrThrow(id);
        jr.setStatus("cancelled");
        jr.setUpdatedAt(LocalDateTime.now());
        return JobRequestResponse.from(jobRequestRepository.save(jr));
    }

    private void applyFields(JobRequest jr, JobRequestRequest req) {
        jr.setUserId(req.userId());
        jr.setCarId(req.carId());
        jr.setDescription(req.description());
        jr.setAcceptsNonOem(req.acceptsNonOem() != null ? req.acceptsNonOem() : false);
        jr.setPartsPreference(req.partsPreference());
        jr.setSearchScope(req.searchScope());
        jr.setCity(req.city());
        jr.setCounty(req.county());
        jr.setOfferDeadline(req.offerDeadline());
    }

    private JobRequest findOrThrow(Long id) {
        return jobRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job request not found: " + id));
    }
}
