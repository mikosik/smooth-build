package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.TestingExpressionS;

public class StringSTest extends TestingExpressionS {
  @Test
  public void to_string() {
    assertThat(stringS(7, "abc").toString())
        .isEqualTo("StringS(String, \"abc\", {prj}/build.smooth:7)");
  }
}
