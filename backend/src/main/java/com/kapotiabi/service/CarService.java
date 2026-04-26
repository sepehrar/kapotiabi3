package com.kapotiabi.service;

import com.kapotiabi.dto.request.CarRequest;
import com.kapotiabi.dto.response.CarResponse;
import com.kapotiabi.entity.CarEntity;
import com.kapotiabi.exception.ForbiddenException;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.CarRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarService {

  private final CarRepository carRepository;

  public List<CarResponse> getByOwner(Long ownerId) {
    return carRepository.findByOwnerId(ownerId).stream().map(CarResponse::from).toList();
  }

  public CarResponse getById(Long id) {
    return CarResponse.from(findOrThrow(id));
  }

  public CarResponse create(CarRequest req, Long authUserId) {
    CarEntity car =
        CarEntity.builder()
            .ownerId(authUserId)
            .plateNumber(req.plateNumber())
            .make(req.make())
            .model(req.model())
            .year(req.year())
            .engine(req.engine())
            .createdAt(LocalDateTime.now())
            .build();
    return CarResponse.from(carRepository.save(car));
  }

  public CarResponse update(Long id, CarRequest req, Long authUserId) {
    CarEntity car = findOrThrow(id);
    if (!authUserId.equals(car.getOwnerId()))
      throw new ForbiddenException("You don't have access to this car");
    car.setPlateNumber(req.plateNumber());
    car.setMake(req.make());
    car.setModel(req.model());
    car.setYear(req.year());
    car.setEngine(req.engine());
    return CarResponse.from(carRepository.save(car));
  }

  public void delete(Long id, Long authUserId) {
    CarEntity car = findOrThrow(id);
    if (!authUserId.equals(car.getOwnerId()))
      throw new ForbiddenException("You don't have access to this car");
    carRepository.deleteById(id);
  }

  private CarEntity findOrThrow(Long id) {
    return carRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Car not found: " + id));
  }
}
