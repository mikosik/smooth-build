package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SNamedExprValueTest extends FrontendCompilerTestContext {
  @Test
  void to_string() {
    var namedExprValueS =
        new SNamedExprValue(sSchema(sStringType()), "myVal", sInt(9), location(7));
    assertThat(namedExprValueS.toString())
        .isEqualTo(
            """
            SNamedExprValue(
              schema = <>String
              name = myVal
              location = {t-project}/module.smooth:7
              body = SInt(Int, 9, {t-project}/module.smooth:1)
            )""");
  }
}
