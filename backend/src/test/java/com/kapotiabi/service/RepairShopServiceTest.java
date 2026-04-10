package com.kapotiabi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kapotiabi.dto.request.RepairShopRequest;
import com.kapotiabi.dto.request.ShopServiceCategoryRequest;
import com.kapotiabi.dto.response.RepairShopResponse;
import com.kapotiabi.dto.response.ShopServiceCategoryResponse;
import com.kapotiabi.entity.RepairShopEntity;
import com.kapotiabi.entity.ShopServiceCategoryEntity;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.RepairShopRepository;
import com.kapotiabi.repository.ReviewRepository;
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
class RepairShopServiceTest {

  @Mock private RepairShopRepository repairShopRepository;

  @Mock private ShopServiceCategoryRepository categoryRepository;

  @Mock private ReviewRepository reviewRepository;

  @InjectMocks private RepairShopService repairShopService;

  private RepairShopEntity buildShop() {
    return RepairShopEntity.builder()
        .id(1L)
        .ownerId(5L)
        .name("Test Garage")
        .city("Tallinn")
        .county("Harju")
        .status("active")
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  private ShopServiceCategoryEntity buildCategory() {
    return ShopServiceCategoryEntity.builder()
        .id(10L)
        .shopId(1L)
        .category("oil_change")
        .priceFrom(new BigDecimal("30.00"))
        .priceTo(new BigDecimal("60.00"))
        .lastUpdated(LocalDateTime.now())
        .build();
  }

  @Test
  void getAll_byCity() {
    when(repairShopRepository.findByCity("Tallinn")).thenReturn(List.of(buildShop()));

    List<RepairShopResponse> result = repairShopService.getAll("Tallinn", null);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).city()).isEqualTo("Tallinn");
  }

  @Test
  void getById() {
    when(repairShopRepository.findById(1L)).thenReturn(Optional.of(buildShop()));

    RepairShopResponse response = repairShopService.getById(1L);

    assertThat(response.id()).isEqualTo(1L);
    assertThat(response.name()).isEqualTo("Test Garage");
  }

  @Test
  void getById_notFound() {
    when(repairShopRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> repairShopService.getById(99L)).isInstanceOf(NotFoundException.class);
  }

  @Test
  void create() {
    RepairShopRequest req =
        new RepairShopRequest(
            5L, "New Shop", null, "Tartu", null, null, null, null, null, "active");
    RepairShopEntity saved =
        RepairShopEntity.builder()
            .id(2L)
            .ownerId(5L)
            .name("New Shop")
            .city("Tartu")
            .status("active")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    when(repairShopRepository.save(any(RepairShopEntity.class))).thenReturn(saved);

    RepairShopResponse response = repairShopService.create(req);

    assertThat(response.id()).isEqualTo(2L);
    assertThat(response.city()).isEqualTo("Tartu");
  }

  @Test
  void update() {
    RepairShopEntity shop = buildShop();
    when(repairShopRepository.findById(1L)).thenReturn(Optional.of(shop));
    when(repairShopRepository.save(any(RepairShopEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    RepairShopRequest req =
        new RepairShopRequest(
            5L, "Updated Garage", null, "Narva", null, null, null, null, null, "active");
    RepairShopResponse response = repairShopService.update(1L, req);

    assertThat(response.name()).isEqualTo("Updated Garage");
    assertThat(response.city()).isEqualTo("Narva");
  }

  @Test
  void addCategory() {
    when(repairShopRepository.findById(1L)).thenReturn(Optional.of(buildShop()));
    ShopServiceCategoryEntity saved = buildCategory();
    when(categoryRepository.save(any(ShopServiceCategoryEntity.class))).thenReturn(saved);

    ShopServiceCategoryRequest req =
        new ShopServiceCategoryRequest(
            "oil_change", new BigDecimal("30.00"), new BigDecimal("60.00"), "OEM");
    ShopServiceCategoryResponse response = repairShopService.addCategory(1L, req);

    assertThat(response.category()).isEqualTo("oil_change");
    assertThat(response.shopId()).isEqualTo(1L);
  }

  @Test
  void updateCategory() {
    when(repairShopRepository.findById(1L)).thenReturn(Optional.of(buildShop()));
    ShopServiceCategoryEntity category = buildCategory();
    when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
    when(categoryRepository.save(any(ShopServiceCategoryEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    ShopServiceCategoryRequest req =
        new ShopServiceCategoryRequest(
            "tire_change", new BigDecimal("50.00"), new BigDecimal("100.00"), "OEM");
    ShopServiceCategoryResponse response = repairShopService.updateCategory(1L, 10L, req);

    assertThat(response.category()).isEqualTo("tire_change");
  }

  @Test
  void deleteCategory() {
    when(repairShopRepository.findById(1L)).thenReturn(Optional.of(buildShop()));
    when(categoryRepository.findById(10L)).thenReturn(Optional.of(buildCategory()));

    repairShopService.deleteCategory(1L, 10L);

    verify(categoryRepository).deleteById(10L);
  }

  @Test
  void getAll_byCounty() {
    when(repairShopRepository.findByCounty("Harju")).thenReturn(List.of(buildShop()));

    List<RepairShopResponse> result = repairShopService.getAll(null, "Harju");

    assertThat(result).hasSize(1);
    assertThat(result.get(0).county()).isEqualTo("Harju");
  }

  @Test
  void getAll_noFilter() {
    when(repairShopRepository.findAll()).thenReturn(List.of(buildShop()));

    List<RepairShopResponse> result = repairShopService.getAll(null, null);

    assertThat(result).hasSize(1);
  }

  @Test
  void getCategories() {
    when(categoryRepository.findByShopId(1L)).thenReturn(List.of(buildCategory()));

    List<ShopServiceCategoryResponse> result = repairShopService.getCategories(1L);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).category()).isEqualTo("oil_change");
  }

  @Test
  void getReviews() {
    when(reviewRepository.findByShopId(1L)).thenReturn(List.of());

    var result = repairShopService.getReviews(1L);

    assertThat(result).isEmpty();
  }

  @Test
  void updateCategory_wrongShop_throwsNotFoundException() {
    when(repairShopRepository.findById(1L)).thenReturn(Optional.of(buildShop()));
    ShopServiceCategoryEntity category = buildCategory();
    category.setShopId(99L); // belongs to a different shop
    when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));

    ShopServiceCategoryRequest req =
        new ShopServiceCategoryRequest(
            "oil_change", new BigDecimal("30.00"), new BigDecimal("60.00"), "OEM");

    assertThatThrownBy(() -> repairShopService.updateCategory(1L, 10L, req))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("10");
  }

  @Test
  void deleteCategory_wrongShop_throwsNotFoundException() {
    when(repairShopRepository.findById(1L)).thenReturn(Optional.of(buildShop()));
    ShopServiceCategoryEntity category = buildCategory();
    category.setShopId(99L); // belongs to a different shop
    when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));

    assertThatThrownBy(() -> repairShopService.deleteCategory(1L, 10L))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("10");
  }
}
