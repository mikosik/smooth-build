package org.smoothbuild.compile.fs.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class AnnotatedValueSTest extends TestContext {
  @Test
  public void to_string() {
    var annotationS = new AnnotationS("myAnnotation", stringS(7, "myPath"), location(17));
    var annotatedValueS = new AnnotatedValueS(
        annotationS, schemaS(stringTS()), "myVal", location(7));
    assertThat(annotatedValueS.toString())
        .isEqualTo("""
            AnnotatedValue(
              AnnotationS(
                name = myAnnotation
                path = StringS(String, "myPath", myBuild.smooth:7)
                location = myBuild.smooth:17
              )
              schema = <>String
              name = myVal
              location = myBuild.smooth:7
            )""");
  }
}
