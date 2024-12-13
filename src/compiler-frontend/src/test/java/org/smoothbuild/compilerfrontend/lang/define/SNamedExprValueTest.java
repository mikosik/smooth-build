package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.base.Id.id;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SNamedExprValueTest extends FrontendCompilerTestContext {
  @Test
  void to_string() {
    var namedExprValueS =
        new SNamedExprValue(sSchema(sStringType()), id("myVal"), sInt(9), location(7));
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
