package org.smoothbuild.compile.lang.type.tool;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compile.lang.type.VarS;

import com.google.common.truth.Truth;

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
    Truth.assertThat(find(index))
        .isEqualTo(new VarS(name));
  }

  private static VarS find(int index) {
    var iterator = new AlphabeticalVarsGenerator();
    for (int j = 0; j < index; j++) {
      iterator.next();
    }
    return iterator.next();
  }
}