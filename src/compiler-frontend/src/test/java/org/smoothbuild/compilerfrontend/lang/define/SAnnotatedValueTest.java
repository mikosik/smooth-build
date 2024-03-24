package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sSchema;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sString;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sStringType;

import org.junit.jupiter.api.Test;

public class SAnnotatedValueTest {
  @Test
  public void to_string() {
    var annotationS = new SAnnotation("myAnnotation", sString(7, "myPath"), location(17));
    var annotatedValueS =
        new SAnnotatedValue(annotationS, sSchema(sStringType()), "myVal", location(7));
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
