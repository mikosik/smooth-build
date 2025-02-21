package org.smoothbuild.compilerfrontend.lang.type.tool;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.lang.name.Name;

public class AlphabeticalTypeNameGeneratorTest {
  private static final String DIGITS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final int RADIX = DIGITS.length();

  @Test
  void one_digit_cases() {
    for (int i = 0; i < DIGITS.length(); i++) {
      checkNameAtIndex(i, Character.toString(DIGITS.charAt(i)));
    }
  }

  @Test
  void aa() {
    checkNameAtIndex(RADIX, "AA");
  }

  @Test
  void zz() {
    checkNameAtIndex(RADIX * RADIX + RADIX - 1, "ZZ");
  }

  @Test
  void aaa() {
    checkNameAtIndex(RADIX * RADIX + RADIX, "AAA");
  }

  private static void checkNameAtIndex(int index, String name) {
    assertThat(find(index)).isEqualTo(Name.typeName(name));
  }

  private static Name find(int index) {
    var iterator = new AlphabeticalTypeNameGenerator();
    for (int j = 0; j < index; j++) {
      iterator.next();
    }
    return iterator.next();
  }
}
