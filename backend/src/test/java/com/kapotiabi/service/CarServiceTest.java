package com.kapotiabi.service;

import com.kapotiabi.dto.request.CarRequest;
import com.kapotiabi.dto.response.CarResponse;
import com.kapotiabi.entity.Car;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock CarRepository carRepository;
    @InjectMocks CarService carService;

    private Car sampleCar() {
        Car car = new Car();
        car.setId(1L);
        car.setOwnerId(10L);
        car.setPlateNumber("123ABC");
        car.setMake("Toyota");
        car.setModel("Corolla");
        car.setYear(2020);
        car.setEngine("1.8L");
        car.setCreatedAt(LocalDateTime.now());
        return car;
    }

    @Test
    void create_success() {
        var req = new CarRequest(10L, "123ABC", "Toyota", "Corolla", 2020, "1.8L");
        when(carRepository.save(any())).thenAnswer(inv -> {
            Car c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        CarResponse result = carService.create(req);

        assertThat(result.plateNumber()).isEqualTo("123ABC");
        assertThat(result.ownerId()).isEqualTo(10L);
    }

    @Test
    void getByOwner_returnsOnlyOwnersCars() {
        when(carRepository.findByOwnerId(10L)).thenReturn(List.of(sampleCar()));

        List<CarResponse> result = carService.getByOwner(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).ownerId()).isEqualTo(10L);
    }

    @Test
    void getById_notFound_throws() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.getById(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_success() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(sampleCar()));
        when(carRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var req = new CarRequest(10L, "NEW123", "Honda", "Civic", 2022, "2.0L");
        CarResponse result = carService.update(1L, req);

        assertThat(result.plateNumber()).isEqualTo("NEW123");
        assertThat(result.make()).isEqualTo("Honda");
    }

    @Test
    void delete_success() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(sampleCar()));

        carService.delete(1L);

        verify(carRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throws() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.delete(99L))
                .isInstanceOf(NotFoundException.class);
    }
}
