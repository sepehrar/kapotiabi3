package com.kapotiabi.service;

import com.kapotiabi.dto.request.ShopOfferRequest;
import com.kapotiabi.dto.response.ShopOfferResponse;
import com.kapotiabi.entity.JobRequest;
import com.kapotiabi.entity.ShopOffer;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.JobRequestRepository;
import com.kapotiabi.repository.ShopOfferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShopOfferServiceTest {

    @Mock ShopOfferRepository offerRepository;
    @Mock JobRequestRepository jobRequestRepository;
    @InjectMocks ShopOfferService shopOfferService;

    ShopOfferServiceTest() {
        // uploadDir is set via @Value, inject it manually for tests
    }

    private JobRequest pendingJobRequest() {
        JobRequest jr = new JobRequest();
        jr.setId(1L);
        jr.setUserId(2L);
        jr.setStatus("pending");
        return jr;
    }

    private ShopOffer sampleOffer() {
        ShopOffer offer = new ShopOffer();
        offer.setId(10L);
        offer.setJobRequestId(1L);
        offer.setShopId(5L);
        offer.setPriceEstimate(new BigDecimal("150.00"));
        offer.setStatus("pending");
        return offer;
    }

    @Test
    void create_success_flipsJobRequestToOffersReceived() {
        var req = new ShopOfferRequest(5L, new BigDecimal("150.00"), "Parts + labour", "oem", new BigDecimal("2"), null);
        when(jobRequestRepository.findById(1L)).thenReturn(Optional.of(pendingJobRequest()));
        when(offerRepository.save(any())).thenAnswer(inv -> {
            ShopOffer o = inv.getArgument(0);
            o.setId(10L);
            return o;
        });

        ShopOfferResponse result = shopOfferService.create(1L, req);

        assertThat(result.status()).isEqualTo("pending");
        assertThat(result.priceEstimate()).isEqualByComparingTo("150.00");
        verify(jobRequestRepository).save(argThat(jr -> "offers_received".equals(jr.getStatus())));
    }

    @Test
    void create_jobRequestNotFound_throws() {
        when(jobRequestRepository.findById(99L)).thenReturn(Optional.empty());

        var req = new ShopOfferRequest(5L, new BigDecimal("100"), null, null, null, null);
        assertThatThrownBy(() -> shopOfferService.create(99L, req))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void reject_setsStatusToRejected() {
        when(offerRepository.findById(10L)).thenReturn(Optional.of(sampleOffer()));
        when(offerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ShopOfferResponse result = shopOfferService.reject(10L);

        assertThat(result.status()).isEqualTo("rejected");
    }

    @Test
    void getById_notFound_throws() {
        when(offerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shopOfferService.getById(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_success() {
        when(offerRepository.findById(10L)).thenReturn(Optional.of(sampleOffer()));
        when(offerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var req = new ShopOfferRequest(5L, new BigDecimal("200.00"), "Updated breakdown", "budget", new BigDecimal("3"), null);
        ShopOfferResponse result = shopOfferService.update(10L, req);

        assertThat(result.priceEstimate()).isEqualByComparingTo("200.00");
        assertThat(result.breakdown()).isEqualTo("Updated breakdown");
    }
}
