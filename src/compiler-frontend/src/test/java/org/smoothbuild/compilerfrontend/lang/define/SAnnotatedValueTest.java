package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SAnnotatedValueTest extends FrontendCompilerTestContext {
  @Test
  void to_source_code() {
    var annotationS = new SAnnotation("MyAnnotation", sString(7, "myPath"), location(17));
    var annotatedValueS =
        new SAnnotatedValue(annotationS, sSchema(varA()), fqn("module:myValue"), location(7));
    assertThat(annotatedValueS.toSourceCode())
        .isEqualTo("""
          @MyAnnotation("myPath")
          A myValue<A>;""");
  }

  @Test
  void to_string() {
    var annotationS = new SAnnotation("myAnnotation", sString(7, "myPath"), location(17));
    var annotatedValueS =
        new SAnnotatedValue(annotationS, sSchema(sStringType()), fqn("myVal"), location(7));
    assertThat(annotatedValueS.toString())
        .isEqualTo(
            """
            SAnnotatedValue(
              annotation = SAnnotation(
                name = myAnnotation
                path = SString(
                  type = String
                  string = myPath
                  location = {t-project}/module.smooth:7
                )
                location = {t-project}/module.smooth:17
              )
              schema = <>String
              name = myVal
              location = {t-project}/module.smooth:7
            )""");
  }
}
