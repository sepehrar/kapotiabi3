package com.kapotiabi.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UserRoleConverterTest {

  private final UserRoleConverter converter = new UserRoleConverter();

  @Test
  void convertToDatabaseColumn_returnsDbValue() {
    assertThat(converter.convertToDatabaseColumn(UserRole.CAR_OWNER)).isEqualTo("car_owner");
    assertThat(converter.convertToDatabaseColumn(UserRole.SHOP_OWNER)).isEqualTo("shop_owner");
    assertThat(converter.convertToDatabaseColumn(UserRole.ADMIN)).isEqualTo("admin");
  }

  @Test
  void convertToDatabaseColumn_null_returnsNull() {
    assertThat(converter.convertToDatabaseColumn(null)).isNull();
  }

  @Test
  void convertToEntityAttribute_returnsCorrectRole() {
    assertThat(converter.convertToEntityAttribute("car_owner")).isEqualTo(UserRole.CAR_OWNER);
    assertThat(converter.convertToEntityAttribute("shop_owner")).isEqualTo(UserRole.SHOP_OWNER);
    assertThat(converter.convertToEntityAttribute("admin")).isEqualTo(UserRole.ADMIN);
  }

  @Test
  void convertToEntityAttribute_null_returnsNull() {
    assertThat(converter.convertToEntityAttribute(null)).isNull();
  }

  @Test
  void roundTrip_preservesRole() {
    for (UserRole role : UserRole.values()) {
      String dbValue = converter.convertToDatabaseColumn(role);
      UserRole restored = converter.convertToEntityAttribute(dbValue);
      assertThat(restored).isEqualTo(role);
    }
  }
}
