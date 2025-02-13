package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Set.set;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SStringTest extends FrontendCompilerTestContext {
  @Test
  void to_source_code() {
    assertThat(sString("abc").toSourceCode(set())).isEqualTo("\"abc\"");
  }

  @Test
  void to_string() {
    assertThat(sString(7, "abc").toString())
        .isEqualTo(
            """
            SString(
              type = String
              string = abc
              location = {t-project}/module.smooth:7
            )""");
  }
}
