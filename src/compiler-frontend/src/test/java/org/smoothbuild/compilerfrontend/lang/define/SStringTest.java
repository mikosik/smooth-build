package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SStringTest extends FrontendCompilerTestContext {
  @Test
  void to_string() {
    assertThat(sString(7, "abc").toString())
        .isEqualTo("SString(String, \"abc\", {t-project}/module.smooth:7)");
  }
}
