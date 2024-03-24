package org.smoothbuild.compilerfrontend.lang.type.tool;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.lang.type.SVar;

public class AlphabeticalVarsGeneratorTest {
  private static final String DIGITS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final int RADIX = DIGITS.length();

  @Test
  public void one_digit_cases() {
    for (int i = 0; i < DIGITS.length(); i++) {
      checkNameAtIndex(i, Character.toString(DIGITS.charAt(i)));
    }
  }

  @Test
  public void aa() {
    checkNameAtIndex(RADIX, "AA");
  }

  @Test
  public void zz() {
    checkNameAtIndex(RADIX * RADIX + RADIX - 1, "ZZ");
  }

  @Test
  public void aaa() {
    checkNameAtIndex(RADIX * RADIX + RADIX, "AAA");
  }

  private static void checkNameAtIndex(int index, String name) {
    assertThat(find(index)).isEqualTo(new SVar(name));
  }

  private static SVar find(int index) {
    var iterator = new AlphabeticalVarsGenerator();
    for (int j = 0; j < index; j++) {
      iterator.next();
    }
    return iterator.next();
  }
}
