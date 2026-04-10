package com.kapotiabi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.kapotiabi.dto.response.CarResponse;
import com.kapotiabi.exception.GlobalExceptionHandler;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.service.CarService;
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
class CarControllerTest {

  @Mock private CarService carService;

  @InjectMocks private CarController carController;

  private MockMvc mockMvc;

  private static final CarResponse CAR =
      new CarResponse(
          1L, 10L, "ABC123", "Toyota", "Corolla", 2020, "1.6 petrol", LocalDateTime.now());

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(carController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void getByOwner_returns200() throws Exception {
    when(carService.getByOwner(10L)).thenReturn(List.of(CAR));

    mockMvc
        .perform(get("/api/v1/cars").param("ownerId", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].plateNumber").value("ABC123"));
  }

  @Test
  void getByOwner_noOwnerIdParam_returns400() throws Exception {
    mockMvc.perform(get("/api/v1/cars")).andExpect(status().isBadRequest());
  }

  @Test
  void getById_found_returns200() throws Exception {
    when(carService.getById(1L)).thenReturn(CAR);

    mockMvc
        .perform(get("/api/v1/cars/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.make").value("Toyota"));
  }

  @Test
  void getById_notFound_returns404() throws Exception {
    when(carService.getById(99L)).thenThrow(new NotFoundException("Car not found: 99"));

    mockMvc
        .perform(get("/api/v1/cars/99"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Car not found: 99"));
  }

  @Test
  void create_validRequest_returns201() throws Exception {
    when(carService.create(any())).thenReturn(CAR);

    mockMvc
        .perform(
            post("/api/v1/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"ownerId":10,"plateNumber":"ABC123","make":"Toyota","model":"Corolla","year":2020,"engine":"1.6 petrol"}
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.plateNumber").value("ABC123"));
  }

  @Test
  void create_missingPlateNumber_returns400() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"ownerId":10,"make":"Toyota","model":"Corolla"}
                    """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_missingOwnerId_returns400() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"plateNumber":"ABC123","make":"Toyota"}
                    """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void update_validRequest_returns200() throws Exception {
    CarResponse updated =
        new CarResponse(
            1L, 10L, "XYZ789", "Honda", "Civic", 2021, "1.5 turbo", LocalDateTime.now());
    when(carService.update(eq(1L), any())).thenReturn(updated);

    mockMvc
        .perform(
            put("/api/v1/cars/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"ownerId":10,"plateNumber":"XYZ789","make":"Honda","model":"Civic","year":2021,"engine":"1.5 turbo"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.plateNumber").value("XYZ789"));
  }

  @Test
  void delete_returns204() throws Exception {
    mockMvc.perform(delete("/api/v1/cars/1")).andExpect(status().isNoContent());

    verify(carService).delete(1L);
  }
}
