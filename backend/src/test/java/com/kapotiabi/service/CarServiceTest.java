package com.kapotiabi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kapotiabi.dto.request.CarRequest;
import com.kapotiabi.dto.response.CarResponse;
import com.kapotiabi.entity.CarEntity;
import com.kapotiabi.exception.ForbiddenException;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.CarRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

  @Mock private CarRepository carRepository;

  @InjectMocks private CarService carService;

  private CarEntity buildCar() {
    return CarEntity.builder()
        .id(1L)
        .ownerId(10L)
        .plateNumber("ABC123")
        .make("Toyota")
        .model("Corolla")
        .year(2020)
        .engine("1.6 petrol")
        .createdAt(LocalDateTime.now())
        .build();
  }

  @Test
  void getByOwner() {
    var car = buildCar();
    when(carRepository.findByOwnerId(10L)).thenReturn(List.of(car));

    var result = carService.getByOwner(10L);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().plateNumber()).isEqualTo("ABC123");
  }

  @Test
  void getById_found() {
    CarEntity car = buildCar();
    when(carRepository.findById(1L)).thenReturn(Optional.of(car));

    var response = carService.getById(1L);

    assertThat(response.id()).isEqualTo(1L);
    assertThat(response.make()).isEqualTo("Toyota");
  }

  @Test
  void getById_notFound() {
    when(carRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> carService.getById(99L))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("99");
  }

  @Test
  void create() {
    CarRequest req = new CarRequest(10L, "XYZ789", "BMW", "3 Series", 2022, "2.0 diesel");
    var saved =
        CarEntity.builder()
            .id(2L)
            .ownerId(10L)
            .plateNumber("XYZ789")
            .make("BMW")
            .model("3 Series")
            .year(2022)
            .engine("2.0 diesel")
            .createdAt(LocalDateTime.now())
            .build();
    when(carRepository.save(any(CarEntity.class))).thenReturn(saved);

    CarResponse response = carService.create(req, 10L);

    assertThat(response.id()).isEqualTo(2L);
    assertThat(response.plateNumber()).isEqualTo("XYZ789");
  }

  @Test
  void update() {
    CarEntity car = buildCar();
    when(carRepository.findById(1L)).thenReturn(Optional.of(car));
    when(carRepository.save(any(CarEntity.class))).thenAnswer(inv -> inv.getArgument(0));

    var req = new CarRequest(10L, "NEW123", "Honda", "Civic", 2021, "1.5 turbo");
    var response = carService.update(1L, req, 10L);

    assertThat(response.plateNumber()).isEqualTo("NEW123");
    assertThat(response.make()).isEqualTo("Honda");
  }

  @Test
  void update_wrongOwner_throwsForbidden() {
    when(carRepository.findById(1L)).thenReturn(Optional.of(buildCar()));

    var req = new CarRequest(10L, "NEW123", "Honda", "Civic", 2021, "1.5 turbo");
    assertThatThrownBy(() -> carService.update(1L, req, 99L))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void delete() {
    var car = buildCar();
    when(carRepository.findById(1L)).thenReturn(Optional.of(car));

    carService.delete(1L, 10L);

    verify(carRepository).deleteById(1L);
  }

  @Test
  void delete_wrongOwner_throwsForbidden() {
    when(carRepository.findById(1L)).thenReturn(Optional.of(buildCar()));

    assertThatThrownBy(() -> carService.delete(1L, 99L)).isInstanceOf(ForbiddenException.class);
  }
}
