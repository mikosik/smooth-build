package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SNamedExprValueTest extends FrontendCompilerTestContext {
  @Test
  void to_source_code() {
    var value = new SNamedExprValue(sSchema(varA()), fqn("module:myValue"), sInt(9), location(7));
    assertThat(value.toSourceCode()).isEqualTo("""
        A myValue<A>
          = 9;""");
  }

  @Test
  void to_string() {
    var namedExprValueS =
        new SNamedExprValue(sSchema(sStringType()), fqn("myVal"), sInt(9), location(7));
    assertThat(namedExprValueS.toString())
        .isEqualTo(
            """
            SNamedExprValue(
              schema = <>String
              name = myVal
              location = {t-project}/module.smooth:7
              body = SInt(
                type = Int
                bigInteger = 9
                location = {t-project}/module.smooth:1
              )
            )""");
  }
}
