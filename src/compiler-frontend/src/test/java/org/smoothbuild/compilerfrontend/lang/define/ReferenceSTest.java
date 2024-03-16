package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.intTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.location;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.schemaS;

import org.junit.jupiter.api.Test;

public class ReferenceSTest {
  @Test
  public void to_string() {
    var refS = new ReferenceS(schemaS(intTS()), "referenced", location(7));
    assertThat(refS.toString())
        .isEqualTo(
            """
            ReferenceS(
              schema = <>Int
              referencedName = referenced
              location = {prj}/build.smooth:7
            )""");
  }
}
