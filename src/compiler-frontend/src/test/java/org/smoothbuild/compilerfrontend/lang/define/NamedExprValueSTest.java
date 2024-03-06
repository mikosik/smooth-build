package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.intS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.location;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.schemaS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.stringTS;

import org.junit.jupiter.api.Test;

public class NamedExprValueSTest {
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
