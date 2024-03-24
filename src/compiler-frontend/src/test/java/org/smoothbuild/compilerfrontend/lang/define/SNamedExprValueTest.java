package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.intS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.schemaS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.stringTS;

import org.junit.jupiter.api.Test;

public class SNamedExprValueTest {
  @Test
  public void to_string() {
    var namedExprValueS = new SNamedExprValue(schemaS(stringTS()), "myVal", intS(9), location(7));
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
