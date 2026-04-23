package com.kapotiabi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kapotiabi.entity.RepairShopEntity;
import com.kapotiabi.entity.UserEntity;
import com.kapotiabi.entity.UserSavedShopEntity;
import com.kapotiabi.exception.NotFoundException;
import com.kapotiabi.repository.RepairShopRepository;
import com.kapotiabi.repository.UserRepository;
import com.kapotiabi.repository.UserSavedShopRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class UserSavedShopServiceTest {

  @Mock private UserSavedShopRepository savedShopRepository;
  @Mock private RepairShopRepository repairShopRepository;
  @Mock private UserRepository userRepository;

  @InjectMocks private UserSavedShopService userSavedShopService;

  private RepairShopEntity buildShop() {
    return RepairShopEntity.builder()
        .id(10L)
        .owner(UserEntity.builder().id(99L).build())
        .name("AutoExpert Tallinn")
        .city("Tallinn")
        .county("Harju")
        .phone("+372 555 1234")
        .status("active")
        .build();
  }

  private UserSavedShopEntity buildSaved() {
    return UserSavedShopEntity.builder()
        .id(1L)
        .user(UserEntity.builder().id(5L).build())
        .shop(buildShop())
        .savedAt(LocalDateTime.now())
        .build();
  }

  // ── getSaved ──────────────────────────────────────────────────────────────

  @Test
  void getSaved_returnsPagedResult() {
    var saved = buildSaved();
    when(savedShopRepository.findByUserId(any(Long.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(saved), PageRequest.of(0, 10), 1));

    var result = userSavedShopService.getSaved(5L, 0, 10);

    assertThat(result.content()).hasSize(1);
    assertThat(result.content().getFirst().shopId()).isEqualTo(10L);
    assertThat(result.content().getFirst().name()).isEqualTo("AutoExpert Tallinn");
    assertThat(result.totalElements()).isEqualTo(1);
    assertThat(result.totalPages()).isEqualTo(1);
    assertThat(result.page()).isEqualTo(0);
    assertThat(result.size()).isEqualTo(10);
  }

  @Test
  void getSaved_emptyList_returnsEmptyPage() {
    when(savedShopRepository.findByUserId(any(Long.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

    var result = userSavedShopService.getSaved(5L, 0, 10);

    assertThat(result.content()).isEmpty();
    assertThat(result.totalElements()).isEqualTo(0);
  }

  // ── save ──────────────────────────────────────────────────────────────────

  @Test
  void save_success_returnsSavedShopResponse() {
    var shop = buildShop();
    var saved = buildSaved();
    when(repairShopRepository.findById(10L)).thenReturn(Optional.of(shop));
    when(userRepository.getReferenceById(5L)).thenReturn(UserEntity.builder().id(5L).build());
    when(savedShopRepository.save(any(UserSavedShopEntity.class))).thenReturn(saved);

    var response = userSavedShopService.save(5L, 10L);

    assertThat(response.shopId()).isEqualTo(10L);
    assertThat(response.name()).isEqualTo("AutoExpert Tallinn");
    verify(savedShopRepository).save(any(UserSavedShopEntity.class));
  }

  @Test
  void save_shopNotFound_throwsNotFoundException() {
    when(repairShopRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userSavedShopService.save(5L, 99L))
        .isInstanceOf(NotFoundException.class)
        .hasMessageContaining("99");
  }

  @Test
  void save_alreadySaved_propagatesDataIntegrityViolation() {
    when(repairShopRepository.findById(10L)).thenReturn(Optional.of(buildShop()));
    when(userRepository.getReferenceById(5L)).thenReturn(UserEntity.builder().id(5L).build());
    when(savedShopRepository.save(any(UserSavedShopEntity.class)))
        .thenThrow(new DataIntegrityViolationException("duplicate key"));

    assertThatThrownBy(() -> userSavedShopService.save(5L, 10L))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  // ── unsave ────────────────────────────────────────────────────────────────

  @Test
  void unsave_success_deletesRecord() {
    when(savedShopRepository.findByUserIdAndShopId(5L, 10L)).thenReturn(Optional.of(buildSaved()));

    userSavedShopService.unsave(5L, 10L);

    verify(savedShopRepository).deleteByUserIdAndShopId(5L, 10L);
  }

  @Test
  void unsave_notFound_throwsNotFoundException() {
    when(savedShopRepository.findByUserIdAndShopId(5L, 10L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userSavedShopService.unsave(5L, 10L))
        .isInstanceOf(NotFoundException.class);
  }
}
