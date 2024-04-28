package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sString;

import org.junit.jupiter.api.Test;

public class SAnnotationTest {
  @Test
  void to_string() {
    var annS = new SAnnotation("myAnnotation", sString(7, "myPath"), location(17));
    assertThat(annS.toString())
        .isEqualTo(
            """
            SAnnotation(
              name = myAnnotation
              path = SString(String, "myPath", {project}/build.smooth:7)
              location = {project}/build.smooth:17
            )""");
  }
}
