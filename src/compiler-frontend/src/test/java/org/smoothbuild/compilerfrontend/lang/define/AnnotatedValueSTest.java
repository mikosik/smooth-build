package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.location;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.schemaS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.stringS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.stringTS;

import org.junit.jupiter.api.Test;

public class AnnotatedValueSTest {
  @Test
  public void to_string() {
    var annotationS = new AnnotationS("myAnnotation", stringS(7, "myPath"), location(17));
    var annotatedValueS =
        new AnnotatedValueS(annotationS, schemaS(stringTS()), "myVal", location(7));
    assertThat(annotatedValueS.toString())
        .isEqualTo(
            """
            AnnotatedValue(
              AnnotationS(
                name = myAnnotation
                path = StringS(String, "myPath", {prj}/build.smooth:7)
                location = {prj}/build.smooth:17
              )
              schema = <>String
              name = myVal
              location = {prj}/build.smooth:7
            )""");
  }
}
