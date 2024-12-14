package org.smoothbuild.compilerfrontend.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.base.CharUtils.isLowerCase;
import static org.smoothbuild.compilerfrontend.lang.base.CharUtils.isUpperCase;

import org.junit.jupiter.api.Test;

public class CharUtilsTest {
  @Test
  void is_upper_case() {
    for (char i = 'A'; i <= 'Z'; i++) {
      assertThat(isUpperCase(i)).isTrue();
    }
    for (char i = 'a'; i <= 'z'; i++) {
      assertThat(isUpperCase(i)).isFalse();
    }
    for (char i = '0'; i <= '9'; i++) {
      assertThat(isUpperCase(i)).isFalse();
    }
    assertThat(isUpperCase('_')).isFalse();
  }

  @Test
  void is_lower_case() {
    for (char i = 'A'; i <= 'Z'; i++) {
      assertThat(isLowerCase(i)).isFalse();
    }
    for (char i = 'a'; i <= 'z'; i++) {
      assertThat(isLowerCase(i)).isTrue();
    }
    for (char i = '0'; i <= '9'; i++) {
      assertThat(isLowerCase(i)).isFalse();
    }
    assertThat(isLowerCase('_')).isFalse();
  }
}
