package com.kapotiabi.service;

import com.kapotiabi.dto.request.RepairShopRequest;
import com.kapotiabi.dto.request.ShopServiceCategoryRequest;
import com.kapotiabi.dto.response.RepairShopResponse;
import com.kapotiabi.dto.response.ShopServiceCategoryResponse;
import com.kapotiabi.entity.RepairShop;
import com.kapotiabi.entity.ShopServiceCategory;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.RepairShopRepository;
import com.kapotiabi.repository.ReviewRepository;
import com.kapotiabi.repository.ShopServiceCategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairShopServiceTest {

    @Mock RepairShopRepository shopRepository;
    @Mock ShopServiceCategoryRepository categoryRepository;
    @Mock ReviewRepository reviewRepository;
    @InjectMocks RepairShopService repairShopService;

    private RepairShop sampleShop() {
        RepairShop shop = new RepairShop();
        shop.setId(1L);
        shop.setOwnerId(5L);
        shop.setName("Fast Fix");
        shop.setCity("Tallinn");
        shop.setCounty("Harju");
        shop.setStatus("active");
        shop.setCreatedAt(LocalDateTime.now());
        shop.setUpdatedAt(LocalDateTime.now());
        return shop;
    }

    @Test
    void create_success() {
        var req = new RepairShopRequest(5L, "Fast Fix", "Str 1", "Tallinn", "Harju",
                "555", "shop@test.com", null, null, "active");
        when(shopRepository.save(any())).thenAnswer(inv -> {
            RepairShop s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        RepairShopResponse result = repairShopService.create(req);

        assertThat(result.name()).isEqualTo("Fast Fix");
        assertThat(result.city()).isEqualTo("Tallinn");
    }

    @Test
    void getAll_filterByCity() {
        when(shopRepository.findByCity("Tallinn")).thenReturn(List.of(sampleShop()));

        List<RepairShopResponse> result = repairShopService.getAll("Tallinn", null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).city()).isEqualTo("Tallinn");
        verify(shopRepository).findByCity("Tallinn");
        verify(shopRepository, never()).findByCounty(any());
    }

    @Test
    void getAll_filterByCounty() {
        when(shopRepository.findByCounty("Harju")).thenReturn(List.of(sampleShop()));

        List<RepairShopResponse> result = repairShopService.getAll(null, "Harju");

        assertThat(result).hasSize(1);
        verify(shopRepository).findByCounty("Harju");
    }

    @Test
    void getById_notFound_throws() {
        when(shopRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> repairShopService.getById(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void addCategory_success() {
        when(shopRepository.findById(1L)).thenReturn(Optional.of(sampleShop()));
        when(categoryRepository.save(any())).thenAnswer(inv -> {
            ShopServiceCategory c = inv.getArgument(0);
            c.setId(10L);
            return c;
        });

        var req = new ShopServiceCategoryRequest("oil_change", new BigDecimal("30"), new BigDecimal("60"), "oem");
        ShopServiceCategoryResponse result = repairShopService.addCategory(1L, req);

        assertThat(result.category()).isEqualTo("oil_change");
        assertThat(result.shopId()).isEqualTo(1L);
    }

    @Test
    void updateCategory_shopNotFound_throws() {
        when(shopRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> repairShopService.updateCategory(99L, 1L,
                new ShopServiceCategoryRequest("oil_change", null, null, null)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteCategory_success() {
        when(shopRepository.findById(1L)).thenReturn(Optional.of(sampleShop()));

        repairShopService.deleteCategory(1L, 10L);

        verify(categoryRepository).deleteById(10L);
    }
}
