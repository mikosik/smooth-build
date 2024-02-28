package org.smoothbuild.compile.frontend.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestExpressionS;

public class StringSTest extends TestExpressionS {
  @Test
  public void to_string() {
    assertThat(stringS(7, "abc").toString())
        .isEqualTo("StringS(String, \"abc\", {prj}/build.smooth:7)");
  }
}
