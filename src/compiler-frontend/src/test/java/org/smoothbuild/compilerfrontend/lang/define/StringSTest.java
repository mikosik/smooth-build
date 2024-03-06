package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.stringS;

import org.junit.jupiter.api.Test;

public class StringSTest {
  @Test
  public void to_string() {
    assertThat(stringS(7, "abc").toString())
        .isEqualTo("StringS(String, \"abc\", {prj}/build.smooth:7)");
  }
}
