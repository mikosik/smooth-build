package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.intTS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.schemaS;

import org.junit.jupiter.api.Test;

public class SReferenceTest {
  @Test
  public void to_string() {
    var refS = new SReference(schemaS(intTS()), "referenced", location(7));
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
