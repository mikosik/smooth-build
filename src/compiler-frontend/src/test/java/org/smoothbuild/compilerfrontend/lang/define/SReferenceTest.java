package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SReferenceTest extends FrontendCompilerTestContext {
  @Test
  void to_string() {
    var refS = new SReference(sSchema(sIntType()), "referenced", location(7));
    assertThat(refS.toString())
        .isEqualTo(
            """
            SReference(
              schema = <>Int
              referencedName = referenced
              location = {t-project}/module.smooth:7
            )""");
  }
}
