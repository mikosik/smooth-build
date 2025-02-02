package org.smoothbuild.compilerfrontend.lang.type.tool;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.lang.type.SVar;

public class AlphabeticalVarsGeneratorTest {
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
    assertThat(find(index)).isEqualTo(new SVar(fqn(name)));
  }

  private static SVar find(int index) {
    var iterator = new AlphabeticalVarsGenerator();
    for (int j = 0; j < index; j++) {
      iterator.next();
    }
    return iterator.next();
  }
}
