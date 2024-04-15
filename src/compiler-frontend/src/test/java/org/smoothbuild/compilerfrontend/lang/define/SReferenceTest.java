package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sIntType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sSchema;

import org.junit.jupiter.api.Test;

public class SReferenceTest {
  @Test
  public void to_string() {
    var refS = new SReference(sSchema(sIntType()), "referenced", location(7));
    assertThat(refS.toString())
        .isEqualTo(
            """
            SReference(
              schema = <>Int
              referencedName = referenced
              location = {project}/build.smooth:7
            )""");
  }
}
