package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SAnnotationTest extends FrontendCompilerTestContext {
  @Test
  void to_string() {
    var annS = new SAnnotation("myAnnotation", sString(7, "myPath"), location(17));
    assertThat(annS.toString())
        .isEqualTo(
            """
            SAnnotation(
              name = myAnnotation
              path = SString(String, "myPath", {t-project}/module.smooth:7)
              location = {t-project}/module.smooth:17
            )""");
  }
}
