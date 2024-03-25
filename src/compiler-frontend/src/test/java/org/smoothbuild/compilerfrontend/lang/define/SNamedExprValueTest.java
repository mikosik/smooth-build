package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sSchema;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sStringType;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.TestingSExpression;

public class SNamedExprValueTest {
  @Test
  public void to_string() {
    var namedExprValueS = new SNamedExprValue(
        sSchema(sStringType()), "myVal", TestingSExpression.sInt(9), location(7));
    assertThat(namedExprValueS.toString())
        .isEqualTo(
            """
            SNamedExprValue(
              schema = <>String
              name = myVal
              location = {prj}/build.smooth:7
              body = SInt(Int, 9, {prj}/build.smooth:1)
            )""");
  }
}
