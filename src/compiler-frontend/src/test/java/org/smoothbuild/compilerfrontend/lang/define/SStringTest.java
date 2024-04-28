package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sString;

import org.junit.jupiter.api.Test;

public class SStringTest {
  @Test
  void to_string() {
    assertThat(sString(7, "abc").toString())
        .isEqualTo("SString(String, \"abc\", {project}/build.smooth:7)");
  }
}
