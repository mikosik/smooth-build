package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SIntTest extends FrontendCompilerTestContext {
  @Test
  void to_source_code() {
    assertThat(sInt(16).toSourceCode(varSetS())).isEqualTo("16");
  }

  @Test
  void to_string() {
    assertThat(sInt(7, 16).toString())
        .isEqualTo(
            """
        SInt(
          type = Int
          bigInteger = 16
          location = {t-project}/module.smooth:7
        )""");
  }
}
