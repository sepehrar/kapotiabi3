package com.kapotiabi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kapotiabi.dto.request.GuestEstimateRequest;
import com.kapotiabi.dto.response.GuestEstimateResponse;
import com.kapotiabi.entity.GuestEstimateEntity;
import com.kapotiabi.entity.GuestEstimateResultEntity;
import com.kapotiabi.entity.RepairShopEntity;
import com.kapotiabi.entity.ShopServiceCategoryEntity;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.GuestEstimateRepository;
import com.kapotiabi.repository.GuestEstimateResultRepository;
import com.kapotiabi.repository.RepairShopRepository;
import com.kapotiabi.repository.ShopServiceCategoryRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GuestEstimateServiceTest {

  @Mock private GuestEstimateRepository guestEstimateRepository;
  @Mock private GuestEstimateResultRepository resultRepository;
  @Mock private RepairShopRepository repairShopRepository;
  @Mock private ShopServiceCategoryRepository categoryRepository;

  @InjectMocks private GuestEstimateService guestEstimateService;

  private GuestEstimateEntity buildEstimate(Long id) {
    return GuestEstimateEntity.builder()
        .id(id)
        .plateNumber("ABC123")
        .description("Oil change")
        .city("Tallinn")
        .createdAt(LocalDateTime.now())
        .build();
  }

  private RepairShopEntity buildShop(Long id) {
    return RepairShopEntity.builder()
        .id(id)
        .name("Shop " + id)
        .city("Tallinn")
        .status("active")
        .build();
  }

  private ShopServiceCategoryEntity buildCategory(Long shopId, BigDecimal from, BigDecimal to) {
    return ShopServiceCategoryEntity.builder()
        .id(shopId * 10)
        .shopId(shopId)
        .category("oil_change")
        .priceFrom(from)
        .priceTo(to)
        .build();
  }

  // ── create ────────────────────────────────────────────────────────────────

  @Test
  void create_byCityFilter_queriesShopsByCity() {
    GuestEstimateEntity saved = buildEstimate(1L);
    when(guestEstimateRepository.save(any())).thenReturn(saved);
    when(repairShopRepository.findByStatusAndCity("active", "Tallinn"))
        .thenReturn(List.of(buildShop(1L)));
    when(categoryRepository.findByShopIdIn(List.of(1L)))
        .thenReturn(
            List.of(buildCategory(1L, new BigDecimal("30.00"), new BigDecimal("60.00"))));
    when(resultRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(resultRepository.findByGuestEstimateId(1L)).thenReturn(List.of());

    GuestEstimateRequest req = new GuestEstimateRequest("Oil change", "ABC123", "Tallinn", null);
    guestEstimateService.create(req);

    verify(repairShopRepository).findByStatusAndCity("active", "Tallinn");
    verify(repairShopRepository, never()).findByStatusAndCounty(any(), any());
    verify(repairShopRepository, never()).findByStatus(any());
  }

  @Test
  void create_byCountyFilter_queriesShopsByCounty() {
    GuestEstimateEntity saved = buildEstimate(2L);
    when(guestEstimateRepository.save(any())).thenReturn(saved);
    when(repairShopRepository.findByStatusAndCounty("active", "Harju"))
        .thenReturn(List.of(buildShop(2L)));
    when(categoryRepository.findByShopIdIn(List.of(2L))).thenReturn(List.of());
    when(resultRepository.findByGuestEstimateId(2L)).thenReturn(List.of());

    GuestEstimateRequest req = new GuestEstimateRequest("Tire change", "XYZ456", null, "Harju");
    guestEstimateService.create(req);

    verify(repairShopRepository).findByStatusAndCounty("active", "Harju");
    verify(repairShopRepository, never()).findByStatusAndCity(any(), any());
  }

  @Test
  void create_noLocationFilter_queriesAllActiveShops() {
    GuestEstimateEntity saved = buildEstimate(3L);
    when(guestEstimateRepository.save(any())).thenReturn(saved);
    when(repairShopRepository.findByStatus("active")).thenReturn(List.of());
    when(categoryRepository.findByShopIdIn(List.of())).thenReturn(List.of());
    when(resultRepository.findByGuestEstimateId(3L)).thenReturn(List.of());

    GuestEstimateRequest req = new GuestEstimateRequest("Brakes", "DEF789", null, null);
    guestEstimateService.create(req);

    verify(repairShopRepository).findByStatus("active");
  }

  @Test
  void create_shopsWithCategories_computesOverallPriceRange() {
    GuestEstimateEntity estimate = buildEstimate(1L);
    // First save returns estimate without price range, second includes it
    GuestEstimateEntity estimateWithPrices =
        GuestEstimateEntity.builder()
            .id(1L)
            .plateNumber("ABC123")
            .description("Oil change")
            .city("Tallinn")
            .priceFrom(new BigDecimal("20.00"))
            .priceTo(new BigDecimal("80.00"))
            .createdAt(LocalDateTime.now())
            .build();
    when(guestEstimateRepository.save(any()))
        .thenReturn(estimate)
        .thenReturn(estimateWithPrices);
    when(repairShopRepository.findByStatusAndCity("active", "Tallinn"))
        .thenReturn(List.of(buildShop(1L), buildShop(2L)));
    when(categoryRepository.findByShopIdIn(List.of(1L, 2L)))
        .thenReturn(
            List.of(
                buildCategory(1L, new BigDecimal("20.00"), new BigDecimal("60.00")),
                buildCategory(2L, new BigDecimal("30.00"), new BigDecimal("80.00"))));
    when(resultRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(resultRepository.findByGuestEstimateId(1L)).thenReturn(List.of());

    GuestEstimateRequest req = new GuestEstimateRequest("Oil change", "ABC123", "Tallinn", null);
    GuestEstimateResponse response = guestEstimateService.create(req);

    assertThat(response).isNotNull();
    // Two result saves - one per shop
    verify(resultRepository, org.mockito.Mockito.times(2)).save(any(GuestEstimateResultEntity.class));
  }

  @Test
  void create_shopWithNoCategories_skipsShop() {
    GuestEstimateEntity saved = buildEstimate(1L);
    when(guestEstimateRepository.save(any())).thenReturn(saved);
    when(repairShopRepository.findByStatusAndCity("active", "Tallinn"))
        .thenReturn(List.of(buildShop(1L)));
    // No categories for this shop
    when(categoryRepository.findByShopIdIn(List.of(1L))).thenReturn(List.of());
    when(resultRepository.findByGuestEstimateId(1L)).thenReturn(List.of());

    GuestEstimateRequest req = new GuestEstimateRequest("Oil change", "ABC123", "Tallinn", null);
    guestEstimateService.create(req);

    // No result should be saved since shop has no categories
    verify(resultRepository, never()).save(any());
  }

  @Test
  void create_returnsResponseWithResults() {
    GuestEstimateEntity estimate = buildEstimate(1L);
    when(guestEstimateRepository.save(any())).thenReturn(estimate);
    when(repairShopRepository.findByStatusAndCity("active", "Tallinn"))
        .thenReturn(List.of(buildShop(1L)));
    when(categoryRepository.findByShopIdIn(List.of(1L)))
        .thenReturn(
            List.of(buildCategory(1L, new BigDecimal("30.00"), new BigDecimal("60.00"))));
    when(resultRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    GuestEstimateResultEntity result =
        GuestEstimateResultEntity.builder()
            .id(1L)
            .guestEstimateId(1L)
            .shopId(1L)
            .priceFrom(new BigDecimal("30.00"))
            .priceTo(new BigDecimal("60.00"))
            .build();
    when(resultRepository.findByGuestEstimateId(1L)).thenReturn(List.of(result));

    GuestEstimateRequest req = new GuestEstimateRequest("Oil change", "ABC123", "Tallinn", null);
    GuestEstimateResponse response = guestEstimateService.create(req);

    assertThat(response.id()).isEqualTo(1L);
    assertThat(response.results()).hasSize(1);
    assertThat(response.results().get(0).shopId()).isEqualTo(1L);
  }

  // ── getById ───────────────────────────────────────────────────────────────

  @Test
  void getById_found() {
    GuestEstimateEntity estimate = buildEstimate(1L);
    when(guestEstimateRepository.findById(1L)).thenReturn(Optional.of(estimate));
    when(resultRepository.findByGuestEstimateId(1L)).thenReturn(List.of());

    GuestEstimateResponse response = guestEstimateService.getById(1L);

    assertThat(response.id()).isEqualTo(1L);
    assertThat(response.description()).isEqualTo("Oil change");
  }

  @Test
  void getById_notFound_throwsNotFoundException() {
    when(guestEstimateRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> guestEstimateService.getById(99L))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("99");
  }
}
