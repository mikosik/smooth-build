package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SAnnotatedValueTest extends FrontendCompilerTestContext {
  @Test
  void to_string() {
    var annotationS = new SAnnotation("myAnnotation", sString(7, "myPath"), location(17));
    var annotatedValueS =
        new SAnnotatedValue(annotationS, sSchema(sStringType()), "myVal", location(7));
    assertThat(annotatedValueS.toString())
        .isEqualTo(
            """
            SAnnotatedValue(
              SAnnotation(
                name = myAnnotation
                path = SString(String, "myPath", {t-project}/module.smooth:7)
                location = {t-project}/module.smooth:17
              )
              schema = <>String
              name = myVal
              location = {t-project}/module.smooth:7
            )""");
  }
}
