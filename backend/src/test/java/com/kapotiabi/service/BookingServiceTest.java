package com.kapotiabi.service;

import com.kapotiabi.dto.response.BookingResponse;
import com.kapotiabi.entity.Booking;
import com.kapotiabi.entity.JobRequest;
import com.kapotiabi.entity.ShopOffer;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.BookingRepository;
import com.kapotiabi.repository.JobRequestRepository;
import com.kapotiabi.repository.ShopOfferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock BookingRepository bookingRepository;
    @Mock ShopOfferRepository offerRepository;
    @Mock JobRequestRepository jobRequestRepository;
    @InjectMocks BookingService bookingService;

    private ShopOffer sampleOffer() {
        ShopOffer offer = new ShopOffer();
        offer.setId(10L);
        offer.setJobRequestId(1L);
        offer.setShopId(5L);
        offer.setPriceEstimate(new BigDecimal("150.00"));
        offer.setStatus("pending");
        return offer;
    }

    private JobRequest sampleJobRequest() {
        JobRequest jr = new JobRequest();
        jr.setId(1L);
        jr.setUserId(2L);
        jr.setStatus("offers_received");
        return jr;
    }

    private Booking sampleBooking() {
        Booking b = new Booking();
        b.setId(20L);
        b.setOfferId(10L);
        b.setJobRequestId(1L);
        b.setUserId(2L);
        b.setShopId(5L);
        b.setConfirmedByShop(false);
        b.setStatus("awaiting_shop");
        return b;
    }

    @Test
    void createFromOffer_success() {
        when(bookingRepository.findByOfferId(10L)).thenReturn(Optional.empty());
        when(offerRepository.findById(10L)).thenReturn(Optional.of(sampleOffer()));
        when(jobRequestRepository.findById(1L)).thenReturn(Optional.of(sampleJobRequest()));
        when(bookingRepository.save(any())).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setId(20L);
            return b;
        });

        BookingResponse result = bookingService.createFromOffer(10L);

        assertThat(result.status()).isEqualTo("awaiting_shop");
        assertThat(result.shopId()).isEqualTo(5L);
        assertThat(result.userId()).isEqualTo(2L);
        verify(offerRepository).save(argThat(o -> "accepted".equals(o.getStatus())));
        verify(jobRequestRepository).save(argThat(jr -> "booked".equals(jr.getStatus())));
    }

    @Test
    void createFromOffer_alreadyExists_throws() {
        when(bookingRepository.findByOfferId(10L)).thenReturn(Optional.of(sampleBooking()));

        assertThatThrownBy(() -> bookingService.createFromOffer(10L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void createFromOffer_offerNotFound_throws() {
        when(bookingRepository.findByOfferId(99L)).thenReturn(Optional.empty());
        when(offerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createFromOffer(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void confirm_success() {
        when(bookingRepository.findById(20L)).thenReturn(Optional.of(sampleBooking()));
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BookingResponse result = bookingService.confirm(20L);

        assertThat(result.status()).isEqualTo("confirmed");
        assertThat(result.confirmedByShop()).isTrue();
    }

    @Test
    void complete_success() {
        when(bookingRepository.findById(20L)).thenReturn(Optional.of(sampleBooking()));
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BookingResponse result = bookingService.complete(20L);

        assertThat(result.status()).isEqualTo("completed");
    }

    @Test
    void cancel_success() {
        when(bookingRepository.findById(20L)).thenReturn(Optional.of(sampleBooking()));
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BookingResponse result = bookingService.cancel(20L);

        assertThat(result.status()).isEqualTo("cancelled");
    }

    @Test
    void getById_notFound_throws() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getById(99L))
                .isInstanceOf(NotFoundException.class);
    }
}
