package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.TestingExpressionS;

public class AnnotationSTest extends TestingExpressionS {
  @Test
  public void to_string() {
    var annS = new AnnotationS("myAnnotation", stringS(7, "myPath"), location(17));
    assertThat(annS.toString())
        .isEqualTo(
            """
            AnnotationS(
              name = myAnnotation
              path = StringS(String, "myPath", {prj}/build.smooth:7)
              location = {prj}/build.smooth:17
            )""");
  }
}
