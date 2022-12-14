package org.smoothbuild.compile.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class NamedExprValueSTest extends TestContext {
  @Test
  public void to_string() {
    var namedExprValueS = new NamedExprValueS(schemaS(stringTS()), "myVal", intS(9), location(7));
    assertThat(namedExprValueS.toString())
        .isEqualTo("""
            NamedExprValue(
              schema = <>String
              name = myVal
              location = myBuild.smooth:7
              body = IntS(Int, 9, myBuild.smooth:1)
            )""");
  }
}
