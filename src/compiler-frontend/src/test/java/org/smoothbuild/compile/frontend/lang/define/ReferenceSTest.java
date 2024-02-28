package org.smoothbuild.compile.frontend.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestExpressionS;

public class ReferenceSTest extends TestExpressionS {
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
