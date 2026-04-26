package com.kapotiabi.service;

import com.kapotiabi.dto.response.PagedResponse;
import com.kapotiabi.dto.response.SavedShopResponse;
import com.kapotiabi.entity.RepairShopEntity;
import com.kapotiabi.entity.UserSavedShopEntity;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.RepairShopRepository;
import com.kapotiabi.repository.UserRepository;
import com.kapotiabi.repository.UserSavedShopRepository;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSavedShopService {

  private final UserSavedShopRepository savedShopRepository;
  private final RepairShopRepository repairShopRepository;
  private final UserRepository userRepository;

  public PagedResponse<SavedShopResponse> getSaved(Long userId, int page, int size) {
    var pageable = PageRequest.of(page, size, Sort.by("savedAt").descending());
    var savedPage = savedShopRepository.findByUserId(userId, pageable);
    var shopIds = savedPage.getContent().stream().map(s -> s.getShop().getId()).toList();
    var shopMap =
        repairShopRepository.findAllById(shopIds).stream()
            .collect(Collectors.toMap(RepairShopEntity::getId, Function.identity()));
    return PagedResponse.from(
        savedPage.map(
            saved -> {
              RepairShopEntity shop = shopMap.get(saved.getShop().getId());
              if (shop == null)
                throw new NotFoundException("Shop not found: " + saved.getShop().getId());
              return SavedShopResponse.from(saved, shop);
            }));
  }

  @Transactional
  public SavedShopResponse save(Long userId, Long shopId) {
    RepairShopEntity shop =
        repairShopRepository
            .findById(shopId)
            .orElseThrow(() -> new NotFoundException("Shop not found: " + shopId));
    UserSavedShopEntity saved =
        savedShopRepository.save(
            UserSavedShopEntity.builder()
                .user(userRepository.getReferenceById(userId))
                .shop(shop)
                .build());
    return SavedShopResponse.from(saved, shop);
  }

  @Transactional
  public void unsave(Long userId, Long shopId) {
    savedShopRepository
        .findByUserIdAndShopId(userId, shopId)
        .orElseThrow(() -> new NotFoundException("Saved shop not found"));
    savedShopRepository.deleteByUserIdAndShopId(userId, shopId);
  }
}
