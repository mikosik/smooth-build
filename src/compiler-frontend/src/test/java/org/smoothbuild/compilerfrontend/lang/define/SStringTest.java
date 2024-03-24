package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sString;

import org.junit.jupiter.api.Test;

public class SStringTest {
  @Test
  public void to_string() {
    assertThat(sString(7, "abc").toString())
        .isEqualTo("StringS(String, \"abc\", {prj}/build.smooth:7)");
  }
}
