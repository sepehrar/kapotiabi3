package com.kapotiabi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.kapotiabi.dto.response.RepairShopResponse;
import com.kapotiabi.dto.response.ReviewResponse;
import com.kapotiabi.dto.response.ShopServiceCategoryResponse;
import com.kapotiabi.exception.GlobalExceptionHandler;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.service.RepairShopService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class RepairShopControllerTest {

  @Mock private RepairShopService repairShopService;

  @InjectMocks private RepairShopController repairShopController;

  private MockMvc mockMvc;

  private static final RepairShopResponse SHOP =
      new RepairShopResponse(
          1L,
          5L,
          "Test Garage",
          "123 Main St",
          "Tallinn",
          "Harju",
          "555-0000",
          "shop@test.ee",
          null,
          null,
          "active",
          LocalDateTime.now());

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(repairShopController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void getAll_returns200() throws Exception {
    when(repairShopService.getAll(null, null)).thenReturn(List.of(SHOP));

    mockMvc
        .perform(get("/api/v1/shops"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Test Garage"));
  }

  @Test
  void getAll_byCity_returns200() throws Exception {
    when(repairShopService.getAll("Tallinn", null)).thenReturn(List.of(SHOP));

    mockMvc
        .perform(get("/api/v1/shops").param("city", "Tallinn"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].city").value("Tallinn"));
  }

  @Test
  void getById_found_returns200() throws Exception {
    when(repairShopService.getById(1L)).thenReturn(SHOP);

    mockMvc
        .perform(get("/api/v1/shops/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.status").value("active"));
  }

  @Test
  void getById_notFound_returns404() throws Exception {
    when(repairShopService.getById(99L))
        .thenThrow(new NotFoundException("Repair shop not found: 99"));

    mockMvc.perform(get("/api/v1/shops/99")).andExpect(status().isNotFound());
  }

  @Test
  void create_validRequest_returns201() throws Exception {
    when(repairShopService.create(any())).thenReturn(SHOP);

    mockMvc
        .perform(
            post("/api/v1/shops")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"ownerId":5,"name":"Test Garage","city":"Tallinn","status":"active"}
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Test Garage"));
  }

  @Test
  void create_missingName_returns400() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/shops")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"ownerId":5,"city":"Tallinn","status":"active"}
                    """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_returns200() throws Exception {
    RepairShopResponse updated =
        new RepairShopResponse(
            1L,
            5L,
            "Updated Garage",
            null,
            "Tartu",
            null,
            null,
            null,
            null,
            null,
            "active",
            LocalDateTime.now());
    when(repairShopService.update(eq(1L), any())).thenReturn(updated);

    mockMvc
        .perform(
            put("/api/v1/shops/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"ownerId":5,"name":"Updated Garage","city":"Tartu","status":"active"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Garage"));
  }

  @Test
  void getCategories_returns200() throws Exception {
    ShopServiceCategoryResponse category =
        new ShopServiceCategoryResponse(
            10L,
            1L,
            "oil_change",
            new BigDecimal("30.00"),
            new BigDecimal("60.00"),
            "OEM",
            LocalDateTime.now());
    when(repairShopService.getCategories(1L)).thenReturn(List.of(category));

    mockMvc
        .perform(get("/api/v1/shops/1/categories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].category").value("oil_change"));
  }

  @Test
  void addCategory_returns201() throws Exception {
    ShopServiceCategoryResponse category =
        new ShopServiceCategoryResponse(
            10L,
            1L,
            "tire_change",
            new BigDecimal("50.00"),
            new BigDecimal("100.00"),
            "OEM",
            LocalDateTime.now());
    when(repairShopService.addCategory(eq(1L), any())).thenReturn(category);

    mockMvc
        .perform(
            post("/api/v1/shops/1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"category":"tire_change","priceFrom":50.00,"priceTo":100.00,"partsTier":"OEM"}
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.category").value("tire_change"));
  }

  @Test
  void updateCategory_returns200() throws Exception {
    ShopServiceCategoryResponse updated =
        new ShopServiceCategoryResponse(
            10L,
            1L,
            "oil_change",
            new BigDecimal("35.00"),
            new BigDecimal("70.00"),
            "OEM",
            LocalDateTime.now());
    when(repairShopService.updateCategory(eq(1L), eq(10L), any())).thenReturn(updated);

    mockMvc
        .perform(
            put("/api/v1/shops/1/categories/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"category":"oil_change","priceFrom":35.00,"priceTo":70.00,"partsTier":"OEM"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.priceFrom").value(35.00));
  }

  @Test
  void deleteCategory_returns204() throws Exception {
    mockMvc.perform(delete("/api/v1/shops/1/categories/10")).andExpect(status().isNoContent());

    verify(repairShopService).deleteCategory(1L, 10L);
  }

  @Test
  void getReviews_returns200() throws Exception {
    ReviewResponse review = new ReviewResponse(100L, 1L, 5L, 1L, 4, "Good", LocalDateTime.now());
    when(repairShopService.getReviews(1L)).thenReturn(List.of(review));

    mockMvc
        .perform(get("/api/v1/shops/1/reviews"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].rating").value(4));
  }
}
