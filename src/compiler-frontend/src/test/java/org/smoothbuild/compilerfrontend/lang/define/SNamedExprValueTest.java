package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sInt;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sSchema;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sStringType;

import org.junit.jupiter.api.Test;

public class SNamedExprValueTest {
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
              location = {t-project}/build.smooth:7
              body = SInt(Int, 9, {t-project}/build.smooth:1)
            )""");
  }
}
