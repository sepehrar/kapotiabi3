package com.kapotiabi.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class UserRoleTest {

  @ParameterizedTest
  @CsvSource({"car_owner, CAR_OWNER", "shop_owner, SHOP_OWNER", "admin, ADMIN"})
  void fromString_dbValue_returnsCorrectRole(String dbValue, UserRole expected) {
    assertThat(UserRole.fromString(dbValue)).isEqualTo(expected);
  }

  @ParameterizedTest
  @CsvSource({"CAR_OWNER, CAR_OWNER", "SHOP_OWNER, SHOP_OWNER", "ADMIN, ADMIN"})
  void fromString_enumName_returnsCorrectRole(String name, UserRole expected) {
    assertThat(UserRole.fromString(name)).isEqualTo(expected);
  }

  @ParameterizedTest
  @CsvSource({"CAR_OWNER", "Car_Owner", "car_owner"})
  void fromString_caseInsensitive(String value) {
    assertThat(UserRole.fromString(value)).isEqualTo(UserRole.CAR_OWNER);
  }

  @Test
  void fromString_unknown_throwsIllegalArgument() {
    assertThatThrownBy(() -> UserRole.fromString("superuser"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unknown role: superuser");
  }

  @Test
  void getDbValue_returnsLowercaseValue() {
    assertThat(UserRole.CAR_OWNER.getDbValue()).isEqualTo("car_owner");
    assertThat(UserRole.SHOP_OWNER.getDbValue()).isEqualTo("shop_owner");
    assertThat(UserRole.ADMIN.getDbValue()).isEqualTo("admin");
  }
}
