package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sString;

import org.junit.jupiter.api.Test;

public class SAnnotationTest {
  @Test
  public void to_string() {
    var annS = new SAnnotation("myAnnotation", sString(7, "myPath"), location(17));
    assertThat(annS.toString())
        .isEqualTo(
            """
            SAnnotation(
              name = myAnnotation
              path = SString(String, "myPath", {prj}/build.smooth:7)
              location = {prj}/build.smooth:17
            )""");
  }
}
