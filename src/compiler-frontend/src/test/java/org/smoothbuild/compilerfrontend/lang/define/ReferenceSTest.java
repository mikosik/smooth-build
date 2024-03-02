package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.TestingExpressionS;

public class ReferenceSTest extends TestingExpressionS {
  @Test
  public void to_string() {
    var refS = new ReferenceS(schemaS(intTS()), "referenced", location(7));
    assertThat(refS.toString())
        .isEqualTo(
            """
            ReferenceS(
              schema = <>Int
              name = referenced
              location = {prj}/build.smooth:7
            )""");
  }
}
