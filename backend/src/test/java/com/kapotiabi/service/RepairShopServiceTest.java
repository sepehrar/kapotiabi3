package com.kapotiabi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kapotiabi.dto.request.RepairShopRequest;
import com.kapotiabi.dto.request.ShopServiceCategoryRequest;
import com.kapotiabi.entity.RepairShopEntity;
import com.kapotiabi.entity.ShopServiceCategoryEntity;
import com.kapotiabi.entity.UserEntity;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.RepairShopRepository;
import com.kapotiabi.repository.ShopServiceCategoryRepository;
import com.kapotiabi.repository.UserRepository;
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

  @Mock private UserRepository userRepository;

  @InjectMocks private RepairShopService repairShopService;

  private RepairShopEntity buildShop() {
    return RepairShopEntity.builder()
        .id(1L)
        .owner(UserEntity.builder().id(5L).build())
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
        .shop(RepairShopEntity.builder().id(1L).build())
        .category("oil_change")
        .priceFrom(new BigDecimal("30.00"))
        .priceTo(new BigDecimal("60.00"))
        .lastUpdated(LocalDateTime.now())
        .build();
  }

  @Test
  void getAll_byCity() {
    when(repairShopRepository.findByCity("Tallinn")).thenReturn(List.of(buildShop()));

    var result = repairShopService.getAll("Tallinn", null);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().city()).isEqualTo("Tallinn");
  }

  @Test
  void getById() {
    when(repairShopRepository.findById(1L)).thenReturn(Optional.of(buildShop()));

    var response = repairShopService.getById(1L);

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
    var req =
        new RepairShopRequest(
            5L, "New Shop", null, "Tartu", null, null, null, null, null, "active");
    var saved =
        RepairShopEntity.builder()
            .id(2L)
            .owner(UserEntity.builder().id(5L).build())
            .name("New Shop")
            .city("Tartu")
            .status("active")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    when(userRepository.getReferenceById(5L)).thenReturn(UserEntity.builder().id(5L).build());
    when(repairShopRepository.save(any(RepairShopEntity.class))).thenReturn(saved);

    var response = repairShopService.create(req);

    assertThat(response.id()).isEqualTo(2L);
    assertThat(response.city()).isEqualTo("Tartu");
  }

  @Test
  void update() {
    var shop = buildShop();
    when(repairShopRepository.findById(1L)).thenReturn(Optional.of(shop));
    when(userRepository.getReferenceById(5L)).thenReturn(UserEntity.builder().id(5L).build());
    when(repairShopRepository.save(any(RepairShopEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    var req =
        new RepairShopRequest(
            5L, "Updated Garage", null, "Narva", null, null, null, null, null, "active");
    var response = repairShopService.update(1L, req);

    assertThat(response.name()).isEqualTo("Updated Garage");
    assertThat(response.city()).isEqualTo("Narva");
  }

  @Test
  void addCategory() {
    when(repairShopRepository.findById(1L)).thenReturn(Optional.of(buildShop()));
    var saved = buildCategory();
    when(categoryRepository.save(any(ShopServiceCategoryEntity.class))).thenReturn(saved);

    var req =
        new ShopServiceCategoryRequest(
            "oil_change", new BigDecimal("30.00"), new BigDecimal("60.00"), "OEM");
    var response = repairShopService.addCategory(1L, req);

    assertThat(response.category()).isEqualTo("oil_change");
    assertThat(response.shopId()).isEqualTo(1L);
  }

  @Test
  void updateCategory() {
    when(repairShopRepository.findById(1L)).thenReturn(Optional.of(buildShop()));
    var category = buildCategory();
    when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
    when(categoryRepository.save(any(ShopServiceCategoryEntity.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    var req =
        new ShopServiceCategoryRequest(
            "tire_change", new BigDecimal("50.00"), new BigDecimal("100.00"), "OEM");
    var response = repairShopService.updateCategory(1L, 10L, req);

    assertThat(response.category()).isEqualTo("tire_change");
  }

  @Test
  void deleteCategory() {
    when(repairShopRepository.findById(1L)).thenReturn(Optional.of(buildShop()));
    when(categoryRepository.findById(10L)).thenReturn(Optional.of(buildCategory()));

    repairShopService.deleteCategory(1L, 10L);

    verify(categoryRepository).deleteById(10L);
  }
}
