package org.smoothbuild.compile.frontend.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestExpressionS;

public class NamedExprValueSTest extends TestExpressionS {
  @Test
  public void to_string() {
    var namedExprValueS = new NamedExprValueS(schemaS(stringTS()), "myVal", intS(9), location(7));
    assertThat(namedExprValueS.toString())
        .isEqualTo(
            """
            NamedExprValue(
              schema = <>String
              name = myVal
              location = {prj}/build.smooth:7
              body = IntS(Int, 9, {prj}/build.smooth:1)
            )""");
  }
}
